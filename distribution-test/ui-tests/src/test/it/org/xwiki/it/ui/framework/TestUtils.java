package org.xwiki.it.ui.framework;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Helper methods for testing, not related to a specific Page Object.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class TestUtils
{
    private static PersistentTestContext context;

    public static enum XWIKI_ACTIONS
    {
        admin,
        cancel,
        commentadd,
        deleteversions,
        edit,
        inline,
        lock,
        objectadd,
        objectremove,
        preview,
        propadd,
        propupdate,
        rollback,
        save,
        saveandcontinue,
        view
    }

    static void setContext(PersistentTestContext context)
    {
        TestUtils.context = context;
    }

    private static final String URL_PREFIX = "http://localhost:8080/xwiki/bin/";

    private static WebDriver getDriver()
    {
        return context.getDriver();
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
        return URL_PREFIX + action + "/" + space + "/" + page + (queryString == null ? "" : "?" + queryString);
    }

    public static String getCurrentAction()
    {
        return StringUtils.substringBetween(getDriver().getCurrentUrl(), "/xwiki/bin/", "/");
    }

    public static String getQueryParameterValue(String parameter)
    {
        String url = getDriver().getCurrentUrl();
        Pattern pattern = Pattern.compile(".*(\\?|&)" + parameter + "=([^&]*)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.matches()) {
            return matcher.group(2);
        }

        return StringUtils.EMPTY;
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

    public static boolean isNewPage(String space, String page)
    {
        String previousURL = getDriver().getCurrentUrl();
        gotoPage(space, page, getDriver());
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        Boolean result = (Boolean) js.executeScript("return XWiki.docisnew");
        getDriver().navigate().to(previousURL);
        return result;
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
