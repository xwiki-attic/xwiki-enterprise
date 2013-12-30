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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.xwiki.extension.DefaultExtensionAuthor;
import org.xwiki.extension.DefaultExtensionDependency;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.ExtensionLicense;
import org.xwiki.extension.test.po.AdvancedSearchPane;
import org.xwiki.extension.test.po.DependencyPane;
import org.xwiki.extension.test.po.ExtensionAdministrationPage;
import org.xwiki.extension.test.po.ExtensionDependenciesPane;
import org.xwiki.extension.test.po.ExtensionDescriptionPane;
import org.xwiki.extension.test.po.ExtensionPane;
import org.xwiki.extension.test.po.ExtensionProgressPane;
import org.xwiki.extension.test.po.LogItemPane;
import org.xwiki.extension.test.po.MergeConflictPane;
import org.xwiki.extension.test.po.PaginationFilterPane;
import org.xwiki.extension.test.po.ProgressBarPane;
import org.xwiki.extension.test.po.SearchResultsPane;
import org.xwiki.extension.test.po.SimpleSearchPane;
import org.xwiki.extension.version.internal.DefaultVersionConstraint;
import org.xwiki.test.ui.AbstractExtensionAdminAuthenticatedTest;
import org.xwiki.test.ui.TestExtension;
import org.xwiki.test.ui.po.ChangesPane;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Functional tests for the Extension Manager user interface.
 * 
 * @version $Id$
 * @since 4.2M1
 */
public class ExtensionTest extends AbstractExtensionAdminAuthenticatedTest
{
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        // The extension page name is either the extension name, if specified, or the extension id. Most of the tests
        // don't set the extension name but some do and we end up with two extensions (two pages) with the same id.
        getUtil().deletePage("Extension", "Alice Wiki Macro");
        getUtil().deletePage("Extension", "Bob Wiki Macro");
        getUtil().deletePage("Extension", "alice-xar-extension");
        getUtil().deletePage("Extension", "bob-xar-extension");

