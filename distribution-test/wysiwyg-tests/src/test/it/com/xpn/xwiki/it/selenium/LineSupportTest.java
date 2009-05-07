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

import com.xpn.xwiki.it.selenium.framework.AbstractWysiwygTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Functional tests for line support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class LineSupportTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Functional tests for line support inside the WYSIWYG editor.");
        suite.addTestSuite(LineSupportTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * Tests if the user can insert a line break in different contexts.
     */
    public void testInsertLineBreak()
    {
        // Under body
        typeText("a");
        typeShiftEnter();
        typeText("b");
        assertWiki("a\nb");

        resetContent();

        // Inside heading
        typeText("c");
        applyStyleTitle1();
        typeShiftEnter();
        typeText("d");
        assertWiki("= c\nd =");

        resetContent();

        // Inside list item
        typeText("e");
        clickUnorderedListButton();
        typeShiftEnter();
        typeText("f");
        assertWiki("* e\nf");

        // Inside table cell
        setWikiContent("|h");
        typeText("g");
        typeShiftEnter();
        assertWiki("|g\nh");

        // Inside table heading
        setWikiContent("|=j");
        typeText("i");
        typeShiftEnter();
        assertWiki("|=i\nj");

        // Inside paragraph
        setWikiContent("l");
        typeText("k");
        typeShiftEnter();
        assertWiki("k\nl");
    }

    /**
     * Tests if the user can split the current line of text.
     */
    public void testSplitLine()
    {
        // Under body
        typeTextThenEnter("a");
        typeText("b");
        assertWiki("a\n\nb");

        // Inside paragraph
        setWikiContent("d");
        typeTextThenEnter("c");
        assertWiki("c\n\nd");

        // Inside heading
        resetContent();
        applyStyleTitle2();
        typeTextThenEnter("e");
        typeText("f");
        assertWiki("== e ==\n\nf");
    }

    /**
     * Tests if the user can split the current line when the caret is after a line break.
     */
    public void testSplitLineAfterLineBreak()
    {
        // Under body
        typeText("a");
        typeShiftEnter();
        typeEnter();
        typeText("b");
        assertWiki("a\n\nb");

        // Inside paragraph
        setWikiContent("d");
        typeText("c");
        typeShiftEnter();
        typeEnter();
        assertWiki("c\n\nd");

        // Inside heading
        resetContent();
        typeText("e");
        applyStyleTitle3();
        typeShiftEnter();
        typeEnter();
        typeText("f");
        assertWiki("=== e ===\n\nf");
    }

    /**
     * Tests if pressing Enter at the beginning of a paragraph or heading inserts an empty line before them.
     * 
     * @see XWIKI-3035: Cannot type more than 2 consecutive &lt;enter&gt; in IE6, following are ignored
     */
    public void testInsertEmptyLine()
    {
        // Before paragraph
        typeText("a");
        typeEnter(2);
        typeText("b");
        assertWiki("a\n\n\nb");

        // Before heading
        resetContent();
        typeTextThenEnter("c");
        applyStyleTitle4();
        typeEnter();
        typeText("d");
        assertWiki("c\n\n\n==== d ====");
    }

    /**
     * @see XWIKI-3573: Title style removed after hitting return then backspace at the beginning of a line
     */
    public void testRemoveEmptyLineBefore()
    {
        // Remove empty lines before header.
        // Create the header first.
        typeText("x");
        applyStyleTitle5();
        // Place the caret at the beginning of the line
        moveCaret("XWE.body.firstChild.firstChild", 0);
        // Insert two empty lines before.
        typeEnter(2);
        // Remove them.
        typeBackspace(2);
        // Check the result.
        assertWiki("===== x =====");

        // Remove empty lines before paragraph
        // Create the paragraph.
        applyStylePlainText();
        // Insert two empty lines before.
        typeEnter(2);
        // Remove them.
        typeBackspace(2);
        // Check the result.
        assertWiki("x");
    }

    /**
     * @see XWIKI-2996: Text area sometimes loses focus when pressing Enter on an empty line
     */
    public void testEnterOnEmptyLine()
    {
        typeEnter();
        typeText("foobar");
        applyStyleTitle1();
        typeEnter();
        typeText("x");
        assertWiki("\n= foobar =\n\nx");
    }

    /**
     * @see XWIKI-3011: Different behavior of &lt;enter&gt; on FF2.0 and FF3.0
     */
    public void testParagraphs()
    {
        typeTextThenEnter("a");
        typeTextThenEnter("b");
        typeText("c");
        assertWiki("a\n\nb\n\nc");
    }

    /**
     * @see XWIKI-2991: Editor is losing the focus when pressing enter after an image
     */
    public void testEnterAfterImage()
    {
        clickMenu("Image");
        clickMenu("Insert image");

        waitForDialogToLoad();
        clickButtonWithText("All pages");
        String imageSpaceSelector = "//div[@class=\"xPageChooser\"]//select[2]";
        String imageSpace = "XWiki";
        waitForCondition("selenium.isElementPresent('" + imageSpaceSelector + "/option[@value=\"" + imageSpace
            + "\"]');");
        getSelenium().select(imageSpaceSelector, imageSpace);
        String imagePageSelector = "//div[@class=\"xPageChooser\"]//select[3]";
        String imagePage = "AdminSheet";
        waitForCondition("selenium.isElementPresent('" + imagePageSelector + "/option[@value=\"" + imagePage + "\"]');");
        getSelenium().select(imagePageSelector, imagePage);
        getSelenium().click("//div[@class=\"xPageChooser\"]//button[text()=\"Update\"]");
        String imageSelector = "//div[@class=\"xImagesSelector\"]//img[@title=\"photos.png\"]";
        waitForCondition("selenium.isElementPresent('" + imageSelector + "');");
        getSelenium().click(imageSelector);
        clickButtonWithText("Select");
        waitForCondition("selenium.isElementPresent('//div[contains(@class, \"xImageConfig\")]');");
        clickButtonWithText("Insert image");
        waitForDialogToClose();

        // The inserted image should be selected. By pressing the right arrow key the caret is not moved after the image
        // thus we are forced to collapse the selection to the end.
        runScript("XWE.selection.collapseToEnd()");
        typeEnter();
        typeText("xyz");
        typeDelete();
        assertXHTML("<!--startimage:xwiki:XWiki.AdminSheet@photos.png-->"
            + "<img src=\"/xwiki/bin/download/XWiki/AdminSheet/photos.png\" alt=\"photos.png\">"
            + "<!--stopimage--><p>xyz</p>");
    }

    /**
     * Creates two paragraphs, makes a selection that spans both paragraphs and then presses Enter.
     */
    public void testEnterOnCrossParagraphSelection()
    {
        // Creates the two paragraphs.
        typeText("ab");
        applyStyleTitle1();
        applyStylePlainText();
        typeEnter();
        typeText("cd");

        // Make a cross paragraph selection.
        select("XWE.body.firstChild.firstChild", 1, "XWE.body.lastChild.firstChild", 1);

        // Press Enter.
        typeEnter();
        assertWiki("a\n\nd");
    }

    /**
     * Inserts a table, types some text in each cell, makes a selection that spans some table cells and then presses
     * Enter.
     */
    public void testEnterOnCrossTableCellSelection()
    {
        // Insert the table.
        clickInsertTableButton();
        getSelenium().click("//div[@class=\"xTableMainPanel\"]/button[text()=\"Insert\"]");

        // Fill the table.
        typeText("ab");
        typeTab();
        typeText("cd");

        // Make a cross table cell selection.
        select("XWE.body.firstChild.rows[0].cells[0].lastChild", 1, "XWE.body.firstChild.rows[0].cells[1].firstChild",
            1);

        // Press Enter.
        typeEnter();
        typeText("x");
        assertWiki("|= a\nx|=d \n| | ");
    }

    /**
     * @see XWIKI-3109: Headers generated from wiki syntax look and behave differently
     */
    public void testEnterInHeader()
    {
        typeText("header");
        applyStyleTitle1();
        assertWiki("= header =");

        // Place the caret in the middle of the header.
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);

        // Type some text to update the tool bar.
        typeText("#");

        // See if the header is detected.
        assertEquals("h1", getSelenium().getValue("//select[@title=\"Apply Style\"]"));

        // Press enter to split the header and generate a paragraph.
        typeEnter();

        // See if the paragraph is detected.
        assertEquals("p", getSelenium().getValue("//select[@title=\"Apply Style\"]"));

        assertWiki("= hea# =\n\nder");
    }
}
