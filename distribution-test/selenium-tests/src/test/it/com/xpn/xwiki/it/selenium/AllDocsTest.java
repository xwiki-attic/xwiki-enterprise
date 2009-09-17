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

import junit.framework.Assert;
import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.ColibriSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the table view for AllDocs wiki document.
 * 
 * @version $Id$
 */
public class AllDocsTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the table view for AllDocs wiki document");
        suite.addTestSuite(AllDocsTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    private void fillTableFilter(String field, String text)
    {
        getSelenium().typeKeys(field, text);
        // setSpeed is used to avoid the error :
        // selenium.browserbot.getCurrentWindow().document.getElementById("ajax-loader") has no properties
        // which is not prevented by the following waitForCondition.
        try {
            getSelenium().setSpeed("200");
            waitForCondition(DOC + "getElementById(\"alldocs-ajax-loader\") != null");
            waitForCondition("typeof " + DOC + "getElementById(\"alldocs-ajax-loader\").style.display == 'string'");
            waitForCondition(DOC + "getElementById(\"alldocs-ajax-loader\").style.display == \"none\"");
        } finally {
            getSelenium().setSpeed("0");
        }
    }

    /**
     * This method makes the following tests :
     * <ul>
     * <li>Validate presence of "Actions" column in table view for administrator.</li>
     * <li>Validate absence of "Actions" column for users without administration rights.</li>
     * <li>Validate input suggest for Page field.</li>
     * <li>Validate input suggest for Space field.</li>
     * <li>Validate input suggest for Last Author field.</li>
     * <li>Validate Copy link action.</li>
     * <li>Validate Rename link action.</li>
     * <li>Validate Delete link action.</li>
     * <li>Validate Rights link action.</li>
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
        assertElementPresent("//th[normalize-space(text())='Actions']");

        // Validate input suggest for Page field.
        fillTableFilter("xpath=//input[@name='doc.name']", "Treeview");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='Treeview']");

        // Validate input suggest for Space field.
        open("Main", "AllDocs");
        fillTableFilter("xpath=//input[@name='doc.space']", "XWiki");
        fillTableFilter("xpath=//input[@name='doc.name']", "treeview");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='Treeview']");

        // Validate input suggest for Last Author field.
        open("Main", "AllDocs");
        fillTableFilter("xpath=//input[@name='doc.author']", "SomeUnknownAuthor");
        assertElementNotPresent("//td[contains(@class, 'doc_name')]/a[text()='Treeview']");

        // Validate Copy link action.
        open("Main", "AllDocs");
        fillTableFilter("xpath=//input[@name='doc.name']", "treeview");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='Treeview']");
        clickLinkWithText("copy");
        setFieldValue("targetdoc", "New.TreeviewCopy");
        getSelenium().click("//input[@value='Copy']");
        open("Main", "AllDocs");
        fillTableFilter("xpath=//input[@name='doc.space']", "New");
        fillTableFilter("xpath=//input[@name='doc.name']", "treeviewcopy");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='TreeviewCopy']");

        // Validate Rename link action.
        open("Main", "AllDocs");
        fillTableFilter("xpath=//input[@name='doc.name']", "TreeviewCopy");
        clickLinkWithLocator("//tbody/tr/td/a[text()='rename']");
        setFieldValue("newPageName", "TreeviewCopyRenamed");
        clickLinkWithLocator("//input[@value='Rename']");
        open("Main", "AllDocs");
        fillTableFilter("xpath=//input[@name='doc.name']", "TreeviewCopyRenamed");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='TreeviewCopyRenamed']");

        // Validate Delete link action.
        open("Main", "AllDocs");
        fillTableFilter("xpath=//input[@name='doc.name']", "Treeviewcopyrenamed");
        clickLinkWithLocator("//tbody/tr/td/a[text()='delete']");
        clickLinkWithLocator("//input[@value='yes']");
        assertTextPresent("The document has been deleted.");
        open("Main", "AllDocs");
        fillTableFilter("xpath=//input[@name='doc.name']", "treeview");
        assertElementNotPresent("//td[contains(@class, 'doc_name')]/a[text()='TreeviewCopyRenamed']");

        // Validate Rights link action.
        open("Main", "AllDocs");
        fillTableFilter("xpath=//input[@name='doc.name']", "Treeview");
        clickLinkWithLocator("//tbody/tr/td/a[text()='rights']");
        Assert.assertEquals("Editing Rights for Tree", getTitle());
    }

    /**
     * Click on a node in the Treeview widget.
     * 
     * @param nodeId Id of the node to be clicked.
     */
    private void clickOnNode(String nodeId)
    {

        getSelenium().click("//img[@name='" + nodeId + "']");
    }

    /**
     * Wait for the node with the given ID to load.
     * 
     * @param nodeId Id of the node to wait for.
     */
    private void waitForNodeToLoad(String nodeId)
    {
        waitForCondition("typeof selenium.browserbot.getCurrentWindow().Treeview.data.findById('" + nodeId
            + "') != 'undefined'");
    }

    /**
     * Validate that space nodes are loaded by the Treeview widget.
     */
    public void testTreeViewInit()
    {
        open("Main", "AllDocs", "view", "view=tree");

        // Wait for the widget to load.
        waitForCondition("typeof selenium.browserbot.getCurrentWindow().Treeview != 'undefined'");

        // Wait for the data to arrive.
        waitForNodeToLoad("xwiki:Blog");
        waitForNodeToLoad("xwiki:Main");
        waitForNodeToLoad("xwiki:Panels");
        waitForNodeToLoad("xwiki:Sandbox");
        waitForNodeToLoad("xwiki:Scheduler");
        waitForNodeToLoad("xwiki:Stats");
        waitForNodeToLoad("xwiki:XWiki");

        // We can't use Selenium to generate events (clicks and keys) for Smartclient widgets.
        // See this thread for more details: http://forums.smartclient.com/showthread.php?t=2312
    }

    /**
     * Validate that the suggest allow to open a node further levels down the tree.
     */
    public void testTreeViewSuggest()
    {
        open("Main", "AllDocs", "view", "view=tree");

        // Wait for the widget to load.
        waitForCondition("typeof selenium.browserbot.getCurrentWindow().Treeview != 'undefined'");
        waitForNodeToLoad("xwiki:XWiki");

        setFieldValue("Treeview_Input", "Main.RecentChanges");
        waitForNodeToLoad("xwiki:Main.RecentChanges");
    }

    /**
     * Validate Treeview API.
     */
    public void testTreeViewAPI()
    {
        open("Main", "AllDocs", "view", "view=tree");

        // Wait for the widget to load.
        waitForCondition("typeof selenium.browserbot.getCurrentWindow().Treeview != 'undefined'");
        waitForNodeToLoad("xwiki:Main");

        setFieldValue("Treeview_Input", "Main.RecentChanges@lquo.gif");
        waitForNodeToLoad("xwiki:Main.RecentChanges@lquo.gif");

        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('wiki') " +
        		"== 'xwiki'");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('space') " +
        		"== 'Main'");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('name') " +
        		"== 'RecentChanges'");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('attachment') " +
        		"== 'lquo.gif'");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('anchor') == ''");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getValue() == 'Main.RecentChanges@lquo.gif'");
    }
}
