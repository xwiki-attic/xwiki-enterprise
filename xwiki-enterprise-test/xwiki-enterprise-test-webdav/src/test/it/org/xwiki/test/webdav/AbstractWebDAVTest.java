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
package org.xwiki.test.webdav;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.webdav.lib.methods.MkcolMethod;
import org.apache.webdav.lib.methods.MoveMethod;
import org.apache.webdav.lib.methods.PropFindMethod;

/**
 * Abstract test class for all webdav tests.
 * 
 * @version $Id$
 * @since 1.8RC1
 */
public class AbstractWebDAVTest extends TestCase
{
    /**
     * Root webdav view.
     */
    public static final String ROOT = "http://localhost:8080/xwiki/webdav";
    
    /** Build a correct escaped URI from the URI constructor */
    
    /** URI scheme */
    public static final String URI_SCHEME = "http";

    /** URI host */
    public static final String URI_HOST = "localhost";
    
    /** URI port */
    public static final int URI_PORT = 8080;
    
    /** URI webdav root */
    public static final String URI_WEBDAV_ROOT = "/xwiki/webdav";
    
    /** location of the spaces view (uri construction) */
    public static final String PATH_SPACES_VIEW = "/spaces";

    /**
     * location of the home view.
     */
    public static final String HOME = ROOT + "/home";

    /**
     * location of the spaces view.
     */
    public static final String SPACES = ROOT + "/spaces";

    /**
     * location of the attachments view.
     */
    public static final String ATTACHMENTS = ROOT + "/attachments";

    /**
     * location of the orphans view.
     */
    public static final String ORPHANS = ROOT + "/orphans";

    /**
     * location of the whatsnew view.
     */
    public static final String WHATSNEW = ROOT + "/whatsnew";

    /**
     * Array of all baseview locations.
     */
    public static String[] BASE_VIEWS = new String[] {SPACES, ATTACHMENTS, HOME, ORPHANS, WHATSNEW};

    /**
     * The {@link HttpClient} used to invoke various methods on the webdav server.
     */
    private HttpClient client;

    /**
     * Initializes the http client.
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        client = new HttpClient();
        client.getState().setCredentials(
            new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME),
            new UsernamePasswordCredentials("Admin", "admin"));
        client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);      
        client.getParams().setAuthenticationPreemptive(true);
    }

    /**
     * @return the {@link HttpClient}.
     */
    protected HttpClient getHttpClient()
    {
        return client;
    }

    /**
     * Executes the given {@link HttpMethod} and tests for the expected return status.
     * 
     * @param method the {@link HttpMethod}.
     * @param expect expected return status.
     */
    protected void testMethod(HttpMethod method, int expect)
    {
        try {
            int status = getHttpClient().executeMethod(method);
            assertEquals(expect, status);
        } catch (HttpException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Tests the PROPFIND method on the given url.
     * 
     * @param url the target url.
     * @param depth depth parameter for the {@link PropFindMethod}.
     * @param expect the return status expected.
     * @return the {@link HttpMethod} which contains the response.
     */
    protected HttpMethod propFind(String url, int depth, int expect)
    {
        PropFindMethod propFindMethod = new PropFindMethod(url);
        propFindMethod.setDoAuthentication(true);
        propFindMethod.setDepth(depth);
        testMethod(propFindMethod, expect);
        return propFindMethod;
    }

    /**
     * Tests the MKCOL method on the given url.
     * 
     * @param url the target url.
     * @param expect the return status expected.
     * @return the {@link HttpMethod} which contains the response.
     */
    protected HttpMethod mkCol(String url, int expect)
    {
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        mkColMethod.setPath(url);
        testMethod(mkColMethod, expect);
        return mkColMethod;
    }

    /**
     * Tests the PUT method on the given url.
     * 
     * @param url the target url.
     * @param the content for the {@link PutMethod}.
     * @param expect the return status expected.
     * @return the {@link HttpMethod} which contains the response.
     */
    protected HttpMethod put(String url, String content, int expect)
    {
        PutMethod putMethod = new PutMethod();
        putMethod.setDoAuthentication(true);
        putMethod.setPath(url);
        putMethod.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(content.getBytes())));
        testMethod(putMethod, expect);
        return putMethod;
    }

    /**
     * Tests the DELETE method on the given url.
     * 
     * @param url the target url.
     * @param expect the return status expected.
     * @return the {@link HttpMethod} which contains the response.
     */
    protected HttpMethod delete(String url, int expect)
    {
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        deleteMethod.setPath(url);
        testMethod(deleteMethod, expect);
        return deleteMethod;
    }

    /**
     * Tests the MOVE method on the given url.
     * 
     * @param url the target url.
     * @param destination the destination parameter for the {@link MoveMethod}.
     * @param expect the return status expected.
     * @return the {@link HttpMethod} which contains the response.
     */
    protected HttpMethod move(String url, String destination, int expect)
    {
        MoveMethod moveMethod = new MoveMethod();
        moveMethod.setDoAuthentication(true);
        moveMethod.setPath(url);
        moveMethod.setDestination(destination);
        testMethod(moveMethod, expect);
        return moveMethod;
    }
    
    /**
     * Tests the GET method on the given url.
     * 
     * @param url the target url.
     * @param expect the return status expected.
     * @return the {@link HttpMethod} which contains the response.
     */
    protected HttpMethod get(String url, int expect)
    {
        GetMethod getMethod = new GetMethod();
        getMethod.setDoAuthentication(true);
        getMethod.setPath(url);
        testMethod(getMethod, expect);
        return getMethod;
    }
}
