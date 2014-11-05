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

import org.openqa.selenium.By;
import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.FlamingoSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

public class PanelWizardTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Test the XWiki Panel Wizard");
        suite.addTestSuite(PanelWizardTest.class, FlamingoSkinExecutor.class);
        return suite;
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        open("Panels", "PanelWizard");
    }

    /**
     * This method makes the following tests:
     * <ul>
     * <li>Opens the Wizard Panels page for XWiki instance.</li>
     * <li>Checks for existence of 2 sections.</li>
     * </ul>
     */
    public void testSections()
    {
        assertElementPresent("//a[text()='Page Layout']");
        assertElementPresent("//a[text()='Panel List']");
    }

    /**
     * This method makes the following tests :
     * <ul>
     * <li>Opens the Wizard Panels page for XWiki instance.</li>
     * <li>Opens and test for all 4 layouts for page.</li>
     * </ul>
     */
    public void testPageLayout()
    {
        clickLinkWithXPath("//a[@href='#PageLayoutSection']", false);
        // tests the page layouts
        clickLinkWithXPath("//div[@id='nosidecolumn']", false);
        waitForCondition("selenium.isElementPresent(\"//div[@id='rightPanels' and @style='display: none;']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='leftPanels' and @style='display: none;']\")!=false;");
        assertElementPresent("//div[@id='rightPanels' and @style='display: none;']");
        assertElementPresent("//div[@id='leftPanels' and @style='display: none;']");
        clickLinkWithXPath("//div[@id='leftcolumn']", false);
        waitForCondition("selenium.isElementPresent(\"//div[@id='rightPanels' and @style='display: none;']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='leftPanels' and @style='display: block;']\")!=false;");
        assertElementPresent("//div[@id='rightPanels' and @style='display: none;']");
        assertElementPresent("//div[@id='leftPanels' and @style='display: block;']");
        clickLinkWithXPath("//div[@id='rightcolumn']", false);
        waitForCondition("selenium.isElementPresent(\"//div[@id='rightPanels' and @style='display: block;']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='leftPanels' and @style='display: none;']\")!=false;");
        assertElementPresent("//div[@id='rightPanels' and @style='display: block;']");
        assertElementPresent("//div[@id='leftPanels' and @style='display: none;']");
        clickLinkWithXPath("//div[@id='bothcolumns']", false);
        waitForCondition("selenium.isElementPresent(\"//div[@id='rightPanels' and @style='display: block;']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='leftPanels' and @style='display: block;']\")!=false;");
        assertElementPresent("//div[@id='rightPanels' and @style='display: block;']");
        assertElementPresent("//div[@id='leftPanels' and @style='display: block;']");
    }

    /**
     * This method makes the following tests :
     * <ul>
     * <li>Opens the Wizard Panels page for XWiki instance.</li>
     * <li>Selects 'bothcolums' layout.</li>
     * <li>Then puts QuickLinks panel on the left side.</li>
     * </ul>
     */
    public void testInsertQuickLinksPanelInLeftColumn()
    {
        clickLinkWithXPath("//a[@href='#PageLayoutSection']", false);
        waitForCondition("selenium.isElementPresent(\"//div[@id='rightcolumn']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='bothcolumns']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='leftcolumn']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='nosidecolumn']\")!=false;");
        clickLinkWithXPath("//div[@id='rightcolumn']", false);
        clickLinkWithXPath("//div[@id='bothcolumns']", false);
        waitForBodyContains("Page Layout");
        waitForBodyContains("Panel List");
        clickLinkWithXPath("//a[@href='#PanelListSection']", false);
        dragAndDrop(By.xpath("//div[@class='panel expanded QuickLinks']"), By.id("leftPanels"));
        dragAndDrop(By.xpath("//div[@class='panel expanded CategoriesPanel']"), By.id("rightPanels"));
        clickLinkWithXPath("//button[text()='Save the new layout']", false);
        waitForAlert();
        assertEquals("The layout has been saved properly.", getSelenium().getAlert());
        open("Panels", "PanelWizard");
        waitForCondition("selenium.isElementPresent(\"//div[@class='panel expanded QuickLinks']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@class='panel expanded Backlinks']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"leftPanels\")!=false;");
        waitForCondition("selenium.isElementPresent(\"rightPanels\")!=false;");
        assertElementPresent("//div[@class='panel expanded QuickLinks']");
        assertElementPresent("//div[@class='panel expanded CategoriesPanel']");
        assertElementPresent("leftPanels");
        assertElementPresent("rightPanels");
    }

    /**
     * This method makes the following tests :
     * <ul>
     * <li>Opens the Wizard Panels page for XWiki instance.</li>
     * <li>Test all 3 buttons.</li>
     * </ul>
     */
    public void testButtons()
    {
        // test button 'Go to Panels home page'
        waitForBodyContains("Go to Panels home page");
        assertElementPresent("//a[text()='Page Layout']");
        assertElementPresent("//a[text()='Panel List']");
        clickLinkWithText("Go to Panels home page");

        // test button 'Revert'
        open("Panels", "PanelWizard");
        clickLinkWithXPath("//a[@href='#PageLayoutSection']", false);
        waitForCondition("selenium.isElementPresent(\"//div[@id='rightcolumn']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='bothcolumns']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='leftcolumn']\")!=false;");
        waitForCondition("selenium.isElementPresent(\"//div[@id='nosidecolumn']\")!=false;");
        clickLinkWithXPath("//div[@id='leftcolumn']", false);
        clickLinkWithXPath("//a[@href='#PanelListSection']", false);
        waitForCondition("selenium.isElementPresent(\"//button[text()='Revert']\")!=false;");
        dragAndDrop(By.xpath("//div[@class = 'panel expanded QuickLinks']"), By.id("leftPanels"));
        getSelenium().click("revertLayout");

        // test button 'Save the new layout'
        waitForCondition("selenium.isElementPresent(\"//a[text()='Panel Wizard']\")!=false;");
        clickLinkWithXPath("//a[text()='Panel Wizard']", true);
        waitForBodyContains("Page Layout");
        waitForBodyContains("Panel List");
        assertElementPresent("//a[text()='Page Layout']");
        assertElementPresent("//a[text()='Panel List']");
        waitForCondition("selenium.isElementPresent(\"//button[text()='Save the new layout']\")!=false;");
        clickLinkWithXPath("//button[text()='Save the new layout']", false);
        waitForAlert();
        assertEquals("The layout has been saved properly.", getSelenium().getAlert());
    }
}
