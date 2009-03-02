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
        setWikiContent("a{{html}}b{{/html}}");
        typeDelete();
        typeText("x");
        assertWiki("x{{html}}b{{/html}}");
    }

    /**
     * Tests that by holding the Delete key down before a macro the caret doesn't get inside the macro, but, instead,
     * the macro is deleted.
     */
    public void testHoldDeleteKeyBeforeMacro()
    {
        setWikiContent("c{{html}}def{{/html}}g");
        typeDelete(2, true);
        typeText("x");
        assertWiki("xg");
    }

    /**
     * Tests that after deleting with Backspace a text selection ending before a macro the caret remains before the
     * macro and not inside the macro.
     */
    public void testSelectCharacterBeforeMacroAndPressBackspace()
    {
        setWikiContent("g{{html}}h{{/html}}");
        // Select the character preceding the macro.
        selectNode("XWE.body.firstChild.firstChild");
        typeBackspace();
        typeText("x");
        assertWiki("x{{html}}h{{/html}}");
    }

    /**
     * Tests that if we select the text before a macro and insert a symbol instead of it then the symbol is inserted
     * before the macro and not inside the macro.
     */
    public void testSelectCharacterBeforeMacroAndInsertSymbol()
    {
        setWikiContent("i{{html}}j{{/html}}");
        // Select the character preceding the macro.
        selectNode("XWE.body.firstChild.firstChild");
        clickSymbolButton();
        getSelenium().click("//div[@title='copyright sign']");
        typeText("x");
        assertWiki("\u00A9x{{html}}j{{/html}}");
    }

    /**
     * Tests that a macro can be deleted by pressing Delete key when the caret is placed before that macro.
     */
    public void testPressDeleteJustBeforeMacro()
    {
        setWikiContent("{{html}}k{{/html}}l");
        typeDelete();
        typeText("x");
        assertWiki("xl");
    }

    /**
     * Tests that after deleting the last character after a macro the caret remains after the macro and not inside the
     * macro.
     */
    public void testDeleteCharacterAfterMacro()
    {
        setWikiContent("a{{html}}b{{/html}}c");
        // Move the caret at the end.
        moveCaret("XWE.body.firstChild.lastChild", 1);
        typeBackspace();
        typeText("x");
        assertWiki("a{{html}}b{{/html}}x");
    }

    /**
     * Tests that by holding the Backspace key down after a macro the caret doesn't get inside the macro, but, instead,
     * the macro is deleted.
     */
    public void testHoldBackspaceKeyAfterMacro()
    {
        setWikiContent("c{{html}}def{{/html}}g");
        // Move the caret at the end.
        moveCaret("XWE.body.firstChild.lastChild", 1);
        typeBackspace(2, true);
        typeText("x");
        assertWiki("cx");
    }

    /**
     * Tests that after deleting with Delete key a text selection starting after a macro the caret remains after the
     * macro and not inside the macro.
     */
    public void testSelectCharacterAfterMacroAndPressDelete()
    {
        setWikiContent("g{{html}}h{{/html}}i");
        // Select the character following the macro.
        selectNode("XWE.body.firstChild.lastChild");
        typeDelete();
        typeText("x");
        assertWiki("g{{html}}h{{/html}}x");
    }

    /**
     * Tests that if we select the text after a macro and insert a symbol instead of it then the symbol is inserted
     * after the macro and not inside the macro.
     */
    public void testSelectCharacterAfterMacroAndInsertSymbol()
    {
        setWikiContent("i{{html}}j{{/html}}k");
        // Select the character following the macro.
        selectNode("XWE.body.firstChild.lastChild");
        clickSymbolButton();
        getSelenium().click("//div[@title='copyright sign']");
        typeText("x");
        assertWiki("i{{html}}j{{/html}}\u00A9x");
    }

    /**
     * Tests that a macro can be deleted by pressing Backspace key when the caret is placed after that macro.
     */
    public void testPressBackspaceJustAfterMacro()
    {
        setWikiContent("k{{html}}l{{/html}}m");
        // Move the caret at the end.
        moveCaret("XWE.body.firstChild.lastChild", 0);
        typeBackspace();
        typeText("x");
        assertWiki("kxm");
    }

    /**
     * Tests that Undo/Redo operations don't affect the macros present in the edited document.
     */
    public void testUndoRedoWhenMacrosArePresent()
    {
        setWikiContent("{{html}}pq{{/html}}");
        applyStyleParagraph();
        typeText("uv");
        clickUndoButton();
        clickRedoButton();
        assertWiki("uv{{html}}pq{{/html}}");
    }

    /**
     * Clicks on a macro and deletes it.
     */
    public void testSelectAndDeleteMacro()
    {
        setWikiContent("{{html}}<p>foo</p>{{/html}}\n\nbar");
        getSelenium().clickAt(getDOMLocator("getElementsByTagName('button')[0]"), "0, 0");
        typeDelete();
        assertWiki("bar");
    }

    /**
     * @see XWIKI-3221: New lines inside code macro are lost when saving
     */
    public void testWhiteSpacesInsideCodeMacroArePreserved()
    {
        String wikiText = "{{code}}\nfunction foo() {\n    alert('bar');\n}\n{{/code}}";
        setWikiContent(wikiText);
        assertWiki(wikiText);
    }
}
