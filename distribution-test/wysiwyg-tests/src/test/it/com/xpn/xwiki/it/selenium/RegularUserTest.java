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

import com.xpn.xwiki.it.selenium.framework.AbstractWysiwygTestCase;

/**
 * Test for the Wysiwyg editing features when editing as a regular user, not an admin.
 */
public class RegularUserTest extends AbstractWysiwygTestCase
{
    /**
     * {@inheritDoc}. Override to login as a regular user (and create the user if necessary).
     */
    @Override
    protected void login()
    {
        loginAndRegisterUser("Pokemon", "Pokemon", false);
    }

    /**
     * Test that creating a link to a page, logged in as a regular user, does not show pages from the default
     * blacklisted spaces in the results search.
     * 
     * @see http://jira.xwiki.org/jira/browse/XWIKI-4412
     */
    public void testWikiLinkSearchedPageHidesBlacklistedSpaces()
    {
        openDialog("Link", "Wiki Page...");

        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab("Search");
        waitForStepToLoad("xPagesSearch");
        // check the results list: Blog, Main and Sandbox are present
        checkSpaceInSearchResults("Blog", true);
        checkSpaceInSearchResults("Main", true);
        checkSpaceInSearchResults("Sandbox", true);
        // check the results list: ColorThemes, Panels, Scheduler, Stats, XWiki are not present
        checkSpaceInSearchResults("ColorThemes", false);
        checkSpaceInSearchResults("Panels", false);
        checkSpaceInSearchResults("Scheduler", false);
        checkSpaceInSearchResults("Stats", false);
        checkSpaceInSearchResults("XWiki", false);

        closeDialog();
    }

    /**
     * Helper method to test if a space appears in the search results or not.
     * 
     * @param spaceName the name of the space to test whether it is returned among the search results or not
     * @param present {@code true} if the space is expected in the search results, {@code false} otherwise
     */
    private void checkSpaceInSearchResults(String spaceName, boolean expected)
    {
        String spaceWebHome = spaceName + ".WebHome";
        typeInInput("Type a keyword to search for a wiki page", spaceWebHome);
        clickButtonWithText("Search");
        // We have to look for the new page selector inside the search panel because it is also present on the recent
        // pages panel (which is hidden, but still present in DOM, while the search tab is selected).
        String newPageSelector =
            "//div[contains(@class, 'xPagesSearch')]" + "//div[contains(@class, 'xListItem')]"
                + "//div[contains(@class, 'xNewPagePreview')]";
        // Wait for the search results. The list is cleared (including the new page selector) as soon as we click the
        // search button and is refilled when the search results are received. The new page selector is (re)added after
        // the list is filled with the search results.
        waitForElement(newPageSelector);
        // Check if the desired element is there or not, but look precisely inside the search panel.
        String pageInListLocator =
            "//div[contains(@class, 'xPagesSearch')]" + "//div[contains(@class, 'xListItem')]"
                + "//div[contains(@class, 'gwt-Label') and .='" + spaceWebHome + "']";
        if (expected) {
            assertElementPresent(pageInListLocator);
        } else {
            assertElementNotPresent(pageInListLocator);
        }
    }

    /**
     * Test that upon selecting the wiki page to create a link to from all the pages in the wiki, with the tree
     * explorer, the blacklisted spaces are not displayed to the regular user to choose from.
     */
    public void testWikiLinkAllPagesPageHidesBlacklistedSpaces()
    {
        String currentSpace = getClass().getSimpleName();
        String currentPage = getName();

        // Save the current page so that it appears in the tree.
        clickEditSaveAndContinue();

        openDialog("Link", "Wiki Page...");
        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab("All pages");
        waitForStepToLoad("xExplorerPanel");
        waitForElement("//td[contains(@class, \"cell\") and nobr=\"" + currentSpace + "\"]");
        waitForElement("//td[contains(@class, \"cellSelected\") and " + "nobr=\"" + currentPage + "\"]");
        // now tree is loaded check for the spaces in it
        // FIXME: this is not very robust as it will return false positive when the space Blog, for example, doesn't
        // appear but a page named "Blog" appears in the Main space. However there is no way we can address only space
        // cells in the explorer tree
        // check the spaces: Blog, Main and Sandbox are present
        assertElementPresent("//td[contains(@class, \"cell\") and nobr=\"Blog\"]");
        assertElementPresent("//td[contains(@class, \"cell\") and nobr=\"Main\"]");
        assertElementPresent("//td[contains(@class, \"cell\") and nobr=\"Sandbox\"]");
        // check the spaces: ColorThemes, Panels, Scheduler, Stats, XWiki are not present
        assertElementNotPresent("//td[contains(@class, \"cell\") and nobr=\"ColorThemes\"]");
        assertElementNotPresent("//td[contains(@class, \"cell\") and nobr=\"Panels\"]");
        assertElementNotPresent("//td[contains(@class, \"cell\") and nobr=\"Scheduler\"]");
        assertElementNotPresent("//td[contains(@class, \"cell\") and nobr=\"Stats\"]");
        assertElementNotPresent("//td[contains(@class, \"cell\") and nobr=\"XWiki\"]");

        closeDialog();
    }

    /**
     * Test that upon selecting an image from all the images in the wiki, the blacklisted spaces are not listed in the
     * space selector for the regular user to choose from.
     */
    public void testImageSelectorHidesBlacklistedSpaces()
    {
        String currentSpace = getClass().getSimpleName();

        // Save the current page so that it appears in the tree.
        clickEditSaveAndContinue();

        openDialog("Image", "Insert Image...");
        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab("All pages");
        waitForStepToLoad("xImagesExplorer");
        // wait for the current space to load in the selector to be sure the spaces list is loaded
        waitForElement("//div[@class=\"xPageChooser\"]//select[2]/option[@value=\"" + currentSpace + "\"]");
        // check the spaces: Blog, Main, Sandbox are present
        assertElementPresent("//div[@class=\"xPageChooser\"]//select[2]/option[@value=\"Blog\"]");
        assertElementPresent("//div[@class=\"xPageChooser\"]//select[2]/option[@value=\"Main\"]");
        assertElementPresent("//div[@class=\"xPageChooser\"]//select[2]/option[@value=\"Sandbox\"]");
        // check the spaces: ColorThemes, Panels, Scheduler, Stats, XWiki are not present
        assertElementNotPresent("//div[@class=\"xPageChooser\"]//select[2]/option[@value=\"ColorThemes\"]");
        assertElementNotPresent("//div[@class=\"xPageChooser\"]//select[2]/option[@value=\"Panels\"]");
        assertElementNotPresent("//div[@class=\"xPageChooser\"]//select[2]/option[@value=\"Scheduler\"]");
        assertElementNotPresent("//div[@class=\"xPageChooser\"]//select[2]/option[@value=\"Stats\"]");
        assertElementNotPresent("//div[@class=\"xPageChooser\"]//select[2]/option[@value=\"XWiki\"]");

        closeDialog();
    }

    protected void waitForStepToLoad(String name)
    {
        waitForElement("//*[contains(@class, '" + name + "')]");
    }

    private void openDialog(String menuName, String menuItemName)
    {
        clickMenu(menuName);
        assertTrue(isMenuEnabled(menuItemName));
        clickMenu(menuItemName);
        waitForDialogToLoad();
    }

    private void clickTab(String tabName)
    {
        String tabSelector = "//div[.='" + tabName + "']";
        getSelenium().click(tabSelector);
    }
}
