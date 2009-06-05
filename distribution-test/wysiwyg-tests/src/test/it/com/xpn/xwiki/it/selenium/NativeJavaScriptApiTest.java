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
 * Functional tests for the native JavaScript API exposed by the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class NativeJavaScriptApiTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests wysiwyg native JS API");
        suite.addTestSuite(NativeJavaScriptApiTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractWysiwygTestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        // Clear the page content.
        clickLinkWithText("Wiki", true);
        setFieldValue("content", "");
        clickEditSaveAndView();
    }

    /**
     * Functional tests for:
     * <ul>
     * <li>WysiwygEditor#getPlainTextArea()</li>
     * <li>WysiwygEditor#getRichTextArea()</li>
     * </ul>
     * .
     */
    public void testTextAreaElementsGetters()
    {
        insertEditor("editor", "displayTabs: true");

        // Test plain text editor.
        assertEquals("xPlainTextEditor", getEval("window.editor.getPlainTextArea().className"));
        assertEquals("textarea", getEval("window.editor.getPlainTextArea().nodeName").toLowerCase());

        // Test rich text editor.
        assertEquals("gwt-RichTextArea", getEval("window.editor.getRichTextArea().className"));
        assertEquals("iframe", getEval("window.editor.getRichTextArea().nodeName").toLowerCase());
    }

    /**
     * Functional test for {@code WysiwygEditor#getSourceText()} when the rich text area is enabled. The rich text area
     * is disable when the source tab is active.
     */
    public void testGetSourceTextWhenRichTextAreaIsEnabled()
    {
        insertEditor("editor", "syntax: 'xwiki/2.0'");
        setContent("<em>xwiki</em> is the <strong>best</strong>!<br/>");
        assertEquals("//xwiki// is the **best**!", getSourceText("editor"));
    }

    /**
     * Functional test for {@code WysiwygEditor#getSourceText()} when the rich text area is disabled. The rich text area
     * is disable when the source tab is active.
     */
    public void testGetSourceTextWhenRichTextAreaIsDisabled()
    {
        insertEditor("editor", "syntax: 'xwiki/2.0',\ndisplayTabs: true,\ndefaultEditor: 'wysiwyg'");
        setContent("<h1>Veni, <em>vidi</em>, vici<br/></h1>");
        switchToSource();
        assertEquals("= Veni, //vidi//, vici =", getSourceText("editor"));
    }

    /**
     * Functional test for {@code WysiwygEditor#release()}.
     */
    public void testRelease()
    {
        switchToWikiEditor();
        StringBuffer content = new StringBuffer();
        content.append("{{velocity}}\n");
        content.append("{{html}}\n");
        content.append("#wysiwyg_import(true)\n");
        content.append("<div id=\"wrapper\"></div>\n");
        content.append("<div><button onclick=\"loadEditor();\">Load Editor</button></div>\n");
        content.append("<script type=\"text/javascript\">\n");
        content.append("function loadEditor() {\n");
        content.append("    if (window.editor) {\n");
        content.append("        editor.release();\n");
        content.append("        editor = undefined;\n");
        content.append("    }\n");
        content.append("    Wysiwyg.onModuleLoad(function() {\n");
        content.append("        document.getElementById('wrapper').innerHTML = '<textarea id=\"test\"></textarea>';\n");
        content.append("        editor = new WysiwygEditor({hookId: 'test', syntax: 'xwiki/2.0'});\n");
        content.append("    });\n");
        content.append("}\n");
        content.append("</script>\n");
        content.append("{{/html}}\n");
        content.append("{{/velocity}}");
        setFieldValue("content", content.toString());
        clickEditSaveAndView();

        clickButtonWithText("Load Editor");
        waitForCondition("typeof window.editor == 'object'");
        focusRichTextArea();

        typeText("x");
        assertEquals("x", getSourceText("editor"));

        typeText("y");
        clickButtonWithText("Load Editor");
        waitForCondition("typeof window.editor == 'object'");
        focusRichTextArea();

        typeText("z");
        applyStyleTitle1();
        assertEquals("= z =", getSourceText("editor"));
    }

    /**
     * Inserts a WYSIWYG editor into the current page.
     * 
     * @param name the name of JavaScript variable to be used for accessing the editor
     * @param config additional configuration parameters to give to the created editor
     */
    protected void insertEditor(String name, String config)
    {
        // Insert the code that creates the editor.
        switchToWikiEditor();
        StringBuffer content = new StringBuffer();
        content.append("{{velocity}}\n");
        content.append("{{html}}\n");
        content.append("#wysiwyg_import(false)\n");
        content.append("<textarea id=\"" + name + "\"></textarea>\n");
        content.append("<script type=\"text/javascript\">\n");
        content.append("Wysiwyg.onModuleLoad(function() {\n");
        content.append("  " + name + " = new WysiwygEditor({\n");
        content.append("    hookId: '" + name + "',\n");
        content.append("    " + config + "\n");
        content.append("  });\n");
        content.append("});\n");
        content.append("</script>\n");
        content.append("{{/html}}\n");
        content.append("{{/velocity}}");
        setFieldValue("content", content.toString());
        clickEditSaveAndView();

        // Wait for the editor to be created.
        waitForCondition("typeof window." + name + " == 'object'");
    }

    /**
     * @param editorName the name of a JavaScript variable holding a reference to a WysiwygEditor instance
     * @return the source text of the specified editor
     */
    protected String getSourceText(String editorName)
    {
        getEval("window." + editorName + ".getSourceText(function(result){window.sourceText = result;})");
        waitForCondition("typeof window.sourceText == 'string'");
        return getEval("window.sourceText");
    }
}
