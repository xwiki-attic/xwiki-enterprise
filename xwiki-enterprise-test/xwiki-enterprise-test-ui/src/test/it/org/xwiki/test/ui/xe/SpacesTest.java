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
import org.junit.rules.TestName;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;
import org.xwiki.test.ui.xe.elements.HomePage;

/**
 * Tests the Space Dashboard.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class SpacesTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The object used to access the name of the current test.
     */
    @Rule
    public final TestName testName = new TestName();

    /**
     * Tests if a new space can be created from the Space dashboard.
     */
    @Test
    public void testCreateSpace()
    {
        HomePage homePage = new HomePage();
        homePage.gotoPage();
        String spaceName = this.testName.getMethodName();
        WYSIWYGEditPage editPage = homePage.getSpacesPane().createSpace(spaceName);

        // Verify that space creation uses the space name as the space home page's title
        Assert.assertEquals(spaceName, editPage.getDocumentTitle());

        // Verify that the space created is correct by looking at the generate metadata in the HTML header
        // (they contain the space name amongst other data).
        Assert.assertEquals(spaceName, editPage.getMetaDataValue("space"));
    }

    @Test
    public void testLinkToSpaceIndexWhenSpecialCharacterInSpaceName()
    {
        String spaceName = this.testName.getMethodName() + "&";
        // Make sure the space WebHome page doesn't exist.
        getUtil().deletePage(spaceName, "WebHome");

        // Create Space with a URL-reserved character in the Space name.
        WikiEditPage editPage = new WikiEditPage();
        editPage.switchToEdit(spaceName, "WebHome");
        editPage.setContent("Content");
        editPage.clickSaveAndView();

        // Navigate to the Home Page and click on the SpaceIndex.
        HomePage homePage = new HomePage();
        homePage.gotoPage();
        homePage.getSpacesPane().clickSpaceIndex(spaceName);

        // TODO: Improve the following test by asserting the content of the Livetable in the SpaceIndexPage
        Assert.assertEquals(getUtil().getURL("Main", "SpaceIndex", "view", "space=" + getUtil().escapeURL(spaceName)),
            getDriver().getCurrentUrl());
    }
}
