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
 * Integration tests for macro support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class MacroSupportTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Integration tests for macro support inside the WYSIWYG editor.");
        suite.addTestSuite(MacroSupportTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * Tests that after deleting the last character before a macro the caret remains before the macro and not inside the
     * macro.
     */
    public void testDeleteCharacterBeforeMacro()
    {
        switchToWikiEditor();
        setFieldValue("content", "a{{html}}b{{/html}}");
        switchToWysiwygEditor();
        typeDelete();
        typeText("x");
        switchToWikiEditor();
        assertEquals("x{{html}}b{{/html}}", getFieldValue("content"));
    }

    /**
     * Tests that by holding the Delete key down before a macro the caret doesn't get inside the macro, but, instead,
     * the macro is deleted.
     */
    public void testHoldDeleteKeyBeforeMacro()
    {
        switchToWikiEditor();
        setFieldValue("content", "c{{html}}def{{/html}}g");
        switchToWysiwygEditor();
        typeDelete(2, true);
        typeText("x");
        switchToWikiEditor();
        assertEquals("xg", getFieldValue("content"));
    }

    /**
     * Tests that after deleting with Backspace a text selection ending before a macro the caret remains before the
     * macro and not inside the macro.
     */
    public void testSelectCharacterBeforeMacroAndPressBackspace()
    {
        switchToWikiEditor();
        setFieldValue("content", "g{{html}}h{{/html}}");
        switchToWysiwygEditor();
        // Select the character preceding the macro.
        selectNode("XWE.body.firstChild.firstChild");
        typeBackspace();
        typeText("x");
        switchToWikiEditor();
        assertEquals("x{{html}}h{{/html}}", getFieldValue("content"));
    }

    /**
     * Tests that if we select the text before a macro and insert a symbol instead of it then the symbol is inserted
     * before the macro and not inside the macro.
     */
    public void testSelectCharacterBeforeMacroAndInsertSymbol()
    {
        switchToWikiEditor();
        setFieldValue("content", "i{{html}}j{{/html}}");
        switchToWysiwygEditor();
        // Select the character preceding the macro.
        selectNode("XWE.body.firstChild.firstChild");
        clickSymbolButton();
        getSelenium().click("//div[@title='copyright sign']");
        typeText("x");
        switchToWikiEditor();
        assertEquals("\u00A9x{{html}}j{{/html}}", getFieldValue("content"));
    }

    /**
     * Tests that a macro can be deleted by pressing Delete key when the caret is placed before that macro.
     */
    public void testPressDeleteJustBeforeMacro()
    {
        switchToWikiEditor();
        setFieldValue("content", "{{html}}k{{/html}}l");
        switchToWysiwygEditor();
        typeDelete();
        typeText("x");
        switchToWikiEditor();
        assertEquals("xl", getFieldValue("content"));
    }

    /**
     * Tests that after deleting the last character after a macro the caret remains after the macro and not inside the
     * macro.
     */
    public void testDeleteCharacterAfterMacro()
    {
        switchToWikiEditor();
        setFieldValue("content", "a{{html}}b{{/html}}c");
        switchToWysiwygEditor();
        // Move the caret at the end.
        moveCaret("XWE.body.firstChild.lastChild", 1);
        typeBackspace();
        typeText("x");
        switchToWikiEditor();
        assertEquals("a{{html}}b{{/html}}x", getFieldValue("content"));
    }

    /**
     * Tests that by holding the Backspace key down after a macro the caret doesn't get inside the macro, but, instead,
     * the macro is deleted.
     */
    public void testHoldBackspaceKeyAfterMacro()
    {
        switchToWikiEditor();
        setFieldValue("content", "c{{html}}def{{/html}}g");
        switchToWysiwygEditor();
        // Move the caret at the end.
        moveCaret("XWE.body.firstChild.lastChild", 1);
        typeBackspace(2, true);
        typeText("x");
        switchToWikiEditor();
        assertEquals("cx", getFieldValue("content"));
    }

    /**
     * Tests that after deleting with Delete key a text selection starting after a macro the caret remains after the
     * macro and not inside the macro.
     */
    public void testSelectCharacterAfterMacroAndPressDelete()
    {
        switchToWikiEditor();
        setFieldValue("content", "g{{html}}h{{/html}}i");
        switchToWysiwygEditor();
        // Select the character following the macro.
        selectNode("XWE.body.firstChild.lastChild");
        typeDelete();
        typeText("x");
        switchToWikiEditor();
        assertEquals("g{{html}}h{{/html}}x", getFieldValue("content"));
    }

    /**
     * Tests that if we select the text after a macro and insert a symbol instead of it then the symbol is inserted
     * after the macro and not inside the macro.
     */
    public void testSelectCharacterAfterMacroAndInsertSymbol()
    {
        switchToWikiEditor();
        setFieldValue("content", "i{{html}}j{{/html}}k");
        switchToWysiwygEditor();
        // Select the character following the macro.
        selectNode("XWE.body.firstChild.lastChild");
        clickSymbolButton();
        getSelenium().click("//div[@title='copyright sign']");
        typeText("x");
        switchToWikiEditor();
        assertEquals("i{{html}}j{{/html}}\u00A9x", getFieldValue("content"));
    }

    /**
     * Tests that a macro can be deleted by pressing Backspace key when the caret is placed after that macro.
     */
    public void testPressBackspaceJustAfterMacro()
    {
        switchToWikiEditor();
        setFieldValue("content", "k{{html}}l{{/html}}m");
        switchToWysiwygEditor();
        // Move the caret at the end.
        moveCaret("XWE.body.firstChild.lastChild", 0);
        typeBackspace();
        typeText("x");
        switchToWikiEditor();
        assertEquals("kxm", getFieldValue("content"));
    }

    /**
     * Tests that Undo/Redo operations don't affect the macros present in the edited document.
     */
    public void testUndoRedoWhenMacrosArePresent()
    {
        switchToWikiEditor();
        setFieldValue("content", "{{html}}pq{{/html}}");
        switchToWysiwygEditor();
        applyStyleParagraph();
        typeText("uv");
        clickUndoButton();
        clickRedoButton();
        switchToWikiEditor();
        assertEquals("uv{{html}}pq{{/html}}", getFieldValue("content"));
    }

    /**
     * Clicks on a macro and deletes it.
     */
    public void testSelectAndDeleteMacro()
    {
        switchToWikiEditor();
        setFieldValue("content", "{{html}}<p>foo</p>{{/html}}\n\nbar");
        switchToWysiwygEditor();
        getSelenium().clickAt(getDOMLocator("getElementsByTagName('button')[0]"), "0, 0");
        typeDelete();
        switchToWikiEditor();
        assertEquals("bar", getFieldValue("content"));
    }

    /**
     * @see XWIKI-3221: New lines inside code macro are lost when saving
     */
    public void testWhiteSpacesInsideCodeMacroArePreserved()
    {
        switchToWikiEditor();
        String wikiText = "{{code}}\nfunction foo() {\n    alert('bar');\n}\n{{/code}}";
        setFieldValue("content", wikiText);
        switchToWysiwygEditor();
        switchToWikiEditor();
        assertEquals(wikiText, getFieldValue("content"));
    }
}
