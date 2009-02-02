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

public class StandardFeaturesTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests wysiwyg essentials features");
        suite.addTestSuite(StandardFeaturesTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    public void testEmptyWysiwyg()
    {
        switchToWikiEditor();
        assertEquals("", getFieldValue("content"));
        switchToWysiwygEditor();
    }

    public void testTypingAndDeletion()
    {
        String text = "foobar";
        typeText(text);
        assertXHTML(text);
        typeBackspaces(text.length());
        testEmptyWysiwyg();
    }

    /**
     * @see XWIKI-3011: Different behavior of &lt;enter&gt; on FF2.0 and FF3.0
     */
    public void testParagraphs()
    {
        typeTextThenEnterTwice("a");
        typeTextThenEnterTwice("b");
        typeText("c");
        // If the caret is followed by a br tag, delete it. See XWIKI-2732.
        typeDelete();
        assertXHTML("a<p>b</p><p>c</p>");
    }

    /**
     * Tests the behavior of the WYSIWYG editor when the user presses Enter once. The right behavior is to generate a
     * BR.
     */
    public void testEnterOnce()
    {
        typeTextThenEnter("a");
        typeText("b");
        typeDelete();
        assertXHTML("a<br>b");
    }

    /**
     * Tests the behavior of the WYSIWYG editor when the user presses Enter twice. The right behavior is to generate a
     * paragraph.
     */
    public void testEnterTwice()
    {
        typeTextThenEnterTwice("a");
        typeText("b");
        typeDelete();
        assertXHTML("a<p>b</p>");
    }

    /**
     * Tests the behavior of the WYSIWYG editor when the user presses Enter thrice. The right behavior is to generate an
     * empty line (an empty paragraph in the DOM)
     * 
     * @see XWIKI-3035: Cannot type more than 2 consecutive &lt;enter&gt; in IE6, following are ignored
     */
    public void testEnterThrice()
    {
        typeTextThenEnterTwice("a");
        typeEnter();
        typeText("b");
        switchToWikiEditor();
        assertEquals("a\n\n\nb", getFieldValue("content"));
    }

    public void testBold()
    {
        typeText("foobar");
        applyStyleParagraph();
        selectElement("p", 1);
        clickBoldButton();
        assertXHTML("<p><strong>foobar</strong></p>");
    }

    public void testItalics()
    {
        typeText("foobar");
        applyStyleParagraph();
        selectElement("p", 1);
        clickItalicsButton();
        assertXHTML("<p><em>foobar</em></p>");
    }

    public void testUnderline()
    {
        typeText("foobar");
        applyStyleParagraph();
        selectElement("p", 1);
        clickUnderlineButton();
        assertXHTML("<p><ins>foobar</ins></p>");
    }

    public void testStrikethrough()
    {
        typeText("foobar");
        applyStyleParagraph();
        selectElement("p", 1);
        clickStrikethroughButton();
        assertXHTML("<p><del>foobar</del></p>");
    }

    public void testSubscript()
    {
        typeText("foobar");
        applyStyleParagraph();
        selectElement("p", 1);
        clickSubscriptButton();
        assertXHTML("<p><sub>foobar</sub></p>");
    }

    public void testSuperscript()
    {
        typeText("foobar");
        applyStyleParagraph();
        selectElement("p", 1);
        clickSuperscriptButton();
        assertXHTML("<p><sup>foobar</sup></p>");
    }

    public void testUnorderedList()
    {
        // Create a list with 3 items
        typeTextThenEnter("a");
        typeTextThenEnter("b");
        typeText("c");
        // If the caret is followed by a br tag, delete it. See XWIKI-2732.
        typeDelete();
        selectAllContent();
        clickUnorderedListButton();
        assertXHTML("<ul><li>a</li><li>b</li><li>c</li></ul>");

        // Undo
        clickUnorderedListButton();
        assertXHTML("a<br>b<br>c");

        // Create a list with 1 item and delete it
        resetContent();
        typeText("a");
        selectAllContent();
        clickUnorderedListButton();
        typeBackspaces(2);
        testEmptyWysiwyg();

        // Create a list with 1 item and delete the bullet
        // FIXME : this should be working.
        /*
         * resetContent(); typeText("a"); selectAllContent(); clickUnorderedListButton(); resetSelection();
         * typeLeftArrow(); typeBackspace(); assertXHTML("a");
         */
    }

    public void testOrderedList()
    {
        // Create a list with 3 items
        typeTextThenEnter("a");
        typeTextThenEnter("b");
        typeText("c");
        // If the caret is followed by a br tag, delete it. See XWIKI-2732.
        typeDelete();
        selectAllContent();
        clickOrderedListButton();
        assertXHTML("<ol><li>a</li><li>b</li><li>c</li></ol>");

        // Undo
        clickOrderedListButton();
        assertXHTML("a<br>b<br>c");

        // Create a list with 1 item and delete it
        resetContent();
        typeText("a");
        selectAllContent();
        clickOrderedListButton();
        typeBackspaces(2);
        testEmptyWysiwyg();

        // Create a list with 1 item and delete the bullet
        // FIXME : this should be working.
        /*
         * resetContent(); typeText("a"); selectAllContent(); clickOrderedListButton(); resetSelection();
         * typeLeftArrow(); typeBackspace(); assertXHTML("a");
         */
    }

    public void testStyle()
    {
        typeText("foobar");
        selectAllContent();

        applyStyleTitle1();
        assertXHTML("<h1>foobar</h1>");

        applyStyleTitle2();
        assertXHTML("<h2>foobar</h2>");

        applyStyleTitle3();
        assertXHTML("<h3>foobar</h3>");

        applyStyleTitle4();
        assertXHTML("<h4>foobar</h4>");

        applyStyleTitle5();
        assertXHTML("<h5>foobar</h5>");

        applyStyleParagraph();
        assertXHTML("<p>foobar</p>");

        applyStyleInLine();
        assertXHTML("foobar");
    }

    /**
     * @see XWIKI-2949: A separator (HR) inserted at the beginning of a document is badly displayed and difficult to
     *      remove
     */
    public void testHR()
    {
        clickHRButton();
        // We don't switch to Wiki because we want to see if the Backspace works.
        assertXHTML("<hr><br class=\"emptyLine\">");

        typeBackspaces(2);
        testEmptyWysiwyg();

        typeText("foobar");
        typeDelete();
        applyStyleTitle1();
        // Since the left arrow key doesn't move the caret we have to use the Range API instead.
        runScript("var range = XWE.selection.getRangeAt(0);\n" + "range.setStart(range.startContainer, 3);\n"
            + "range.collapse(true);");
        clickHRButton();
        assertXHTML("<h1>foo</h1><hr><h1>bar</h1>");
    }

    /**
     * @see XWIKI-3012: Exception when opening a WYSIWYG dialog in FF2.0
     * @see XWIKI-2992: Place the caret after the inserted symbol
     */
    public void testInsertSymbol()
    {
        clickSymbolButton();
        getSelenium().click("//div[@title='copyright sign']");
        clickSymbolButton();
        getSelenium().click("//div[@title='Close']");
        clickSymbolButton();
        getSelenium().click("//div[@title='registered sign']");
        switchToWikiEditor();
        assertEquals("\u00A9\u00AE", getFieldValue("content"));
    }

    /**
     * @see XWIKI-2996: Text area sometimes loses focus when pressing Enter on an empty line
     */
    public void testEnterOnEmptyLine()
    {
        typeEnter();
        typeText("foobar");
        applyStyleTitle1();
        typeEnter(2);
        typeText("x");
        switchToWikiEditor();
        assertEquals("\n= foobar =\n\nx", getFieldValue("content"));
    }

    /**
     * @see XWIKI-2991: Editor is losing the focus when pressing enter after an image
     */
    public void testEnterAfterImage()
    {
        clickInsertImageButton();

        String spaceSelector = "//div[@class=\"xImageChooser\"]//select[2]";
        String space = "XWiki";
        waitForCondition("selenium.isElementPresent('" + spaceSelector + "/option[@value=\"" + space + "\"]');");
        getSelenium().select(spaceSelector, space);

        String pageSelector = "//div[@class=\"xImageChooser\"]//select[3]";
        String page = "AdminSheet";
        waitForCondition("selenium.isElementPresent('" + pageSelector + "/option[@value=\"" + page + "\"]');");
        getSelenium().select(pageSelector, page);

        getSelenium().click("//div[@class=\"xImageChooser\"]//button[text()=\"Update\"]");

        String imageSelector = "//div[@class=\"xImagesContainerPanel\"]//img[@title=\"photos.png\"]";
        waitForCondition("selenium.isElementPresent('" + imageSelector + "');");
        getSelenium().click(imageSelector);

        getSelenium().click("//div[@class=\"xImageDialogMain\"]/button[text()=\"OK\"]");

        // The inserted image should be selected. By pressing the right arrow key the caret is not moved after the image
        // thus we are forced to collapse the selection to the end.
        runScript("XWE.selection.collapseToEnd()");
        typeEnter(2);
        typeText("xyz");
        typeDelete();
        assertXHTML("<!--startimage:XWiki.AdminSheet@photos.png-->"
            + "<img src=\"/xwiki/bin/download/XWiki/AdminSheet/photos.png\" alt=\"photos.png\">"
            + "<!--stopimage--><p>xyz</p>");
    }

    /**
     * The rich text area should remain focused and the text shouldn't be changed.
     * 
     * @see XWIKI-3043: Prevent tab from moving focus from the new WYSIWYG editor
     */
    public void testTabDefault()
    {
        typeText("a");
        typeTab();
        typeText("b");
        typeShiftTab();
        typeText("c");
        assertXHTML("a&nbsp;&nbsp;&nbsp; bc");
    }

    /**
     * The list item should be indented or outdented depending on the Shift key.
     * 
     * @see XWIKI-3043: Prevent tab from moving focus from the new WYSIWYG editor
     */
    public void testTabInListItem()
    {
        typeText("x");
        // Since the left arrow key doesn't move the caret we have to use the Range API instead.
        runScript("var range = XWE.selection.getRangeAt(0);\n" + "range.setStart(range.startContainer, 0);\n"
            + "range.collapse(true);");
        clickUnorderedListButton();
        typeTab();
        assertXHTML("<ul><ul><li>x</li></ul></ul>");
        typeShiftTab();
        assertXHTML("<ul><li>x</li></ul>");
    }

    /**
     * The caret should be moved to the next or previous cell, depending on the Shift key.
     * 
     * @see XWIKI-3043: Prevent tab from moving focus from the new WYSIWYG editor
     */
    public void testTabInTableCell()
    {
        clickInsertTableButton();
        getSelenium().click("//div[@class=\"xTableMainPanel\"]/button[text()=\"Insert\"]");
        typeText("a");
        // Shit+Tab should do nothing since we are in the first cell.
        typeShiftTab();
        typeText("b");
        typeTab(3);
        typeText("c");
        // Tab should insert a new row since we are in the last cell.
        typeTab();
        typeText("d");
        typeShiftTab(4);
        typeText("e");
        switchToWikiEditor();
        assertEquals("|=e ab|= \n| |c \n|d | ", getFieldValue("content"));
    }

    /**
     * @see XWIKI-2735: Clicking on the space between two lines hides the cursor
     */
    public void testEmptyLinesAreEditable()
    {
        switchToWikiEditor();
        setFieldValue("content", "a\n\n\n\nb");
        switchToWysiwygEditor();
        assertXHTML("<p>a</p><p><br class=\"emptyLine\"></p><p><br class=\"emptyLine\"></p><p>b</p>");
        // TODO: Since neither the down arrow key nor the click doesn't seem to move the caret we have to find another
        // way of placing the caret on the empty lines, without using the Range API.
        // TODO: Assert by switching to Wiki editor to avoid hard-coding class="emptyLine".
    }

    /**
     * @see XWIKI-3039: Changes are lost if an exception is thrown during saving
     */
    public void testRecoverAfterConversionException()
    {
        // We removed the startwikilink comment to force a parsing failure.
        String html = "<span class=\"wikiexternallink\"><a href=\"mailto:x@y.z\">xyz</a></span><!--stopwikilink-->";
        setContent(html);
        // Test to see if the HTML was accepted by the rich text area.
        assertXHTML(html);
        // Let's see what happens when we save an continue.
        clickEditSaveAndContinue();
        // The user shouldn't loose his changes.
        assertXHTML(html);
    }

    /**
     * @see XWIKI-2732: Unwanted BR tags
     */
    public void testUnwantedBRsAreRemoved()
    {
        typeTextThenEnter("a");
        typeTextThenEnter("b");
        switchToWikiEditor();
        assertEquals("a\nb\n", getFieldValue("content"));
    }

    /**
     * @see XWIKI-2723: Empty paragraphs should not be displayed even if they have styles applied to them
     */
    public void testEmptyParagraphsGenerateEmptyLines()
    {
        switchToWikiEditor();
        setFieldValue("content", "(% style=\"color: blue; text-align: center;\" %)\nHello world");

        switchToWysiwygEditor();

        // Place the caret after "Hello ".
        runScript("var range = XWE.selection.getRangeAt(0);\n" + "range.setEnd(XWE.body.firstChild.firstChild, 6);\n"
            + "range.collapse(false);");

        typeEnter(4);

        switchToWikiEditor();
        assertEquals("(% style=\"color: blue; text-align: center;\" %)\nHello\n\n\n\nworld", getFieldValue("content"));
    }

    /**
     * Basic integration test for the history mechanism.
     */
    public void testUndoRedo()
    {
        typeText("alice bob");
        typeTab();
        typeText("carol");
        clickSymbolButton();
        getSelenium().click("//div[@title='copyright sign']");
        applyStyleTitle1();
        clickUndoButton(4);
        assertXHTML("alice bob<br class=\"emptyLine\">");
        clickUndoButton(3);
        assertXHTML("<br class=\"emptyLine\">");
        clickRedoButton(7);
        assertXHTML("<h1>alice bob&nbsp;&nbsp;&nbsp; carol\u00A9<br class=\"emptyLine\"></h1>");
    }

    /**
     * @see XWIKI-3138: WYSIWYG 2.0 Preview Error
     */
    public void testPreview()
    {
        typeText("x");
        selectAllContent();
        clickBoldButton();
        clickEditPreview();
        clickBackToEdit();
        switchToWikiEditor();
        assertEquals("**x**", getFieldValue("content"));
    }

    /**
     * Creates two paragraphs, makes a selection that spans both paragraphs and then presses Enter.
     */
    public void testEnterOnCrossParagraphSelection()
    {
        // Creates the two paragraphs.
        typeText("ab");
        applyStyleParagraph();
        typeEnter(2);
        typeText("cd");

        // Make a cross paragraph selection.
        runScript("var range = XWE.selection.getRangeAt(0);\n" + "range.setStart(XWE.body.firstChild.firstChild, 1);\n"
            + "range.setEnd(XWE.body.lastChild.firstChild, 1);");

        // Press Enter.
        typeEnter();
        switchToWikiEditor();
        assertEquals("a\nd", getFieldValue("content"));
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
        runScript("var range = XWE.selection.getRangeAt(0);\n"
            + "range.setStart(XWE.body.firstChild.rows[0].cells[0].lastChild, 1);\n"
            + "range.setEnd(XWE.body.firstChild.rows[0].cells[1].firstChild, 1);");

        // Press Enter.
        typeEnter();
        typeText("x");
        switchToWikiEditor();
        assertEquals("|= a\nx|=d \n| | ", getFieldValue("content"));
    }

    /**
     * @see XWIKI-2993: Insert horizontal line on a selection of unordered list.
     */
    public void testInsertHRInPlaceOfASelectedList()
    {
        typeTextThenEnter("foo");
        typeText("bar");
        selectAllContent();
        clickUnorderedListButton();
        clickHRButton();
        switchToWikiEditor();
        assertEquals("----", getFieldValue("content"));
    }

    /**
     * @see XWIKI-3109: Headers generated from wiki syntax look and behave differently
     */
    public void testEnterTwiceInHeader()
    {
        typeText("header");
        applyStyleTitle1();
        switchToWikiEditor();
        assertEquals("= header =", getFieldValue("content"));
        switchToWysiwygEditor();

        // Place the caret in the middle of the header.
        runScript("var range = XWE.selection.getRangeAt(0);\n"
            + "range.setEnd(XWE.body.firstChild.firstChild.firstChild, 3);\n" + "range.collapse(false);");

        // Type some text to update the tool bar.
        typeText("#");

        // See if the header is detected.
        assertEquals("h1", getSelenium().getValue("//select[@title=\"Apply Style\"]"));

        // Press enter twice to split the header and generate a paragraph.
        typeEnter(2);

        // See if the paragraph is detected.
        assertEquals("p", getSelenium().getValue("//select[@title=\"Apply Style\"]"));

        switchToWikiEditor();
        assertEquals("= hea# =\n\nder", getFieldValue("content"));
    }

    /**
     * @see XWIKI-3090: Cannot move cursor before table
     * @see XWIKI-3089: Cannot move cursor after table
     */
    public void testMoveCaretBeforeAndAfterTable()
    {
        switchToWikiEditor();
        setFieldValue("content", "|=Space|=Page\n|Main|WebHome");
        switchToWysiwygEditor();

        // Place the caret in one of the table cells.
        runScript("var range = XWE.selection.getRangeAt(0);\n"
            + "range.setEnd(XWE.body.firstChild.rows[0].cells[0].firstChild, 2);\n" + "range.collapse(false);");

        // Move the caret before the table and type some text.
        getSelenium().controlKeyDown();
        typeUpArrow();
        getSelenium().controlKeyUp();
        typeText("before");

        // Place the caret again in one of the table cells.
        runScript("var range = XWE.selection.getRangeAt(0);\n"
            + "range.setEnd(XWE.body.lastChild.rows[1].cells[1].firstChild, 3);\n" + "range.collapse(false);");

        // Move the caret after the table and type some text.
        getSelenium().controlKeyDown();
        typeDownArrow();
        getSelenium().controlKeyUp();
        typeText("after");

        switchToWikiEditor();
        assertEquals("before\n\n|=Space|=Page\n|Main|WebHome\n\nafter", getFieldValue("content"));
    }

    /**
     * @see XWIKI-3053: When a HR is inserted at the beginning of a paragraph an extra empty paragraph is generated
     *      before that HR
     */
    public void testInsertHRInsideParagraph()
    {
        typeText("xy");
        applyStyleParagraph();

        // Insert HR at the end of the paragraph.
        clickHRButton();

        // More the caret between x and y.
        runScript("var range = XWE.selection.getRangeAt(0);\n" + "range.setStart(XWE.body.firstChild.firstChild, 1);\n"
            + "range.collapse(true);");

        // Insert HR in the middle of the paragraph.
        clickHRButton();

        // Move the caret before x.
        runScript("var range = XWE.selection.getRangeAt(0);\n" + "range.setStart(XWE.body.firstChild.firstChild, 0);\n"
            + "range.collapse(true);");

        // Insert HR at the beginning of the paragraph.
        clickHRButton();

        // We have to assert the XHTML because the arrow keys don't move the caret so we can't test if the user can edit
        // the generated empty paragraphs. The fact that they contain a BR proves this.
        assertXHTML("<p><br class=\"emptyLine\"></p><hr><p>x</p><hr><p>y</p><hr><p><br class=\"emptyLine\"></p>");
    }
}
