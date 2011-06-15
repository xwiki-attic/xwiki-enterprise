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
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.xwiki.test.ui.administration.elements.ProfileUserProfilePage;
import org.xwiki.test.ui.framework.AbstractTest;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.watchlist.elements.WatchlistUserProfilePage;
import org.xwiki.test.ui.watchlist.elements.editor.WatchlistPreferencesEditPage;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;

public class WatchThisPageAndWholeSpaceTest extends AbstractTest
{
    private GreenMail greenMail;

    private WatchlistUserProfilePage watchlistPage;

    private ProfileUserProfilePage profilePage;

    @BeforeClass
    public static void beforeClass()
    {
        // Make sure we are admin
        if (getUtil().getLoggedInUserName() != "Admin") {
            getDriver().get(getUtil().getURLToLoginAsAdmin());
        }

        // Set the SMTP port to the default port used by Greenmail (3025)
        getUtil().updateObject("XWiki", "XWikiPreferences", "XWiki.XWikiPreferences", 0, "smtp_port", 3025);
    }
    
    @Before
    public void setUp() throws InterruptedException
    {
        // Start GreenMail test server
        this.greenMail = new GreenMail();
        this.greenMail.start();

        String userName = RandomStringUtils.randomAlphanumeric(5);

        this.profilePage = new ProfileUserProfilePage(userName);

        getUtil().registerLoginAndGotoPage(profilePage.getUsername(), "password", profilePage.getURL());

        // Set the Admin user's email address to use a localhost domain so that the mail is caught by our
        // GreenMail Mock mail server.
        getUtil().updateObject("XWiki", this.profilePage.getUsername(), "XWiki.XWikiUsers", 0, "email",
            "admin@localhost");

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
    public void testWatchThisPageAndWholeSpace() throws Exception
    {
        // Clear the list of watched documents and spaces
        getUtil().updateObject("XWiki", this.profilePage.getUsername(), "XWiki.WatchListClass", 0, "spaces", "",
            "documents", "");

        // Watch Test.TestWatchThisPage
        ViewPage page = getUtil().createPage("Test", "TestWatchThisPage", "TestWatchThisPage ui", null);
        page.watchDocument();

        // Watch TestWatchWholeSpace
        page = getUtil().createPage("TestWatchWholeSpace", "Test1", "TestWatchWholeSpace ui", null);
        page.watchSpace();

        // Verify that the watched page & space are present in the watchlist manager
        this.watchlistPage.gotoPage();
        // TODO: use LiveTableElement instead but does not seems to work...
        // LiveTableElement watchlist = this.watchlistPage.getWatchList();
        // watchlist.waitUntilReady();
        this.watchlistPage.waitUntilElementIsVisible(By
            .xpath("//tbody[@id='mywatchlist-display']/tr/td/a[@href='/xwiki/bin/view/Test/TestWatchThisPage']"));
        Assert.assertTrue(this.watchlistPage.isWatched("Test", "TestWatchThisPage"));
        Assert.assertTrue(this.watchlistPage.isWatched("TestWatchWholeSpace"));

        // Edit preferences
        WatchlistPreferencesEditPage watchlistPreferences = this.watchlistPage.editPreferences();

        // Ensure the frequency set is every hour so that Hourly job we've modified is used
        watchlistPreferences.setNotifierDaily();
        watchlistPreferences.clickSaveAndContinue();

        // Switch to Admin user
        getDriver().get(getUtil().getURLToLoginAsAdmin());

        // Trigger the notification
        getUtil().gotoPage("Scheduler", "WebHome");
        getDriver().findElement(By.xpath("//a[contains(@href, 'do=trigger&which=Scheduler.WatchListDailyNotifier')]"))
            .click();

        // Wait for the email with a timeout
        Assert.assertTrue("Mail not received", this.greenMail.waitForIncomingEmail(70000, 1));

        String messageFromXWiki = GreenMailUtil.getBody(this.greenMail.getReceivedMessages()[0]);
        Assert.assertFalse(messageFromXWiki.contains("Exception"));
        Assert.assertTrue(messageFromXWiki.contains("TestWatchThisPage"));
        Assert.assertTrue(messageFromXWiki.contains("TestWatchWholeSpace"));
    }
}
