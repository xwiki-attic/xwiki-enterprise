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
package org.xwiki.test.selenium;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

/**
 * Tests the WYSIWYG editor (content edited in WYSIWYG mode).
 * 
 * @version $Id$
 */
public class WysiwygEditorTest extends AbstractXWikiTestCase
{
    private static final String SYNTAX = "xwiki/1.0";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests the wysiwyg editor");
        suite.addTestSuite(WysiwygEditorTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        editInWysiwyg("Test", "TestWysiwyg", SYNTAX);
        clearWysiwygContent();
    }

    public void testSimpleList()
    {
        typeInWysiwyg("item1");
        clickWysiwygUnorderedListButton();
        typeEnterInWysiwyg();
        typeInWysiwyg("item2");
        typeEnterInWysiwyg();
        typeInWysiwyg("item3");

        assertWikiTextGeneratedByWysiwyg("* item1\n* item2\n* item3");
    }

    public void testIndentation()
    {
        clickWysiwygIndentButton();
        typeInWysiwyg("indented");
        typeEnterInWysiwyg();
        clickWysiwygOutdentButton();

        assertWikiTextGeneratedByWysiwyg("<blockquote>indented</blockquote>");
    }

    public void testLineFeed()
    {
        typeInWysiwyg("Text");
        typeEnterInWysiwyg();
        typeInWysiwyg("Text");

        assertWikiTextGeneratedByWysiwyg("Text\n\nText");
    }

    public void testLineFeedWhenUsingShiftEnter()
    {
        typeInWysiwyg("Text");
        typeShiftEnterInWysiwyg();
        typeInWysiwyg("Text");

        assertWikiTextGeneratedByWysiwyg("Text\\\\Text");
    }

    public void testLineFeedBeforeAndAfterLists()
    {
        typeInWysiwyg("Text");
        typeEnterInWysiwyg();
        typeInWysiwyg("item");
        clickWysiwygUnorderedListButton();
        typeEnterInWysiwyg();
        clickWysiwygUnorderedListButton();
        typeInWysiwyg("Text");

        assertWikiTextGeneratedByWysiwyg("Text\n\n* item\n\nText");
    }

    public void testEscapedHtmlElement()
    {
        String text = "http://\\<yourserver\\>:8080/something";
        typeInWysiwyg(text);
        assertWikiTextGeneratedByWysiwyg(text);
    }

    public void testHtmlElementIsRendered()
    {
        // Test with any HTML.
        typeInWysiwyg("<img src=\"whatever\">");
        assertWikiTextGeneratedByWysiwyg("<img src=\"whatever\">");
    }

    public void testNestedOrderedList()
    {
        clickWysiwygOrderedListButton();
        typeInWysiwyg("level 1");
        typeEnterInWysiwyg();
        clickWysiwygIndentButton();
        typeInWysiwyg("level 2");

        assertWikiTextGeneratedByWysiwyg("1. level 1\n11. level 2");
    }
}
