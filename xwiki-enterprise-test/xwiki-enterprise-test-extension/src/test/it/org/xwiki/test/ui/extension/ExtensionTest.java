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
package org.xwiki.test.ui.extension;

import junit.framework.Assert;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.xwiki.extension.test.po.AdvancedSearchPane;
import org.xwiki.extension.test.po.ExtensionAdministrationPage;
import org.xwiki.extension.test.po.ExtensionPane;
import org.xwiki.extension.test.po.PaginationFilterPane;
import org.xwiki.extension.test.po.SearchResultsPane;
import org.xwiki.extension.test.po.SimpleSearchPane;
import org.xwiki.test.ui.AbstractExtensionAdminAuthenticatedTest;

/**
 * Functional tests for the Extension Manager user interface.
 * 
 * @version $Id$
 * @since 4.2M1
 */
public class ExtensionTest extends AbstractExtensionAdminAuthenticatedTest
{
    /**
     * The extension search results pagination.
     */
    @Test
    public void testPagination()
    {
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickCoreExtensionsSection();

        SearchResultsPane searchResults = adminPage.getSearchResults();
        Assert.assertNull(searchResults.getNoResultsMessage());
        Assert.assertEquals(20, searchResults.getDisplayedResultsCount());

        PaginationFilterPane pagination = searchResults.getPagination();
        Assert.assertEquals(1 + pagination.getResultsCount() / 20, pagination.getPageCount());
        Assert.assertEquals("1 - 20", pagination.getCurrentRange());
        Assert.assertEquals(1, pagination.getCurrentPageIndex());
        Assert.assertFalse(pagination.hasPreviousPage());
        Assert.assertTrue(pagination.hasNextPage());
        Assert.assertTrue(pagination.getPageCount() > 5);
        Assert.assertTrue(pagination.getResultsCount() > 100);
        String firstExtensionName = searchResults.getExtension(0).getName();

        pagination = pagination.gotoPage(4);
        searchResults = new SearchResultsPane();
        Assert.assertEquals(20, searchResults.getDisplayedResultsCount());
        Assert.assertEquals("61 - 80", pagination.getCurrentRange());
        Assert.assertEquals(4, pagination.getCurrentPageIndex());
        Assert.assertTrue(pagination.hasNextPage());
        String secondExtensionName = searchResults.getExtension(0).getName();
        Assert.assertFalse(firstExtensionName.equals(secondExtensionName));

        pagination = pagination.previousPage();
        searchResults = new SearchResultsPane();
        Assert.assertEquals(20, searchResults.getDisplayedResultsCount());
        Assert.assertEquals("41 - 60", pagination.getCurrentRange());
        Assert.assertEquals(3, pagination.getCurrentPageIndex());
        String thirdExtensionName = searchResults.getExtension(0).getName();
        Assert.assertFalse(firstExtensionName.equals(thirdExtensionName));
        Assert.assertFalse(secondExtensionName.equals(thirdExtensionName));

        pagination = pagination.nextPage();
        searchResults = new SearchResultsPane();
        Assert.assertEquals(20, searchResults.getDisplayedResultsCount());
        Assert.assertEquals("61 - 80", pagination.getCurrentRange());
        Assert.assertEquals(4, pagination.getCurrentPageIndex());
        Assert.assertEquals(secondExtensionName, searchResults.getExtension(0).getName());

        pagination = pagination.gotoPage(pagination.getPageCount());
        searchResults = new SearchResultsPane();
        Assert.assertEquals(pagination.getResultsCount() % 20, searchResults.getDisplayedResultsCount());
        Assert.assertEquals(pagination.getPageCount(), pagination.getCurrentPageIndex());
        Assert.assertFalse(pagination.hasNextPage());
        Assert.assertTrue(pagination.hasPreviousPage());
    }

