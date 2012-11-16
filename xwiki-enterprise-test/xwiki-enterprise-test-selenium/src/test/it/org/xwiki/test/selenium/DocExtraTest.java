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
 * Verify the document extra feature of XWiki.
 * 
 * @version $Id$
 */
public class DocExtraTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the document extra feature of XWiki");
        suite.addTestSuite(DocExtraTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    /**
     * Test document extras presence after a click on the corresponding tabs.
     */
    public void testDocExtraLoadingFromTabClicks()
    {
        open("Main", "WebHome");

        waitForElement("//a[@id='Attachmentslink']");
        clickLinkWithXPath("//a[@id='Attachmentslink']", false);
        waitForCondition("selenium.browserbot.findElement(\"Attachmentspane\").className.indexOf(\"empty\") == -1");
        assertElementPresent("attachform");

        waitForElement("//a[@id='Historylink']");
        clickLinkWithXPath("//a[@id='Historylink']", false);
        waitForCondition("selenium.browserbot.findElement(\"Historypane\").className.indexOf(\"empty\") == -1");
        assertElementPresent("historyform");

        waitForElement("//a[@id='Informationlink']");
        clickLinkWithXPath("//a[@id='Informationlink']", false);
        waitForCondition("selenium.browserbot.findElement(\"Informationpane\").className.indexOf(\"empty\") == -1");
        assertTextPresent("Created");

        waitForElement("//a[@id='Commentslink']");
        clickLinkWithXPath("//a[@id='Commentslink']", false);
        waitForCondition("selenium.browserbot.findElement(\"Commentspane\").className.indexOf(\"empty\") == -1");
        assertElementPresent("commentform");
    }

    /**
     * Test document extras presence after pressing the corresponding keyboard shortcuts. This test also verify that the
     * browser scrolls to the bottom of the page.
     * 
     * @throws InterruptedException if selenium fails to simulate keyboard shortcut.
     */
    public void testDocExtraLoadingFromKeyboardShortcuts() throws InterruptedException
    {
        open("Main", "WebHome");
        int initialScrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));

        getSkinExecutor().pressKeyboardShortcut("a", false, false, false);
        waitForCondition("selenium.isTextPresent('Add an attachment')!=-1;");
        assertElementPresent("attachform");
        int scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(initialScrollY < scrollY);
        getSelenium().getEval("window.scroll(0,0);");

        getSkinExecutor().pressKeyboardShortcut("h", false, false, false);
        waitForCondition("selenium.browserbot.findElement(\"Historypane\").className.indexOf(\"empty\") == -1");
        assertElementPresent("historyform");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(initialScrollY < scrollY);
        getSelenium().getEval("window.scroll(0,0);");

        getSkinExecutor().pressKeyboardShortcut("i", false, false, false);
        waitForCondition("selenium.browserbot.findElement(\"Informationpane\").className.indexOf(\"empty\") == -1");
        assertTextPresent("*No parent*");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(initialScrollY < scrollY);
        getSelenium().getEval("window.scroll(0,0);");

        getSkinExecutor().pressKeyboardShortcut("c", false, false, false);
        waitForCondition("selenium.isTextPresent('Add comment')!=-1;");
        assertElementPresent("commentform");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(initialScrollY < scrollY);
        getSelenium().getEval("window.scroll(0,0);");
    }

    /**
     * Test document extra presence when the user arrives from an URL with anchor. This test also verify that the
     * browser scrolls to the bottom of the page.
     */
    public void testDocExtraLoadingFromURLAnchor()
    {
        // We have to load a different page first since opening the same page with a new anchor doesn't call
        // our functions (on purpose)
        open("Main", "ThisPageDoesNotExist");
        open("Main", "WebHome#Attachments");
        waitForElement("attachform");
        int scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(scrollY > 0);

        open("Main", "ThisPageDoesNotExist");
        open("Main", "WebHome#History");
        waitForElement("historyform");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(scrollY > 0);

        open("Main", "ThisPageDoesNotExist");
        open("Main", "WebHome#Information");
        waitForBodyContains("No parent");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(scrollY > 0);

        open("Main", "ThisPageDoesNotExist");
        open("Main", "WebHome#Comments");
        waitForElement("commentform");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(scrollY > 0);
    }

    /**
     * Test document extra presence after clicks on links directing to the extra tabs (top menu for Toucan skin for
     * example and shortcuts for Colibri skin for example). This test also verify that the browser scrolls to the bottom
     * of the page.
     */
    public void testDocExtraLoadingFromLinks()
    {
        open("Main", "WebHome");

        clickShowAttachments();
        waitForCondition("selenium.browserbot.findElement(\"Attachmentspane\").className.indexOf(\"empty\") == -1");
        assertElementPresent("attachform");
        int scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(scrollY > 0);
        getSelenium().getEval("window.scroll(0,0);");

        clickShowHistory();
        waitForCondition("selenium.browserbot.findElement(\"Historypane\").className.indexOf(\"empty\") == -1");
        assertElementPresent("historyform");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(scrollY > 0);
        getSelenium().getEval("window.scroll(0,0);");

        clickShowInformation();
        waitForCondition("selenium.browserbot.findElement(\"Informationpane\").className.indexOf(\"empty\") == -1");
        assertTextPresent("Created");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(scrollY > 0);
        getSelenium().getEval("window.scroll(0,0);");

        clickShowComments();
        waitForCondition("selenium.browserbot.findElement(\"Commentspane\").className.indexOf(\"empty\") == -1");
        assertElementPresent("commentform");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertTrue(scrollY > 0);
        getSelenium().getEval("window.scroll(0,0);");
    }
}
