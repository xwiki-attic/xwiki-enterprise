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
import org.junit.Test;
import org.xwiki.test.po.xe.HomePage;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.po.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.po.editor.WikiEditPage;

/**
 * Tests the Space Dashboard.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class SpacesTest extends AbstractAdminAuthenticatedTest
{
    /**
     * Tests if a new space can be created from the Space dashboard.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
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
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
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

        // TODO: Improve the following test by asserting the content of the Livetable in the SpaceIndexPage
        Assert.assertEquals(getUtil().getURL("Main", "SpaceIndex", "view", "space=" + getUtil().escapeURL(spaceName)),
            getDriver().getCurrentUrl());
    }
}
