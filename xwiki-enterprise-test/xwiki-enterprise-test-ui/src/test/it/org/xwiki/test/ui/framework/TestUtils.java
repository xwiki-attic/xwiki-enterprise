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
package org.xwiki.test.ui.framework;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.TimeoutException;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Helper methods for testing, not related to a specific Page Object. Also made available to tests classes.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class TestUtils
{
    private static final String SCREENSHOT_DIR = System.getProperty("screenshotDirectory");

    private static PersistentTestContext context;

    /**
     * How long to wait before failing a test because an element cannot be found. Can be overridden with setTimeout.
     */
    private int timeout = 10;

    /** Used so that AllTests can set the persistent test context. */
    public static void setContext(PersistentTestContext context)
    {
        TestUtils.context = context;
    }

    protected XWikiWrappingDriver getDriver()
    {
        return context.getDriver();
    }

    private final String baseURL = "http://localhost:8080/xwiki/bin/";

    public Session getSession()
    {
        return this.new Session(getDriver().manage().getCookies());
    }

    public void setSession(Session session)
    {
        WebDriver.Options options = getDriver().manage();
        options.deleteAllCookies();
        if (session != null) {
            for (Cookie cookie : session.getCookies()) {
                options.addCookie(cookie);
            }
        }
    }

    /**
     * Consider using setSession(null) because it will drop the cookies which is faster than invoking a logout action.
     */
    public String getURLToLogout()
    {
        return getURL("XWiki", "XWikiLogin", "logout");
    }

    public String getURLToLoginAsAdmin()
    {
        return getURLToLoginAs("Admin", "admin");
    }

    public String getURLToLoginAs(final String username, final String password)
    {
        return getURLToLoginAndGotoPage(username, password, null);
    }

    /**
     * @param pageURL the URL of the page to go to after logging in.
     * @return URL to accomplish login and goto.
     */
    public String getURLToLoginAsAdminAndGotoPage(final String pageURL)
    {
        return getURLToLoginAndGotoPage("Admin", "admin", pageURL);
    }

    /**
     * @param username the name of the user to log in as.
     * @param password the password for the user to log in.
     * @param pageURL the URL of the page to go to after logging in.
     * @return URL to accomplish login and goto.
     */
    public String getURLToLoginAndGotoPage(final String username, final String password, final String pageURL)
    {
        Map<String, String> parameters = new HashMap<String, String>(){{
            put("j_username", username);
            put("j_password", password);
            if (pageURL != null && pageURL.length() > 0) {
                put("xredirect", pageURL);
            }
        }};
        return getURL("XWiki", "XWikiLogin", "loginsubmit", parameters);
    }

    /**
     * After successful completion of this function, you are guaranteed to be logged in as the given user and on the
     * page passed in pageURL.
     * 
     * @param pageURL
     */
    public void assertOnPage(final String pageURL)
    {
        final String pageURI = pageURL.replaceAll("\\?.*", "");
        try {
            Wait<WebDriver> wait = new WebDriverWait(getDriver().getWrappedDriver(), getTimeout());
            wait.until(new ExpectedCondition<Boolean>()
            {
                public Boolean apply(WebDriver driver)
                {
                    return getDriver().getCurrentUrl().contains(pageURI);
                }
            });
        } catch (TimeoutException e) {
            takeScreenshot();
            Assert.fail("Failed to go to the page: " + pageURL + "\nCurrent page is " + getDriver().getCurrentUrl());
        }
    }

    public String getLoggedInUserName()
    {
        String loggedInUserName = null;
        List<WebElement> elements = getDriver().findElements(By.xpath("//div[@id='tmUser']/span/a"));
        if (!elements.isEmpty()) {
            String href = elements.get(0).getAttribute("href");
            loggedInUserName = href.substring(href.lastIndexOf("/") + 1);
        }
        return loggedInUserName;
    }

    public void registerLoginAndGotoPage(final String username, final String password, final String pageURL)
    {
        String registerURL = getURL("XWiki", "Register", "register", new HashMap<String, String>(){{
            put("register", "1");
            put("xwikiname", username);
            put("register_password", password);
            put("register2_password", password);
            put("register_email", "");
            put("xredirect", getURLToLoginAndGotoPage(username, password, pageURL));
        }});
        getDriver().get(registerURL);
    }

    public ViewPage gotoPage(String space, String page)
    {
        gotoPage(space, page, "view");
        return new ViewPage();
    }

    public void gotoPage(String space, String page, String action)
    {
        gotoPage(space, page, action, "");
    }

    public void gotoPage(String space, String page, String action, Map<String, String> queryParameters)
    {
        getDriver().get(getURL(space, page, action, queryParameters));
    }

    public void gotoPage(String space, String page, String action, String queryString)
    {
        // Only navigate if the current URL is different from the one to go to, in order to improve performances.
        String url = getURL(space, page, action, queryString);
        if (!getDriver().getCurrentUrl().equals(url)) {
            getDriver().get(url);
        }
    }

    public String getURLToDeletePage(String space, String page)
    {
        return getURL(space, page, "delete", "confirm=1");
    }

    public ViewPage createPage(String space, String page, String content, String title)
    {
        return createPage(space, page, content, title, null);
    }

    public ViewPage createPage(String space, String page, String content, String title, String syntaxId)
    {
        Map<String, String> queryMap = new HashMap<String, String>();
        if (content != null) {
            queryMap.put("content", content);
        }
        if (title != null) {
            queryMap.put("title", title);
        }
        if (syntaxId != null) {
            queryMap.put("syntaxId", syntaxId);
        }
        gotoPage(space, page, "save", queryMap);
        return new ViewPage();
    }

    public void deletePage(String space, String page)
    {
        getDriver().get(getURLToDeletePage(space, page));
    }

    /**
     * Get the URL to view a page.
     * 
     * @param space the space in which the page resides.
     * @param page the name of the page.
     */
    public String getURL(String space, String page)
    {
        return getURL(space, page, "view");
    }

    /**
     * Get the URL of an action on a page.
     * 
     * @param space the space in which the page resides.
     * @param page the name of the page.
     * @param action the action to do on the page.
     */
    public String getURL(String space, String page, String action)
    {
        return getURL(space, page, action, "");
    }

    /**
     * Get the URL of an action on a page with a specified query string.
     * 
     * @param space the space in which the page resides.
     * @param page the name of the page.
     * @param action the action to do on the page.
     * @param queryString the query string to pass in the URL.
     */
    public String getURL(String space, String page, String action, String queryString)
    {
        return this.baseURL + action + "/" + escapeURL(space) + "/" + escapeURL(page)
            + ((queryString == null || queryString.length() < 1) ? "" : "?" + queryString);
    }

    /**
     * Get the URL of an action on a page with specified parameters. If you need to pass multiple parameters with the
     * same key, this function will not work.
     * 
     * @param space the space in which the page resides.
     * @param page the name of the page.
     * @param action the action to do on the page.
     * @param queryParameters the parameters to pass in the URL, these will be automatically URL encoded.
     */
    public String getURL(String space, String page, String action, Map<String, String> queryParameters)
    {
        String queryString = "";
        for (String key : queryParameters.keySet()) {
            queryString += escapeURL(key) + "=" + escapeURL(queryParameters.get(key)) + "&";
        }
        return getURL(space, page, action, queryString);
    }

    /**
     * Encodes a given string so that it may be used as a URL component. Compatable with javascript decodeURIComponent,
     * though more strict than encodeURIComponent: all characters except [a-zA-Z0-9], '.', '-', '*', '_' are converted
     * to hexadecimal, and spaces are substituted by '+'.
     * 
     * @param s
     */
    public String escapeURL(String s)
    {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not happen
            throw new RuntimeException(e);
        }
    }

    /**
     * This class represents all cookies stored in the browser. Use with getSession() and setSession()
     */
    public class Session
    {
        private final Set<Cookie> cookies;

        private Session(final Set<Cookie> cookies)
        {
            this.cookies = Collections.unmodifiableSet(new HashSet<Cookie>(){{
                addAll(cookies);
            }});
        }

        private Set<Cookie> getCookies()
        {
            return this.cookies;
        }
    }

    public int getTimeout()
    {
        return this.timeout;
    }

    /**
     * @param timeout the number of seconds after which we consider the action to have failed
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    /**
     * @since 2.6RC1
     */
    public boolean isInWYSIWYGEditMode()
    {
        return getDriver()
            .findElements(By.xpath("//div[@id='tmCurrentEditor']//a/strong[contains(text(), 'WYSIWYG')]")).size() > 0;
    }

    /**
     * @since 2.6RC1
     */
    public boolean isInWikiEditMode()
    {
        return getDriver().findElements(By.xpath("//div[@id='tmCurrentEditor']//a/strong[contains(text(), 'Wiki')]"))
            .size() > 0;
    }

    /**
     * @since 2.6RC1
     */
    public boolean isInViewMode()
    {
        return getDriver().findElements(By.xpath("//div[@id='tmEdit']")).size() > 0;
    }

    /**
     * @since 2.6RC1
     */
    public boolean isInInlineEditMode()
    {
        return getDriver().getCurrentUrl().contains("/inline/");
    }

    /**
     * @since 2.6RC1
     */
    public boolean isInRightsEditMode()
    {
        return getDriver().getCurrentUrl().contains("editor=rights");
    }

    /**
     * @since 2.6RC1
     */
    public boolean isInObjectEditMode()
    {
        return getDriver().getCurrentUrl().contains("editor=object");
    }

    /**
     * @since 2.6RC1
     */
    public boolean isInClassEditMode()
    {
        return getDriver().getCurrentUrl().contains("editor=class");
    }

    /**
     * @since 2.6RC1
     */
    public boolean isInDeleteMode()
    {
        return getDriver().getCurrentUrl().contains("/delete/");
    }

    /**
     * @since 2.6RC1
     */
    public boolean isInRenameMode()
    {
        return getDriver().getCurrentUrl().contains("xpage=rename");
    }

    /**
     * @since 3.1M2 
     */
    public boolean isInCreateMode()
    {
        return getDriver().getCurrentUrl().contains("/create/");
    }

    /**
     * @since 3.1M2
     */
    public boolean isNonExistingPage()
    {
        // there's a view mode, there is no title for the document and there is a message. Unfortunately I think this is
        // also the case when an exception occurs, so we should somehow eliminate that case
        return isInViewMode() && getDriver().findElements(By.id("document-title")).size() == 0
            && getDriver().findElements(By.className("xwikimessage")).size() > 0;
    }

    /**
     * Takes a screenshot and puts the generated image in the temporary directory.
     *
     * @since 3.2M1
     */
    public void takeScreenshot()
    {
        if (!(getDriver().getWrappedDriver() instanceof  TakesScreenshot)) {
            return;
        }

        try {
            File scrFile = ((TakesScreenshot) getDriver().getWrappedDriver()).getScreenshotAs(OutputType.FILE);
            File screenshotFile;
            if (SCREENSHOT_DIR != null) {
                File screenshotDir = new File(SCREENSHOT_DIR);
                screenshotDir.mkdirs();
                screenshotFile = new File(screenshotDir, context.getCurrentTestName() + ".png");
            } else {
                screenshotFile = new File(new File(System.getProperty("java.io.tmpdir")),
                    context.getCurrentTestName() + ".png");
            }
            FileUtils.copyFile(scrFile, screenshotFile);
            try {
                throw new Exception("Screenshot for failing test [" + context.getCurrentTestName() + "] saved at ["
                + screenshotFile.getAbsolutePath() + "]");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Failed to take screenshot for failing test [" + context.getCurrentTestName() + "]");
            e.printStackTrace();
        }
    }
}
