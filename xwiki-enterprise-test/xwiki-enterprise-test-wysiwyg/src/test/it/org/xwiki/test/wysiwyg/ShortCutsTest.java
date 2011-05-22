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

import org.xwiki.test.wysiwyg.framework.AbstractWysiwygTestCase;

public class ShortCutsTest extends AbstractWysiwygTestCase
{
    /**
     * @see XWIKI-5560: Shortcut key malfunction when saving a page within source view.
     */
    public void testShortCutsForSaveAndView()
    {
        // Set initial content to WYSIWYG editor
        setContent("<strong>foo</strong>");
        switchToWysiwyg(false);
        switchToWysiwyg();
        // Switch to source editor and change the contents.
        switchToSource();
        setSourceText("**changeInSource**");
        // type alt+s to save and view the contents.
        typeShortCutsforSaveAndView();
        // Open the Wiki editor and assert the content.
        clickEditPageInWikiSyntaxEditor();
        assertEquals("**changeInSource**", getFieldValue("content"));
    }

    /**
     * @see XWIKI-5560: Shortcut key malfunction when saving a page within source view.
     */
    public void testShortCutsForSaveAndContinue()
    {
        // Set initial content to WYSIWYG editor
        setContent("<strong>foo</strong>");
        switchToWysiwyg(false);
        switchToWysiwyg();
        // Switch to source editor and change the contents.
        switchToSource();
        setSourceText("**changeInSource**");
        // type alt+shift+s to save the contents and continue to edit.
        typeShortCutsforSaveAndContinue();
        // Open the edited wiki page.
        this.open("ShortCutsTest", "testShortCutsForSaveAndContinue");
        // Open the Wiki editor and assert the content.
        clickEditPageInWikiSyntaxEditor();
        assertEquals("**changeInSource**", getFieldValue("content"));
    }

    /**
     * Press Alt+s to save and view.
     */
    public void typeShortCutsforSaveAndView()
    {
        getSelenium().altKeyDown();
        typeKeyInSource("s", true, 1, false);
        getSelenium().altKeyUp();
    }

    /**
     * Press Alt+shift+s to save and continue.
     */
    public void typeShortCutsforSaveAndContinue()
    {
        getSelenium().altKeyDown();
        getSelenium().shiftKeyDown();
        typeKeyInSource("s", true, 1, false);
        getSelenium().shiftKeyUp();
        getSelenium().altKeyUp();
    }

    /**
     * Presses the specified key for the given number of times.
     * 
     * @param key the key to be pressed
     * @param fireKeyPress {@code true} if the specified key should generate a key press event, {@code false} otherwise.
     *            Normally only printable keys generate a key press event.
     * @param count the number of times to press the specified key
     * @param hold {@code false} if the key should be released after each key press, {@code true} if it should be hold
     *            down and released just at the end
     */
    public void typeKeyInSource(String key, boolean fireKeyPress, int count, boolean hold)
    {
        for (int i = 0; i < count; i++) {
            getSelenium().keyDown(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, key);
            if (fireKeyPress) {
                getSelenium().keyPress(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, key);
            }
            if (!hold) {
                getSelenium().keyUp(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, key);
            }
        }
        if (hold && count > 0) {
            getSelenium().keyUp(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, key);
        }
    }
}
