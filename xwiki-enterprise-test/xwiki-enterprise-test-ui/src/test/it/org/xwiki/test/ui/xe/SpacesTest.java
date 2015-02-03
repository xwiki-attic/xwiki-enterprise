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
package org.xwiki.test.ui.xe;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.index.test.po.SpaceIndexPage;
import org.xwiki.test.po.xe.HomePage;
import org.xwiki.test.ui.AbstractTest;
import org.xwiki.test.ui.AdminAuthenticationRule;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.browser.IgnoreBrowsers;
import org.xwiki.test.ui.po.LiveTableElement;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.test.ui.po.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.po.editor.WikiEditPage;

/**
 * Tests the Space Dashboard.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class SpacesTest extends AbstractTest
{
    @Rule
    public AdminAuthenticationRule adminAuthenticationRule = new AdminAuthenticationRule(getUtil(), getDriver());

    /**
     * Tests if a new space can be created from the Space dashboard.
     */
    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void testCreateSpace()
    {
        HomePage homePage = HomePage.gotoPage();
        WYSIWYGEditPage editPage = homePage.getSpacesPane().createSpace(getTestClassName());

        // Verify that space creation uses the space name as the space home page's title
        Assert.assertEquals(getTestClassName(), editPage.getDocumentTitle());

        // Verify that the space created is correct by looking at the generate metadata in the HTML header
        // (they contain the space name amongst other data).
        Assert.assertEquals(getTestClassName(), editPage.getMetaDataValue("space"));
    }

    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void testLinkToSpaceIndexWhenSpecialCharacterInSpaceName()
    {
        String spaceName = getTestClassName() + "&";
        // Make sure the space WebHome page doesn't exist.
        getUtil().deletePage(spaceName, "WebHome");

        // Create Space with a URL-reserved character in the Space name.
        WikiEditPage editPage = WikiEditPage.gotoPage(spaceName, "WebHome");
        editPage.setContent("Content");
        editPage.clickSaveAndView();

        // Navigate to the Home Page and click on the SpaceIndex.
        HomePage homePage = HomePage.gotoPage();
        homePage.getSpacesPane().clickSpaceIndex(spaceName);

        // Assert the content of the space index live table.
        LiveTableElement spaceIndexLiveTable = new SpaceIndexPage().getLiveTable();
        spaceIndexLiveTable.waitUntilReady();
        Assert.assertEquals(1, spaceIndexLiveTable.getRowCount());
        Assert.assertTrue(spaceIndexLiveTable.hasRow("Page", "WebHome"));
        Assert.assertTrue(spaceIndexLiveTable.hasRow("Space", spaceName));
    }

    /**
     * @see XE-1228: Broken links displayed in the Spaces widget if a space name contains a colon
     * @see XE-1298: Spaces macro doesn't list spaces that contain a colon in their name
     */
    @Test
    public void testColonInSpaceName()
    {
        String spaceName = getTestClassName() + ":" + getTestMethodName();
        getUtil().createPage(spaceName, "WebHome", getTestMethodName() + " content", getTestMethodName() + " Title");
        ViewPage spaceHomePage = HomePage.gotoPage().getSpacesPane().clickSpaceHome(spaceName);
        Assert.assertEquals(getTestMethodName() + " Title", spaceHomePage.getDocumentTitle());
    }
}
