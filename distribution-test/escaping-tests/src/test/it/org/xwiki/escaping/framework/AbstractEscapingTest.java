/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xwiki.escaping.framework;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.xwiki.escaping.suite.FileTest;
import org.xwiki.validator.ValidationError;

/**
 * Abstract base class for escaping tests. Implements common initialization pattern and some utility methods
 * like URL escaping, retrieving page content by URL etc. Subclasses need to implement parsing
 * and custom tests.
 * <p>
 * Note: JUnit4 requires tests to have one public default constructor, subclasses will need to implement
 * it and pass pattern matcher to match file names they can handle.</p>
 * <p>
 * Starting and stopping XWiki server is handled transparently for all subclasses, tests can be run
 * alone using -Dtest=ClassName, a parent test suite should start XWiki server before running all
 * tests for efficiency using {@link SingleXWikiExecutor}.</p>
 * <p>
 * The following configuration properties are supported (set in maven):
 * <ul>
 * <li>pattern (optional): Additional pattern to select files to be tested (use -Dpattern="substring-regex").
 *                         Matches all files if empty.</li>
 * </ul></p>
 * <p>
 * Automatic tests (see {@link AbstractAutomaticTest}) additionally support:
 * <ul>
 * <li>filesProduceNoOutput (optional): List of files that are expected to produce empty response</li>
 * <li>patternExcludeFiles (optional): List of RegEx patterns to exclude files from the tests</li>
 * </ul></p>
 * 
 * @version $Id$
 * @since 2.5M1
 */
public abstract class AbstractEscapingTest implements FileTest
{
    /** Static part of the test URL. */
    private static final String URL_START = "http://127.0.0.1:8080/xwiki/bin/";

    /** Language parameter name. */
    private static final String LANGUAGE = "language";

    /** HTTP client shared between all subclasses. */
    private static HttpClient client;

    /** A flag controlling login. If true, administrator credentials are used. */
    private static boolean loggedIn = true;

    /** File name of the template to use. */
    protected String name;

    /** User provided data found in the file. */
    protected Set<String> userInput;

    /**
     * Test fails if response is empty, but output is expected and vice versa.
     * To set to false, add file name to "filesProduceNoOutput" 
     */
    protected boolean shouldProduceOutput = true;

    /** Pattern used to match files by name. */
    private Pattern namePattern;

    /**
     * Create new AbstractEscapingTest.
     * 
     * @param fileNameMatcher regex pattern used to filter files by name
     */
    protected AbstractEscapingTest(Pattern fileNameMatcher)
    {
        this.namePattern = fileNameMatcher;
    }

    /**
     * Start XWiki server if run alone.
     * 
     * @throws Exception on errors
     */
    @BeforeClass
    public static void startExecutor() throws Exception
    {
        SingleXWikiExecutor.getExecutor().start();
    }

    /**
     * Stop XWiki server if run alone.
     * 
     * @throws Exception on errors
     */
    @AfterClass
    public static void stopExecutor() throws Exception
    {
        SingleXWikiExecutor.getExecutor().stop();
    }

    /**
     * Change multi-language mode. Note: XWiki server must already be started.
     * 
     * @param enabled enable the multi-language mode if true, disable otherwise
     */
    protected static void setMultiLanguageMode(boolean enabled)
    {
        String url = AbstractEscapingTest.URL_START + "save/XWiki/XWikiPreferences?";
        url += "XWiki.XWikiPreferences_0_languages=&XWiki.XWikiPreferences_0_multilingual=";
        AbstractEscapingTest.getUrlContent(url + (enabled ? 1 : 0));
        // set language=en to prevent false positives coming from the cookies
        String langUrl = AbstractEscapingTest.URL_START + "view/Main/?" + LANGUAGE + "=en";
        AbstractEscapingTest.getUrlContent(langUrl);
    }

    /**
     * {@inheritDoc}
     * 
     * The implementation for escaping tests checks if the given file name matches the supported name pattern and parses
     * the file.
     * 
     * @see org.xwiki.escaping.suite.FileTest#initialize(java.lang.String, java.io.Reader)
     */
    public boolean initialize(String name, final Reader reader)
    {
        this.name = name;
        if (!fileNameMatches(name) || !patternMatches(name) || isExcludedFile(name)) {
            // TODO debug log the reason why the test was skipped
            return false;
        }

        this.shouldProduceOutput = isOutputProducingFile(name);
        this.userInput = parse(reader);
        return true;
    }

