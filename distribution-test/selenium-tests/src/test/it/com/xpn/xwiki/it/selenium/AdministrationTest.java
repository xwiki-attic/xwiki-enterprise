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

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;
import junit.framework.Test;

/**
 * Verify the overall Administration application features.
 *
 * @version $Id: $
 */
public class AdministrationTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the Administration application features.");
        suite.addTestSuite(AdministrationTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

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
     * <li>Validate presence of default sections for global and space
     * sections.</li>
     * <li>Validate presence of application administration sections at global level only.</li>
     * </ul>
     */
    public void testGlobalAndSpaceSections()
    {
        clickLinkWithText("Administrate wiki");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='General']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Presentation']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Registration']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Programming']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Rights']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Users']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Groups']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Import']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Export']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Panels']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Blog']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Photos']");

        // select space administration
        getSelenium().select("goto-select", "label=Main");
        waitPage();
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Presentation']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Rights']");
        assertElementPresent("//ul[@id='admin-icons']/li[@class='Panels']");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='General']");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='Registration']");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='Programming']");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='Users']");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='Groups']");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='Import']");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='Export']");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='Blog']");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='Photos']");
    }

    /*
     * Test to see an application page is included only if that application exists
     */
    public void testApplicationSection()
    {
        // Delete the Photos.Links page and test it's not present in the admin global menu anymore
        deletePage("Photos", "Links");
        clickLinkWithText("Administrate wiki");
        assertElementNotPresent("//ul[@id='admin-icons']/li[@class='Photos']");
    }

    /*
     * Test modifying XWiki.XWikiPreferences multi-language field and save it.
     */
    public void testSettingXWikiPreferences()
    {
        getSelenium().select("goto-select", "label=Wiki administration");
        getSelenium().click("//span[text()='General']");
        waitPage();
        getSelenium().select("//select[@name='XWiki.XWikiPreferences_0_multilingual']", "label=Yes");
        getSelenium().click("//input[@value='Save']");
        waitPage();
        assertElementPresent("//span[@id='headerlanguages']");
    }

    /*
     * Test adding a new category in Blog Categories
     */
    public void testBlogAdmin()
    {
        // select global administration
        getSelenium().select("goto-select", "label=Wiki administration");
        clickLinkWithLocator("//span[text()='General']", true);
        getSelenium().select("//select[@id='XWiki.XWikiPreferences_0_editor']", "label=Text");
        clickLinkWithLocator("//input[@value='Save']");
        assertElementPresent("//span[@id='showsectionswrapper']");
        getSelenium().click("//a[@id='showsections']");
        assertElementPresent("//span[@id='hidesectionswrapper']");
        clickLinkWithLocator("//span[text()='Blog categories']");
        clickLinkWithText("Add a category", true);
        setFieldValue("Blog.Categories_3_name", "New Category");
        getSelenium().type("Blog.Categories_3_description", "New Category Content");
        clickLinkWithLocator("//input[@value='Save & View']", true);
        assertElementPresent("//td[text()='New Category']");
        assertElementPresent("//td[text()='New Category Content']");
    }

    /*
     * Test Panel Wizard
     */
    public void testPanelsAdmin()
    {
        //test panel wizard at global level
        getSelenium().select("goto-select", "label=Wiki administration");
        clickLinkWithLocator("//span[text()='Panel Wizard']");
        getSelenium().click("//a[@href='#PageLayoutSection']");
        getSelenium().click("//div[@id='nosidecolumn']");
        getSelenium().click("//a[text()='Save the new layout']");
        waitForCondition("selenium.isAlertPresent()");
        assertEquals("The layout has been saved properly.", getSelenium().getAlert());
        open("Main", "WebHome");
        assertElementNotPresent("leftPanels");
        assertElementNotPresent("rightPanels");

        //test panel wizard at space level
        open("Main", "WebPreferences", "admin");
        clickLinkWithLocator("//span[text()='Panel Wizard']");
        getSelenium().click("//a[@href='#PageLayoutSection']");
        getSelenium().click("//div[@id='leftcolumn']");
        getSelenium().click("//a[@href='#PanelListSection']");
        getSelenium().dragAndDropToObject("//div[@class='panel expanded QuickLinks']", "//div[@id='leftPanels']");
        getSelenium().click("//a[text()='Save the new layout']");
        waitForCondition("selenium.isAlertPresent()");
        assertEquals("The layout has been saved properly.", getSelenium().getAlert());
        open("Main", "WebHome");
        assertElementPresent("leftPanels");
        assertElementPresent("//div[@class='panel expanded QuickLinks']");
        open("XWiki", "WebHome");
        assertElementNotPresent("leftPanels");
        assertElementNotPresent("//div[@class='panel expanded QuickLinks']");
    }
}
