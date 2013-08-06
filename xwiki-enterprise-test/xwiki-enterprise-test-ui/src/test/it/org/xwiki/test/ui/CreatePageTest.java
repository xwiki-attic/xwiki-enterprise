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
package org.xwiki.test.ui;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.administration.test.po.TemplateProviderInlinePage;
import org.xwiki.administration.test.po.TemplatesAdministrationSectionPage;
import org.xwiki.test.po.xe.HomePage;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.browser.IgnoreBrowsers;
import org.xwiki.test.ui.po.CreatePagePage;
import org.xwiki.test.ui.po.CreateSpacePage;
import org.xwiki.test.ui.po.DocumentDoesNotExistPage;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.test.ui.po.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.po.editor.WikiEditPage;

/**
 * Tests page creation using a Template Provider and a Template.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class CreatePageTest extends AbstractAdminAuthenticatedTest
{
    /**
     * Name of the template.
     */
    public static final String TEMPLATE_NAME = "MyTemplate";

    /**
     * Helper function to Create a template provider for the tests in this class.
     */
    private ViewPage createTemplate(String templateProviderName, String templateContent, String templateTitle,
        boolean saveAndEdit)
    {
        String space = this.getClass().getSimpleName();
        getUtil().deletePage(space, TEMPLATE_NAME);
        getUtil().deletePage(space, TEMPLATE_NAME + "Provider");

        String templateFullName = space + "." + TEMPLATE_NAME;

        // Create a template
        WikiEditPage editTemplatePage = WikiEditPage.gotoPage(space, TEMPLATE_NAME);
        editTemplatePage.setTitle(templateTitle);
        editTemplatePage.setContent(templateContent);
        editTemplatePage.clickSaveAndView();

        // Create the template provider
        TemplatesAdministrationSectionPage sectionPage = TemplatesAdministrationSectionPage.gotoPage();
        TemplateProviderInlinePage templateProviderInline =
            sectionPage.createTemplateProvider(space, templateProviderName);
        templateProviderInline.setTemplateName("My Template");
        templateProviderInline.setTemplate(templateFullName);
        if (saveAndEdit) {
            templateProviderInline.setSaveAndEdit();
        }
        return templateProviderInline.clickSaveAndView();
    }

    /**
     * Tests if a new page can be created from a template.
     */
    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void testCreatePageFromTemplate()
    {
        // Setup the correct environment for the test
        getUtil().deletePage(getTestClassName(), getTestMethodName());
        // all these pages are created during this test
        getUtil().deletePage(getTestClassName(), TEMPLATE_NAME + "Instance");
        getUtil().deletePage(getTestClassName(), "NewPage");
        getUtil().deletePage(getTestClassName(), TEMPLATE_NAME + "UnexistingInstance");
        getUtil().deletePage(getTestClassName(), "EmptyPage");

        String templateContent = "My Template Content";
        String templateTitle = "My Template Title";
        String templateProviderName = TEMPLATE_NAME + "Provider";
        String templateProviderFullName = getTestClassName() + "." + templateProviderName;

        ViewPage templateProviderView = createTemplate(templateProviderName, templateContent, templateTitle, false);

        // Create the new document from template
        CreatePagePage createPagePage = templateProviderView.createPage();
        // Save the number of available templates so that we can make some checks later on.
        int availableTemplateSize = createPagePage.getAvailableTemplateSize();
        String templateInstanceName = TEMPLATE_NAME + "Instance";
        WYSIWYGEditPage templateInstanceEditWysiwyg =
            createPagePage.createPageFromTemplate(getTestClassName(), templateInstanceName, templateProviderFullName);
        templateInstanceEditWysiwyg.waitUntilPageIsLoaded();
        WikiEditPage templateInstanceEdit = templateInstanceEditWysiwyg.clickSaveAndView().editWiki();

        // Verify template instance content
        Assert.assertEquals(templateInstanceName, templateInstanceEdit.getTitle());
        Assert.assertEquals(templateContent, templateInstanceEdit.getContent());
        // check the parent of the template instance
        Assert.assertEquals(templateProviderFullName, templateInstanceEdit.getParent());

        // Put a wanted link in the template instance
        templateInstanceEdit.setContent("[[NewPage]]");
        ViewPage vp = templateInstanceEdit.clickSaveAndView();

        // Verify that clicking on the wanted link pops up a box to choose the template.
        vp.clickWantedLink(getTestClassName(), "NewPage", true);
        List<WebElement> templates = getDriver().findElements(By.name("templateprovider"));
        // Note: We need to remove 1 to exclude the "Empty Page" template entry
        Assert.assertEquals(availableTemplateSize, templates.size() - 1);
        Assert.assertTrue(createPagePage.getAvailableTemplates().contains(templateProviderFullName));

        // Create a new page from template by going to a non-existing page
        // And make sure we're on a non-existing page
        Assert.assertFalse(getUtil().gotoPage(getTestClassName(), TEMPLATE_NAME + "UnexistingInstance").exists());
        DocumentDoesNotExistPage unexistingPage = new DocumentDoesNotExistPage();
        unexistingPage.clickEditThisPageToCreate();
        CreatePagePage createUnexistingPage = new CreatePagePage();
        // Make sure we're in create mode.
        Assert.assertTrue(getUtil().isInCreateMode());
        // count the available templates, make sure they're as many as before and that our template is among them
        templates = getDriver().findElements(By.name("templateprovider"));
        // Note: We need to remove 1 to exclude the "Empty Page" template entry
        Assert.assertEquals(availableTemplateSize, templates.size() - 1);
        Assert.assertTrue(createPagePage.getAvailableTemplates().contains(templateProviderFullName));
        // select it
        createUnexistingPage.setTemplate(templateProviderFullName);
        // and create
        createUnexistingPage.clickCreate();
        WYSIWYGEditPage unexistingPageEditWysiwyg = new WYSIWYGEditPage();
        unexistingPageEditWysiwyg.waitUntilPageIsLoaded();
        WikiEditPage unexistingPageEdit = unexistingPageEditWysiwyg.clickSaveAndView().editWiki();

        // Verify template instance content
        Assert.assertEquals(TEMPLATE_NAME + "UnexistingInstance", unexistingPageEdit.getTitle());
        Assert.assertEquals(templateContent, unexistingPageEdit.getContent());
        // test that this page has no parent
        Assert.assertEquals("", unexistingPageEdit.getParent());

        // create an empty page when there is a template available, make sure it's empty
        CreatePagePage createEmptyPage = CreatePagePage.gotoPage();
        Assert.assertTrue(createEmptyPage.getAvailableTemplateSize() > 0);
        WYSIWYGEditPage editEmptyPage = createEmptyPage.createPage(getTestClassName(), "EmptyPage");
        Assert.assertTrue(getUtil().isInWYSIWYGEditMode());
        // wait to load editor to make sure that what we're saving is the content that is supposed to be in this
        // document
        editEmptyPage.waitUntilPageIsLoaded();
        ViewPage emptyPage = editEmptyPage.clickSaveAndView();
        // make sure it's empty
        Assert.assertEquals("", emptyPage.getContent());
        // make sure parent is the right one
        Assert.assertTrue(emptyPage.hasBreadcrumbContent("Wiki Home", false));
        // mare sure title is the right one
        Assert.assertEquals("EmptyPage", emptyPage.getDocumentTitle());

        // Restrict the template to its own space
        templateProviderView = getUtil().gotoPage(getTestClassName(), TEMPLATE_NAME + "Provider");
        templateProviderView.editInline();
        TemplateProviderInlinePage templateProviderInline = new TemplateProviderInlinePage();
        List<String> allowedSpaces = new ArrayList<String>();
        allowedSpaces.add(getTestClassName());
        templateProviderInline.setSpaces(allowedSpaces);
        templateProviderView = templateProviderInline.clickSaveAndView();

        // Verify we can still create a page from template in the test space
        createPagePage = templateProviderView.createPage();
        // Make sure we get in create mode.
        Assert.assertTrue(getUtil().isInCreateMode());
        Assert.assertEquals(availableTemplateSize, createPagePage.getAvailableTemplateSize());
        Assert.assertTrue(createPagePage.getAvailableTemplates().contains(templateProviderFullName));

        // Modify the target space and verify the form can't be submitted
        createPagePage.setTemplate(templateProviderFullName);
        createPagePage.setSpace("Foobar");
        String currentURL = getDriver().getCurrentUrl();
        createPagePage.clickCreate();
        Assert.assertEquals(currentURL, getDriver().getCurrentUrl());
        // and check that an error is displayed to the user
        createPagePage.waitForFieldErrorMessage();

        // Verify the template we have removed is no longer available.
        createEmptyPage = CreatePagePage.gotoPage();

        // make sure that the template provider is not in the list of templates
        Assert.assertFalse(createPagePage.getAvailableTemplates().contains(templateProviderFullName));
    }

    /**
     * Tests that creating a space works fine.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason = "See http://jira.xwiki.org/browse/XE-1146")
    public void testCreateSpace()
    {
        // create a random space
        String space = this.getClass().getSimpleName() + ((int) (Math.random() * 1000));
        getUtil().deletePage(space, "WebHome");

        // start creating a space
        HomePage homePage = HomePage.gotoPage();
        CreateSpacePage createSpace = homePage.createSpace();

        WYSIWYGEditPage editSpaceWebhomePage = createSpace.createSpace(space);
        // expect wysiwyg edit mode for the webhome of the space
        Assert.assertTrue(getUtil().isInWYSIWYGEditMode());
        Assert.assertEquals(space, editSpaceWebhomePage.getMetaDataValue("space"));
        Assert.assertEquals("WebHome", editSpaceWebhomePage.getMetaDataValue("page"));
        // The default parent is the home page of the current wiki (XWIKI-7572).
        Assert.assertEquals("Main.WebHome", editSpaceWebhomePage.getParent());
        // and the title the name of the space
        Assert.assertEquals(space, editSpaceWebhomePage.getDocumentTitle());
    }

    /**
     * Tests that creating a page or a space that already exists displays an error.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason = "See http://jira.xwiki.org/browse/XE-1146")
    public void testCreateExistingPage()
    {
        String space = this.getClass().getSimpleName();
        // create a template to make sure that we have a template to create from
        String templateProviderName = TEMPLATE_NAME + "Provider";
        String templateContent = "Templates are fun";
        String templateTitle = "Funny templates";
        createTemplate(templateProviderName, templateContent, templateTitle, false);

        // create a page and a space webhome
        String existingPageName = "ExistingPage";
        getUtil().createPage(space, existingPageName, "Page that already exists", "Existing page");
        String existingSpace = this.getClass().getSimpleName() + ((int) (Math.random() * 1000));
        getUtil().createPage(existingSpace, "WebHome", "{{spaceindex /}}", "New random space");

        // 1/ create an empty page that already exists
        HomePage homePage = HomePage.gotoPage();
        CreatePagePage createPage = homePage.createPage();
        createPage.setSpace(space);
        createPage.setPage(existingPageName);
        String currentURL = getDriver().getCurrentUrl();
        createPage.clickCreate();
        // make sure that we stay on the same page and that an error is displayed to the user. Maybe we should check the
        // error
        Assert.assertEquals(currentURL, getDriver().getCurrentUrl());
        createPage.waitForErrorMessage();

        // 2/ create a page from template that already exists
        // restart everything to make sure it's not the error before
        homePage = HomePage.gotoPage();
        createPage = homePage.createPage();
        createPage.setSpace(space);
        createPage.setPage(existingPageName);
        createPage.setTemplate(space + "." + templateProviderName);
        currentURL = getDriver().getCurrentUrl();
        createPage.clickCreate();
        // make sure that we stay on the same page and that an error is displayed to the user. Maybe we should check the
        // error
        Assert.assertEquals(currentURL, getDriver().getCurrentUrl());
        createPage.waitForErrorMessage();

        // 3/ create a space that already exists
        homePage = HomePage.gotoPage();
        CreateSpacePage createSpace = homePage.createSpace();
        currentURL = getDriver().getCurrentUrl();
        // strip the parameters out of this URL
        currentURL =
            currentURL.substring(0, currentURL.indexOf('?') > 0 ? currentURL.indexOf('?') : currentURL.length());
        createSpace.createSpace(existingSpace);
        String urlAfterSubmit = getDriver().getCurrentUrl();
        urlAfterSubmit =
            urlAfterSubmit.substring(0,
                urlAfterSubmit.indexOf('?') > 0 ? urlAfterSubmit.indexOf('?') : urlAfterSubmit.length());
        // make sure that we stay on the same page and that an error is displayed to the user. Maybe we should check the
        // error
        Assert.assertEquals(currentURL, urlAfterSubmit);
        Assert.assertTrue(createSpace.hasError());
    }

    /**
     * Tests what happens when creating a page when no template is available in the specific space.
     */
    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void testCreatePageWhenNoTemplateAvailable()
    {
        // prepare the test environment, create a test space and exclude all templates for this space
        String space = this.getClass().getSimpleName();
        // create the webhome of this space to make sure the space exists
        WikiEditPage editTemplatePage = WikiEditPage.gotoPage(space, "WebHome");
        editTemplatePage.setTitle("Welcome to the templates test space");
        editTemplatePage.setContent("You can have fun with templates here");
        editTemplatePage.clickSaveAndView();
        // we'll create all these pages during this test
        getUtil().deletePage(space, "NewUnexistingPage");
        getUtil().deletePage(space, "NewPage");
        getUtil().deletePage(space, "NewLinkedPage");
        // go through all the templates and make sure they are disabled on this space
        TemplatesAdministrationSectionPage sectionPage = TemplatesAdministrationSectionPage.gotoPage();

        // get the links to existing templates, navigate to each of them and disable the current space
        List<String> spacesToExclude = new ArrayList<String>();
        spacesToExclude.add(space);
        List<WebElement> existingTemplatesLinks = sectionPage.getExistingTemplatesLinks();
        for (int i = 0; i < existingTemplatesLinks.size(); i++) {
            WebElement link = existingTemplatesLinks.get(i);
            link.click();
            ViewPage templateViewPage = new ViewPage();
            templateViewPage.editInline();
            TemplateProviderInlinePage providerEditPage = new TemplateProviderInlinePage();
            if (providerEditPage.isPageTemplate()) {
                providerEditPage.excludeSpaces(spacesToExclude);
                providerEditPage.clickSaveAndView();
            }

            // go back to the admin page, to leave this in a valid state
            sectionPage = TemplatesAdministrationSectionPage.gotoPage();
            existingTemplatesLinks = sectionPage.getExistingTemplatesLinks();
        }

        // TODO: should reset these template settings at the end of the test, to leave things in the same state as they
        // were at the beginning of the test

        // and now start testing!

        // 1/ create a page from the link in the page displayed when navigating to a non-existing page
        Assert.assertFalse(getUtil().gotoPage(space, "NewUnexistingPage").exists());
        DocumentDoesNotExistPage nonExistingPage = new DocumentDoesNotExistPage();
        nonExistingPage.clickEditThisPageToCreate();
        // make sure we're not in create mode anymore
        Assert.assertFalse(getUtil().isInCreateMode());
        // make sure we're directly in edit mode
        Assert.assertTrue(getUtil().isInWYSIWYGEditMode());
        // TODO: check that we're indeed in the edit mode of space.NewUnexitingPage
        WYSIWYGEditPage editNewUnexistingPage = new WYSIWYGEditPage();
        Assert.assertEquals(space, editNewUnexistingPage.getMetaDataValue("space"));
        Assert.assertEquals("NewUnexistingPage", editNewUnexistingPage.getMetaDataValue("page"));

        // 2/ create a page from the create menu on an existing page, by filling in space and name
        ViewPage spaceHomePage = getUtil().gotoPage(space, "WebHome");
        CreatePagePage createNewPage = spaceHomePage.createPage();
        // we expect no templates available
        Assert.assertEquals(0, createNewPage.getAvailableTemplateSize());
        // fill in data and create the page
        createNewPage.setSpace(space);
        createNewPage.setPage("NewPage");
        createNewPage.clickCreate();
        // we expect to go to the edit mode of the new page
        Assert.assertFalse(getUtil().isInCreateMode());
        Assert.assertTrue(getUtil().isInWYSIWYGEditMode());
        WYSIWYGEditPage editNewPage = new WYSIWYGEditPage();
        editNewPage.waitUntilPageIsLoaded();
        Assert.assertEquals(space, editNewPage.getMetaDataValue("space"));
        Assert.assertEquals("NewPage", editNewPage.getMetaDataValue("page"));

        // 3/ create a page from a link in another page
        WikiEditPage editNewPageWiki = editNewPage.clickSaveAndView().editWiki();
        // put a link to the new page to create
        editNewPageWiki.setContent("[[NewLinkedPage]]");
        ViewPage newPage = editNewPageWiki.clickSaveAndView();
        // no templates are available, so we don't expect a panel with a list of templates, we just expect it goes
        // directly to edit mode of the new page
        // it would be nice to be able to test here that the create page panel is not displayed, ever. However, we can't
        // since we need to wait for that time, and we don't know how much is never.
        newPage.clickWantedLink(space, "NewLinkedPage", false);
        WYSIWYGEditPage editNewLinkedPage = new WYSIWYGEditPage();
        // since the edit mode loads as a result of a redirect that comes from a async call made by the click, we need
        // to wait for the page to load
        editNewLinkedPage.waitUntilElementIsVisible(By
            .xpath("//div[@id='tmCurrentEditor']//a/strong[contains(text(), 'WYSIWYG')]"));
        // make sure we're in edit mode (of the new page)
        Assert.assertTrue(getUtil().isInWYSIWYGEditMode());
        Assert.assertEquals(space, editNewLinkedPage.getMetaDataValue("space"));
        Assert.assertEquals("NewLinkedPage", editNewLinkedPage.getMetaDataValue("page"));
    }

    /**
     * Tests the creation of a page from a save and edit template, tests that the page is indeed saved.
     */
    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void testCreatePageWithSaveAndEditTemplate()
    {
        String space = this.getClass().getSimpleName();
        String newPageName = "NewPage";
        getUtil().deletePage(space, newPageName);

        // create a template
        String templateProviderName = TEMPLATE_NAME + "Provider";
        String templateContent = "Templates are fun";
        String templateTitle = "Funny templates";
        String templateProviderFullName = space + "." + templateProviderName;
        ViewPage templatePage = createTemplate(templateProviderName, templateContent, templateTitle, true);

        // create the page
        CreatePagePage createPage = templatePage.createPage();
        WYSIWYGEditPage editCreatedPage =
            createPage.createPageFromTemplate(space, newPageName, templateProviderFullName);
        // ensure that we're indeed in edit mode
        Assert.assertTrue(getUtil().isInWYSIWYGEditMode());
        // wait for editor to load (so that content is loaded)
        editCreatedPage.waitUntilPageIsLoaded();
        // and now cancel it
        ViewPage newPage = editCreatedPage.clickCancel();
        // make sure we're not in unexisting page
        Assert.assertTrue(newPage.exists());
        // we should be in view mode (useless check since the impl of isNonExisting page calls it anyway)
        Assert.assertTrue(getUtil().isInViewMode());
        // make sure it's the page we want
        Assert.assertEquals(space, newPage.getMetaDataValue("space"));
        Assert.assertEquals(newPageName, newPage.getMetaDataValue("page"));
        // and now test the title is the name of the page and the content is the one from the template
        Assert.assertEquals(newPageName, newPage.getDocumentTitle());
        Assert.assertEquals(templateContent, newPage.getContent());
        // and the parent, it should be the template provider, since that's where we created it from
        Assert.assertTrue(newPage.hasBreadcrumbContent(templateProviderName, false));
    }
}
