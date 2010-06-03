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
package org.xwiki.it.ui.framework;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.TimeoutException;
/**
 * Helper methods for testing, not related to a specific Page Object.
 * Also made available to tests classes.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class TestUtils
{
    private static PersistentTestContext context;

    /** Used so that AllTests can set the persistent test context. */
    public static void setContext(PersistentTestContext context)
    {
        TestUtils.context = context;
    }

    protected WebDriver getDriver()
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

    public void logout()
    {
        gotoPage("XWiki", "XWikiLogin", "logout");
    }

    public void loginAsAdmin()
    {
        loginAs("Admin", "admin");
    }

    public void loginAs(final String username, final String password)
    {
        loginAndGotoPage(username, password, null);
    }

    public void loginAsAdminAndGotoPage(final String pageURL)
    {
        loginAndGotoPage("Admin", "admin", pageURL);
    }

    /**
     * Establish a predictable state for the tests.
     * After successful completion of this function, you are guarenteed to be logged in as the given user
     * and on the page passed in pageURL.
     *
     * @param username the name of the user to log in as.
     * @param password the password for the user to log in.
     * @param pageURL the URL of the page to go to after logging in. If pageURL is empty (null or "") then
     *                the resulting page location is undefined.
     */
    public void loginAndGotoPage(final String username, final String password, final String pageURL)
    {
        Map<String, String> parameters = new HashMap<String, String>(){{
            put("j_username", username);
            put("j_password", password);
            if (pageURL != null && pageURL.length() > 0) {
                put("xredirect", pageURL);
            }
        }};
        gotoPage("XWiki", "XWikiLogin", "loginsubmit", parameters);

        if (pageURL != null && pageURL.length() > 0) {
            final String pageURI = pageURL.replaceAll("\\?.*", "");
            try {
                Wait<WebDriver> wait = new WebDriverWait(getDriver(), 10);
                wait.until(new ExpectedCondition<Boolean>()
                {
                    public Boolean apply(WebDriver driver)
                    {
                        return getDriver().getCurrentUrl().contains(pageURI)
                               && username.equals(getLoggedInUserName());
                    }
                });
            } catch (TimeoutException e) {
                throw new WebDriverException("Failed to go to the page: " + pageURL + "\nCurrent page is "
                                                 + getDriver().getCurrentUrl() + "\nThe URL opened to log in was: "
                                                     + getURL("XWiki", "XWikiLogin", "loginsubmit", parameters));
            }
        }
    }

    private String getLoggedInUserName()
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
            put("xredirect", getURL("XWiki", "XWikiLogin", "loginsubmit", new HashMap<String, String>(){{
                put("j_username", username);
                put("j_password", password);
                if (pageURL != null && pageURL.length() > 0) {
                    put("xredirect", pageURL);
                }
            }}));
        }});

        getDriver().get(registerURL);

        if (pageURL != null && pageURL.length() > 0) {
            final String pageURI = pageURL.replaceAll("\\?.*", "");
            try {
                Wait<WebDriver> wait = new WebDriverWait(getDriver(), 10);
                wait.until(new ExpectedCondition<Boolean>()
                {
                    public Boolean apply(WebDriver driver)
                    {
                        return getDriver().getCurrentUrl().contains(pageURI)
                               && username.equals(getLoggedInUserName());
                    }
                });
            } catch (TimeoutException e) {
                throw new WebDriverException("Failed to go to the page: " + pageURL + "\nCurrent page is "
                                                 + getDriver().getCurrentUrl() + "\nThe URL opened to register was: "
                                                     + registerURL);
            }
        }
    }

    public void gotoPage(String space, String page)
    {
        gotoPage(space, page, "view");
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

    public void deletePage(String space, String page)
    {
        gotoPage(space, page, "delete", "confirm=1");
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
        return baseURL + action + "/" + space + "/" + page
               + ((queryString == null || queryString.length() < 1) ? "" : "?" + queryString);
    }

    /** 
     * Get the URL of an action on a page with specified parameters.
     * If you need to pass multiple parameters with the same key, this function will not work.
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
     * Encodes a given string so that it may be used as a URL component.
     * Compatable with javascript decodeURIComponent though more strict than encodeURIComponent
     *
     * All characters except [a-zA-Z0-9], '.', '-', '*', '_' are converted to hexadecimal
     * spaces are substituted by '+'
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
     * This class represents all cookies stored in the browser.
     * Use with getSession() and setSession()
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
            return cookies;
        }
    }
}
