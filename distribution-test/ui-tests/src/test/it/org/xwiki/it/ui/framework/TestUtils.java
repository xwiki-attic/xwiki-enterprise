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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.openqa.selenium.WebDriver;

/**
 * Helper methods for testing, not related to a specific Page Object.
 * 
 * @deprecated These functions should be moved into objects where they make sense. If a Util class is needed it should
 *             be instanciated.
 * @version $Id$
 * @since 2.3M1
 */
@Deprecated
public class TestUtils
{
    @Deprecated
    public static void gotoPage(String space, String page, WebDriver driver)
    {
        gotoPage(space, page, "view", driver);
    }

    @Deprecated
    public static void gotoPage(String space, String page, String action, WebDriver driver)
    {
        gotoPage(space, page, action, null, driver);
    }

    @Deprecated
    public static void gotoPage(String space, String page, String action, String queryString, WebDriver driver)
    {
        String url =
            "http://localhost:8080/xwiki/bin/" + action + "/" + space + "/" + page
                + (queryString == null ? "" : "?" + queryString);

        // Verify if we're already on the correct page and if so don't do anything
        if (!driver.getCurrentUrl().equals(url)) {
            driver.get(url);
        }
    }

    @Deprecated
    public static void deletePage(String space, String page, WebDriver driver)
    {
        TestUtils.gotoPage(space, page, "delete", "confirm=1", driver);
    }

    /**
     * URL-escapes given string.
     * 
     * @param s
     */
    @Deprecated
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
