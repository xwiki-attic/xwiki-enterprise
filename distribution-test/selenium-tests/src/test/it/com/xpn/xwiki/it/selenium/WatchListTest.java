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
import com.xpn.xwiki.it.selenium.framework.ColibriSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the watchlist feature of XWiki.
 * 
 * @version $Id$
 */
public class WatchListTest extends AbstractXWikiTestCase
{
    private GreenMail greenMail;

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the watchlist feature of XWiki");
        suite.addTestSuite(WatchListTest.class, ColibriSkinExecutor.class);
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
        open("XWiki", "XWikiPreferences", "edit", "editor=object");
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

        // Watch Test.TestWatchThisPage
        createPage("Test", "TestWatchThisPage", "TestWatchThisPage selenium");
        clickLinkWithText("Watch Page", false);

        // Watch TestWatchWholeSpace
        createPage("TestWatchWholeSpace", "Test1", "TestWatchWholeSpace selenium");
        clickLinkWithText("Watch Space", false);

        // Verify that the watched page & space are present in the watchlist manager
        clickLinkWithLocator("link=Watchlist");
        assertTextPresent("TestWatchThisPage");
        assertTextPresent("TestWatchWholeSpace");

        // Click on the inline edit button
        clickLinkWithLocator("xpath=//a[@id='tmEditInline']");

        // Click the watchlist button because it forgets that we are at the watchlist, no new page loads so we don't wait.
        clickLinkWithLocator("xpath=//li[@id='watchlistTab']/a", false);

        // Ensure the frequency set is every hour so that Hourly job we've modified is used
        getSelenium().select("XWiki.WatchListClass_0_interval", "value=Scheduler.WatchListDailyNotifier");
        clickEditSaveAndView();
        
        // Trigger the notification
        open("Scheduler", "WebHome");
        clickLinkWithXPath("//a[text()='trigger']");

        // Wait for the email with a timeout
        this.greenMail.waitForIncomingEmail(70000, 1);

        String messageFromXWiki = GreenMailUtil.getBody(this.greenMail.getReceivedMessages()[0]);
        assertFalse(messageFromXWiki.contains("Exception"));
        assertTrue(messageFromXWiki.contains("TestWatchThisPage"));
        assertTrue(messageFromXWiki.contains("TestWatchWholeSpace"));

        // Reset the SMTP port
        open("XWiki", "XWikiPreferences", "edit", "editor=object");
        clickLinkWithXPath("//div[@id='xobject_XWiki.XWikiPreferences_0_title']", false);
        setFieldValue("XWiki.XWikiPreferences_0_smtp_port", "25");
        clickEditSaveAndView();
    }
}
