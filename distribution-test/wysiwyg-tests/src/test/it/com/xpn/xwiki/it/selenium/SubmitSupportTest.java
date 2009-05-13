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
 * Functional tests for the submit support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class SubmitSupportTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Functional tests for the submit support inside the WYSIWYG editor.");
        suite.addTestSuite(SubmitSupportTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * Loads the editor and submits its content without changing it.
     */
    public void testSubmitAfterEditorIsLoadedAndHasFocus()
    {
        clickLinkWithText("Wiki", true);
        setFieldValue("content", "a**b**c");
        clickLinkWithText("WYSIWYG", true);
        // Focus the editor after the page has finished loading.
        getSelenium().getEval("triggerEvent(window." + getDOMLocator("defaultView") + ", 'focus', false);");
        waitForCondition("window." + getDOMLocator("defaultView") + ".getSelection().rangeCount > 0");
        // Switch back to Wiki editor and assert the content.
        clickLinkWithText("Wiki", true);
        assertEquals("a**b**c", getFieldValue("content"));
    }

    /**
     * Loads the editor and submits its content without changing it and without focusing the rich text area.
     */
    public void testSubmitAfterEditorIsLoadedWithoutGainingFocus()
    {
        clickLinkWithText("Wiki", true);
        setFieldValue("content", "1**2**3");
        // Switch to WYSIWYG editor but don't focus the rich text area.
        clickLinkWithText("WYSIWYG", true);
        // Switch back to Wiki editor and assert the content.
        clickLinkWithText("Wiki", true);
        assertEquals("1**2**3", getFieldValue("content"));
    }

    /**
     * Loads the editor and submits its content. We test if the content of the rich text area is stored when the rich
     * text area looses focus.
     */
    public void testSubmitAfterChangingContentWithFocus()
    {
        // We go to the Wiki editor and come back to be sure we fully control how the WYSIWYG editor is loaded.
        clickLinkWithText("Wiki", true);
        clickLinkWithText("WYSIWYG", true);
        // Focus the editor after the page has finished loading.
        getSelenium().getEval("triggerEvent(window." + getDOMLocator("defaultView") + ", 'focus', false);");
        waitForCondition("window." + getDOMLocator("defaultView") + ".getSelection().rangeCount > 0");
        // Change the content of the rich text area.
        runScript("XWE.body.innerHTML = 'x<em>y</em>z';");
        // Blur the rich text area to save the new content.
        getSelenium().getEval("triggerEvent(window." + getDOMLocator("defaultView") + ", 'blur', false);");
        // Switch back to Wiki editor and assert the content.
        clickLinkWithText("Wiki", true);
        assertEquals("x//y//z", getFieldValue("content"));
    }

    /**
     * Loads the editor and submits its content after changing it without focusing the rich text area. We test if the
     * content of the rich text area is stored when the HTML form hosting the rich text area is submitted.
     */
    public void testSubmitAfterChangingContentWithoutFocus()
    {
        // We go to the Wiki editor and come back to be sure we fully control how the WYSIWYG editor is loaded.
        clickLinkWithText("Wiki", true);
        clickLinkWithText("WYSIWYG", true);
        // Focus the title input to be sure the rich text area doesn't have the focus when its content is changed.
        getSelenium().click("title");
        // Change the content of the rich text area when it doesn't have the focus.
        runScript("XWE.body.innerHTML = 'u<tt>v</tt>w';");
        // Save and view.
        clickEditSaveAndView();
        // Open the Wiki editor and assert the content.
        clickLinkWithText("Wiki", true);
        assertEquals("u##v##w", getFieldValue("content"));
    }
}
