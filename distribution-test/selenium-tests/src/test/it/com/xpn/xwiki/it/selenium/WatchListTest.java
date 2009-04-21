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

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
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
    private GreenMail greenMail;

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the watchlist feature of XWiki");
        suite.addTestSuite(WatchListTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp()
    {
        // Start GreenMail test server
        this.greenMail = new GreenMail();
        this.greenMail.start();
    }

    @Override
    protected void tearDown()
    {
        // Stop GreenMail test server
        this.greenMail.stop();

        // Restore XWiki.WatchListManager from the trash since it's been deleted by the tests below.
        open("XWiki", "WatchListManager");
        if (isElementPresent("link=Restore")) {
            clickLinkWithLocator("link=Restore");
            assertTextPresent("Watchlist for Administrator");
        }
    }

    public void testWatchThisPageAndWholeSpace() throws Exception
    {
        loginAsAdmin();

        // Set the Admin user's email address to use a localhost domain so that the mail is caught by our
        // GreenMail Mock mail server.
        open("/xwiki/bin/edit/XWiki/Admin?editor=object");
        clickLinkWithXPath("//div[@id='xobject_XWiki.XWikiUsers_0_title']", false);
        waitForCondition("selenium.isElementPresent(\"//input[@id='XWiki.XWikiUsers_0_email']\")!=false;");
        setFieldValue("XWiki.XWikiUsers_0_email", "admin@localhost");
        clickEditSaveAndView();

        // Set the SMTP port to the default port used by Greenmail (3025)
        open("XWiki", "XWikiPreferences", "admin");
        clickLinkWithLocator("tmEditObjects", true);
        clickLinkWithXPath("//div[@id='xobject_XWiki.XWikiPreferences_0_title']", false);
        setFieldValue("XWiki.XWikiPreferences_0_smtp_port", "3025");
        clickEditSaveAndView();

        // Clear the list of watched documents and spaces
        open("XWiki", "Admin", "edit", "editor=object");
        setFieldValue("XWiki.WatchListClass_0_spaces", "");
        setFieldValue("XWiki.WatchListClass_0_documents", "");
        clickEditSaveAndView();

        // Test if the email template document exists
        open("XWiki", "WatchListMessage", "edit", "editor=object");
        assertTextPresent("Mail 0:");

        // Test if the watchlist manager document exists
        assertTrue("Page XWiki.WatchListManager doesn't exist", isExistingPage("XWiki", "WatchListManager"));

        // Changing the Scheduler Hourly job so that watchlist checks for changes every minute for the test
        // (default is every hour).
        open("Scheduler", "WebHome");
        clickLinkWithXPath("//a[text()='unschedule']");
        clickLinkWithXPath("//a[@href='/xwiki/bin/inline/Scheduler/WatchListJob1']");
        setFieldValue("XWiki.SchedulerJobClass_0_cron", "0 * * * * ?");
        clickEditSaveAndView();
        clickLinkWithText("Back to the job list", true);
        clickLinkWithXPath("//a[text()='schedule']");

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

        // Ensure the frequency set is every hour so that Hourly job we've modified is used
        getSelenium().select("XWiki.WatchListClass_0_interval", "label=hourly");
        clickLinkWithXPath("//input[@value='Save']", true);

        // Wait for the email with a timeout
        this.greenMail.waitForIncomingEmail(70000, 1);

        String messageFromXWiki = GreenMailUtil.getBody(this.greenMail.getReceivedMessages()[0]);
        assertFalse(messageFromXWiki.contains("Exception"));
        assertTrue(messageFromXWiki.contains("TestWatchThisPage"));
        assertTrue(messageFromXWiki.contains("TestWatchWholeSpace"));

        // Reset the SMTP port
        open("XWiki", "XWikiPreferences", "admin");
        clickLinkWithLocator("tmEditObjects", true);
        clickLinkWithXPath("//div[@id='xobject_XWiki.XWikiPreferences_0_title']", false);
        setFieldValue("XWiki.XWikiPreferences_0_smtp_port", "25");
        clickEditSaveAndView();

        // XWIKI-2125
        // Verify that the Watchlist menu entry is not present if XWiki.WatchListManager does not exists
        // We start by copying
        deletePage("XWiki", "WatchListManager");
        assertTextNotPresent("Manage your watchlist");
    }
}