        // Make sure the extensions we are playing with are not already installed.
        getExtensionTestUtils().finishCurrentJob();
        getExtensionTestUtils().uninstall("alice-xar-extension");
        getExtensionTestUtils().uninstall("bob-xar-extension");
        getExtensionTestUtils().uninstall("scriptServiceJarExtension");
    }

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
        Assert.assertEquals("search extension...", searchBar.getSearchInput().getAttribute("placeholder"));
        // Check that the input is empty
        Assert.assertEquals("", searchBar.getSearchInput().getAttribute("value"));

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
        Assert.assertEquals("There were no extensions found matching 'blahblah'. Try different keywords. "
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
        Assert.assertEquals("We couldn't find any extension with id 'foo' and version 'bar'. "
            + "Make sure you have the right extension repositories configured.", searchResults.getNoResultsMessage());

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
    public void testRepositorySelector() throws Exception
    {
        // Setup the extension.
        ExtensionId extensionId = new ExtensionId("alice-xar-extension", "1.3");
        TestExtension extension = getRepositoryTestUtils().getTestExtension(extensionId, "xar");
        getRepositoryTestUtils().addExtension(extension);

        // Check if the section links point to the right repository.
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickAddExtensionsSection();
        Select repositorySelect = adminPage.getSearchBar().getRepositorySelect();
        Assert.assertEquals("All Extensions", repositorySelect.getFirstSelectedOption().getText());

        adminPage = adminPage.clickCoreExtensionsSection();
        repositorySelect = adminPage.getSearchBar().getRepositorySelect();
        Assert.assertEquals("Core extensions", repositorySelect.getFirstSelectedOption().getText());

        adminPage = adminPage.clickInstalledExtensionsSection();
        repositorySelect = adminPage.getSearchBar().getRepositorySelect();
        Assert.assertEquals("Installed extensions", repositorySelect.getFirstSelectedOption().getText());

        // Check that a remote extension appears only in the list of "All Extensions".
        SearchResultsPane searchResults = adminPage.getSearchBar().search("alice");
        Assert.assertNull(searchResults.getExtension(extensionId));

        new SimpleSearchPane().getRepositorySelect().selectByVisibleText("All Extensions");
        adminPage = new ExtensionAdministrationPage();
        adminPage.waitUntilPageIsLoaded();
        // The value of the search input must be preserved when we switch the repository.
        Assert.assertEquals("alice", adminPage.getSearchBar().getSearchInput().getAttribute("value"));
        Assert.assertNotNull(adminPage.getSearchResults().getExtension(extensionId));
        Assert.assertNull(new SimpleSearchPane().selectRepository("local").getExtension(extensionId));

        // Check that an installed extension appears also in "Installed Extensions" and "Local Extensions".
        getExtensionTestUtils().install(extensionId);
        adminPage = ExtensionAdministrationPage.gotoPage().clickInstalledExtensionsSection();
        searchResults = adminPage.getSearchBar().search("alice");
        Assert.assertNotNull(searchResults.getExtension(extensionId));
        Assert.assertNotNull(new SimpleSearchPane().selectRepository("local").getExtension(extensionId));
        Assert.assertNotNull(new SimpleSearchPane().selectRepository("").getExtension(extensionId));

        // Check local extension.
        getExtensionTestUtils().uninstall(extensionId.getId(), true);
        adminPage = ExtensionAdministrationPage.gotoPage().clickInstalledExtensionsSection();
        searchResults = adminPage.getSearchBar().search("alice");
        Assert.assertNull(searchResults.getExtension(extensionId));
        Assert.assertNotNull(new SimpleSearchPane().selectRepository("local").getExtension(extensionId));
        Assert.assertNotNull(new SimpleSearchPane().selectRepository("").getExtension(extensionId));
    }

    /**
     * Tests the extension details (license, web site).
     */
    @Test
    public void testShowDetails() throws Exception
    {
        // Setup the extension.
        ExtensionId extensionId = new ExtensionId("alice-xar-extension", "1.3");
        TestExtension extension = getRepositoryTestUtils().getTestExtension(extensionId, "xar");
        extension.setName("Alice Wiki Macro");
        extension.setSummary("A **useless** macro");
        extension.addAuthor(new DefaultExtensionAuthor("Thomas", null));
        extension.addAuthor(new DefaultExtensionAuthor("Marius", null));
        extension.addLicense(new ExtensionLicense("My own license", null));
        extension.setWebsite("http://www.alice.com");
        getRepositoryTestUtils().addExtension(extension);

        // Search the extension and assert the displayed information.
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickAddExtensionsSection();
        ExtensionPane extensionPane =
            adminPage.getSearchBar().clickAdvancedSearch().search(extensionId).getExtension(0);
        Assert.assertEquals("remote", extensionPane.getStatus());
        Assert.assertNull(extensionPane.getStatusMessage());
        Assert.assertEquals(extension.getName(), extensionPane.getName());
        Assert.assertEquals(extensionId.getVersion().getValue(), extensionPane.getVersion());
        List<WebElement> authors = extensionPane.getAuthors();
        Assert.assertEquals(2, authors.size());
        Assert.assertEquals("Thomas", authors.get(0).getText());
        Assert.assertEquals("Marius", authors.get(1).getText());
        Assert.assertEquals(extension.getSummary(), extensionPane.getSummary());

        // Check the extension details.
        ExtensionDescriptionPane descriptionPane = extensionPane.openDescriptionSection();
        Assert.assertEquals(extension.getLicenses().iterator().next().getName(), descriptionPane.getLicense());
        Assert.assertEquals(extension.getId().getId(), descriptionPane.getId());
        Assert.assertEquals(extension.getType(), descriptionPane.getType());
        WebElement webSiteLink = descriptionPane.getWebSite();
        Assert.assertEquals(extension.getWebSite().substring("http://".length()), webSiteLink.getText());
        Assert.assertEquals(extension.getWebSite() + '/', webSiteLink.getAttribute("href"));
    }

    /**
     * Tests how extension dependencies are displayed (both direct and backward dependencies).
     */
    @Test
    public void testDependencies() throws Exception
    {
        // Setup the extension and its dependencies.
        ExtensionId dependencyId = new ExtensionId("bob-xar-extension", "2.5-milestone-2");
        TestExtension dependency = getRepositoryTestUtils().getTestExtension(dependencyId, "xar");
        dependency.setName("Bob Wiki Macro");
        dependency.setSummary("Required by Alice");
        getRepositoryTestUtils().addExtension(dependency);

        ExtensionId extensionId = new ExtensionId("alice-xar-extension", "1.3");
        TestExtension extension = getRepositoryTestUtils().getTestExtension(extensionId, "xar");
        extension.addDependency(new DefaultExtensionDependency(dependencyId.getId(), new DefaultVersionConstraint(
            dependencyId.getVersion().getValue())));
        extension.addDependency(new DefaultExtensionDependency("missing-dependency",
            new DefaultVersionConstraint("135")));
        extension.addDependency(new DefaultExtensionDependency("org.xwiki.platform:xwiki-platform-sheet-api",
            new DefaultVersionConstraint("[3.2,)")));
        extension.addDependency(new DefaultExtensionDependency("org.xwiki.commons:xwiki-commons-diff-api",
            new DefaultVersionConstraint("2.7")));
        extension.addDependency(new DefaultExtensionDependency("org.xwiki.platform:xwiki-platform-display-api",
            new DefaultVersionConstraint("100.1")));
        getRepositoryTestUtils().addExtension(extension);

        // Search the extension and assert the list of dependencies.
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickAddExtensionsSection();
        ExtensionPane extensionPane =
            adminPage.getSearchBar().clickAdvancedSearch().search(extensionId).getExtension(0);
        ExtensionDependenciesPane dependenciesPane = extensionPane.openDependenciesSection();

        List<DependencyPane> directDependencies = dependenciesPane.getDirectDependencies();
        Assert.assertEquals(5, directDependencies.size());

        Assert.assertEquals(dependency.getName(), directDependencies.get(0).getName());
        Assert.assertEquals(dependencyId.getVersion().getValue(), directDependencies.get(0).getVersion());
        Assert.assertEquals("remote", directDependencies.get(0).getStatus());
        Assert.assertNull(directDependencies.get(0).getStatusMessage());

        Assert.assertNull(directDependencies.get(1).getLink());
        Assert.assertEquals("missing-dependency", directDependencies.get(1).getName());
        Assert.assertEquals("135", directDependencies.get(1).getVersion());
        Assert.assertEquals("unknown", directDependencies.get(1).getStatus());
        Assert.assertNull(directDependencies.get(1).getStatusMessage());

        Assert.assertNotNull(directDependencies.get(2).getLink());
        Assert.assertEquals("XWiki Platform - Sheet - API", directDependencies.get(2).getName());
        Assert.assertEquals("[3.2,)", directDependencies.get(2).getVersion());
        Assert.assertEquals("core", directDependencies.get(2).getStatus());
        Assert.assertEquals("Provided", directDependencies.get(2).getStatusMessage());

        Assert.assertNotNull(directDependencies.get(3).getLink());
        Assert.assertEquals("XWiki Commons - Diff API", directDependencies.get(3).getName());
        Assert.assertEquals("2.7", directDependencies.get(3).getVersion());
        Assert.assertEquals("remote-core", directDependencies.get(3).getStatus());
        Assert.assertTrue(directDependencies.get(3).getStatusMessage().matches("Version [^\\s]+ is provided"));

        Assert.assertEquals("XWiki Platform - Display API", directDependencies.get(4).getName());
        Assert.assertEquals("100.1", directDependencies.get(4).getVersion());
        Assert.assertEquals("remote-core-incompatible", directDependencies.get(4).getStatus());
        Assert.assertTrue(directDependencies.get(4).getStatusMessage()
            .matches("Incompatible with provided version [^\\s]+"));

        Assert.assertTrue(dependenciesPane.getBackwardDependencies().isEmpty());

        // Follow the link to a dependency.
        directDependencies.get(0).getLink().click();
        adminPage = new ExtensionAdministrationPage();
        extensionPane = adminPage.getSearchResults().getExtension(0);
        Assert.assertEquals(dependency.getName(), extensionPane.getName());
        Assert.assertEquals(dependencyId.getVersion().getValue(), extensionPane.getVersion());
        Assert.assertEquals(dependency.getSummary(), extensionPane.getSummary());

        // Check that we are still in the administration.
        adminPage.clickInstalledExtensionsSection();
    }

    /**
     * Tests how an extension is installed.
     */
    @Test
    public void testInstall() throws Exception
    {
        // Setup the extension and its dependencies.
        ExtensionId extensionId = new ExtensionId("alice-xar-extension", "1.3");
        TestExtension extension = getRepositoryTestUtils().getTestExtension(extensionId, "xar");

        ExtensionId dependencyId = new ExtensionId("bob-xar-extension", "2.5-milestone-2");
        getRepositoryTestUtils().addExtension(getRepositoryTestUtils().getTestExtension(dependencyId, "xar"));
        extension.addDependency(new DefaultExtensionDependency(dependencyId.getId(), new DefaultVersionConstraint(
            dependencyId.getVersion().getValue())));

        extension.addDependency(new DefaultExtensionDependency("org.xwiki.platform:xwiki-platform-sheet-api",
            new DefaultVersionConstraint("[3.2,)")));
        getRepositoryTestUtils().addExtension(extension);

        // Search the extension and install it.
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickAddExtensionsSection();
        ExtensionPane extensionPane =
            adminPage.getSearchBar().clickAdvancedSearch().search(extensionId).getExtension(0);
        extensionPane = extensionPane.install();

        // Assert the install plan.
        List<DependencyPane> installPlan = extensionPane.openProgressSection().getJobPlan();
        Assert.assertEquals(2, installPlan.size());
        Assert.assertEquals(dependencyId, installPlan.get(0).getId());
        Assert.assertEquals(extensionId, installPlan.get(1).getId());

        // Finish the install and assert the install log.
        List<LogItemPane> log = extensionPane.confirm().openProgressSection().getJobLog();
        int logSize = log.size();
        Assert.assertTrue(logSize > 1);
        Assert.assertEquals("info", log.get(0).getLevel());
        Assert.assertEquals("Starting job of type [install] with identifier "
            + "[extension/action/alice-xar-extension/wiki:xwiki]", log.get(0).getMessage());
        Assert.assertEquals("info", log.get(logSize - 1).getLevel());
        Assert.assertEquals("Finished job of type [install] with identifier "
            + "[extension/action/alice-xar-extension/wiki:xwiki]", log.get(logSize - 1).getMessage());

        // Test that both extensions are usable.
        ViewPage viewPage = getUtil().createPage(getTestClassName(), getTestMethodName(), "{{alice/}}\n\n{{bob/}}", "");
        String content = viewPage.getContent();
        Assert.assertTrue(content.contains("Alice says hello!"));
        Assert.assertTrue(content.contains("Bob says hi!"));

        // Check the list of installed extensions.
        adminPage = ExtensionAdministrationPage.gotoPage().clickInstalledExtensionsSection();

        SearchResultsPane searchResults = adminPage.getSearchBar().search("bob");
        Assert.assertEquals(1, searchResults.getDisplayedResultsCount());
        extensionPane = searchResults.getExtension(0);
        Assert.assertEquals("installed", extensionPane.getStatus());
        Assert.assertEquals("Installed", extensionPane.getStatusMessage());
        Assert.assertEquals(dependencyId, extensionPane.getId());
        Assert.assertNotNull(extensionPane.getUninstallButton());

        searchResults = new SimpleSearchPane().search("alice");
        Assert.assertEquals(1, searchResults.getDisplayedResultsCount());
        extensionPane = searchResults.getExtension(0);
        Assert.assertEquals("installed", extensionPane.getStatus());
        Assert.assertEquals("Installed", extensionPane.getStatusMessage());
        Assert.assertEquals(extensionId, extensionPane.getId());
        Assert.assertNotNull(extensionPane.getUninstallButton());

        // Check if the progress log is persisted.
        extensionPane = extensionPane.showDetails();
        log = extensionPane.openProgressSection().getJobLog();
        Assert.assertEquals(logSize, log.size());
        Assert.assertEquals("info", log.get(0).getLevel());
        Assert.assertEquals("Starting job of type [install] with identifier "
            + "[extension/action/alice-xar-extension/wiki:xwiki]", log.get(0).getMessage());
        Assert.assertEquals("info", log.get(logSize - 1).getLevel());
        Assert.assertEquals("Finished job of type [install] with identifier "
            + "[extension/action/alice-xar-extension/wiki:xwiki]", log.get(logSize - 1).getMessage());

        // Check if the dependency is properly listed as installed.
        List<DependencyPane> dependencies = extensionPane.openDependenciesSection().getDirectDependencies();
        Assert.assertEquals(2, dependencies.size());
        Assert.assertEquals(dependencyId, dependencies.get(0).getId());
        Assert.assertEquals("installed", dependencies.get(0).getStatus());
        Assert.assertEquals("Installed", dependencies.get(0).getStatusMessage());

        // Check the backward dependency.
        dependencies.get(0).getLink().click();
        extensionPane = new ExtensionAdministrationPage().getSearchResults().getExtension(0);
        dependencies = extensionPane.openDependenciesSection().getBackwardDependencies();
        Assert.assertEquals(1, dependencies.size());
        Assert.assertEquals(extensionId, dependencies.get(0).getId());
        Assert.assertEquals("installed", dependencies.get(0).getStatus());
        Assert.assertEquals("Installed", dependencies.get(0).getStatusMessage());
    }

    /**
     * Tests how an extension is uninstalled.
     */
    @Test
    public void testUninstall() throws Exception
    {
        // Setup the extension and its dependencies.
        ExtensionId dependencyId = new ExtensionId("bob-xar-extension", "2.5-milestone-2");
        getRepositoryTestUtils().addExtension(getRepositoryTestUtils().getTestExtension(dependencyId, "xar"));

        ExtensionId extensionId = new ExtensionId("alice-xar-extension", "1.3");
        TestExtension extension = getRepositoryTestUtils().getTestExtension(extensionId, "xar");
        extension.addDependency(new DefaultExtensionDependency(dependencyId.getId(), new DefaultVersionConstraint(
            dependencyId.getVersion().getValue())));
        getRepositoryTestUtils().addExtension(extension);

        // Install the extensions.
        getExtensionTestUtils().install(extensionId);

        // Check if the installed pages are present.
        Assert.assertTrue(getUtil().pageExists("ExtensionTest", "Alice"));
        Assert.assertTrue(getUtil().pageExists("ExtensionTest", "Bob"));

        // Uninstall the dependency.
        ExtensionAdministrationPage adminPage =
            ExtensionAdministrationPage.gotoPage().clickInstalledExtensionsSection();
        ExtensionPane extensionPane =
            adminPage.getSearchBar().clickAdvancedSearch().search(dependencyId).getExtension(0);
        extensionPane = extensionPane.uninstall();

        // Check the uninstall plan. Both extensions should be included.
        List<DependencyPane> uninstallPlan = extensionPane.openProgressSection().getJobPlan();
        Assert.assertEquals(2, uninstallPlan.size());

        Assert.assertEquals(extensionId, uninstallPlan.get(0).getId());
        Assert.assertEquals("installed", uninstallPlan.get(0).getStatus());
        Assert.assertEquals("Installed", uninstallPlan.get(0).getStatusMessage());

        Assert.assertEquals(dependencyId, uninstallPlan.get(1).getId());
        Assert.assertEquals("installed", uninstallPlan.get(1).getStatus());
        Assert.assertEquals("Installed", uninstallPlan.get(1).getStatusMessage());

        // Finish the uninstall and check the log.
        extensionPane = extensionPane.confirm();
        List<LogItemPane> log = extensionPane.openProgressSection().getJobLog();
        Assert.assertTrue(log.size() > 2);
        Assert.assertEquals("info", log.get(2).getLevel());
        Assert.assertEquals("Resolving extension [bob-xar-extension 2.5-milestone-2] from namespace [Home]", log
            .get(2).getMessage());
        Assert.assertEquals("info", log.get(log.size() - 1).getLevel());
        Assert.assertEquals("Finished job of type [uninstall] with identifier "
            + "[extension/action/bob-xar-extension/wiki:xwiki]", log.get(log.size() - 1).getMessage());

        // Check if the uninstalled pages have been deleted.
        Assert.assertFalse(getUtil().pageExists("ExtensionTest", "Alice"));
        Assert.assertFalse(getUtil().pageExists("ExtensionTest", "Bob"));

        // Install both extension again and uninstall only the one with the dependency.
        getExtensionTestUtils().install(extensionId);

        adminPage = ExtensionAdministrationPage.gotoPage().clickInstalledExtensionsSection();
        extensionPane = adminPage.getSearchBar().clickAdvancedSearch().search(extensionId).getExtension(0);
        extensionPane = extensionPane.uninstall();

        // Check the uninstall plan. Only one extension should be included.
        uninstallPlan = extensionPane.openProgressSection().getJobPlan();
        Assert.assertEquals(1, uninstallPlan.size());
        Assert.assertEquals(extensionId, uninstallPlan.get(0).getId());

        // Finish the uninstall and check the log.
        log = extensionPane.confirm().openProgressSection().getJobLog();
        Assert.assertTrue(log.size() > 2);
        Assert.assertEquals("info", log.get(2).getLevel());
        Assert.assertEquals("Resolving extension [alice-xar-extension 1.3] from namespace [Home]", log.get(2)
            .getMessage());
        Assert.assertEquals("info", log.get(log.size() - 1).getLevel());
        Assert.assertEquals("Finished job of type [uninstall] with identifier "
            + "[extension/action/alice-xar-extension/wiki:xwiki]", log.get(log.size() - 1).getMessage());

        // Check if the uninstalled pages have been deleted.
        Assert.assertFalse(getUtil().pageExists("ExtensionTest", "Alice"));
        Assert.assertTrue(getUtil().pageExists("ExtensionTest", "Bob"));

        // Check the list of installed extensions. It should contain only the second extension.
        adminPage = ExtensionAdministrationPage.gotoPage().clickInstalledExtensionsSection();
        SearchResultsPane searchResults = adminPage.getSearchBar().search("alice");
        Assert.assertEquals(0, searchResults.getDisplayedResultsCount());
        Assert.assertNotNull(searchResults.getNoResultsMessage());

        searchResults = new SimpleSearchPane().search("bob");
        Assert.assertEquals(1, searchResults.getDisplayedResultsCount());
        extensionPane = searchResults.getExtension(0);
        Assert.assertEquals("installed", extensionPane.getStatus());
        Assert.assertEquals(dependencyId, extensionPane.getId());
    }

    /**
     * Tests that an extension can be installed and uninstalled without reloading the extension manager UI.
     */
    @Test
    public void testInstallAndUninstallWithoutReload() throws Exception
    {
        // Setup the extension.
        ExtensionId extensionId = new ExtensionId("alice-xar-extension", "1.3");
        getRepositoryTestUtils().addExtension(getRepositoryTestUtils().getTestExtension(extensionId, "xar"));

        // Search the extension to install.
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickAddExtensionsSection();
        ExtensionPane extensionPane =
            adminPage.getSearchBar().clickAdvancedSearch().search(extensionId).getExtension(0);

        // Install and uninstall.
        extensionPane = extensionPane.install().confirm().uninstall().confirm().install();
        Assert.assertEquals("remote", extensionPane.getStatus());
    }

    /**
     * Tests how an extension is upgraded.
     */
    @Test
    public void testUpgrade() throws Exception
    {
        // Setup the extension.
        String extensionId = "alice-xar-extension";
        String oldVersion = "1.3";
        String newVersion = "2.1.4";
        TestExtension oldExtension =
            getRepositoryTestUtils().getTestExtension(new ExtensionId(extensionId, oldVersion), "xar");
        getRepositoryTestUtils().addExtension(oldExtension);
        TestExtension newExtension =
            getRepositoryTestUtils().getTestExtension(new ExtensionId(extensionId, newVersion), "xar");
        getRepositoryTestUtils().attachFile(newExtension);
        getRepositoryTestUtils().addVersionObject(newExtension, newVersion,
            "attach:" + newExtension.getFile().getName());

        // Make sure the old version is installed.
        getExtensionTestUtils().install(new ExtensionId(extensionId, oldVersion));

        // Upgrade the extension.
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickAddExtensionsSection();
        ExtensionPane extensionPane =
            adminPage.getSearchBar().clickAdvancedSearch().search(extensionId, newVersion).getExtension(0);
        Assert.assertEquals("remote-installed", extensionPane.getStatus());
        Assert.assertEquals("Version 1.3 is installed", extensionPane.getStatusMessage());
        extensionPane = extensionPane.upgrade();

        // Check the upgrade plan.
        List<DependencyPane> upgradePlan = extensionPane.openProgressSection().getJobPlan();
        Assert.assertEquals(1, upgradePlan.size());
        Assert.assertEquals(extensionId, upgradePlan.get(0).getName());
        Assert.assertEquals(newVersion, upgradePlan.get(0).getVersion());
        Assert.assertEquals("remote-installed", upgradePlan.get(0).getStatus());
        Assert.assertEquals("Version 1.3 is installed", upgradePlan.get(0).getStatusMessage());

        // Finish the upgrade and check the upgrade log.
        extensionPane = extensionPane.confirm();
        Assert.assertEquals("installed", extensionPane.getStatus());
        Assert.assertEquals("Installed", extensionPane.getStatusMessage());
        List<LogItemPane> log = extensionPane.openProgressSection().getJobLog();
        Assert.assertTrue(log.size() > 2);
        Assert.assertEquals("info", log.get(2).getLevel());
        Assert.assertEquals("Resolving extension [alice-xar-extension 2.1.4] on namespace [Home]", log.get(2)
            .getMessage());
        Assert.assertEquals("info", log.get(log.size() - 1).getLevel());
        Assert.assertEquals("Finished job of type [install] with identifier "
            + "[extension/action/alice-xar-extension/wiki:xwiki]", log.get(log.size() - 1).getMessage());

        // Assert the changes.
        ViewPage viewPage = getUtil().gotoPage("ExtensionTest", "Alice");
        Assert.assertEquals("Alice Wiki Macro (upgraded)", viewPage.getDocumentTitle());
        Assert.assertTrue(viewPage.getContent().contains("Alice says hi guys!"));
    }

    /**
     * Tests how an extension is upgraded when there is a merge conflict.
     */
    @Test
    public void testUpgradeWithMergeConflict() throws Exception
    {
        // Setup the extension.
        String extensionId = "alice-xar-extension";
        String oldVersion = "1.3";
        String newVersion = "2.1.4";
        TestExtension oldExtension =
            getRepositoryTestUtils().getTestExtension(new ExtensionId(extensionId, oldVersion), "xar");
        getRepositoryTestUtils().addExtension(oldExtension);
        TestExtension newExtension =
            getRepositoryTestUtils().getTestExtension(new ExtensionId(extensionId, newVersion), "xar");
        getRepositoryTestUtils().attachFile(newExtension);
        getRepositoryTestUtils().addVersionObject(newExtension, newVersion,
            "attach:" + newExtension.getFile().getName());

        // Make sure the old version is installed.
        getExtensionTestUtils().install(new ExtensionId(extensionId, oldVersion));

        // Edit the installed version so that we have a merge conflict.
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("title", "Alice Extension");
        queryParameters.put("content", "== Usage ==\n\n{{code language=\"none\"}}\n"
            + "{{alice/}}\n{{/code}}\n\n== Output ==\n\n{{alice/}}");
        queryParameters.put("XWiki.WikiMacroClass_0_code", "{{info}}Alice says hello!{{/info}}");
        getUtil().gotoPage("ExtensionTest", "Alice", "save", queryParameters);

        // Initiate the upgrade process.
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickAddExtensionsSection();
        SearchResultsPane searchResults =
            adminPage.getSearchBar().clickAdvancedSearch().search(extensionId, newVersion);
        ExtensionPane extensionPane = searchResults.getExtension(0);
        extensionPane = extensionPane.upgrade().confirm();

        // Check the merge conflict UI.
        Assert.assertEquals("loading", extensionPane.getStatus());
        Assert.assertNull(extensionPane.getStatusMessage());

        ProgressBarPane progressBar = extensionPane.getProgressBar();
        Assert.assertEquals(83, progressBar.getPercent());
        Assert.assertEquals("Conflict between [@@ -1,1 +1,1 @@] and [@@ -1,1 +1,1 @@]", progressBar.getMessage());

        ExtensionProgressPane progressPane = extensionPane.openProgressSection();
        List<LogItemPane> upgradeLog = progressPane.getJobLog();
        LogItemPane lastLogItem = upgradeLog.get(upgradeLog.size() - 1);
        Assert.assertEquals("loading", lastLogItem.getLevel());
        Assert.assertEquals(progressBar.getMessage(), lastLogItem.getMessage());

        MergeConflictPane mergeConflictPane = progressPane.getMergeConflict();
        ChangesPane changesPane = mergeConflictPane.getChanges();
        Assert.assertEquals("Alice <ins>Wiki </ins>Extension<ins> (upgraded)</ins>",
            changesPane.getMetaDataChanges("Title"));
        Assert.assertFalse(changesPane.getContentChanges().isEmpty());
        Assert.assertEquals("@@ -1,1 +1,1 @@\n-<del>Test</del> macro.\n+<ins>A</ins> <ins>cool </ins>macro.",
            changesPane.getObjectChanges("XWiki.WikiMacroClass", 0, "Macro description"));

        mergeConflictPane.getFromVersionSelect().selectByVisibleText("Previous version");
        mergeConflictPane.getToVersionSelect().selectByVisibleText("Current version");
        mergeConflictPane = mergeConflictPane.clickShowChanges();

        changesPane = mergeConflictPane.getChanges();
        StringBuilder expectedDiff = new StringBuilder();
        expectedDiff.append("@@ -1,9 +1,9 @@\n");
        expectedDiff.append("-= Usage =\n");
        expectedDiff.append("+=<ins>=</ins> Usage =<ins>=</ins>\n");
        expectedDiff.append(" \n");
        expectedDiff.append("-{{code}}\n");
        expectedDiff.append("+{{code<ins> language=\"none\"</ins>}}\n");
        expectedDiff.append(" {{alice/}}\n");
        expectedDiff.append(" {{/code}}\n");
        expectedDiff.append(" \n");
        expectedDiff.append("-= <del>Res</del>u<del>l</del>t =\n");
        expectedDiff.append("+=<ins>=</ins> <ins>O</ins>ut<ins>put</ins> =<ins>=</ins>\n");
        expectedDiff.append(" \n");
        expectedDiff.append(" {{alice/}}");
        Assert.assertEquals(expectedDiff.toString(), changesPane.getContentChanges());
        Assert.assertEquals(1, changesPane.getObjectChangeSummaries().size());
        Assert.assertEquals(
            "@@ -1,1 +1,1 @@\n-Alice says hello!\n+<ins>{{info}}</ins>Alice says hello!<ins>{{/info}}</ins>",
            changesPane.getObjectChanges("XWiki.WikiMacroClass", 0, "Macro code"));

        // Finish the merge.
        mergeConflictPane.getVersionToKeepSelect().selectByValue("NEXT");
        // FIXME: We get the extension pane from the search results because it is reloaded when we compare the versions.
        extensionPane = searchResults.getExtension(0).confirm();

        Assert.assertEquals("installed", extensionPane.getStatus());
        Assert.assertNull(extensionPane.getProgressBar());
        upgradeLog = extensionPane.openProgressSection().getJobLog();
        lastLogItem = upgradeLog.get(upgradeLog.size() - 1);
        Assert.assertEquals("info", lastLogItem.getLevel());
        Assert.assertEquals("Finished job of type [install] with identifier "
            + "[extension/action/alice-xar-extension/wiki:xwiki]", lastLogItem.getMessage());

        // Check the merge result.
        ViewPage mergedPage = getUtil().gotoPage("ExtensionTest", "Alice");
        Assert.assertEquals("Alice Wiki Macro (upgraded)", mergedPage.getDocumentTitle());
    }

    /**
     * Tests how an extension is downgraded.
     */
    @Test
    public void testDowngrade() throws Exception
    {
        // Setup the extension.
        String extensionId = "alice-xar-extension";
        String oldVersion = "1.3";
        String newVersion = "2.1.4";
        TestExtension oldExtension =
            getRepositoryTestUtils().getTestExtension(new ExtensionId(extensionId, oldVersion), "xar");
        getRepositoryTestUtils().addExtension(oldExtension);
        TestExtension newExtension =
            getRepositoryTestUtils().getTestExtension(new ExtensionId(extensionId, newVersion), "xar");
        getRepositoryTestUtils().attachFile(newExtension);
        getRepositoryTestUtils().addVersionObject(newExtension, newVersion,
            "attach:" + newExtension.getFile().getName());

        // Make sure the new version is installed.
        getExtensionTestUtils().install(new ExtensionId(extensionId, newVersion));

        // Downgrade the extension.
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickAddExtensionsSection();
        ExtensionPane extensionPane =
            adminPage.getSearchBar().clickAdvancedSearch().search(extensionId, oldVersion).getExtension(0);
        Assert.assertEquals("remote-installed", extensionPane.getStatus());
        Assert.assertEquals("Version 2.1.4 is installed", extensionPane.getStatusMessage());
        extensionPane = extensionPane.downgrade();

        // Check the downgrade plan.
        List<DependencyPane> downgradePlan = extensionPane.openProgressSection().getJobPlan();
        Assert.assertEquals(1, downgradePlan.size());
        Assert.assertEquals(extensionId, downgradePlan.get(0).getName());
        Assert.assertEquals(oldVersion, downgradePlan.get(0).getVersion());
        Assert.assertEquals("remote-installed", downgradePlan.get(0).getStatus());
        Assert.assertEquals("Version 2.1.4 is installed", downgradePlan.get(0).getStatusMessage());

        // Finish the downgrade and check the downgrade log.
        extensionPane = extensionPane.confirm();
        Assert.assertEquals("installed", extensionPane.getStatus());
        Assert.assertEquals("Installed", extensionPane.getStatusMessage());
        List<LogItemPane> log = extensionPane.openProgressSection().getJobLog();
        Assert.assertTrue(log.size() > 2);
        Assert.assertEquals("info", log.get(2).getLevel());
        Assert.assertEquals("Resolving extension [alice-xar-extension 1.3] on namespace [Home]", log.get(2)
            .getMessage());
        Assert.assertEquals("info", log.get(log.size() - 1).getLevel());
        Assert.assertEquals("Finished job of type [install] with identifier "
            + "[extension/action/alice-xar-extension/wiki:xwiki]", log.get(log.size() - 1).getMessage());

        // Assert the changes.
        ViewPage viewPage = getUtil().gotoPage("ExtensionTest", "Alice");
        Assert.assertEquals("Alice Macro", viewPage.getDocumentTitle());
        Assert.assertTrue(viewPage.getContent().contains("Alice says hello!"));
    }

    /**
     * Tests if a Java component script service is properly installed.
     */
    @Test
    public void testInstallScriptService() throws Exception
    {
        // Make sure the script service is not available before the extension is installed.
        ViewPage viewPage =
            getUtil().createPage(
                getTestClassName(),
                getTestMethodName(),
                "{{velocity}}$services.greeter.greet('world') "
                    + "$services.greeter.greet('XWiki', 'default'){{/velocity}}", "");
        Assert.assertFalse(viewPage.getContent().contains("Hello world! Hello XWiki!"));

        // Setup the extension.
        ExtensionId extensionId = new ExtensionId("scriptServiceJarExtension", "4.2-milestone-1");
        TestExtension extension = getRepositoryTestUtils().getTestExtension(extensionId, "jar");
        getRepositoryTestUtils().addExtension(extension);

        // Search the extension and install it.
        ExtensionAdministrationPage adminPage = ExtensionAdministrationPage.gotoPage().clickAddExtensionsSection();
        ExtensionPane extensionPane =
            adminPage.getSearchBar().clickAdvancedSearch().search(extensionId).getExtension(0);
        extensionPane.install().confirm();

        // Check the result.
        Assert.assertEquals("Hello world! Hello XWiki!", getUtil().gotoPage(getTestClassName(), getTestMethodName())
            .getContent());
    }
}
