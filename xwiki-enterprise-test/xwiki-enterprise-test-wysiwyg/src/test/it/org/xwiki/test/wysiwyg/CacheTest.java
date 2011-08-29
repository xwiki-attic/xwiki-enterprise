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

/**
 * Tests if the state of the WYSIWYG editor is preserved (cached) against the browser's Back button and the "soft" page
 * refresh.
 * 
 * @version $Id$
 */
public class CacheTest extends AbstractWysiwygTestCase
{
    /**
     * Test that the content of the rich text area is preserved when the user leaves the editing without saving and then
     * comes back.
     */
    public void testPreserveUnsavedRichContentAgainstBackButton()
    {
        // Type text and cancel edition.
        typeText("1");
        clickEditCancelEdition();
        getSelenium().goBack();
        waitPage();
        waitForEditorToLoad();

        // Type text and leave the editing by clicking on a link.
        typeText("2");
        getSelenium().click("//a[@title = 'Home']");
        waitPage();
        getSelenium().goBack();
        waitPage();
        waitForEditorToLoad();

        // Check the result.
        typeText("3");
        switchToSource();
        assertSourceText("321");
    }

    /**
     * Test that the content of the source text area is preserved when the user leaves the editing without saving and
     * then comes back.
     */
    public void testPreserveUnsavedSourceAgainstBackButton()
    {
        switchToSource();

        // Type text and cancel edition.
        getSelenium().typeKeys(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, "a");
        clickEditCancelEdition();
        getSelenium().goBack();
        waitPage();
        waitForEditorToLoad();

        // Type text and leave the editing by clicking on a link.
        getSelenium().typeKeys(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, "b");
        getSelenium().click("//a[@title = 'Home']");
        waitPage();
        getSelenium().goBack();
        waitPage();
        waitForEditorToLoad();

        // Check the result.
        getSelenium().typeKeys(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, "c");
        assertSourceText("cba");
    }

    /**
     * Tests that the currently active editor (WYSIWYG or Source) is preserved when the user leaves the editing without
     * saving and then comes back.
     */
    public void testPreserveSelectedEditorAgainstBackButton()
    {
        // The WYSIWYG editor should be initially active.
        assertFalse(getSelenium().isEditable(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA));

        // Switch to Source editor, cancel the edition and then come back.
        switchToSource();
        clickEditCancelEdition();
        getSelenium().goBack();
        waitPage();
        waitForEditorToLoad();

        // The Source editor should be active now because it was selected before canceling the edition.
        assertTrue(getSelenium().isEditable(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA));

        // Switch to WYSIWYG editor, leave editing and then come back.
        switchToWysiwyg();
        getSelenium().click("//a[@title = 'Home']");
        waitPage();
        getSelenium().goBack();
        waitPage();
        waitForEditorToLoad();

        // The WYSIWYG editor should be active now because it was selected before we left the edit mode.
        assertFalse(getSelenium().isEditable(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA));
    }

    /**
     * Test that the content of the rich text area is preserved when the user refreshes the page.
     */
    public void testPreserveUnsavedRichContentAgainstRefresh()
    {
        if (!isBrowserWindowFocused()) {
            // Refreshing the page by pressing F5 requires the browser window to be focused.
            return;
        }

        // Type text and refresh the page.
        typeText("2");
        refresh();

        // Type more text and check the result.
        typeText("1");
        switchToSource();
        assertSourceText("12");
    }

    /**
     * Test that the content of the source text area is preserved when the user refreshes the page.
     */
    public void testPreserveUnsavedSourceAgainstRefresh()
    {
        if (!isBrowserWindowFocused()) {
            // Refreshing the page by pressing F5 requires the browser window to be focused.
            return;
        }

        // Type text and refresh the page.
        switchToSource();
        getSelenium().typeKeys(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, "1");
        refresh();

        // Type more text and check the result.
        switchToSource();
        getSelenium().typeKeys(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, "2");
        assertSourceText("12");
    }

    /**
     * Tests that the currently active editor (WYSIWYG or Source) is preserved when the user refreshes the page.
     */
    public void testPreserveSelectedEditorAgainstRefresh()
    {
        if (!isBrowserWindowFocused()) {
            // Refreshing the page by pressing F5 requires the browser window to be focused.
            return;
        }

        // The WYSIWYG editor should be initially active.
        assertFalse(getSelenium().isEditable(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA));

        // Switch to Source editor and refresh the page.
        switchToSource();
        refresh();

        // The Source editor should be active now because it was selected before the refresh.
        assertTrue(getSelenium().isEditable(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA));

        // Switch to WYSIWYG editor and refresh the page again.
        switchToWysiwyg();
        refresh();

        // The WYSIWYG editor should be active now because it was selected before the refresh.
        assertFalse(getSelenium().isEditable(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA));
    }

    /**
     * @see XWIKI-4162: When in edit mode (all editors) back/forward looses the content you have changed
     */
    public void testBackForwardCache()
    {
        // Make sure we can go back.
        clickEditCancelEdition();
        // We expect the default editor to be the WYSIWYG.
        clickLinkWithText("edit this page");
        waitForEditorToLoad();
        // Write some text.
        typeText("123");
        // Go back.
        getSelenium().goBack();
        waitPage();
        // Go forward.
        // See http://jira.openqa.org/browse/SEL-543 (Simulate forward button in browser).
        getSelenium().getEval("selenium.browserbot.goForward()");
        waitPage();
        // Make sure the rich text area is loaded.
        waitForEditorToLoad();
        // Assert the text content.
        assertEquals("123", getSelenium().getEval("window." + getDOMLocator("body.textContent")));
    }

    /**
     * Refreshes the current page by pressing the F5 key, which is not the same as calling {@code
     * getSelenium().refresh()}.
     */
    private void refresh()
    {
        // Make sure only the window caches the F5 key.
        getSelenium().focus("document.defaultView");
        // Press the F5 key.
        getSelenium().keyPressNative("116");
        // Wait for the page to load.
        waitPage();
        waitForEditorToLoad();
    }
}
