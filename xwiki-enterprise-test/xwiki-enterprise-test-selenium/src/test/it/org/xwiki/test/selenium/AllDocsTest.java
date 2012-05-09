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

import junit.framework.Assert;
import junit.framework.Test;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

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
        // Validate absence of "Actions" column for users without administration rights and verify there are
        // elements in the table
        open("Main", "AllDocs");
        // We verify we have a least 3 pages displayed
        waitForTextContains("//span[@class='xwiki-livetable-pagination-content']", "1 2 3");
        if (isAuthenticated()) {
            logout();
        }
        assertElementNotPresent("//td[text()='Actions']");

        // Validate presence of "Actions" column in table view for administrator.
        loginAsAdmin();
        open("Main", "AllDocs");
        waitForTextContains("//span[@class='xwiki-livetable-pagination-content']", "1 2 3");
        assertElementPresent("//th[normalize-space(text())='Actions']");

        // Validate input suggest for Page field.
        getSelenium().focus("doc.name");
        getSelenium().typeKeys("doc.name", "Treeview");
        // Note: We wait on the pagination result since it's the last element updated and it's done after the
        // table rows have been updated so this allows us to wait on it. In the code below "1" represents the
        // displayed pages.
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "1");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='Treeview']");

        // Validate input suggest for Space field.
        open("Main", "AllDocs");
        waitForTextContains("//span[@class='xwiki-livetable-pagination-content']", "1 2 3");
        getSelenium().type("xpath=//input[@name='doc.space']", "XWiki");
        getSelenium().focus("doc.name");
        getSelenium().typeKeys("doc.name", "treeview");
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "1");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='Treeview']");

        // Validate input suggest for Last Author field.
        open("Main", "AllDocs");
        waitForTextContains("//span[@class='xwiki-livetable-pagination-content']", "1 2 3");
        getSelenium().focus("doc.author");
        getSelenium().typeKeys("doc.author", "SomeUnknownAuthor");
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "");
        assertElementNotPresent("//td[contains(@class, 'doc_name')]/a[text()='Treeview']");

        // Validate Copy link action.
        open("Main", "AllDocs");
        waitForTextContains("//span[@class='xwiki-livetable-pagination-content']", "1 2 3");
        getSelenium().focus("doc.name");
        getSelenium().typeKeys("doc.name", "treeview");
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "1");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='Treeview']");
        clickLinkWithText("copy");
        // The copy page form doesn't allow us to copy to a new space.
        setFieldValue("targetSpaceName", "Sandbox");
        setFieldValue("targetPageName", "TreeviewNew");
        clickLinkWithLocator("//input[@value='Copy']");
        open("Main", "AllDocs");
        getSelenium().focus("doc.space");
        getSelenium().typeKeys("doc.space", "Sandbox");
        getSelenium().focus("doc.name");
        getSelenium().typeKeys("doc.name", "treeviewnew");
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "1");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='TreeviewNew']");

        // Validate Rename link action.
        open("Main", "AllDocs");
        waitForTextContains("//span[@class='xwiki-livetable-pagination-content']", "1 2 3");
        getSelenium().focus("doc.name");
        getSelenium().typeKeys("doc.name", "TreeviewNew");
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "1");
        clickLinkWithLocator("//tbody/tr/td/a[text()='rename']");
        setFieldValue("newPageName", "TreeviewNewRenamed");
        clickLinkWithLocator("//input[@value='Rename']");
        open("Main", "AllDocs");
        getSelenium().focus("doc.name");
        getSelenium().typeKeys("doc.name", "TreeviewNewRenamed");
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "1");
        assertElementPresent("//td[contains(@class, 'doc_name')]/a[text()='TreeviewNewRenamed']");

        // Validate Delete link action.
        open("Main", "AllDocs");
        waitForTextContains("//span[@class='xwiki-livetable-pagination-content']", "1 2 3");
        getSelenium().focus("doc.name");
        getSelenium().typeKeys("doc.name", "Treeviewnewrenamed");
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "1");
        clickLinkWithLocator("//tbody/tr/td/a[text()='delete']");
        clickLinkWithLocator("//input[@value='yes']");
        assertTextPresent("The document has been deleted.");
        open("Main", "AllDocs");
        getSelenium().focus("doc.name");
        getSelenium().typeKeys("doc.name", "treeview");
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "1");
        assertElementNotPresent("//td[contains(@class, 'doc_name')]/a[text()='TreeviewNewRenamed']");

        // Validate Rights link action.
        open("Main", "AllDocs");
        waitForTextContains("//span[@class='xwiki-livetable-pagination-content']", "1 2 3");
        getSelenium().focus("doc.name");
        getSelenium().typeKeys("doc.name", "Treeview");
        waitForTextPresent("//span[@class='xwiki-livetable-pagination-content']", "1");
        clickLinkWithLocator("//tbody/tr/td/a[text()='rights']");
        Assert.assertEquals("Editing Rights for Tree", getTitle());
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
        waitForNodeToLoad("xwiki:Dashboard");
        waitForNodeToLoad("xwiki:Main");
        waitForNodeToLoad("xwiki:Sandbox");
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

        setFieldValue("Treeview_Input", "Main.Spaces");
        waitForNodeToLoad("xwiki:Main.Spaces");
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

        setFieldValue("Treeview_Input", "Sandbox.WebHome@XWikiLogo.png");
        waitForNodeToLoad("xwiki:Sandbox.WebHome@XWikiLogo.png");

        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('wiki') "
            + "== 'xwiki'");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('space') "
            + "== 'Sandbox'");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('name') "
            + "== 'WebHome'");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('attachment') "
            + "== 'XWikiLogo.png'");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getSelectedResourceProperty('anchor') == ''");
        waitForCondition("selenium.browserbot.getCurrentWindow().Treeview.getValue() == 'Sandbox.WebHome@XWikiLogo.png'");
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
}
