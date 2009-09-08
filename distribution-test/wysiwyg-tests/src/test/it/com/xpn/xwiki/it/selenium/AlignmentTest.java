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
 * Functional tests for alignment support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class AlignmentTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Functional tests for alignment support inside the WYSIWYG editor.");
        suite.addTestSuite(AlignmentTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * Tests if the text directly under body can be aligned.
     */
    public void testAlignBody()
    {
        clickAlignCenterButton();
        typeText("a");
        assertWiki("(% style=\"text-align: center;\" %)\na");

        resetContent();

        typeText("a");
        clickAlignRightButton();
        typeText("b");
        assertWiki("(% style=\"text-align: right;\" %)\nab");

        resetContent();

        typeText("abc");
        select("XWE.body.firstChild", 1, "XWE.body.firstChild", 2);
        clickAlignFullButton();
        typeText("x");
        assertWiki("(% style=\"text-align: justify;\" %)\naxc");

        resetContent();

        typeText("a");
        typeShiftEnter();
        typeText("b");
        selectNode("XWE.body.childNodes[2]");
        clickAlignLeftButton();
        typeText("x");
        assertWiki("(% style=\"text-align: left;\" %)\na\nx");
    }

    /**
     * Tests if a single paragraph can be aligned.
     */
    public void testAlignParagraph()
    {
        applyStyleTitle1();
        applyStylePlainText();
        clickAlignCenterButton();
        typeText("a");
        assertTrue(isAlignCenterDetected());
        assertWiki("(% style=\"text-align: center;\" %)\na");

        // Assert again the center alignment after coming back from the Wiki editor.
        assertTrue(isAlignCenterDetected());

        typeText("b");
        typeShiftEnter();
        clickAlignRightButton();
        assertTrue(isAlignRightDetected());
        assertWiki("(% style=\"text-align: right;\" %)\nb\na");

        // Assert again the right alignment after coming back from the Wiki editor.
        assertTrue(isAlignRightDetected());

        selectNode("XWE.body.firstChild.lastChild");
        clickAlignFullButton();
        typeText("c");
        assertTrue(isAlignFullDetected());
        assertWiki("(% style=\"text-align: justify;\" %)\nb\nc");

        // Assert again the full alignment after coming back from the Wiki editor.
        assertTrue(isAlignFullDetected());

        // Remove the full alignment (toggle full alignment off).
        clickAlignFullButton();
        assertFalse(isAlignFullDetected());
        assertWiki("b\nc");

        typeText("x");
        clickAlignLeftButton();
        assertTrue(isAlignLeftDetected());
        assertWiki("(% style=\"text-align: left;\" %)\nxb\nc");

        // Assert again the left alignment after coming back from the Wiki editor.
        assertTrue(isAlignLeftDetected());
    }

    /**
     * Tests if a table cell can be aligned.
     */
    public void testAlignTableCell()
    {
        setWikiContent("|=a|=b\n|c|d");
        clickAlignRightButton();
        assertTrue(isAlignRightDetected());
        assertWiki("|=(% style=\"text-align: right;\" %)a|=b\n|c|d");

        // Assert again the right alignment after coming back from the Wiki editor.
        assertTrue(isAlignRightDetected());

        typeTextThenEnter("x");
        clickAlignFullButton();
        assertTrue(isAlignFullDetected());
        assertWiki("|=(% style=\"text-align: justify;\" %)x\na|=b\n|c|d");

        selectNodeContents("XWE.body.getElementsByTagName('td')[0]");
        clickAlignCenterButton();
        assertTrue(isAlignCenterDetected());
        assertWiki("|=(% style=\"text-align: justify;\" %)x\na|=b\n|(% style=\"text-align: center;\" %)c|d");

        selectNodeContents("XWE.body.getElementsByTagName('td')[0]");
        assertTrue(isAlignCenterDetected());
    }

    /**
     * Tests if more paragraphs can be aligned at once.
     */
    public void testAlignParagraphs()
    {
        setWikiContent("ab\n\ncd");

        moveCaret("XWE.body.getElementsByTagName('p')[0].firstChild", 1);
        clickAlignCenterButton();
        assertTrue(isAlignCenterDetected());

        select("XWE.body.getElementsByTagName('p')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('p')[1].firstChild", 1);
        assertFalse(isAlignCenterDetected());
        clickAlignRightButton();
        assertTrue(isAlignRightDetected());
        assertWiki("(% style=\"text-align: right;\" %)\nab\n\n(% style=\"text-align: right;\" %)\ncd");

        // Assert again the right alignment after coming back from the Wiki editor.
        assertTrue(isAlignRightDetected());

        // Remove the right alignment (toggle off the 'Align right' button).
        select("XWE.body.getElementsByTagName('p')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('p')[1].firstChild", 1);
        clickAlignRightButton();
        assertFalse(isAlignRightDetected());
        assertWiki("ab\n\ncd");
    }

    /**
     * Tests if more table cells can be aligned at once.
     */
    public void testAlignTableCells()
    {
        setWikiContent("|ab|cd");

        moveCaret("XWE.body.getElementsByTagName('td')[1].firstChild", 2);
        clickAlignRightButton();
        assertTrue(isAlignRightDetected());

        select("XWE.body.getElementsByTagName('td')[0].firstChild", 0,
            "XWE.body.getElementsByTagName('td')[1].firstChild", 1);
        assertFalse(isAlignRightDetected());
        clickAlignCenterButton();
        assertTrue(isAlignCenterDetected());
        assertWiki("|(% style=\"text-align: center;\" %)ab|(% style=\"text-align: center;\" %)cd");

        // Assert again the center alignment after coming back from the Wiki editor.
        assertTrue(isAlignCenterDetected());

        // Remove the center alignment (toggle off the 'Align center' button).
        select("XWE.body.getElementsByTagName('td')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('td')[1].firstChild", 2);
        clickAlignCenterButton();
        assertFalse(isAlignCenterDetected());
        assertWiki("|ab|cd");
    }

    /**
     * Makes a selection that includes a paragraph and a table cell and aligns them.
     */
    public void testSelectAndAlignParagraphAndTableCell()
    {
        setWikiContent("ab\nxy\n\n|cd\n12|ef");

        // Align the paragraph to the right.
        clickAlignRightButton();
        assertTrue(isAlignRightDetected());

        // Select the paragraph and the first table cell and align them full.
        select("XWE.body.getElementsByTagName('p')[0].lastChild", 2,
            "XWE.body.getElementsByTagName('td')[0].firstChild", 0);
        assertFalse(isAlignRightDetected());
        clickAlignFullButton();
        assertTrue(isAlignFullDetected());
        assertWiki("(% style=\"text-align: justify;\" %)\nab\nxy\n\n|(% style=\"text-align: justify;\" %)cd\n12|ef");

        // Assert again the full alignment after coming back from the Wiki editor.
        assertTrue(isAlignFullDetected());

        // Remove the full alignment (toggle off the 'Align full' button).
        select("XWE.body.getElementsByTagName('p')[0].firstChild", 1,
            "XWE.body.getElementsByTagName('td')[0].lastChild", 0);
        clickAlignFullButton();
        assertFalse(isAlignFullDetected());
        assertWiki("ab\nxy\n\n|cd\n12|ef");
    }

    /**
     * Tests if a paragraph inside a table cell can be aligned.
     */
    public void testAlignParagraphInsideTableCell()
    {
        setWikiContent("|(((ab\n\ncd)))|ef");

        // Place the caret inside the first paragraph from the first table cell.
        moveCaret("XWE.body.getElementsByTagName('p')[0].firstChild", 2);
        clickAlignRightButton();
        assertTrue(isAlignRightDetected());
        assertWiki("|((((% style=\"text-align: right;\" %)\nab\n\ncd)))|ef");

        // Assert again the right alignment after coming back from the Wiki editor.
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
