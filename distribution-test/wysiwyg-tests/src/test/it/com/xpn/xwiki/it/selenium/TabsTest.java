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

public class TabsTest extends AbstractWysiwygTestCase
{
    public void testMultipleClicksOnTheSameTab()
    {
        setContent("<strong>foo</strong>");
        switchToWysiwyg(false);
        switchToWysiwyg();
        assertWiki("**foo**");
    }

    /**
     * Tests that XWIKI-3834 remains fixed.
     */
    public void testMultipleSwitches()
    {
        StringBuffer content = new StringBuffer();
        // We put quite a lot of content so that the conversion is not immediate.
        content.append("<strong>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor "
            + "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation "
            + "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in "
            + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non "
            + "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        content.append("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt "
            + "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco "
            + "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in "
            + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non "
            + "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        content.append("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt "
            + "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco "
            + "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in "
            + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non "
            + "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        content.append("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt "
            + "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco "
            + "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in "
            + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non "
            + "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</strong>");
        setContent(content.toString());

        // We go back and forth multiple times to be sure that it is not a matter of chance.
        switchToSource(false);
        switchToWysiwyg(false);
        switchToSource(false);
        switchToWysiwyg(false);
        switchToSource();

        assertFalse(getSourceText().contains("strong"));
    }

    /**
     * @see XWIKI-4079: Links are lost when switching to Source in the WYSIWYG editor.
     */
    public void testLinksAreNotLostWhenSwitchingToSourceTab()
    {
        String content = "Visit [[XWiki>>http://www.xwiki.org]] and our [[blog>>Blog.WebHome]].";
        setWikiContent(content);
        switchToSource();
        assertSource(content);
    }

    /**
     * @see XWIKI-4392: Place the caret at the beginning of the content when swtching to WYSIWYG Source editor.
     */
    public void testCaretAtStartAfterSwitchToSourceTab()
    {
        typeText("2");
        switchToSource();
        getSelenium().typeKeys(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, "1");
        assertSource("12");
    }

    /**
     * @see XWIKI-3965: Relative images are not displayed when switching from Source tab to Wysiwyg tab.
     */
    public void testContextDocumentIsPreserved()
    {
        // Uploading an image to the current document is difficult. Instead we use a context sensitive velocity script.
        setWikiContent("{{velocity}}$doc.fullName{{/velocity}}");
        String expected = getEval("window.XWE.body.textContent");
        switchToSource();
        switchToWysiwyg();
        assertEquals(expected, getEval("window.XWE.body.textContent"));
    }

    /**
     * @return the text from the source text area
     */
    private String getSourceText()
    {
        return getSelenium().getValue(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA);
    }

    /**
     * Asserts that the source text area has the given value.
     * 
     * @param expectedSourceText the expected value of the source text area
     */
    private void assertSource(String expectedSourceText)
    {
        assertEquals(expectedSourceText, getSourceText());
    }
}