    /**
     * Check if the internal file name pattern matches the given file name.
     * 
     * @param fileName file name to check
     * @return true if the name matches, false otherwise
     */
    protected boolean fileNameMatches(String fileName)
    {
        return this.namePattern != null && this.namePattern.matcher(fileName).matches();
    }

    /**
     * Check if the system property "pattern" matches (substring regular expression) the file name.
     * Empty pattern matches everything.
     * 
     * @param fileName file name to check
     * @return true if the pattern matches, false otherwise
     */
    protected boolean patternMatches(String fileName)
    {
        String pattern = System.getProperty("pattern", "");
        if (pattern == null || pattern.equals("")) {
            return true;
        }
        return Pattern.matches(".*" + pattern + ".*", fileName);
    }

    /**
     * Check if the given file should be excluded from the tests.
     * 
     * @param fileName file name to check
     * @return true if the file should be excluded, false otherwise
     */
    protected abstract boolean isExcludedFile(String fileName);

    /**
     * Check if the given file name should produce output.
     * 
     * @param fileName file name to check
     * @return true if the file is expected to produce some output when requested from the server, false otherwise
     */
    protected abstract boolean isOutputProducingFile(String fileName);

    /**
     * Parse the file and collect parameters controlled by the user.
     * 
     * @param reader the reader associated with the file
     * @return collection of user-controlled input parameters
     */
    protected abstract Set<String> parse(Reader reader);

    /**
     * Check if the authentication status.
     * 
     * @return true if the requests will be sent authenticated as admin, false otherwise
     */
    protected static boolean isLoggedIn()
    {
        return loggedIn;
    }

    /**
     * Set authentication status.
     * 
     * @param value the value to set
     */
    protected static void setLoggedIn(boolean value)
    {
        loggedIn = value;
    }

    /**
     * Download a page from the server and return its content. Throws a {@link RuntimeException}
     * on connection problems etc.
     * 
     * @param url URL of the page
     * @return content of the page
     */
    protected static InputStream getUrlContent(String url)
    {
        GetMethod get = new GetMethod(url);
        get.setFollowRedirects(true);
        if (isLoggedIn()) {
            get.setDoAuthentication(true);
            get.addRequestHeader("Authorization", "Basic " + new String(Base64.encodeBase64("Admin:admin".getBytes())));
        }

        try {
            int statusCode = AbstractEscapingTest.getClient().executeMethod(get);
            switch (statusCode) {
                case HttpStatus.SC_OK:
                    // everything is fine
                    break;
                case HttpStatus.SC_UNAUTHORIZED:
                    // do not fail on 401 (unauthorized), used in some tests
                    System.out.println("WARNING, Ignoring status 401 (unauthorized) for URL: " + url);
                    break;
                case HttpStatus.SC_CONFLICT:
                    // do not fail on 409 (conflict), used in some templates
                    System.out.println("WARNING, Ignoring status 409 (conflict) for URL: " + url);
                    break;
                case HttpStatus.SC_NOT_FOUND:
                    // ignore 404 (the page is still rendered)
                    break;
                default:
                    throw new RuntimeException("HTTP GET request returned status " + statusCode + " ("
                        + get.getStatusText() + ") for URL: " + url);
            }

            // get the data, converting to utf-8
            String str = get.getResponseBodyAsString();
            if (str == null) {
                return null;
            }
            return new ByteArrayInputStream(str.getBytes("utf-8"));
        } catch (IOException exception) {
            throw new RuntimeException("Error retrieving URL: " + url + "\n" + exception);
        } finally {
            get.releaseConnection();
        }
    }

