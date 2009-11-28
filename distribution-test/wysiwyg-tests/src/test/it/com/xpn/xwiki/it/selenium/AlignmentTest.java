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

        // Check the default alignment.
        boolean defaultAlignFull = isAlignFullDetected();

        // Center text.
        clickAlignCenterButton();
        typeText("a");
        assertTrue(isAlignCenterDetected());
        switchToSource();
        assertSourceText("(% style=\"text-align: center;\" %)\na");

        // Assert again the center alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        assertTrue(isAlignCenterDetected());

        typeText("b");
        typeShiftEnter();
        clickAlignRightButton();
        assertTrue(isAlignRightDetected());
        switchToSource();
        assertSourceText("(% style=\"text-align: right;\" %)\nb\na");

        // Assert again the right alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        assertTrue(isAlignRightDetected());

        selectNode("XWE.body.firstChild.childNodes[2]");
        clickAlignFullButton();
        typeText("c");
        assertTrue(isAlignFullDetected());
        switchToSource();
        assertSourceText("(% style=\"text-align: justify;\" %)\nb\nc");

        // Assert again the full alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        assertTrue(isAlignFullDetected());

        // Remove the full alignment (toggle full alignment off).
        clickAlignFullButton();
        // If paragraphs are justified by default then the "Justified" button remains toggled.
        assertEquals(defaultAlignFull, isAlignFullDetected());
        switchToSource();
        assertSourceText("b\nc");
        switchToWysiwyg();

        typeText("x");
        clickAlignLeftButton();
        assertTrue(isAlignLeftDetected());
        switchToSource();
        assertSourceText("(% style=\"text-align: left;\" %)\nxb\nc");

        // Assert again the left alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        assertTrue(isAlignLeftDetected());
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
        assertTrue(isAlignRightDetected());
        switchToSource();
        assertSourceText("|=(% style=\"text-align: right;\" %)a|=b\n|c|d");

        // Assert again the right alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        assertTrue(isAlignRightDetected());

        typeTextThenEnter("x");
        clickAlignFullButton();
        assertTrue(isAlignFullDetected());
        switchToSource();
        assertSourceText("|=(% style=\"text-align: justify;\" %)x\na|=b\n|c|d");
        switchToWysiwyg();

        selectNodeContents("XWE.body.getElementsByTagName('td')[0]");
        clickAlignCenterButton();
        assertTrue(isAlignCenterDetected());
        switchToSource();
        assertSourceText("|=(% style=\"text-align: justify;\" %)x\na|=b\n|(% style=\"text-align: center;\" %)c|d");
        switchToWysiwyg();

        selectNodeContents("XWE.body.getElementsByTagName('td')[0]");
        assertTrue(isAlignCenterDetected());
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
        assertTrue(isAlignCenterDetected());

        select("XWE.body.getElementsByTagName('p')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('p')[1].firstChild", 1);
        assertFalse(isAlignCenterDetected());
        clickAlignRightButton();
        assertTrue(isAlignRightDetected());
        switchToSource();
        assertSourceText("(% style=\"text-align: right;\" %)\nab\n\n(% style=\"text-align: right;\" %)\ncd");

        // Assert again the right alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        assertTrue(isAlignRightDetected());

        // Remove the right alignment (toggle off the 'Align right' button).
        select("XWE.body.getElementsByTagName('p')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('p')[1].firstChild", 1);
        clickAlignRightButton();
        assertFalse(isAlignRightDetected());
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
        assertTrue(isAlignRightDetected());

        select("XWE.body.getElementsByTagName('td')[0].firstChild", 0,
            "XWE.body.getElementsByTagName('td')[1].firstChild", 1);
        assertFalse(isAlignRightDetected());
        clickAlignCenterButton();
        assertTrue(isAlignCenterDetected());
        switchToSource();
        assertSourceText("|(% style=\"text-align: center;\" %)ab|(% style=\"text-align: center;\" %)cd");

        // Assert again the center alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        assertTrue(isAlignCenterDetected());

        // Remove the center alignment (toggle off the 'Align center' button).
        select("XWE.body.getElementsByTagName('td')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('td')[1].firstChild", 2);
        clickAlignCenterButton();
        assertFalse(isAlignCenterDetected());
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
        assertTrue(isAlignRightDetected());

        // Select the paragraph and the first table cell and align them full.
        select("XWE.body.getElementsByTagName('p')[0].lastChild", 2,
            "XWE.body.getElementsByTagName('td')[0].firstChild", 0);
        assertFalse(isAlignRightDetected());
        clickAlignFullButton();
        assertTrue(isAlignFullDetected());
        switchToSource();
        assertSourceText("(% style=\"text-align: justify;\" %)\nab\nxy\n\n|(% style=\"text-align: justify;\" %)cd\n12|ef");

        // Assert again the full alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        assertTrue(isAlignFullDetected());

        // Remove the full alignment (toggle off the 'Align full' button).
        select("XWE.body.getElementsByTagName('p')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('td')[0].lastChild", 0);
        clickAlignFullButton();
        assertFalse(isAlignFullDetected());
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
        assertTrue(isAlignRightDetected());
        switchToSource();
        assertSourceText("|(((\n(% style=\"text-align: right;\" %)\nab\n\ncd\n)))|ef");

        // Assert again the right alignment after coming back to WYSIWYG editor.
        switchToWysiwyg();
        assertTrue(isAlignRightDetected());
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
     * @return {@code true} if the WYSIWYG editor detects the left alignment of the current selection, {@code false}
     *         otherwise
     */
    protected boolean isAlignLeftDetected()
    {
        triggerToolbarUpdate();
        return isToggleButtonDown("Align Left");
    }

    /**
     * @return {@code true} if the WYSIWYG editor detects the center alignment of the current selection, {@code false}
     *         otherwise
     */
    protected boolean isAlignCenterDetected()
    {
        triggerToolbarUpdate();
        return isToggleButtonDown("Centered");
    }

    /**
     * @return {@code true} if the WYSIWYG editor detects the right alignment of the current selection, {@code false}
     *         otherwise
     */
    protected boolean isAlignRightDetected()
    {
        triggerToolbarUpdate();
        return isToggleButtonDown("Align Right");
    }

    /**
     * @return {@code true} if the WYSIWYG editor detects the full alignment of the current selection, {@code false}
     *         otherwise
     */
    protected boolean isAlignFullDetected()
    {
        triggerToolbarUpdate();
        return isToggleButtonDown("Justified");
    }
}
