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
package org.xwiki.test.ui.watchlist;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.xwiki.scheduler.test.po.SchedulerHomePage;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.browser.IgnoreBrowsers;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.watchlist.test.po.WatchlistUserProfilePage;
import org.xwiki.watchlist.test.po.editor.WatchlistPreferencesEditPage;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;

public class WatchThisPageAndWholeSpaceTest extends AbstractAdminAuthenticatedTest
{
    private GreenMail greenMail;

    private WatchlistUserProfilePage watchlistPage;

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();

        // Set the SMTP port to the default port used by Greenmail (3025)
        getUtil().updateObject("XWiki", "XWikiPreferences", "XWiki.XWikiPreferences", 0, "smtp_port", 3025);

        // Start GreenMail test server
        this.greenMail = new GreenMail();
        this.greenMail.start();

        // Create a user for the test
        String userName = RandomStringUtils.randomAlphanumeric(5);
        getUtil().createUser(userName, "password");
        WatchlistUserProfilePage profilePage = WatchlistUserProfilePage.gotoPage(userName);

        // Set the Admin user's email address to use a localhost domain so that the mail is caught by our
        // GreenMail Mock mail server.
        getUtil().updateObject("XWiki", profilePage.getUsername(), "XWiki.XWikiUsers", 0, "email", "admin@localhost");

        this.watchlistPage = profilePage.switchToWatchlist();

        // Disable auto watch
        WatchlistPreferencesEditPage watchlistPreferences = this.watchlistPage.editPreferences();
        watchlistPreferences.setAutomaticWatchNone();
        watchlistPreferences.clickSaveAndContinue();
    }

    @After
    public void tearDown()
    {
        // Stop GreenMail test server
        this.greenMail.stop();
    }

    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason = "See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason = "See http://jira.xwiki.org/browse/XE-1177")})
    public void testWatchThisPageAndWholeSpace() throws Exception
    {
        // Clear the list of watched documents and spaces
        getUtil().updateObject("XWiki", this.watchlistPage.getUsername(), "XWiki.WatchListClass", 0, "spaces", "",
            "documents", "");

        // Watch Test.TestWatchThisPage
        ViewPage page = getUtil().createPage("Test", "TestWatchThisPage", "TestWatchThisPage ui", null);
        page.watchDocument();

        // Watch TestWatchWholeSpace.Test1
        page = getUtil().createPage("TestWatchWholeSpace", "Test1", "TestWatchWholeSpace ui", null);
        page.watchSpace();

        // Verify that the watched page & space are present in the watchlist manager
        this.watchlistPage = WatchlistUserProfilePage.gotoPage(this.watchlistPage.getUsername());
        // TODO: use LiveTableElement instead but does not seems to work...
        // LiveTableElement watchlist = this.watchlistPage.getWatchList();
        // watchlist.waitUntilReady();
        this.watchlistPage.waitUntilElementIsVisible(By
            .xpath("//tbody[@id='mywatchlist-display']/tr/td/a[@href='/xwiki/bin/view/Test/TestWatchThisPage']"));
        Assert.assertTrue(this.watchlistPage.isWatched("Test", "TestWatchThisPage"));
        Assert.assertTrue(this.watchlistPage.isWatched("TestWatchWholeSpace"));

        // Ensure that the watchlist notified is set to Daily since we're going to trigger that notifier scheduler job
        WatchlistPreferencesEditPage watchlistPreferences = this.watchlistPage.editPreferences();
        watchlistPreferences.setNotifierDaily();
        watchlistPreferences.clickSaveAndContinue();

        // Switch to Admin user and go to the scheduler home page
        SchedulerHomePage schedulerHomePage = new SchedulerHomePage();
        getDriver().get(getUtil().getURLToLoginAsAdminAndGotoPage(schedulerHomePage.getURL()));
        getUtil().recacheSecretToken();

        // Trigger the notification for the Daily job
        schedulerHomePage.clickJobActionTrigger("WatchList daily notifier");

        // Wait for the email with a timeout
        Assert.assertTrue("Mail not received", this.greenMail.waitForIncomingEmail(70000, 1));

        // Verify email content
        String messageFromXWiki = GreenMailUtil.getBody(this.greenMail.getReceivedMessages()[0]);
        Assert.assertFalse(messageFromXWiki.contains("Exception"));
        Assert.assertTrue(messageFromXWiki.contains("TestWatchThisPage"));
        Assert.assertTrue(messageFromXWiki.contains("TestWatchWholeSpace"));
    }
}