    /**
     * URL-escape given string.
     * 
     * @param str string to escape, "" is used if null
     * @return URL-escaped {@code str}
     */
    protected static String escapeUrl(String str)
    {
        try {
            return URLEncoder.encode(str == null ? "" : str, "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            // should not happen
            throw new RuntimeException("Should not happen: ", exception);
        }
    }

    /**
     * Get an instance of the HTTP client to use.
     * 
     * @return HTTP client initialized with admin credentials
     */
    protected static HttpClient getClient()
    {
        if (AbstractEscapingTest.client == null) {
            HttpClient adminClient = new HttpClient();
            Credentials defaultcreds = new UsernamePasswordCredentials("Admin", "admin");
            adminClient.getState().setCredentials(AuthScope.ANY, defaultcreds);
            HttpClientParams clientParams = new HttpClientParams();
            clientParams.setSoTimeout(2000);
            HttpConnectionManagerParams connectionParams = new HttpConnectionManagerParams();
            connectionParams.setConnectionTimeout(30000);
            adminClient.getHttpConnectionManager().setParams(connectionParams);
            AbstractEscapingTest.client = adminClient;
        }
        return AbstractEscapingTest.client;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.name + (this.shouldProduceOutput ? " " : " (NO OUTPUT) ") + this.userInput;
    }

    /**
     * Check for unescaped data in the given {@code content}. Throws {@link RuntimeException} on errors.
     * 
     * @param url URL used in the test
     * @return list of found validation errors
     */
    protected List<ValidationError> getUnderEscapingErrors(String url)
    {
        // TODO better use log4j
        System.out.println("Testing URL: " + url);

        InputStream content = AbstractEscapingTest.getUrlContent(url);
        String where = "  Template: " + this.name + "\n  URL: " + url;
        Assert.assertNotNull("Response is null\n" + where, content);
        XMLEscapingValidator validator = new XMLEscapingValidator();
        validator.setShouldBeEmpty(!this.shouldProduceOutput);
        validator.setDocument(content);
        try {
            return validator.validate();
        } catch (EscapingError error) {
            // most probably false positive, generate an error instead of failing the test
            throw new RuntimeException(EscapingError.formatMessage(error.getMessage(), this.name, url, null));
        }
    }

    /**
     * A convenience method that throws an {@link EscapingError} on failure.
     *
     * @param url URL used in the test
     * @param description description of the test
     */
    protected void checkUnderEscaping(String url, String description)
    {
        List<ValidationError> errors = getUnderEscapingErrors(url);
        if (!errors.isEmpty()) {
            throw new EscapingError("Escaping test for " + description + " failed.", this.name, url, errors);
        }
    }

    /**
     * Create the target URL from the given parameters. URL-escapes everything. Adds language=en if the parameter map
     * does not contain language parameter.
     * 
     * @param action action to use, "view" is used if null
     * @param space space name to use, "Main" is used if null
     * @param page page name to use, "WebHome" is used if null
     * @param parameters list of parameters with values, parameters are omitted if null, "" is used is a value is null
     * @return the resulting absolute URL
     */
    protected static String createUrl(String action, String space, String page, Map<String, String> parameters)
    {
        return createUrl(action, space, page, parameters, true);
    }

    /**
     * Create the target URL from the given parameters. URL-escapes everything.
     * 
     * @param action action to use, "view" is used if null
     * @param space space name to use, "Main" is used if null
     * @param page page name to use, "WebHome" is used if null
     * @param parameters list of parameters with values, parameters are omitted if null, "" is used is a value is null
     * @param addLanguage add language=en if it is not set in the parameter map
     * @return the resulting absolute URL
     */
    protected static String createUrl(String action, String space, String page, Map<String, String> parameters,
        boolean addLanguage)
    {
        String url = URL_START + escapeUrl(action == null ? "view" : action) + "/";
        url += escapeUrl(space == null ? "Main" : space) + "/";
        url += escapeUrl(page == null ? "WebHome" : page);
        if (parameters == null) {
            return url;
        }
        String delimiter = "?";
        for (String parameter : parameters.keySet()) {
            if (parameter != null && !parameter.equals("")) {
                String value = parameters.get(parameter);
                url += delimiter + escapeUrl(parameter) + "=" + escapeUrl(value);
            }
            delimiter = "&";
        }
        // special handling for language parameter to exclude false positives (language setting is saved in cookies and
        // sent on subsequent requests)
        if (addLanguage && !parameters.containsKey(LANGUAGE)) {
            url += delimiter + LANGUAGE + "=en";
        }
        return url;
    }
}
