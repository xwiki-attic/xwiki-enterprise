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
import com.xpn.xwiki.it.selenium.framework.ColibriSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

public class StandardFeaturesTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests wysiwyg essentials features");
        suite.addTestSuite(StandardFeaturesTest.class, ColibriSkinExecutor.class);
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

    public void testBold()
    {
        typeText("foobar");
        applyStyleTitle5();
        selectElement("h5", 1);
        clickBoldButton();
        assertXHTML("<h5><strong>foobar</strong></h5>");
    }

    public void testItalics()
    {
        typeText("foobar");
        applyStyleTitle5();
        selectElement("h5", 1);
        clickItalicsButton();
        assertXHTML("<h5><em>foobar</em></h5>");
    }

    public void testUnderline()
    {
        typeText("foobar");
        applyStyleTitle5();
        selectElement("h5", 1);
        clickUnderlineButton();
        assertXHTML("<h5><ins>foobar</ins></h5>");
    }

    public void testStrikethrough()
    {
        typeText("foobar");
        applyStyleTitle5();
        selectElement("h5", 1);
        clickStrikethroughButton();
        assertXHTML("<h5><del>foobar</del></h5>");
    }

    public void testSubscript()
    {
        typeText("foobar");
        applyStyleTitle5();
        selectElement("h5", 1);
        clickSubscriptButton();
        assertXHTML("<h5><sub>foobar</sub></h5>");
    }

    public void testSuperscript()
    {
        typeText("foobar");
        applyStyleTitle5();
        selectElement("h5", 1);
        clickSuperscriptButton();
        assertXHTML("<h5><sup>foobar</sup></h5>");
    }

    public void testUnorderedList()
    {
        // Create a list with 3 items
        typeTextThenEnter("a");
        typeTextThenEnter("b");
        // We press Enter here to be sure there's no bogus BR after the typed text.
        typeTextThenEnter("c");
        // Delete the empty line which was created only to avoid bogus BRs. See XWIKI-2732.
        typeBackspace();
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
        // We press Enter here to be sure there's no bogus BR after the typed text.
        typeTextThenEnter("c");
        // Delete the empty line which was created only to avoid bogus BRs. See XWIKI-2732.
        typeBackspace();
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

        applyStyleTitle6();
        assertXHTML("<h6>foobar</h6>");

        applyStylePlainText();
        assertXHTML("<p>foobar</p>");
    }

    /**
     * @see XWIKI-2949: A separator (HR) inserted at the beginning of a document is badly displayed and difficult to
     *      remove
     */
    public void testHR()
    {
        clickHRButton();
        // Create a heading and then delete it just to remove the bogus BR at the end.
        applyStyleTitle1();
        typeBackspace();
        // We don't switch to Wiki because we want to see if the Backspace works.
        assertXHTML("<hr>");

        // Strange but we need to type Backspace twice although there's nothing else besides the horizontal ruler.
        typeBackspace(2);
        testEmptyWysiwyg();

        typeText("foobar");
        applyStyleTitle1();
        // Type Enter then Backspace to remove the bogus BR at the end.
        typeEnter();
        typeBackspace();
        // Since the left arrow key doesn't move the caret we have to use the Range API instead.
        moveCaret("XWE.selection.getRangeAt(0).startContainer", 3);
        clickHRButton();
        assertXHTML("<h1>foo</h1><hr><h1>bar</h1>");
    }

    /**
     * @see XWIKI-3012: Exception when opening a WYSIWYG dialog in FF2.0
     * @see XWIKI-2992: Place the caret after the inserted symbol
     * @see XWIKI-3682: Trademark symbol is not displayed correctly.
     */
    public void testInsertSymbol()
    {
        clickSymbolButton();
        getSelenium().click("//div[@title='copyright sign']");
        clickSymbolButton();
        closeDialog();
        clickSymbolButton();
        getSelenium().click("//div[@title='registered sign']");
        clickSymbolButton();
        getSelenium().click("//div[@title='trade mark sign']");
        assertWiki("\u00A9\u00AE\u2122");
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
        typeShiftEnter();
        // "y" (lower case only) is misinterpreted.
        // See http://jira.openqa.org/browse/SIDE-309
        // See http://jira.openqa.org/browse/SRC-385
        typeText("Y");
        selectAllContent();
        clickUnorderedListButton();
        // Since the left arrow key doesn't move the caret we have to use the Range API instead.
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 0);
        typeTab();
        assertXHTML("<ul><li>x<ul><li>Y</li></ul></li></ul>");
        typeShiftTab();
        assertXHTML("<ul><li>x</li><li>Y</li></ul>");
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
        typeText("a");
        typeShiftEnter();
        typeText("b");
        typeShiftEnter();
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

        typeEnter(3);

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

        // Redo 2 steps.
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
     * @see XWIKI-3053: When a HR is inserted at the beginning of a paragraph an extra empty paragraph is generated
     *      before that HR
     */
    public void testInsertHRInsideParagraph()
    {
        // "y" (lower case only) is misinterpreted.
        // See http://jira.openqa.org/browse/SIDE-309
        // See http://jira.openqa.org/browse/SRC-385
        typeText("xY");
        applyStyleTitle1();
        applyStylePlainText();

        // Insert HR at the end of the paragraph.
        clickHRButton();

        // More the caret between x and Y.
        moveCaret("XWE.body.firstChild.firstChild", 1);

        // Insert HR in the middle of the paragraph.
        clickHRButton();

        // Move the caret before x.
        moveCaret("XWE.body.firstChild.firstChild", 0);

        // Insert HR at the beginning of the paragraph.
        clickHRButton();

        // We have to assert the XHTML because the arrow keys don't move the caret so we can't test if the user can edit
        // the generated empty paragraphs. The fact that they contain a BR proves this.
        assertXHTML("<p><br class=\"spacer\"></p><hr><p>x</p><hr><p>Y</p><hr><p><br class=\"spacer\"></p>");
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

    /**
     * @see XWIKI-2669: New WYSIWYG editor doesn't work when special characters are entered by the user.
     */
    public void testHTMLSpecialChars()
    {
        typeText("<\"'&#\"'>");
        assertWiki("<\"'&#\"'>");
        assertXHTML("<p>&lt;\"'&amp;#\"'&gt;</p>");
        assertWiki("<\"'&#\"'>");
    }

    /**
     * @see XWIKI-4033: When saving after section edit entire page is overwritten.
     */
    public void testSectionEditing()
    {
        // Save the current location to be able to get back to it later.
        String location = getSelenium().getLocation();

        // Create two sections.
        switchToWikiEditor();
        setFieldValue("content", "= s1 =\n\nabc\n\n= s2 =\n\nxyz");
        clickEditSaveAndView();

        // Edit the second section.
        open(location + (location.indexOf('?') < 0 ? "?" : "") + "&section=2");
        focusRichTextArea();
        typeDelete(2);
        typeText("Section 2");
        assertWiki("= Section 2 =\n\nxyz");
        clickEditSaveAndView();

        // Check the content of the page.
        open(location);
        focusRichTextArea();
        assertWiki("= s1 =\n\nabc\n\n= Section 2 =\n\nxyz");
    }

    /**
     * @see XWIKI-4335: Typing ">" + text in wysiwyg returns a quote
     */
    public void testQuoteSyntaxIsEscaped()
    {
        typeText("> 1");
        switchToSource();
        switchToWysiwyg();
        assertEquals("> 1", getEval("window.XWE.body.textContent"));
    }
}
