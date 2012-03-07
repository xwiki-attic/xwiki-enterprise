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
package org.xwiki.test.selenium;

import junit.framework.Test;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

/**
 * Verify the overall Administration application features.
 * 
 * @version $Id$
 */
public class AdministrationTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the Administration application features.");
        suite.addTestSuite(AdministrationTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    /**
     * This method makes the following tests :
     * 
     * <ul>
     * <li>Login as global admin.</li>
     * <li>Validate presence of default sections for global and space sections.</li>
     * <li>Validate presence of application administration sections at global level only.</li>
     * </ul>
     */
    public void testGlobalAndSpaceSections()
    {
        clickLinkWithText("Administer Wiki");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Editing')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Localization')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Email')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Presentation')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Elements')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Registration')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Users')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Groups')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Rights')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Registration')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Import')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Export')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Templates')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=MessageStream')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Panels.PanelWizard')]");

        // select space administration
        getSelenium().select("goto-select", "label=Main");
        waitPage();
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Editing')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Localization')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Email')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Presentation')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Elements')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Registration')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Users')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Groups')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Rights')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Registration')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Import')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Export')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Templates')]");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=MessageStream')]");
        assertElementPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Panels.PanelWizard')]");
    }

    /*
     * Test to see an application page is included only if that application exists
     */
    public void testApplicationSection()
    {
        // Delete the Blog.Categories page and test it's not present in the admin global menu anymore
        deletePage("XWiki", "SearchAdmin");
        clickLinkWithText("Administer Wiki");
        assertElementNotPresent("//*[contains(@class, 'admin-menu')]//a[contains(@href, 'section=Search')]");
        restorePage("XWiki", "SearchAdmin");
    }

    /*
     * Test modifying XWiki.XWikiPreferences multi-language field and save it.
     */
    public void testSettingXWikiPreferences()
    {
        clickLinkWithText("Administer Wiki");
        getSelenium().select("goto-select", "label=Wiki administration");
        clickLinkWithXPath("//a[text()='Localization']", true);
        getSelenium().select("//select[@name='XWiki.XWikiPreferences_0_multilingual']", "label=Yes");
        clickLinkWithXPath("//input[@value='Save']", true);
        assertElementPresent("//span[@id='headerlanguages']");
    }

    /*
     * Test adding a new category in Blog Categories
     */
    /* Disabled until the new blog can insert its own administration page.
    public void testBlogAdmin()
    {
        open("XWiki", "XWikiPreferences", "admin");
       
        // select global administration
        clickLinkWithLocator("//span[text()='General']", true);
        getSelenium().select("//select[@id='XWiki.XWikiPreferences_0_editor']", "label=Text");
        clickLinkWithLocator("//input[@value='Save']");
        assertElementPresent("//span[@id='showsectionswrapper']");
        clickLinkWithXPath("//a[@id='showsections']", false);
        assertElementPresent("//span[@id='hidesectionswrapper']");
        clickLinkWithLocator("//span[text()='Blog categories']");
        setFieldValue("name", "New Category");
        setFieldValue("description", "New Category Content");              
        clickLinkWithLocator("//input[@value='Add']", true);
        assertTextPresent("New Category");               
    }*/

    /*
     * Test Panel Wizard
     */
    public void testPanelsAdmin()
    {
        open("XWiki", "XWikiPreferences", "admin");

        // test panel wizard at global level
        clickLinkWithLocator("//a[text()='Panel Wizard']");
        waitForCondition("selenium.page().bodyText().indexOf('Panel List')!=-1;");
        clickLinkWithXPath("//a[@href='#PageLayoutSection']", false);
        waitForCondition("selenium.isElementPresent(\"//div[@id='bothcolumns']\")!=false;");
        clickLinkWithXPath("//div[@id='bothcolumns']", false);
        waitForCondition("selenium.page().bodyText().indexOf('Page Layout')!=-1;");
        clickLinkWithXPath("//a[@href='#PanelListSection']", false);
        getSelenium().dragAndDropToObject("//div[@class='panel expanded QuickLinks']", "//div[@id='leftPanels']");
        clickLinkWithXPath("//a[text()='Save the new layout']", false);
        waitForCondition("selenium.isAlertPresent()");
        assertEquals("The layout has been saved properly.", getSelenium().getAlert());
        open("Main", "WebHome");
        assertElementPresent("leftPanels");
        assertElementPresent("rightPanels");

        // Revert changes
        open("XWiki", "XWikiPreferences", "admin");
        clickLinkWithLocator("//a[text()='Panel Wizard']");
        waitForCondition("selenium.page().bodyText().indexOf('Page Layout')!=-1;");
        clickLinkWithXPath("//a[@href='#PageLayoutSection']", false);
        waitForCondition("selenium.isElementPresent(\"//div[@id='rightcolumn']\")!=false;");
        clickLinkWithXPath("//div[@id='rightcolumn']", false);
        clickLinkWithXPath("//a[text()='Save the new layout']", false);
        waitForCondition("selenium.isAlertPresent()");
        assertEquals("The layout has been saved properly.", getSelenium().getAlert());
        open("Main", "WebHome");
        assertElementNotPresent("leftPanels");
        assertElementPresent("rightPanels");

        // test panel wizard at space level
        open("TestPanelsAdmin", "WebHome", "edit", "editor=wiki");
        setFieldValue("content", "aaa");
        clickEditSaveAndView();
        open("TestPanelsAdmin", "WebPreferences", "admin");
        clickLinkWithLocator("//a[text()='Panel Wizard']");
        waitForCondition("selenium.page().bodyText().indexOf('Page Layout')!=-1;");
        clickLinkWithXPath("//a[@href='#PageLayoutSection']", false);
        waitForCondition("selenium.isElementPresent(\"//div[@id='leftcolumn']\")!=false;");
        clickLinkWithXPath("//div[@id='leftcolumn']", false);
        waitForCondition("selenium.page().bodyText().indexOf('Panel List')!=-1;");
        clickLinkWithXPath("//a[@href='#PanelListSection']", false);
        getSelenium().dragAndDropToObject("//div[@class='panel expanded QuickLinks']", "//div[@id='leftPanels']");
        clickLinkWithXPath("//a[text()='Save the new layout']", false);
        waitForCondition("selenium.isAlertPresent()");
        assertEquals("The layout has been saved properly.", getSelenium().getAlert());
        open("TestPanelsAdmin", "WebHome");
        assertElementPresent("leftPanels");
        assertElementPresent("//div[@class='panel expanded QuickLinks']");
        open("XWiki", "WebHome");
        assertElementNotPresent("leftPanels");
        assertElementNotPresent("//div[@class='panel expanded QuickLinks']");
    }

    /*
     * Test add configurable application to existing section.
     *
     * This test depends on the "Presentation" section existing.
     * Tests: XWiki.ConfigurableClass
     */
    public void testAddConfigurableApplicationInExistingSection()
    {
        // Create the configurable for global admin.
        createConfigurableApplication("Main", "TestConfigurable", "Presentation", true);
        // Check it's available in global section.
        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=Presentation");
        assertConfigurationPresent("Main", "TestConfigurable");
        // Check it's not available in space section.
        open("Main", "WebPreferences", "admin", "editor=spaceadmin&section=Presentation");
        assertConfigurationNotPresent("Main", "TestConfigurable");
        // Switch application to non-global
        open("Main", "TestConfigurable", "edit", "editor=object");
        getSelenium().uncheck("XWiki.ConfigurableClass_0_configureGlobally");
        clickEditSaveAndView();
        // Check that it is available in space section.
        open("Main", "WebPreferences", "admin", "editor=spaceadmin&section=Presentation");
        assertConfigurationPresent("Main", "TestConfigurable");
        // Check that it's not available in another space.
        open("XWiki", "WebPreferences", "admin", "editor=spaceadmin&section=Presentation");
        assertConfigurationNotPresent("Main", "TestConfigurable");
        // Check that it's not available in global section.
        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=Presentation");
        assertConfigurationNotPresent("Main", "TestConfigurable");
    }

    /**
     * Test add configurable application to a nonexistent section.
     * <p>
     * This test depends on the "HopingThereIsNoSectionByThisName" section not existing.<br/>
     * Tests: XWiki.ConfigurableClass
     */
    public void testAddConfigurableApplicationInNonexistantSection()
    {
        String section = "HopingThereIsNoSectionByThisName";
        // Create the configurable for global admin.
        createConfigurableApplication("Main", "TestConfigurable", section, true);
        // Check it's available in global section.
        clickLinkWithText("Administer Wiki");
        assertTrue(isAdminMenuItemPresent(section));
        clickLinkWithText(section);
        assertConfigurationPresent("Main", "TestConfigurable");
        // Check that it's not available in space section.
        open("Main", "WebPreferences", "admin");
        // Assert there is no menu item in the administration menu for our configurable application.
        assertFalse(isAdminMenuItemPresent(section));
    }

    /**
     * Make sure some joker can't edit an application of he doesn't have permission.
     * <p>
     * This test depends on the "Presentation" section existing.<br/>
     * This test depends on the "HopingThereIsNoSectionByThisName" section not existing.<br/>
     * Tests: XWiki.ConfigurableClass
     */
    public void testConfigurationNotEditableWithoutPermission()
    {
        String nonExistingSection = "HopingThereIsNoSectionByThisName";
        String existingSection = "Presentation";
        // Create the configurable for global admin.
        createConfigurableApplication("Main", "TestConfigurable", nonExistingSection, true);
        loginAndRegisterUser("someJoker", "bentOnMalice", false);
        loginAsAdmin();

        // Add an XWikiRights object giving someJoker access.
        open("XWiki", "XWikiPreferences", "edit", "editor=object");
        setFieldValue("classname", "XWiki.XWikiRights");
        clickButtonAndContinue("//input[@name='action_objectadd']");
        getSelenium().select("//dd/select[@name='XWiki.XWikiRights_0_levels']", "admin");
        getSelenium().select("//dd/select[@name='XWiki.XWikiRights_0_users']", "someJoker");
        clickEditSaveAndView();

        // Add an XWikiRights object to make sure someJoker has no edit access to Main.TestConfigurable
        open("Main", "TestConfigurable", "edit", "editor=object");
        setFieldValue("classname", "XWiki.XWikiRights");
        clickButtonAndContinue("//input[@name='action_objectadd']");
        getSelenium().select("//dd/select[@name='XWiki.XWikiRights_0_levels']", "edit");
        getSelenium().select("//dd/select[@name='XWiki.XWikiRights_0_users']", "someJoker");
        getSelenium().select("//dd/select[@name='XWiki.XWikiRights_0_allow']", "Deny");
        clickEditSaveAndView();

        loginAndRegisterUser("someJoker", "bentOnMalice", false);
        open("XWiki", "XWikiPreferences", "admin");
        assertTrue(isAdminMenuItemPresent(nonExistingSection));

        // Make sure the error message is displayed.
        // FIXME In 3.0 inaccessible sections don't appear anymore
        // assertElementPresent("//ul[@id='admin-icons']/li[@class='" + nonExistingSection +
        // "']/a/span[@class='errormessage']");

        // If someJoker is clever enough to try the url of the section...
        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=" + nonExistingSection);
        assertConfigurationNotEditable("Main", "TestConfigurable");

        // Now we'll make sure someJoker can't edit an application in an existing section
        loginAsAdmin();
        open("Main", "TestConfigurable", "edit", "editor=object");
        setFieldValue("XWiki.ConfigurableClass_0_displayInSection", existingSection);
        clickEditSaveAndView();
        loginAndRegisterUser("someJoker", "bentOnMalice", false);
        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=" + existingSection);
        assertConfigurationNotEditable("Main", "TestConfigurable");
    }

    /**
     * Fails if a user can create a Configurable application without having edit access to the configuration page (in
     * this case: XWikiPreferences)
     * <p>
     * Tests: XWiki.ConfigurableClass
     */
    public void testConfigurableCreatedByUnauthorizedWillNotExecute()
    {
        // Make sure the configurable page doesn't exist because otherwise we may fail to overwrite it with a
        // non-administrator user.
        deletePage("Main", "TestConfigurable");
        // Create the configurable for global administrator.
        loginAndRegisterUser("anotherJoker", "bentOnMalice", false);
        String nonExistingSection = "HopingThereIsNoSectionByThisName";
        createConfigurableApplication("Main", "TestConfigurable", nonExistingSection, true);
        loginAsAdmin();
        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=" + nonExistingSection);
        assertConfigurationNotEditable("Main", "TestConfigurable");
    }

    /*
     * Creates a document with 2 configurable objects, one gets configured globally in one section and displays
     * 2 configuration fields, the other is configured in the space in another section and displays the other 2
     * fields. Fails if they are not displayed as they should be.
     *
     * Tests: XWiki.ConfigurableClass
     */
    public void testApplicationConfiguredInMultipleSections()
    {
        String space = "Main";
        String page = "TestConfigurable";

        createConfigurableApplication(space, page, "TestSection1", true);
        open(space, page, "edit", "editor=object");
        // Add a second configurable object.
        setFieldValue("classname", "XWiki.ConfigurableClass");
        clickButtonAndContinue("//input[@name='action_objectadd']");
        setFieldValue("XWiki.ConfigurableClass_1_displayInSection", "TestSection2");
        setFieldValue("XWiki.ConfigurableClass_1_heading", "Some Other Heading");
        setFieldValue("XWiki.ConfigurableClass_1_configurationClass", space + "." + page);
        getSelenium().uncheck("XWiki.ConfigurableClass_1_configureGlobally");
        // Set propertiesToShow so that each config only shows half of the properties.
        setFieldValue("XWiki.ConfigurableClass_1_propertiesToShow", "TextArea, Select");
        setFieldValue("XWiki.ConfigurableClass_0_propertiesToShow", "String, Boolean");
        clickEditSaveAndView();

        // Assert that half of the configuration shows up but not the other half.
        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=TestSection1");
        assertElementPresent("//div[@id='admin-page-content']/h2[@id='HSomeHeading']/span");
        // Fields
        String fullName = space + "." + page;
        String form = "//div[@id='admin-page-content']/form[@action='/xwiki/bin/save/" + space + "/" + page + "']";
        assertElementPresent(form + "/fieldset//label['String']");
        assertElementPresent(form + "/fieldset//input[@name='" + fullName + "_0_String']");
        assertElementPresent(form + "/fieldset//label['Boolean']");
        assertElementPresent(form + "/fieldset//select[@name='" + fullName + "_0_Boolean']");
        assertElementPresent(form + "/fieldset/input[@id='" + fullName + "_redirect']");
        // xredirect
        assertElementPresent(form + "/fieldset/input[@value='" + getSelenium().getLocation() + "'][@name='xredirect']");
        // Save button
        // assertElementPresent(form + "/div/p/span/input[@type='submit']");
        // Javascript injects a save button outside of the form and removes the default save button.
        waitForElement("//div/div/p/span/input[@type='submit'][@value='Save']");
        // Should not be here
        assertElementNotPresent(form + "/fieldset//textarea[@name='" + fullName + "_0_TextArea']");
        assertElementNotPresent(form + "/fieldset//select[@name='" + fullName + "_0_Select']");

        // Now we go to where the other half of the configuration should be.
        open("Main", "WebPreferences", "admin", "editor=spaceadmin&section=TestSection2");
        assertElementPresent("//h2[@id='HSomeOtherHeading']/span");
        // Fields
        assertElementPresent(form + "/fieldset//label");
        assertElementPresent(form + "/fieldset//textarea[@name='" + fullName + "_0_TextArea']");
        assertElementPresent(form + "/fieldset//select[@name='" + fullName + "_0_Select']");
        assertElementPresent(form + "/fieldset/input[@id='" + fullName + "_redirect']");
        // xredirect
        assertElementPresent(form + "/fieldset/input[@value='" + getSelenium().getLocation() + "'][@name='xredirect']");
        // Save button
        // assertElementPresent(form + "/div/p/span/input[@type='submit']");
        // Javascript injects a save button outside of the form and removes the default save button.
        waitForElement("//div/div/p/span/input[@type='submit'][@value='Save']");
        // Should not be here
        assertElementNotPresent(form + "/fieldset//input[@name='" + fullName + "_0_String']");
        assertElementNotPresent(form + "/fieldset//select[@name='" + fullName + "_0_Boolean']");
    }

    /*
     * Make sure html macros and pre tags are not being stripped 
     * @see: http://jira.xwiki.org/jira/browse/XAADMINISTRATION-141
     *
     * Tests: XWiki.ConfigurableClass
     */
    public void testNotStrippingHtmlMacros()
    {
        String space = "Main";
        String page = "TestConfigurable";
        String test = "{{html}} <pre> {{html clean=\"false\"}} </pre> {{/html}}";

        String fullName = space + "." + page;
        String form = "//div[@id='admin-page-content']/form[@action='/xwiki/bin/save/" + space + "/" + page + "']";

        createConfigurableApplication(space, page, "TestSection1", true);
        open(space, page, "edit", "editor=object");
        setFieldValue(fullName + "_0_TextArea", test);
        setFieldValue(fullName + "_0_String", test);
        clickEditSaveAndView();

        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=TestSection1");
        waitForTextPresent(form + "/fieldset//textarea[@name='" + fullName + "_0_TextArea']", test);
        // Getting content from an input field required getValue and not getText
        assertTrue(getSelenium().getValue(form + "/fieldset//input[@name='" + fullName + "_0_String']").equals(test));
    }

    /*
     * If a value is specified for linkPrefix, then a link is generated with linkPrefix + prettyName of the property from
     * the configuration class.
     * linkPrefix = "http://www.xwiki.org/bin/view/Main/"
     * property prettyName = "WebHome"
     * generated link should equal "http://www.xwiki.org/bin/view/Main/WebHome"
     *
     * Tests: XWiki.ConfigurableClass
     */
    public void testLabelLinkGeneration()
    {
        String space = "Main";
        String page = "TestConfigurable";
        createConfigurableApplication(space, page, "TestSection3", true);
        open(space, page, "edit", "editor=object");
        setFieldValue("XWiki.ConfigurableClass_0_linkPrefix", "TheLinkPrefix");
        clickEditSaveAndView();

        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=TestSection3");
        assertElementPresent("//form/fieldset//a[@href='TheLinkPrefixString']");
        assertElementPresent("//form/fieldset//a[@href='TheLinkPrefixBoolean']");
        assertElementPresent("//form/fieldset//a[@href='TheLinkPrefixTextArea']");
        assertElementPresent("//form/fieldset//a[@href='TheLinkPrefixSelect']");
    }

    /*
     * Fails unless XWiki.ConfigurableClass locks each page on view and unlocks any other configurable page.
     * Also fails if codeToExecute is not being evaluated.
     *
     * Tests: XWiki.ConfigurableClass
     */
    public void testLockingAndUnlocking()
    {
        String space = "Main";
        String page1 = "TestConfigurable";
        String page2 = "TestConfigurable2";
        String isThisPageLocked = "{{velocity}}Is This Page Locked $doc.getLocked(){{/velocity}}";
        createConfigurableApplication(space, page1, "TestSection4", true);
        createConfigurableApplication(space, page2, "TestSection5", true);
        open(space, page1, "edit", "editor=wiki");
        setFieldValue("content", isThisPageLocked);
        clickEditSaveAndView();
        open(space, page2, "edit", "editor=wiki");
        setFieldValue("content", isThisPageLocked);
        clickEditSaveAndView();

        // Now we go to the documents and see which is locked.
        // Clear any locks by visiting the main page.
        open("XWiki", "XWikiPreferences", "admin");
        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=TestSection4");

        // We have to switch user context without logging out, logging out removes all locks.
        open(space, page1, "view");
        open(getSelenium().getLocation().replaceAll("http://localhost", "http://127.0.0.1"));
        assertTextPresent("Is This Page Locked true");

        open(space, page2, "view");
        open(getSelenium().getLocation().replaceAll("http://localhost", "http://127.0.0.1"));
        assertTextNotPresent("Is This Page Locked true");

        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=TestSection5");

        open(space, page1, "view");
        open(getSelenium().getLocation().replaceAll("http://localhost", "http://127.0.0.1"));
        assertTextNotPresent("Is This Page Locked true");

        open(space, page2, "view");
        open(getSelenium().getLocation().replaceAll("http://localhost", "http://127.0.0.1"));
        assertTextPresent("Is This Page Locked true");
    }

    /*
     * If CodeToExecute is defined in a configurable app, then it should be evaluated.
     * Also header should be evaluated and not just printed.
     * If XWiki.ConfigurableClass is saved with programming rights, it should resave itself so that it doesn't have them.
     */
    public void testCodeToExecutionAndAutoSandboxing()
    {
        String space = "Main";
        String page = "TestConfigurable";
        // Note: We are forced to use the silent notation because Selenium 2.20.0 doesn't escape properly the string
        // passed to the Selenium.type() method and it seems ${...} has a special meaning, throwing an exception with
        // the message "replacement is undefined". Escaping the value using backslash or doubling the { didn't work.
        // See http://code.google.com/p/selenium/issues/detail?id=3510 .
        String codeToExecute = "#set($code = 's sh')"
                             + "Thi$!{code}ould be displayed."
                             + "#if($xcontext.hasProgrammingRights())"
                             + "This should not be displayed."
                             + "#end";
        String heading = "#set($code = 'his sho')"
                       + "T$!{code}uld also be displayed.";
        createConfigurableApplication(space, page, "TestSection6", true);
        open(space, page, "edit", "editor=object");
        setFieldValue("XWiki.ConfigurableClass_0_codeToExecute", codeToExecute);
        setFieldValue("XWiki.ConfigurableClass_0_heading", heading);
        setFieldValue("XWiki.ConfigurableClass_0_configurationClass", "");
        clickEditSaveAndView();

        // Our admin will foolishly save XWiki.ConfigurableClass, giving it programming rights.
        open("XWiki", "ConfigurableClass", "edit", "editor=wiki");

        // Since we modify ConfigurableClass, we must modify it back after to prevent polluting further tests.
        // See the previous note about silent notation to understand why we perform a string replacement.
        String originalContent = getFieldValue("content").replace("${", "$!{");
        try {
            setFieldValue("content", originalContent
                          + "{{velocity}}Has Programming permission: $xcontext.hasProgrammingRights(){{/velocity}}");
            clickEditSaveAndContinue();

            // Now we look at the section for our configurable.
            open("XWiki", "ConfigurableClass", "view", "editor=globaladmin&section=TestSection6");

            assertTextPresent("This should be displayed.");
            assertTextPresent("This should also be displayed.");
            assertTextNotPresent("This should not be displayed.");
            assertTextPresent("Has Programming permission: false");
            // Make sure javascript has not added a Save button.
            assertElementNotPresent("//div/div/p/span/input[@type='submit'][@value='Save']");
        } finally {
            open("XWiki", "ConfigurableClass", "edit", "editor=wiki");
            setFieldValue("content", originalContent);
            clickEditSaveAndContinue();
        }
    }

    /*
     * Proves that ConfigurationClass#codeToExecute is not rendered inline even if there is no
     * custom configuration class and the on;y content is custom content.
     * Tests: XWiki.ConfigurableClass
     */
    public void testCodeToExecuteNotInlineIfNoConfigurationClass()
    {
        String space = "Main";
        String page = "TestConfigurable";
        String test = "{{html}} <div> <p> hello </p> </div> {{/html}}";

        open(space, page, "delete", "confirm=1");
        createConfigurableApplication(space, page, "TestSection1", true);
        open(space, page, "edit", "editor=object");
        setFieldValue("XWiki.ConfigurableClass_0_configurationClass", "");
        setFieldValue("XWiki.ConfigurableClass_0_codeToExecute", test);
        clickEditSaveAndView();

        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=TestSection1");
        assertElementNotPresent("//span[@class='xwikirenderingerror']");
    }

    /*
     * Proves that ConfigurationClass#codeToExecute is not rendered inline whether it's at the top of the
     * form or inside of the form.
     * Tests: XWiki.ConfigurableClass
     */
    public void testCodeToExecuteNotInline()
    {
        String space = "Main";
        String page = "TestConfigurable";
        String test = "{{html}} <div> <p> hello </p> </div> {{/html}}";

        createConfigurableApplication(space, page, "TestSection1", true);
        open(space, page, "edit", "editor=object");
        setFieldValue("classname", "XWiki.ConfigurableClass");
        clickButtonAndContinue("//input[@name='action_objectadd']");
        setFieldValue("XWiki.ConfigurableClass_0_codeToExecute", test);
        setFieldValue("XWiki.ConfigurableClass_0_propertiesToShow", "String, Boolean");

        setFieldValue("XWiki.ConfigurableClass_1_displayInSection", "TestSection1");
        setFieldValue("XWiki.ConfigurableClass_1_configurationClass", space + "." + page);
        setFieldValue("XWiki.ConfigurableClass_1_propertiesToShow", "TextArea, Select");
        setFieldValue("XWiki.ConfigurableClass_1_codeToExecute", test);
        getSelenium().check("XWiki.ConfigurableClass_1_configureGlobally");
        clickEditSaveAndView();

        open("XWiki", "XWikiPreferences", "admin", "editor=globaladmin&section=TestSection1");
        assertElementNotPresent("//span[@class='xwikirenderingerror']");
    }

    /**
     * Test functionality of the ForgotUsername page:
     * <ul>
     * <li>A user can be found using correct email</li>
     * <li>No user is found using wrong email</li>
     * <li>Email text is properly escaped</li>
     * </ul>
     */
    public void testForgotUsername()
    {
        String space = "Test";
        String page = "SQLTestPage";
        String mail = "webmaster@xwiki.org"; // default Admin mail
        String user = "Admin";
        String badMail = "bad_mail@evil.com";

        // Ensure there is a page we will try to find using HQL injection
        editInWikiEditor(space, page);
        setFieldValue("title", page);
        setFieldValue("content", page);
        clickEditSaveAndView();

        // test that it finds the correct user
        open("XWiki", "ForgotUsername");
        setFieldValue("e", mail);
        submit("//input[@type='submit']"); // there are no other buttons
        assertTextNotPresent("No account is registered using this email address");
        assertElementPresent("//div[@id='xwikicontent']//strong[text()='" + user + "']");

        // test that bad mail results in no results
        open("XWiki", "ForgotUsername");
        setFieldValue("e", badMail);
        submit("//input[@type='submit']"); // there are no other buttons
        assertTextPresent("No account is registered using this email address");
        assertElementNotPresent("//div[@id='xwikicontent']//strong[@value='" + user + "']");

        // XWIKI-4920 test that the email is properly escaped
        open("XWiki", "ForgotUsername");
        setFieldValue("e", "a' synta\\'x error");
        submit("//input[@type='submit']"); // there are no other buttons
        assertTextPresent("No account is registered using this email address");
        assertTextNotPresent("Error");
    }

    /**
     * Asserts that a menu item with the given label is present on the administration menu.
     */
    public boolean isAdminMenuItemPresent(String label)
    {
        return isElementPresent("//*[contains(@class, 'admin-menu')]//a[. = '" + label + "']");
    }

    /*
     * Fails if there is an administration icon for the named section.
     * Must be in the administration app first.
     * Tests: XWiki.ConfigurableClass
     */
    public void assertConfigurationIconNotPresent(String section)
    {
        assertElementNotPresent("//div[contains(@class,'admin-menu')]//li[contains(@href,'section=" + section + "')]");
    }

    /**
     * Will fail unless it detects a configuration of the type created by createConfigurableApplication.<br/>
     * Tests: XWiki.ConfigurableClass
     */
    public void assertConfigurationPresent(String space, String page)
    {
        assertElementPresent("//div[@id='admin-page-content']/h2[@id='HSomeHeading']/span");
        // Fields
        String fullName = space + "." + page;
        String form = "//div[@id='admin-page-content']/form[@action='/xwiki/bin/save/" + space + "/" + page + "']";
        assertElementPresent(form + "/fieldset/dl/dt[1]/label");
        assertElementPresent(form + "/fieldset/dl/dd[1]/input[@name='" + fullName + "_0_String']");
        assertElementPresent(form + "/fieldset/dl/dt[2]/label");
        assertElementPresent(form + "/fieldset/dl/dd[2]/select[@name='" + fullName + "_0_Boolean']");
        assertElementPresent(form + "/fieldset/dl/dt[3]/label");
        assertElementPresent(form + "/fieldset/dl/dd[3]/textarea[@name='" + fullName + "_0_TextArea']");
        assertElementPresent(form + "/fieldset/dl/dt[4]/label");
        assertElementPresent(form + "/fieldset/dl/dd[4]/select[@name='" + fullName + "_0_Select']");
        assertElementPresent(form + "/fieldset/input[@id='" + fullName + "_redirect']");
        assertElementPresent(form + "/fieldset/input[@value='" + getSelenium().getLocation() + "'][@name='xredirect']");
        // JavaScript injects a save button outside of the form and removes the default save button.
        waitForElement("//*[@class = 'admin-buttons']//input[@type = 'submit' and @value = 'Save']");
    }

    /*
     * Will fail if it detects a configuration of the type created by createConfigurableApplication.
     * Tests: XWiki.ConfigurableClass
     */
    public void assertConfigurationNotPresent(String space, String page)
    {
        assertElementNotPresent("//div[@id='admin-page-content']/h1[@id='HCustomize" + space + "." + page + ":']/span");
        assertElementNotPresent("//div[@id='admin-page-content']/h2[@id='HSomeHeading']/span");
        assertConfigurationNotEditable(space, page);
    }

    public void assertConfigurationNotEditable(String space, String page)
    {
        assertElementNotPresent("//div[@id='admin-page-content']/form[@action='/xwiki/bin/save/"
                                + space + "/" + page + "']");
    }

    /**
     * Creates a new page with a configuration class with some simple fields<br/>
     * then adds an object of class configurable and one of it's own class.<br/>
     * Tests: XWiki.ConfigurableClass
     */
    public void createConfigurableApplication(String space, String page, String section, boolean global)
    {
        // We have to use an existing space because the copy page form doesn't allow entering a new space.
        String storageSpace = "Sandbox";
        String storagePage = "CreateConfigurableApplication";

        if (!tryToCopyPage(storageSpace, storagePage, space, page)) {
            // Create the page with a simple configuration class.
            createPage(space, page, "Test configurable application.", "xwiki/2.0");
            open(space, page, "edit", "editor=class");
            setFieldValue("propname", "String");
            setFieldValue("proptype", "com.xpn.xwiki.objects.classes.StringClass");
            clickButtonAndContinue("//input[@name='action_propadd']");
            setFieldValue("propname", "Boolean");
            setFieldValue("proptype", "com.xpn.xwiki.objects.classes.BooleanClass");
            clickButtonAndContinue("//input[@name='action_propadd']");
            setFieldValue("propname", "TextArea");
            setFieldValue("proptype", "com.xpn.xwiki.objects.classes.TextAreaClass");
            clickButtonAndContinue("//input[@name='action_propadd']");
            setFieldValue("propname", "Select");
            setFieldValue("proptype", "com.xpn.xwiki.objects.classes.StaticListClass");
            clickButtonAndContinue("//input[@name='action_propadd']");

            // Go to the object section.
            open(space, page, "edit", "editor=object");

            // Add a configurable object which points to the new class as the configuration class.
            setFieldValue("classname", "XWiki.ConfigurableClass");
            clickButtonAndContinue("//input[@name='action_objectadd']");
            clickEditSaveAndView();

            // Try to place it in the storage area.
            tryToCopyPage(space, page, storageSpace, storagePage);
        }

        // Go to the object section.
        open(space, page, "edit", "editor=object");

        // Add an object of the new class.
        setFieldValue("classname", space + "." + page);
        clickButtonAndContinue("//input[@name='action_objectadd']");

        setFieldValue("XWiki.ConfigurableClass_0_displayInSection", section);
        setFieldValue("XWiki.ConfigurableClass_0_heading", "Some Heading");
        setFieldValue("XWiki.ConfigurableClass_0_configurationClass", space + "." + page);
        if (global == true) {
            getSelenium().check("XWiki.ConfigurableClass_0_configureGlobally");
        } else {
            getSelenium().uncheck("XWiki.ConfigurableClass_0_configureGlobally");
        }
        // We won't set linkPrefix, propertiesToShow, codeToExecute, or iconAttachment.

        clickEditSaveAndView();
    }

    /**
     * This is used by createConfigurableApplication to store a copy of the default configurable to speed up making
     * them.
     */
    public boolean tryToCopyPage(String fromSpace, String fromPage, String toSpace, String toPage)
    {
        open(fromSpace, fromPage);
        if (!isExistingPage()) {
            return false;
        }
        return copyPage(fromSpace, fromPage, toSpace, toPage);
    }
}
