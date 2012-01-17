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
package org.xwiki.test.wysiwyg;

import java.awt.event.KeyEvent;

import org.xwiki.test.wysiwyg.framework.AbstractWysiwygTestCase;

/**
 * Functional tests for history support (undo/redo) inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class HistoryTest extends AbstractWysiwygTestCase
{
    /**
     * Basic integration test for the history mechanism.
     */
    public void testUndoRedo()
    {
        typeText("a b");
        typeTab();
        typeText("c");
        clickSymbolButton();
        getSelenium().click("//div[@title='copyright sign']");
        applyStyleTitle1();
        waitForPushButton(TOOLBAR_BUTTON_UNDO_TITLE, true);
        clickUndoButton(4);
        assertContent("a b<br>");
        clickUndoButton(3);
        assertContent("<br>");
        waitForPushButton(TOOLBAR_BUTTON_REDO_TITLE, true);
        clickRedoButton(7);
        assertContent("<h1>a b&nbsp;&nbsp;&nbsp; c\u00A9<br></h1>");
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
        setContent("March 9th, 2009");
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
        // The undo tool bar button is initially disabled because no action has been taken on the edited document. We
        // have to wait for it to become enabled because the tool bar is updated with delay after each edit action.
        waitForPushButton(TOOLBAR_BUTTON_UNDO_TITLE, true);
        getSelenium().metaKeyDown();
        typeText("ZZZ");
        getSelenium().metaKeyUp();

        // Redo 2 steps.
        // We have to wait for the redo tool bar button to become enabled because the tool bar is updated with delay
        // after an undo operation.
        waitForPushButton(TOOLBAR_BUTTON_REDO_TITLE, true);
        getSelenium().controlKeyDown();
        typeText("YY");
        getSelenium().controlKeyUp();

        switchToSource();
        assertSourceText("**//March//** 9th, 2009");
    }

    /**
     * Test if an undo step reverts only one paste operation from a sequence, and not all of them.
     */
    public void testUndoRepeatedPaste()
    {
        typeText("q");

        // NOTE: We have to use native keyboard events because otherwise the native copy & paste behavior is not
        // triggered. Also, the shortcut keys must use upper case letters.
        // Select all text.
        getSelenium().keyDownNative(String.valueOf(KeyEvent.VK_CONTROL));
        getSelenium().keyPressNative(String.valueOf((int) 'A'));
        // Copy selected text.
        getSelenium().keyPressNative(String.valueOf((int) 'C'));
        // Paste selected text 4 times.
        for (int i = 0; i < 4; i++) {
            getSelenium().keyPressNative(String.valueOf((int) 'V'));
        }
        getSelenium().keyUpNative(String.valueOf(KeyEvent.VK_CONTROL));

        // Native keyboard events are sent asynchronously so we must wait for the browser to handle them.
        waitForTextContains(getDOMLocator("body"), "qqqq");

        // The undo tool bar button is initially disabled because no action has been taken on the edited document. We
        // have to wait for it to become enabled because the tool bar is updated with delay after each edit action.
        waitForPushButton(TOOLBAR_BUTTON_UNDO_TITLE, true);
        clickUndoButton();
        switchToSource();
        assertSourceText("qqq");
    }
}
