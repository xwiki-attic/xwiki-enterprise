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

import junit.framework.Test;
import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the document extra feature of XWiki.
 *
 * @version $Id: $
 */
public class DocExtraTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the document extra feature of XWiki");
        suite.addTestSuite(DocExtraTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * Test document extras presence after a click on the corresponding tabs. This test also verify that the browser
     * doesn't scroll after tab clicks.
     */
    public void testDocExtraLoadingFromTabClicks()
    {
        loginAsAdmin();
        int initialScrollY = Integer.parseInt(getSelenium().getEval("window.scrollX"));

        clickLinkWithXPath("//a[@id='Attachmentslink']", false);
        waitForCondition("selenium.browserbot.findElement(\"Attachmentspane\").className.indexOf(\"empty\") == -1");
        assertTextPresent("Choose file to upload:");
        int scrollY = Integer.parseInt(getSelenium().getEval("window.scrollX"));
        assertEquals(initialScrollY, scrollY);

        clickLinkWithXPath("//a[@id='Historylink']", false);
        waitForCondition("selenium.browserbot.findElement(\"Historypane\").className.indexOf(\"empty\") == -1");
        assertElementPresent("historyform");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertEquals(initialScrollY, scrollY);

        clickLinkWithXPath("//a[@id='Informationlink']", false);
        waitForCondition("selenium.browserbot.findElement(\"Informationpane\").className.indexOf(\"empty\") == -1");
        assertTextPresent("Creation:");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertEquals(initialScrollY, scrollY);

        clickLinkWithXPath("//a[@id='Commentslink']", false);
        waitForCondition("selenium.browserbot.findElement(\"Commentspane\").className.indexOf(\"empty\") == -1");
        assertTextPresent("Add Comment");
        scrollY = Integer.parseInt(getSelenium().getEval("window.scrollY"));
        assertEquals(initialScrollY, scrollY);
    }

    /**
     * Test document extras presence after pressing the corresponding keyboard shortcuts. This test also verify that the
     * browser scrolls to the bottom of the page.
     *
     * @throws InterruptedException if selenium fails to simulate keyboard shortcut.
     */
    public void testDocExtraLoadingFromKeyboardShortcuts() throws InterruptedException
    {
        int initialScrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));

        getSkinExecutor().pressKeyboardShortcut("a", false, false, false);
        assertTextPresent("Add an attachment");
        int scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(initialScrollY < scrollY);
        getSelenium().getEval("this.browserbot.getCurrentWindow().scroll(0,0);");

        getSkinExecutor().pressKeyboardShortcut("h", false, false, false);
        assertElementPresent("historyform");
        scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(initialScrollY < scrollY);
        getSelenium().getEval("this.browserbot.getCurrentWindow().scroll(0,0);");

        getSkinExecutor().pressKeyboardShortcut("i", false, false, false);
        assertTextPresent("Creation:");
        scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(initialScrollY < scrollY);
        getSelenium().getEval("this.browserbot.getCurrentWindow().scroll(0,0);");

        getSkinExecutor().pressKeyboardShortcut("c", false, false, false);
        assertTextPresent("Add Comment");
        scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(initialScrollY < scrollY);
        getSelenium().getEval("this.browserbot.getCurrentWindow().scroll(0,0);");
    }

    /**
     * Test document extra presence when the user arrives from an URL with anchor. This test also verify that the
     * browser scrolls to the bottom of the page.
     */
    public void testDocExtraLoadingFromURLAnchor()
    {
        // We have to load a different page first since opening the same page with a new anchor doesn't call
        // our functions (on purpose)
        open("/xwiki/bin/view/Main/ThisPageDoesNotExist");
        open("/xwiki/bin/view/Main/WebHome#Attachments");
        waitForCondition("selenium.page().bodyText().indexOf('Add an attachment')!=-1;");
        int scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(scrollY > 0);

        open("/xwiki/bin/view/Main/ThisPageDoesNotExist");
        open("/xwiki/bin/view/Main/WebHome#History");
        waitForCondition("selenium.isElementPresent(\"historyform\")!=false;");
        scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(scrollY > 0);

        open("/xwiki/bin/view/Main/ThisPageDoesNotExist");
        open("/xwiki/bin/view/Main/WebHome#Information");
        waitForCondition("selenium.page().bodyText().indexOf('Creation:')!=-1;");
        scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(scrollY > 0);

        open("/xwiki/bin/view/Main/ThisPageDoesNotExist");
        open("/xwiki/bin/view/Main/WebHome#Comments");
        waitForCondition("selenium.page().bodyText().indexOf('Add Comment')!=-1;");
        scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(scrollY > 0);
    }

    /**
     * Test document extra presence after clicks on top menu links. This test also verify that the browser scrolls to
     * the bottom of the page.
     */
    public void testDocExtraLoadingFromTopMenuLinks()
    {
        open("Main", "WebHome");

        clickLinkWithXPath("//a[@id='tmShowAttachments']", false);
        waitForCondition("selenium.browserbot.findElement(\"Attachmentspane\").className.indexOf(\"empty\") == -1");
        assertTextPresent("Choose file to upload:");
        int scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(scrollY > 0);
        getSelenium().getEval("this.browserbot.getCurrentWindow().scroll(0,0);");

        clickLinkWithXPath("//a[@id='tmShowHistory']", false);
        waitForCondition("selenium.browserbot.findElement(\"Historypane\").className.indexOf(\"empty\") == -1");
        assertElementPresent("historyform");
        scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(scrollY > 0);
        getSelenium().getEval("this.browserbot.getCurrentWindow().scroll(0,0);");

        clickLinkWithXPath("//a[@id='tmShowInformation']", false);
        waitForCondition("selenium.browserbot.findElement(\"Informationpane\").className.indexOf(\"empty\") == -1");
        assertTextPresent("Creation:");
        scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(scrollY > 0);
        getSelenium().getEval("this.browserbot.getCurrentWindow().scroll(0,0);");

        clickLinkWithXPath("//a[@id='tmShowComments']", false);
        waitForCondition("selenium.browserbot.findElement(\"Commentspane\").className.indexOf(\"empty\") == -1");
        assertTextPresent("Add Comment");
        scrollY = Integer.parseInt(getSelenium().getEval("this.browserbot.getCurrentWindow().scrollY"));
        assertTrue(scrollY > 0);
        getSelenium().getEval("this.browserbot.getCurrentWindow().scroll(0,0);");
    }
}
