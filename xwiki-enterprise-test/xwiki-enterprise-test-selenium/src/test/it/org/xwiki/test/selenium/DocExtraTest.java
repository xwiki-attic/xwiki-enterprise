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

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.FlamingoSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

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
        suite.addTestSuite(DocExtraTest.class, FlamingoSkinExecutor.class);
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

        clickLinkWithXPath("//a[@id='Attachmentslink']", false);
        waitForDocExtraPaneActive("attachments");

        clickLinkWithXPath("//a[@id='Historylink']", false);
        waitForDocExtraPaneActive("history");

        clickLinkWithXPath("//a[@id='Informationlink']", false);
        waitForDocExtraPaneActive("information");

        clickLinkWithXPath("//a[@id='Commentslink']", false);
        waitForDocExtraPaneActive("comments");
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
        int initialVerticalScroll = getVerticalScroll();

        getSkinExecutor().pressKeyboardShortcut("a", false, false, false);
        waitForDocExtraPaneActive("attachments");
        assertTrue(initialVerticalScroll < getVerticalScroll());
        scrollToPageTop();

        getSkinExecutor().pressKeyboardShortcut("h", false, false, false);
        waitForDocExtraPaneActive("history");
        assertTrue(initialVerticalScroll < getVerticalScroll());
        scrollToPageTop();

        getSkinExecutor().pressKeyboardShortcut("i", false, false, false);
        waitForDocExtraPaneActive("information");
        assertTrue(initialVerticalScroll < getVerticalScroll());
        scrollToPageTop();

        getSkinExecutor().pressKeyboardShortcut("c", false, false, false);
        waitForDocExtraPaneActive("comments");
        assertTrue(initialVerticalScroll < getVerticalScroll());
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
        waitForDocExtraPaneActive("attachments");
        assertTrue(getVerticalScroll() > 0);

        open("Main", "ThisPageDoesNotExist");
        open("Main", "WebHome#History");
        waitForDocExtraPaneActive("history");
        assertTrue(getVerticalScroll() > 0);

        open("Main", "ThisPageDoesNotExist");
        open("Main", "WebHome#Information");
        waitForDocExtraPaneActive("information");
        assertTrue(getVerticalScroll() > 0);

        open("Main", "ThisPageDoesNotExist");
        open("Main", "WebHome#Comments");
        waitForDocExtraPaneActive("comments");
        assertTrue(getVerticalScroll() > 0);
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
        waitForDocExtraPaneActive("attachments");
        assertTrue(getVerticalScroll() > 0);
        scrollToPageTop();

        clickShowHistory();
        waitForDocExtraPaneActive("history");
        assertTrue(getVerticalScroll() > 0);
        scrollToPageTop();

        clickShowInformation();
        waitForDocExtraPaneActive("information");
        assertTrue(getVerticalScroll() > 0);
        scrollToPageTop();

        clickShowComments();
        waitForDocExtraPaneActive("comments");
        assertTrue(getVerticalScroll() > 0);
    }

    /**
     * @param paneId valid values: "history", "comments", etc
     */
    private void waitForDocExtraPaneActive(String paneId)
    {
        waitForElement(paneId + "content");
    }

    private int getVerticalScroll()
    {
        return Integer.parseInt(getSelenium().getEval("window.scrollY"));
    }

    private void scrollToPageTop()
    {
        getSelenium().getEval("window.scroll(0,0);");
    }
}
