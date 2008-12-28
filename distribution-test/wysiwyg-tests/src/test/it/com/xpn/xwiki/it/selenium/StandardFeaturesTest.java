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
        assertXHTML("<br>");
    }

    public void testTypingAndDeletion()
    {
        String text = "foobar";
        typeText(text);
        assertXHTML(text);
        typeBackspaces(text.length());
        assertXHTML("<br>");
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
        typeDelete();
        assertXHTML("a<p><br></p><p>b</p>");
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
        assertXHTML("<br>");

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
        assertXHTML("<br>");

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
        assertXHTML("<hr>");

        typeBackspaces(2);
        assertXHTML("<br>");

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
        // getSelenium().setSpeed("1000");
        clickSymbolButton();
        getSelenium().click("//div[@title='copyright sign']");
        clickSymbolButton();
        getSelenium().click("//div[@title='Close']");
        clickSymbolButton();
        getSelenium().click("//div[@title='registered sign']");
        assertXHTML("\u00A9\u00AE");
        // getSelenium().setSpeed("0");
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
        typeEnter();
        typeText("x");
        typeDelete();
        assertXHTML("<p><br></p><h1>foobar</h1><p>x</p>");
    }

    /**
     * @see XWIKI-2991: Editor is losing the focus when pressing enter after an image
     */
    public void testEnterAfterImage()
    {
        clickInsertImageButton();
        getSelenium().select("//div[@class=\"xImageChooser\"]//select[2]", "Main");
        getSelenium().select("//div[@class=\"xImageChooser\"]//select[3]", "LuceneSearch");
        getSelenium().click("//div[@class=\"xImageChooser\"]//button[text()=\"Update\"]");
        getSelenium().click("//div[@class=\"xImagesContainerPanel\"]//img[@title=\"next.png\"]");
        getSelenium().click("//div[@class=\"xImageDialogMain\"]/button[text()=\"Insert\"]");
        // The inserted image should be selected. By pressing the right arrow key the caret is not moved after the image
        // thus we are forced to collapse the selection to the end.
        runScript("XWE.selection.collapseToEnd()");
        typeEnter();
        typeEnter();
        typeText("xyz");
        typeDelete();
        assertXHTML("<!--startimage:Main.LuceneSearch@next.png-->"
            + "<img src=\"/xwiki/bin/download/Main/LuceneSearch/next.png\" alt=\"next.png\">"
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
        typeShiftTab();
        typeText("b");
        typeTab(3);
        typeText("c");
        typeTab();
        typeText("d");
        typeShiftTab(4);
        typeText("e");
        assertXHTML("<table><tbody><tr><th>e ab</th><th>&nbsp;</th></tr>"
            + "<tr><td>&nbsp;</td><td>cd&nbsp;</td></tr></tbody></table>");
    }

    /**
     * @see XWIKI-2735: Clicking on the space between two lines hides the cursor
     */
    public void testEmptyLinesAreEditable()
    {
        clickLinkWithText("Wiki", true);
        setFieldValue("content", "a\n\n\n\nb");
        clickLinkWithText("WYSIWYG", true);
        assertXHTML("<p>a</p><p><br></p><p><br></p><p>b</p>");
        // TODO: Since neither the down arrow key nor the click doesn't seem to move the caret we have to find another
        // way of placing the caret on the empty lines, without using the Range API.
    }
}
