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
        assertWiki("");
    }

    public void testTypingAndDeletion()
    {
        String text = "foobar";
        typeText(text);
        assertXHTML(text);
        typeBackspace(text.length());
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
        assertWiki("a\n\n\nb");
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
        typeBackspace(2);
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
        typeBackspace(2);
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
        assertXHTML("<hr><br class=\"spacer\">");

        typeBackspace(2);
        testEmptyWysiwyg();

        typeText("foobar");
        typeDelete();
        applyStyleTitle1();
        // Since the left arrow key doesn't move the caret we have to use the Range API instead.
        moveCaret("XWE.selection.getRangeAt(0).startContainer", 3);
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
        closeDialog();
        clickSymbolButton();
        getSelenium().click("//div[@title='registered sign']");
        assertWiki("\u00A9\u00AE");
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
        assertWiki("\n= foobar =\n\nx");
    }

    /**
     * @see XWIKI-2991: Editor is losing the focus when pressing enter after an image
     */
    public void testEnterAfterImage()
    {
        clickMenu("Image");
        clickMenu("Image insert");
        
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
        typeEnter(2);
        typeText("xyz");
        typeDelete();
        assertXHTML("<!--startimage:xwiki:XWiki.AdminSheet@photos.png-->"
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
        typeTextThenEnter("x");
        typeText("y");
        selectAllContent();
        clickUnorderedListButton();
        // Since the left arrow key doesn't move the caret we have to use the Range API instead.
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 0);
        typeTab();
        assertXHTML("<ul><li>x<ul><li>y</li></ul></li></ul>");
        typeShiftTab();
        assertXHTML("<ul><li>x</li><li>y</li></ul>");
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
        assertWiki("|=e ab|= \n| |c \n|d | ");
    }

    /**
     * @see XWIKI-2735: Clicking on the space between two lines hides the cursor
     */
    public void testEmptyLinesAreEditable()
    {
        setWikiContent("a\n\n\n\nb");
        assertXHTML("<p>a</p><p><br class=\"spacer\"></p><p><br class=\"spacer\"></p><p>b</p>");
        // TODO: Since neither the down arrow key nor the click doesn't seem to move the caret we have to find another
        // way of placing the caret on the empty lines, without using the Range API.
        // TODO: Assert by switching to Wiki editor to avoid hard-coding class="spacer".
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
        assertWiki("a\nb\\\\");
    }

    /**
     * @see XWIKI-2723: Empty paragraphs should not be displayed even if they have styles applied to them
     */
    public void testEmptyParagraphsGenerateEmptyLines()
    {
        setWikiContent("(% style=\"color: blue; text-align: center;\" %)\nHello world");

        // Place the caret after "Hello ".
        moveCaret("XWE.body.firstChild.firstChild", 6);

        typeEnter(4);

        assertWiki("(% style=\"color: blue; text-align: center;\" %)\nHello\n\n\n\nworld");
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
        assertXHTML("alice bob<br class=\"spacer\">");
        clickUndoButton(3);
        assertXHTML("<br class=\"spacer\">");
        clickRedoButton(7);
        assertXHTML("<h1>alice bob&nbsp;&nbsp;&nbsp; carol\u00A9<br class=\"spacer\"></h1>");
    }

    /**
     * Tests the shortcut keys for undo and redo operations. Undo is triggered by CTRL+Z or META+Z. The second is used
     * on apple keyboards. Redo is triggered by CTRL+Y or META+Y. The second is also used on apple keyboards.
     * 
     * @see XWIKI-3048: Undo/Redo/Copy/Paste/Cut Mac shortcuts should be mapped to the corresponding features of the
     *      WYSIWYG editor
     */
    public void testUndoRedoShortcutKeys()
    {
        typeText("March 9th, 2009");
        select("XWE.body.firstChild", 0, "XWE.body.firstChild", 5);

        // Make text bold.
        getSelenium().controlKeyDown();
        typeText("B");
        getSelenium().controlKeyUp();

        // Make text italic.
        getSelenium().metaKeyDown();
        typeText("I");
        getSelenium().metaKeyUp();

        // Make text underline.
        getSelenium().controlKeyDown();
        typeText("U");
        getSelenium().controlKeyUp();

        // Undo last 3 steps.
        getSelenium().metaKeyDown();
        typeText("ZZZ");
        getSelenium().metaKeyUp();

        // Redo 2 steps
        getSelenium().controlKeyDown();
        typeText("YY");
        getSelenium().controlKeyUp();

        assertWiki("**//March//** 9th, 2009");
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
        assertWiki("**x**");
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
        select("XWE.body.firstChild.firstChild", 1, "XWE.body.lastChild.firstChild", 1);

        // Press Enter.
        typeEnter();
        assertWiki("a\nd");
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
     * @see XWIKI-2993: Insert horizontal line on a selection of unordered list.
     */
    public void testInsertHRInPlaceOfASelectedList()
    {
        typeTextThenEnter("foo");
        typeText("bar");
        selectAllContent();
        clickUnorderedListButton();
        clickHRButton();
        assertWiki("----");
    }

    /**
     * @see XWIKI-3109: Headers generated from wiki syntax look and behave differently
     */
    public void testEnterTwiceInHeader()
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

        // Press enter twice to split the header and generate a paragraph.
        typeEnter(2);

        // See if the paragraph is detected.
        assertEquals("p", getSelenium().getValue("//select[@title=\"Apply Style\"]"));

        assertWiki("= hea# =\n\nder");
    }

    /**
     * @see XWIKI-3090: Cannot move cursor before table
     * @see XWIKI-3089: Cannot move cursor after table
     */
    public void testMoveCaretBeforeAndAfterTable()
    {
        setWikiContent("|=Space|=Page\n|Main|WebHome");

        // Place the caret in one of the table cells.
        moveCaret("XWE.body.firstChild.rows[0].cells[0].firstChild", 2);

        // Move the caret before the table and type some text.
        getSelenium().controlKeyDown();
        typeUpArrow();
        getSelenium().controlKeyUp();
        typeText("before");

        // Place the caret again in one of the table cells.
        moveCaret("XWE.body.lastChild.rows[1].cells[1].firstChild", 3);

        // Move the caret after the table and type some text.
        getSelenium().controlKeyDown();
        typeDownArrow();
        getSelenium().controlKeyUp();
        typeText("after");

        assertWiki("before\n\n|=Space|=Page\n|Main|WebHome\n\nafter");
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
        moveCaret("XWE.body.firstChild.firstChild", 1);

        // Insert HR in the middle of the paragraph.
        clickHRButton();

        // Move the caret before x.
        moveCaret("XWE.body.firstChild.firstChild", 0);

        // Insert HR at the beginning of the paragraph.
        clickHRButton();

        // We have to assert the XHTML because the arrow keys don't move the caret so we can't test if the user can edit
        // the generated empty paragraphs. The fact that they contain a BR proves this.
        assertXHTML("<p><br class=\"spacer\"></p><hr><p>x</p><hr><p>y</p><hr><p><br class=\"spacer\"></p>");
    }

    /**
     * @see XWIKI-3191: New lines at the end of list items are not preserved by the wysiwyg
     */
    public void testNewLinesAtTheEndOfListItemsArePreserved()
    {
        String wikiText = "* \\\\\n** \\\\\n*** test1";
        setWikiContent(wikiText);
        assertWiki(wikiText);
    }

    /**
     * @see XWIKI-3040: A rich text area on a dialog box looses its content if we move the dialog box
     */
    public void testDialogContentIsPreservedAfterBeingMoved()
    {
        clickOfficeImporterButton();
        // Put some content inside the rich text area of the Office Imported dialog.
        runScript("\nvar iframes = document.getElementsByTagName('iframe');\n"
            + "for (var i = 0; i < iframes.length; i++) {\n" + "\tvar iframe = iframes[i];\n"
            + "\tif (iframe.className == 'gwt-RichTextArea xImporterClipboardTabEditor') {\n"
            + "\t\tiframe.contentWindow.document.body.innerHTML = 'office';\n" + "\t\tbreak;\n\t}\n}\n");
        // Move the dialog.
        getSelenium().dragdrop("//div[@class='gwt-Label xDialogCaption']", "100, 100");
        // Test if the rich text area has the content we set.
        assertEquals("office", getSelenium().getEval("window.iframe.contentWindow.document.body.innerHTML"));
        // close the dialog
        closeDialog();
    }

    /**
     * @see XWIKI-3194: Cannot remove just one text style when using the style attribute instead of formatting tags
     */
    public void testRemoveBoldStyleWhenTheStyleAttributeIsUsed()
    {
        setWikiContent("hello (% style=\"font-weight: bold; font-family: monospace;\" %)vincent(%%) world");

        // Select the word in bold.
        selectNodeContents("XWE.body.firstChild.childNodes[1]");
        assertTrue(isBoldDetected());

        // Remove the bold style.
        clickBoldButton();
        assertFalse(isBoldDetected());

        // Check the XWiki syntax.
        assertWiki("hello (% style=\"font-weight: normal; font-family: monospace;\" %)vincent(%%) world");
    }

    /**
     * @see XWIKI-2997: Cannot un-bold a text with style Title 1
     */
    public void testRemoveBoldStyleWithinHeading()
    {
        // Insert a heading and make sure it has bold style.
        setWikiContent("(% style=\"font-weight: bold;\" %)\n= Title 1 =");

        // Select a part of the heading.
        select("XWE.body.firstChild.firstChild.firstChild", 3, "XWE.body.firstChild.firstChild.firstChild", 5);
        assertTrue(isBoldDetected());

        // Remove the bold style.
        clickBoldButton();
        assertFalse(isBoldDetected());

        // Check the XWiki syntax.
        assertWiki("(% style=\"font-weight: bold;\" %)\n= Tit(% style=\"font-weight: normal;\" %)le(%%) 1 =");
    }

    /**
     * @see XWIKI-3111: A link to an email address can be removed by removing the underline style
     */
    public void testRemoveUnderlineStyleFromALink()
    {
        // Insert a link to an email address.
        setWikiContent("[[foo>>mailto:x@y.z||title=\"bar\"]]");

        // Select the text of the link.
        selectNode("XWE.body.getElementsByTagName('a')[0]");
        assertTrue(isUnderlineDetected());

        // Try to remove the underline style.
        clickUnderlineButton();
        // The underline style is still present although we changed the value of the text-decoration property. I don't
        // think we can do something about this.
        assertTrue(isUnderlineDetected());

        // Check the XWiki syntax.
        assertWiki("[[foo>>mailto:x@y.z||style=\"text-decoration: none;\" title=\"bar\"]]");
    }

    /**
     * Tests if the state of the tool bar buttons is updated immediately after the editor finished loading.
     */
    public void testToolBarIsUpdatedOnLoad()
    {
        setWikiContent("**__abc__**");
        assertTrue(isBoldDetected());
        assertTrue(isUnderlineDetected());
    }
}
