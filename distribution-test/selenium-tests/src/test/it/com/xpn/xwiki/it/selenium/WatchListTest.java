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

    public void testWatchThisPageAndWholeSpace()
    {
        loginAsAdmin();

        // Test if the email template exists
        open("/xwiki/bin/edit/XWiki/WatchListMessage?editor=object");
        assertTextPresent("XWiki.Mail[0]");

        // Watch Test.TestWatchThisPage
        open("/xwiki/bin/edit/Test/TestWatchThisPage?editor=wiki");
        setFieldValue("content", "TestWatchThisPage selenium");
        clickEditSaveAndView();
        getSelenium().click("link=Watch this page");

        // Watch TestWatchWholeSpace
        open("/xwiki/bin/edit/TestWatchWholeSpace/Test1?editor=wiki");
        setFieldValue("content", "TestWatchWholeSpace selenium");
        clickEditSaveAndView();
        getSelenium().click("link=Watch whole space");

        getSelenium().click("link=Manage your watchlist");
        assertTextPresent("TestWatchThisPage");
        assertTextPresent("TestWatchWholeSpace");
    }
}
