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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.test.ui.administration.elements.AdminTemplatesPage;
import org.xwiki.test.ui.administration.elements.TemplateProviderInlinePage;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.CreatePagePage;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;
import org.xwiki.test.ui.xe.elements.HomePage;

/**
 * Tests page creation using a Template Provider and a Template.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class CreatePageTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The object used to access the name of the current test.
     */
    @Rule
    public final TestName testName = new TestName();

    /**
     * Name of the template.
     */
    public static final String TEMPLATE_NAME = "MyTemplate";

    /**
     * Tests if a new page can be created from a template.
     */
    @Test
    public void testCreatePageFromTemplate()
    {
        String space = this.getClass().getSimpleName();
        getUtil().deletePage(space, this.testName.getMethodName());
        getUtil().deletePage(space, TEMPLATE_NAME);
        getUtil().deletePage(space, TEMPLATE_NAME + "Provider");
        getUtil().deletePage(space, TEMPLATE_NAME + "Instance");

        String templateContent = "My Template Content";
        String templateTitle = "My Template Title";
        String templateFullName = space + "." + TEMPLATE_NAME;

        // Create a template
        HomePage homePage = new HomePage();
        homePage.gotoPage();
        CreatePagePage createPagePage = homePage.createPage();
        WYSIWYGEditPage editWysiwygTemplatePage = createPagePage.createPage(space, TEMPLATE_NAME);
        editWysiwygTemplatePage.clickSaveAndContinue();
        WikiEditPage editTemplatePage = editWysiwygTemplatePage.clickEditWiki();
        editTemplatePage.setTitle(templateTitle);
        editTemplatePage.setContent(templateContent);
        editTemplatePage.clickSaveAndView();

        // Create the template provider
        AdminTemplatesPage adminTemplatesPage = new AdminTemplatesPage();
        adminTemplatesPage.gotoPage();
        TemplateProviderInlinePage templateProviderInline =
            adminTemplatesPage.createTemplateProvider(space, TEMPLATE_NAME + "Provider");
        templateProviderInline.setTemplateName("My Template");
        templateProviderInline.setTemplate(templateFullName);
        ViewPage templateProviderView = templateProviderInline.clickSaveAndView();

        // Create the new document from template
        createPagePage = templateProviderView.createPage();
        String templateInstanceName = TEMPLATE_NAME + "Instance";
        WYSIWYGEditPage templateInstanceEditWysiwyg =
            createPagePage.createPageFromTemplate(space, templateInstanceName, templateFullName);
        WikiEditPage templateInstanceEdit = templateInstanceEditWysiwyg.clickSaveAndView().clickEditWiki();

        // Verify template instance content
        Assert.assertEquals(templateInstanceName, templateInstanceEdit.getTitle());
        Assert.assertEquals(templateContent, templateInstanceEdit.getContent());

        // Put a broken link in the template instance
        templateInstanceEdit.setContent("[[NewPage]]");
        templateInstanceEdit.clickSaveAndView();

        WebElement brokenLink = getDriver().findElement(
            By.xpath("//a[contains(@href,'/create/" + space + "/NewPage')]"));
        brokenLink.click();

        // Ensure that the template choice popup is displayed
        List<WebElement> templates = getDriver().findElements(By.name("template"));
        Assert.assertFalse(templates.isEmpty());

        // Restrict the template to its own space
        templateProviderView = getUtil().gotoPage(space, TEMPLATE_NAME + "Provider");
        templateProviderView.clickEditInline();
        List<String> allowedSpaces = new ArrayList<String>();
        allowedSpaces.add(space);
        templateProviderInline.setSpaces(allowedSpaces);
        templateProviderView = templateProviderInline.clickSaveAndView();

        // Verify we can still create a page from template in the test space
        createPagePage = templateProviderView.createPage();
        Assert.assertTrue(createPagePage.areTemplatesAvailable());

        // Modify the target space and verify the form can't be submitted
        createPagePage.setTemplate(templateFullName);
        createPagePage.setSpace("Foobar");
        String currentURL = getDriver().getCurrentUrl();
        createPagePage.clickCreate();
        Assert.assertEquals(currentURL, getDriver().getCurrentUrl());

        // Verify the template we have removed is no longer available.
        homePage.gotoPage();
        createPagePage = homePage.createPage();
        Assert.assertFalse(createPagePage.areTemplatesAvailable());
    }
}
