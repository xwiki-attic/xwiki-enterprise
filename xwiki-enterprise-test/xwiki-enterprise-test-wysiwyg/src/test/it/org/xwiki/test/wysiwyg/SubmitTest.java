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
 * Functional tests for the submit support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class SubmitTest extends AbstractWysiwygTestCase
{
    /**
     * Loads the editor and submits its content without changing it.
     */
    public void testSubmitAfterEditorIsLoadedAndHasFocus()
    {
        // Set the content without saving it.
        clickEditPageInWikiSyntaxEditor();
        setFieldValue("content", "a**b**c");
        clickEditPageInWysiwyg();
        waitForEditorToLoad();
        // Focus the editor.
        focus(getDOMLocator("defaultView"));
        // Switch back to Wiki editor and assert the content.
        clickEditPageInWikiSyntaxEditor();
        assertEquals("a**b**c", getFieldValue("content"));
    }

    /**
     * Loads the editor and submits its content without changing it and without focusing the rich text area.
     */
    public void testSubmitAfterEditorIsLoadedWithoutGainingFocus()
    {
        // Set the content without saving it.
        clickEditPageInWikiSyntaxEditor();
        setFieldValue("content", "1**2**3");
        // Switch to WYSIWYG editor but don't focus the rich text area.
        clickEditPageInWysiwyg();
        waitForEditorToLoad();
        // Switch back to Wiki editor and assert the content.
        clickEditPageInWikiSyntaxEditor();
        assertEquals("1**2**3", getFieldValue("content"));
    }

    /**
     * Loads the editor and submits its content. We test if the content of the rich text area is stored when the rich
     * text area looses focus.
     */
    public void testSubmitAfterChangingContentWithFocus()
    {
        // Focus the editor.
        focus(getDOMLocator("defaultView"));
        // Change the content of the rich text area.
        setContent("x<em>y</em>z");
        // Blur the rich text area to save the new content.
        blur(getDOMLocator("defaultView"));
        // Switch back to Wiki editor and assert the content.
        clickEditPageInWikiSyntaxEditor();
        assertEquals("x//y//z", getFieldValue("content"));
    }

    /**
     * Loads the editor and submits its content after changing it without focusing the rich text area. We test if the
     * content of the rich text area is stored when the HTML form hosting the rich text area is submitted.
     */
    public void testSubmitAfterChangingContentWithoutFocus()
    {
        // Focus the title input to be sure the rich text area doesn't have the focus when its content is changed.
        getSelenium().click("title");
        // Change the content of the rich text area when it doesn't have the focus.
        setContent("u<tt>v</tt>w");
        // Save and view.
        clickEditSaveAndView();
        // Open the Wiki editor and assert the content.
        clickEditPageInWikiSyntaxEditor();
        assertEquals("u##v##w", getFieldValue("content"));
    }
}
