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
import junit.framework.Assert;
import junit.framework.Test;

/**
 * Verify the table view for AllDocs wiki document.
 *
 * @version $Id: $
 */
public class AllDocsTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite =
            new XWikiTestSuite("Verify the table view for AllDocs wiki document");
        suite.addTestSuite(AllDocsTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    private void fillTableFilter(String field, String text)
    {
        getSelenium().typeKeys(field, text);
        // setSpeed is used to avoid the error :
        // selenium.browserbot.getCurrentWindow().document.getElementById("ajax-loader") has no properties
        // which is not prevented by the following waitForCondition.
        getSelenium().setSpeed("200");
        waitForCondition("selenium.browserbot.getCurrentWindow().document." +
            "getElementById(\"ajax-loader\") != null");
        waitForCondition("typeof selenium.browserbot.getCurrentWindow().document." +
            "getElementById(\"ajax-loader\").style.display == 'string'");
        waitForCondition("selenium.browserbot.getCurrentWindow().document." +
            "getElementById(\"ajax-loader\").style.display == \"none\"");
        getSelenium().setSpeed("0");
    }

    /**
     * This method makes the following tests :
     *
     * <ul>
     *   <li>Validate presence of "Actions" column in table view for administrator.</li>
     *   <li>Validate absence of "Actions" column for users without administration rights.</li>
     *   <li>Validate input suggest for Page field.</li>
     *   <li>Validate input suggest for Space field.</li>
     *   <li>Validate input suggest for Last Author field.</li>
     *   <li>Validate Copy link action.</li>
     *   <li>Validate Rename link action.</li>
     *   <li>Validate Delete link action.</li>
     *   <li>Validate Rights link action.</li>
     * </ul>
     */
    public void testTableViewActions()
    {
        // Validate absence of "Actions" column for users without administration rights.
        open("Main", "AllDocs");
        if (isAuthenticated()) {
            logout();
        }
        assertElementNotPresent("//td[text()='Actions']");

        // Validate presence of "Actions" column in table view for administrator.
        loginAsAdmin();
        open("Main", "AllDocs");
        assertElementPresent("//td[text()='Actions']");

        // Validate input suggest for Page field.
        fillTableFilter("page", "Treeview");
        assertElementPresent("//td[@class='pagename']/a[text()='Treeview']");

        // Validate input suggest for Space field.
        open("Main", "AllDocs");
        fillTableFilter("space", "XWiki");
        fillTableFilter("page", "treeview");
        assertElementPresent("//td[@class='pagename']/a[text()='Treeview']");

        // Validate input suggest for Last Author field.
        open("Main", "AllDocs");
        fillTableFilter("author", "SomeUnknownAuthor");
        assertElementNotPresent("//td[@class='pagename']/a[text()='Treeview']");

        // Validate Copy link action.
        open("Main", "AllDocs");
        fillTableFilter("page", "treeview");
        assertElementPresent("//td[@class='pagename']/a[text()='Treeview']");
        clickLinkWithText("Copy");
        setFieldValue("targetdoc", "New.TreeviewCopy");
        getSelenium().click("//input[@value='Copy']");
        open("Main", "AllDocs");
        fillTableFilter("space", "New");
        fillTableFilter("page", "treeviewcopy");
        assertElementPresent("//td[@class='pagename']/a[text()='TreeviewCopy']");

        // Validate Rename link action.
        open("Main", "AllDocs");
        fillTableFilter("page", "TreeviewCopy");
        clickLinkWithLocator("//tbody/tr/td/a[text()='Rename']");
        setFieldValue("newPageName", "TreeviewCopyRenamed");
        clickLinkWithLocator("//input[@value='Rename']");
        open("Main", "AllDocs");
        fillTableFilter("page", "TreeviewCopyRenamed");
        assertElementPresent("//td[@class='pagename']/a[text()='TreeviewCopyRenamed']");

        // Validate Delete link action.
        open("Main", "AllDocs");
        fillTableFilter("page", "Treeviewcopyrenamed");
        clickLinkWithLocator("//tbody/tr/td/a[text()='Delete']");
        clickLinkWithLocator("//input[@value='yes']");
        assertTextPresent("The document has been deleted.");
        open("Main", "AllDocs");
        fillTableFilter("page", "treeview");
        assertElementNotPresent("//td[@class='pagename']/a[text()='TreeviewCopyRenamed']");

        // Validate Rights link action.
        open("Main", "AllDocs");
        fillTableFilter("page", "Treeview");
        clickLinkWithLocator("//tbody/tr/td/a[text()='Rights']");
        Assert.assertEquals("Editing Rights for Treeview", getTitle());
    }

    private void assertNodeOpen(String nodeName, int type)
    {
    	String className = "";
    	/* 1 = interior node without children
    	 * 2 = first node without children
    	 * 3 = node with children
    	 */
    	switch(type)
    	{
    		case 1: className = "ygtvtn"; break;
    		case 2: className = "ygtvln"; break;
    		case 3: className = "ygtvtm"; break;
    	}
    	String xpath = "//a[text()='" + nodeName + "']/ancestor::*[position()=1]/preceding-sibling::*[position()=1]";
    	getSelenium().click(xpath);
    	waitForCondition("selenium.browserbot.findElement(\"" + xpath + "\").className == '" + className + "'");
    }

    /**
     * <ul><li>Validate that multilevel nodes open correctly.</li></ul>
     */
    public void testTreeViewActions()
    {
    	open("Main", "AllDocs", "view", "view=tree");
    	// Verify two-level nodes
    	assertNodeOpen("Main", 3);
    	assertNodeOpen("Dashboard", 1);
    	// Verify three-level nodes
    	assertNodeOpen("Stats", 3);
    	assertNodeOpen("Activity", 3);
    	assertNodeOpen("ActivityData", 2);
    	// Verify four-level nodes
    	// assertNodeOpen("Main", 3); //Main was open first
    	assertNodeOpen("AllDocs", 3);
    	assertNodeOpen("Tableview", 3);
    	assertNodeOpen("Tableresults", 2);
    }
}
