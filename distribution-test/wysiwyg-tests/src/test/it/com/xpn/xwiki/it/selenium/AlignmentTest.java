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

import com.xpn.xwiki.it.selenium.framework.AbstractWysiwygTestCase;

/**
 * Functional tests for alignment support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class AlignmentTest extends AbstractWysiwygTestCase
{
    /**
     * Tests if the text directly under body can be aligned.
     */
    public void testAlignBody()
    {
        clickAlignCenterButton();
        typeText("a");
        switchToSource();
        assertSourceText("(% style=\"text-align: center;\" %)\na");

        setSourceText("");
        switchToWysiwyg();

        typeText("a");
        clickAlignRightButton();
        typeText("b");
        switchToSource();
        assertSourceText("(% style=\"text-align: right;\" %)\nab");

        setSourceText("");
        switchToWysiwyg();

        typeText("abc");
        select("XWE.body.firstChild", 1, "XWE.body.firstChild", 2);
        clickAlignFullButton();
        typeText("x");
        switchToSource();
        assertSourceText("(% style=\"text-align: justify;\" %)\naxc");

        setSourceText("");
        switchToWysiwyg();

        typeText("a");
        typeShiftEnter();
        typeText("b");
        selectNode("XWE.body.childNodes[2]");
        clickAlignLeftButton();
        typeText("x");
        switchToSource();
        assertSourceText("(% style=\"text-align: left;\" %)\na\nx");
    }

    /**
     * Tests if a single paragraph can be aligned.
     */
    public void testAlignParagraph()
    {
        // Create the paragraph.
        applyStyleTitle1();
        applyStylePlainText();

        // Wait for the tool bar to be updated.
        waitForPushButton(TOOLBAR_BUTTON_UNDO_TITLE, true);
        // Check the default alignment.
        boolean defaultAlignFull = isToggleButtonDown("Justified");

        // Center text.
        clickAlignCenterButton();
        typeText("a");
        waitForAlignCenterDetected(true);
        switchToSource();
        assertSourceText("(% style=\"text-align: center;\" %)\na");

        // Assert again the center alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        waitForAlignCenterDetected(true);

        typeShiftEnter();
        typeText("b");
        clickAlignRightButton();
        waitForAlignRightDetected(true);
        switchToSource();
        assertSourceText("(% style=\"text-align: right;\" %)\na\nb");

        // Assert again the right alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        waitForAlignRightDetected(true);

        selectNode("XWE.body.firstChild.childNodes[2]");
        clickAlignFullButton();
        typeText("c");
        waitForAlignFullDetected(true);
        switchToSource();
        assertSourceText("(% style=\"text-align: justify;\" %)\na\nc");

        // Assert again the full alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        waitForAlignFullDetected(true);

        // Remove the full alignment (toggle full alignment off).
        clickAlignFullButton();
        // If paragraphs are justified by default then the "Justified" button remains toggled.
        waitForAlignFullDetected(defaultAlignFull);
        switchToSource();
        assertSourceText("a\nc");
        switchToWysiwyg();

        typeText("x");
        clickAlignLeftButton();
        waitForAlignLeftDetected(true);
        switchToSource();
        assertSourceText("(% style=\"text-align: left;\" %)\na\ncx");

        // Assert again the left alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        waitForAlignLeftDetected(true);
    }

    /**
     * Tests if a table cell can be aligned.
     */
    public void testAlignTableCell()
    {
        switchToSource();
        setSourceText("|=a|=b\n|c|d");
        switchToWysiwyg();
        clickAlignRightButton();
        waitForAlignRightDetected(true);
        switchToSource();
        assertSourceText("|=(% style=\"text-align: right;\" %)a|=b\n|c|d");

        // Assert again the right alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        waitForAlignRightDetected(true);

        typeTextThenEnter("x");
        clickAlignFullButton();
        waitForAlignFullDetected(true);
        switchToSource();
        assertSourceText("|=(% style=\"text-align: justify;\" %)x\na|=b\n|c|d");
        switchToWysiwyg();

        selectNodeContents("XWE.body.getElementsByTagName('td')[0]");
        clickAlignCenterButton();
        waitForAlignCenterDetected(true);
        switchToSource();
        assertSourceText("|=(% style=\"text-align: justify;\" %)x\na|=b\n|(% style=\"text-align: center;\" %)c|d");
        switchToWysiwyg();

        selectNodeContents("XWE.body.getElementsByTagName('td')[0]");
        waitForAlignCenterDetected(true);
    }

    /**
     * Tests if more paragraphs can be aligned at once.
     */
    public void testAlignParagraphs()
    {
        switchToSource();
        setSourceText("ab\n\ncd");
        switchToWysiwyg();

        moveCaret("XWE.body.getElementsByTagName('p')[0].firstChild", 1);
        clickAlignCenterButton();
        waitForAlignCenterDetected(true);

        select("XWE.body.getElementsByTagName('p')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('p')[1].firstChild", 1);
        waitForAlignCenterDetected(false);
        clickAlignRightButton();
        waitForAlignRightDetected(true);
        switchToSource();
        assertSourceText("(% style=\"text-align: right;\" %)\nab\n\n(% style=\"text-align: right;\" %)\ncd");

        // Assert again the right alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        waitForAlignRightDetected(true);

        // Remove the right alignment (toggle off the 'Align right' button).
        select("XWE.body.getElementsByTagName('p')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('p')[1].firstChild", 1);
        clickAlignRightButton();
        waitForAlignRightDetected(false);
        switchToSource();
        assertSourceText("ab\n\ncd");
    }

    /**
     * Tests if more table cells can be aligned at once.
     */
    public void testAlignTableCells()
    {
        switchToSource();
        setSourceText("|ab|cd");
        switchToWysiwyg();

        moveCaret("XWE.body.getElementsByTagName('td')[1].firstChild", 2);
        clickAlignRightButton();
        waitForAlignRightDetected(true);

        select("XWE.body.getElementsByTagName('td')[0].firstChild", 0,
            "XWE.body.getElementsByTagName('td')[1].firstChild", 1);
        waitForAlignRightDetected(false);
        clickAlignCenterButton();
        waitForAlignCenterDetected(true);
        switchToSource();
        assertSourceText("|(% style=\"text-align: center;\" %)ab|(% style=\"text-align: center;\" %)cd");

        // Assert again the center alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        waitForAlignCenterDetected(true);

        // Remove the center alignment (toggle off the 'Align center' button).
        select("XWE.body.getElementsByTagName('td')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('td')[1].firstChild", 2);
        clickAlignCenterButton();
        waitForAlignCenterDetected(false);
        switchToSource();
        assertSourceText("|ab|cd");
    }

    /**
     * Makes a selection that includes a paragraph and a table cell and aligns them.
     */
    public void testSelectAndAlignParagraphAndTableCell()
    {
        switchToSource();
        setSourceText("ab\nxy\n\n|cd\n12|ef");
        switchToWysiwyg();

        // Align the paragraph to the right.
        clickAlignRightButton();
        waitForAlignRightDetected(true);

        // Select the paragraph and the first table cell and align them full.
        select("XWE.body.getElementsByTagName('p')[0].lastChild", 2,
            "XWE.body.getElementsByTagName('td')[0].firstChild", 0);
        waitForAlignRightDetected(false);
        clickAlignFullButton();
        waitForAlignFullDetected(true);
        switchToSource();
        assertSourceText("(% style=\"text-align: justify;\" %)\nab\nxy\n\n|(% style=\"text-align: justify;\" %)cd\n12|ef");

        // Assert again the full alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        waitForAlignFullDetected(true);

        // Remove the full alignment (toggle off the 'Align full' button).
        select("XWE.body.getElementsByTagName('p')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('td')[0].lastChild", 0);
        clickAlignFullButton();
        waitForAlignFullDetected(false);
        switchToSource();
        assertSourceText("ab\nxy\n\n|cd\n12|ef");
    }

    /**
     * Tests if a paragraph inside a table cell can be aligned.
     */
    public void testAlignParagraphInsideTableCell()
    {
        switchToSource();
        setSourceText("|(((ab\n\ncd)))|ef");
        switchToWysiwyg();

        // Place the caret inside the first paragraph from the first table cell.
        moveCaret("XWE.body.getElementsByTagName('p')[0].firstChild", 2);
        clickAlignRightButton();
        waitForAlignRightDetected(true);
        switchToSource();
        assertSourceText("|(((\n(% style=\"text-align: right;\" %)\nab\n\ncd\n)))|ef");

        // Assert again the right alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        waitForAlignRightDetected(true);
    }

    /**
     * Clicks the 'Align left' button from the tool bar.
     */
    protected void clickAlignLeftButton()
    {
        pushToolBarButton("Align Left");
    }

    /**
     * Clicks the 'Align center' button from the tool bar.
     */
    protected void clickAlignCenterButton()
    {
        pushToolBarButton("Centered");
    }

    /**
     * Clicks the 'Align right' button from the tool bar.
     */
    protected void clickAlignRightButton()
    {
        pushToolBarButton("Align Right");
    }

    /**
     * Clicks the 'Align full' button from the tool bar.
     */
    protected void clickAlignFullButton()
    {
        pushToolBarButton("Justified");
    }

    /**
     * Waits for the left alignment toggle button to have the specified state.
     * 
     * @param detected {@code true} to wait till the left alignment is detected, {@code false} to wait till it is
     *            undetected
     */
    protected void waitForAlignLeftDetected(boolean detected)
    {
        triggerToolbarUpdate();
        waitForToggleButtonState("Align Left", detected);
    }

    /**
     * Waits for the centered alignment toggle button to have the specified state.
     * 
     * @param detected {@code true} to wait till the centered alignment is detected, {@code false} to wait till it is
     *            undetected
     */
    protected void waitForAlignCenterDetected(boolean detected)
    {
        triggerToolbarUpdate();
        waitForToggleButtonState("Centered", detected);
    }

    /**
     * Waits for the right alignment toggle button to have the specified state.
     * 
     * @param detected {@code true} to wait till the right alignment is detected, {@code false} to wait till it is
     *            undetected
     */
    protected void waitForAlignRightDetected(boolean detected)
    {
        triggerToolbarUpdate();
        waitForToggleButtonState("Align Right", detected);
    }

    /**
     * Waits for the justified alignment toggle button to have the specified state.
     * 
     * @param detected {@code true} to wait till the justified alignment is detected, {@code false} to wait till it is
     *            undetected
     */
    protected void waitForAlignFullDetected(boolean detected)
    {
        triggerToolbarUpdate();
        waitForToggleButtonState("Justified", detected);
    }
}