    /**
     * Tests the simple search form.
     */
    @Test
    public void testSimpleSearch()
    {
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickCoreExtensionsSection();
        int coreExtensionCount = adminPage.getSearchResults().getPagination().getResultsCount();
        SimpleSearchPane searchBar = adminPage.getSearchBar();

        // Check if the tip is displayed.
        Assert.assertEquals("search extension...", searchBar.getSearchInput().getAttribute("value"));

        SearchResultsPane searchResults = searchBar.search("XWiki Rendering");
        Assert.assertTrue(searchResults.getPagination().getResultsCount() < coreExtensionCount);

        // Make sure the search input is not cleared.
        searchBar = new SimpleSearchPane();
        Assert.assertEquals("XWiki Rendering", searchBar.getSearchInput().getAttribute("value"));

        Assert.assertNull(searchResults.getNoResultsMessage());

        // Check that the result matches the search query.
        ExtensionPane extension = searchResults.getExtension(RandomUtils.nextInt(20));
        Assert.assertTrue(extension.getName().toLowerCase().contains("rendering"));
        Assert.assertEquals("core", extension.getStatus());

        // Test search query with no results.
        searchResults = new SimpleSearchPane().search("blahblah");
        Assert.assertEquals(0, searchResults.getDisplayedResultsCount());
        Assert.assertNull(searchResults.getPagination());
        Assert.assertEquals("There were no extensions found matching 'blahblah'. Try different keywords.\n"
            + "Alternatively, if you know the identifier and the version of the extension you're "
            + "looking for, you can use the Advanced Search form above.", searchResults.getNoResultsMessage());

        // Test a search query with only a few results (only one page).
        searchResults = searchBar.search("restlet");
        Assert.assertNull(searchResults.getNoResultsMessage());
        Assert.assertNull(searchResults.getPagination());
        Assert.assertEquals(3, searchResults.getDisplayedResultsCount());

        extension = searchResults.getExtension(0);
        Assert.assertEquals("core", extension.getStatus());
        Assert.assertTrue(extension.getName().toLowerCase().contains("restlet"));
    }

    /**
     * Tests the advanced search form.
     */
    @Test
    public void testAdvancedSearch()
    {
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickCoreExtensionsSection();

        SearchResultsPane searchResults = adminPage.getSearchBar().search("restlet");
        String version = searchResults.getExtension(0).getVersion();

        searchResults = new SimpleSearchPane().clickAdvancedSearch().search("org.restlet.jse:org.restlet", version);
        Assert.assertEquals(1, searchResults.getDisplayedResultsCount());
        Assert.assertNull(searchResults.getNoResultsMessage());
        ExtensionPane extension = searchResults.getExtension(0);
        Assert.assertEquals("core", extension.getStatus());
        Assert.assertTrue(extension.getName().toLowerCase().contains("restlet"));
        Assert.assertEquals(version, extension.getVersion());

        searchResults = new SimpleSearchPane().clickAdvancedSearch().search("foo", "bar");
        Assert.assertEquals(0, searchResults.getDisplayedResultsCount());
        Assert.assertNull(searchResults.getPagination());
        Assert.assertEquals("We couldn't find any extension with id 'foo' and version 'bar'.",
            searchResults.getNoResultsMessage());

        // Test cancel advanced search.
        AdvancedSearchPane advancedSearchPane = new SimpleSearchPane().clickAdvancedSearch();
        advancedSearchPane.getIdInput().sendKeys("id");
        Assert.assertTrue(advancedSearchPane.getVersionInput().isDisplayed());
        advancedSearchPane.getCancelButton().click();
        Assert.assertFalse(advancedSearchPane.getVersionInput().isDisplayed());
    }

    /**
     * Tests how core extensions are displayed.
     */
    @Test
    public void testCoreExtensions()
    {
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickCoreExtensionsSection();

        // Assert that the core extension repository is selected.
        SimpleSearchPane searchBar = adminPage.getSearchBar();
        Assert.assertEquals("Core extensions", searchBar.getRepositorySelect().getFirstSelectedOption().getText());

        ExtensionPane extension = adminPage.getSearchResults().getExtension(RandomUtils.nextInt(20));
        Assert.assertEquals("core", extension.getStatus());
        Assert.assertEquals("Provided", extension.getStatusMessage());
        Assert.assertNull(extension.getInstallButton());
        Assert.assertNull(extension.getUninstallButton());
        Assert.assertNull(extension.getUpgradeButton());
        Assert.assertNull(extension.getDowngradeButton());
        // Just test that the button to show the extension details is present.
        Assert.assertEquals("core", extension.showDetails().getStatus());
    }

    /**
     * Tests the extension repository selector (all, core, installed, local).
     */
    @Test
    public void testRepositorySelector()
    {
        // TODO
    }

    /**
     * Tests the extension details (license, web site).
     */
    @Test
    public void testShowDetails()
    {
        // TODO
    }

    /**
     * Tests how extension dependencies are displayed (both direct and backward dependencies).
     */
    @Test
    public void testDependencies()
    {
        // TODO
    }

    /**
     * Tests how an extension is installed.
     */
    @Test
    public void testInstall()
    {
        // TODO
    }

    /**
     * Tests how an extension is uninstalled.
     */
    @Test
    public void testUninstall()
    {
        // TODO
    }

    /**
     * Tests how an extension is upgraded.
     */
    @Test
    public void testUpgrade()
    {
        // TODO
    }

    /**
     * Tests how an extension is upgraded when there is a merge conflict.
     */
    @Test
    public void testUpgradeWithMergeConflict()
    {
        // TODO
    }

    /**
     * Tests how an extension is downgraded.
     */
    @Test
    public void testDowngrade()
    {
        //
    }
}
