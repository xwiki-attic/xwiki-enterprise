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
package com.xpn.xwiki.it.selenium;

import junit.framework.Test;
import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the watchlist feature of XWiki.
 *
 * @version $Id: $
 */
public class WatchListTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the watchlist feature of XWiki");
        suite.addTestSuite(WatchListTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    protected void tearDown()
    {
        // Restore XWiki.WatchListManager from the trash since it's been deleted by the tests below.
        open("XWiki", "WatchListManager");
        if (isElementPresent("link=Restore")) {
            clickLinkWithLocator("link=Restore");
            assertTextPresent("Watchlist for Administrator");
        }
    }

    public void testWatchThisPageAndWholeSpace()
    {
        loginAsAdmin();

        // Clear the list of watched documents and spaces
        open("XWiki", "Admin", "edit", "editor=object");
        setFieldValue("XWiki.WatchListClass_0_spaces", "");
        setFieldValue("XWiki.WatchListClass_0_documents", "");
        clickEditSaveAndView();

        // Test if the email template document exists
        open("XWiki", "WatchListMessage", "edit", "editor=object");
        assertTextPresent("XWiki.Mail[0]");

        // Test if the watchlist manager document exists
        assertTrue("Page XWiki.WatchListManager doesn't exist", isExistingPage("XWiki", "WatchListManager"));

        // Watch Test.TestWatchThisPage
        createPage("Test", "TestWatchThisPage", "TestWatchThisPage selenium");
        clickLinkWithText("Watch this page", false);

        // Watch TestWatchWholeSpace
        createPage("TestWatchWholeSpace", "Test1", "TestWatchWholeSpace selenium");
        clickLinkWithText("Watch space : TestWatchWholeSpace", false);

        // Verify that the watched page & space are present in the watchlist manager
        clickLinkWithLocator("link=Manage your watchlist");
        assertTextPresent("TestWatchThisPage");
        assertTextPresent("TestWatchWholeSpace");

        // XWIKI-2125
        // Verify that the Watchlist menu entry is not present if XWiki.WatchListManager does not exists
        // We start by copying
        deletePage("XWiki", "WatchListManager");
        assertTextNotPresent("Manage your watchlist");
    }
}
