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

/**
 * Tests if the state of the WYSIWYG editor is preserved (cached) against the browser's Back button and the "soft" page
 * refresh.
 * 
 * @version $Id$
 */
public class CacheTest extends AbstractWysiwygTestCase
{
    /**
     * Locates the source text area.
     */
    private static final String SOURCE_TEXT_AREA_LOCATOR = "//textarea[contains(@class, 'xPlainTextEditor')]";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Functional tests for cache support inside the WYSIWYG editor.");
        suite.addTestSuite(CacheTest.class, ColibriSkinExecutor.class);
        return suite;
    }

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
        focusRichTextArea();

        // Type text and leave the editing by clicking on a link.
        typeText("2");
        getSelenium().click("//a[@title = 'Home']");
        waitPage();
        getSelenium().goBack();
        waitPage();
        focusRichTextArea();

        // Check the result.
        typeText("3");
        assertWiki("321");
    }

    /**
     * Test that the content of the source text area is preserved when the user leaves the editing without saving and
     * then comes back.
     */
    public void testPreserveUnsavedSourceAgainstBackButton()
    {
        switchToSource();

        // Type text and cancel edition.
        getSelenium().typeKeys(SOURCE_TEXT_AREA_LOCATOR, "a");
        clickEditCancelEdition();
        getSelenium().goBack();
        waitPage();
        switchToSource();

        // Type text and leave the editing by clicking on a link.
        getSelenium().typeKeys(SOURCE_TEXT_AREA_LOCATOR, "b");
        getSelenium().click("//a[@title = 'Home']");
        waitPage();
        getSelenium().goBack();
        waitPage();
        switchToSource();

        // Check the result.
        getSelenium().typeKeys(SOURCE_TEXT_AREA_LOCATOR, "c");
        // We need to switch to WYSIWYG tab because #assertWiki(String) is currently written to work from there.
        switchToWysiwyg();
        assertWiki("abc");
    }

    /**
     * Tests that the currently active editor (WYSIWYG or Source) is preserved when the user leaves the editing without
     * saving and then comes back.
     */
    public void testPreserveSelectedEditorAgainstBackButton()
    {
        // The WYSIWYG editor should be initially active.
        assertFalse(getSelenium().isEditable(SOURCE_TEXT_AREA_LOCATOR));

        // Switch to Source editor, cancel the edition and then come back.
        switchToSource();
        clickEditCancelEdition();
        getSelenium().goBack();
        waitPage();

        // The Source editor should be active now because it was selected before canceling the edition.
        assertTrue(getSelenium().isEditable(SOURCE_TEXT_AREA_LOCATOR));

        // Switch to WYSIWYG editor, leave editing and then come back.
        switchToWysiwyg();
        getSelenium().click("//a[@title = 'Home']");
        waitPage();
        getSelenium().goBack();
        waitPage();

        // The WYSIWYG editor should be active now because it was selected before we left the edit mode.
        assertFalse(getSelenium().isEditable(SOURCE_TEXT_AREA_LOCATOR));
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

        // Reload the page to be sure we don't have any post data on the session.
        reload();

        // Type text and refresh the page.
        focusRichTextArea();
        typeText("2");
        refresh();

        // Type more text and check the result.
        focusRichTextArea();
        typeText("1");
        assertWiki("12");
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

        // Reload the page to be sure we don't have any post data on the session.
        reload();

        // Type text and refresh the page.
        switchToSource();
        getSelenium().typeKeys(SOURCE_TEXT_AREA_LOCATOR, "1");
        refresh();

        // Type more text and check the result.
        switchToSource();
        getSelenium().typeKeys(SOURCE_TEXT_AREA_LOCATOR, "2");
        // We need to switch to WYSIWYG tab because #assertWiki(String) is currently written to work from there.
        switchToWysiwyg();
        assertWiki("12");
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

        // Reload the page to be sure we don't have any post data on the session.
        reload();

        // The WYSIWYG editor should be initially active.
        assertFalse(getSelenium().isEditable(SOURCE_TEXT_AREA_LOCATOR));

        // Switch to Source editor and refresh the page.
        switchToSource();
        refresh();

        // The Source editor should be active now because it was selected before the refresh.
        assertTrue(getSelenium().isEditable(SOURCE_TEXT_AREA_LOCATOR));

        // Switch to WYSIWYG editor and refresh the page again.
        switchToWysiwyg();
        refresh();

        // The WYSIWYG editor should be active now because it was selected before the refresh.
        assertFalse(getSelenium().isEditable(SOURCE_TEXT_AREA_LOCATOR));
    }

    /**
     * @see XWIKI-4162: When in edit mode (all editors) back/forward looses the content you have changed
     */
    public void testBackForwardCache()
    {
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
        focusRichTextArea();
        // Assert the text content.
        assertEquals("123", getEval("window.XWE.body.textContent"));
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
    }

    /**
     * Reloads the current page. By calling this method you loose any POST data found on the browser session.
     */
    private void reload()
    {
        getSelenium().open(getSelenium().getLocation());
        waitPage();
    }
}
