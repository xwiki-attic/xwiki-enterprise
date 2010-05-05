package org.xwiki.it.ui.framework;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Helper methods for testing, not related to a specific Page Object.
 *
 * @version $Id$ 
 * @since 2.3M1
 */
public class TestUtils
{
    private static final String URL_PREFIX = "http://localhost:8080/xwiki/bin/";

    private static WebDriver driver;

    public static void initDriver()
    {
        driver = new FirefoxDriver();
    }

    public static WebDriver getDriver()
    {
        return driver;
    }

    public static void closeDriver()
    {
        driver.close();
        driver = null;
    }
    
    public static void gotoPage(String space, String page, WebDriver driver)
    {
        gotoPage(space, page, "view", driver);
    }

    public static void gotoPage(String space, String page, String action, WebDriver driver)
    {
        gotoPage(space, page, action, null, driver);
    }

    public static void gotoPage(String space, String page, String action, String queryString, WebDriver driver)
    {
        String url = getURLForPage(space, page, action, queryString);

        // Verify if we're already on the correct page and if so don't do anything
        if (!driver.getCurrentUrl().equals(url)) {
            driver.get(url);
        }
    }

    public static String getURLForPage(String space, String page, String action)
    {
        return getURLForPage(space, page, action, null);
    }

    public static String getURLForPage(String space, String page, String action, String queryString)
    {
        return URL_PREFIX + action + "/" + space + "/" + page
            + (queryString == null ? "" : "?" + queryString);
    }

    public static boolean isOnPage(String space, String page, String action, WebDriver driver)
    {
        return driver.getCurrentUrl().equals(getURLForPage(space, page, action));
    }

    public static boolean isOnPage(String space, String page, WebDriver driver)
    {
        return isOnPage(space, page, "view", driver);
    }

    public static void deletePage(String space, String page, WebDriver driver)
    {
        TestUtils.gotoPage(space, page, "delete", "confirm=1", driver);
    }

    /**
     * URL-escapes given string.
     * 
     * @param s
     */
    public static String escapeURL(String s)
    {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should not happen
            throw new RuntimeException(e);
        }
    }
}
