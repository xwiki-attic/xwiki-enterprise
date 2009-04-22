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
 * Functional tests for font support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class FontSupportTest extends AbstractWysiwygTestCase
{
    /**
     * The XPath selector used to access the font size list box.
     */
    private static final String FONT_SIZE_SELECTOR = "//select[@title=\"Font Size\"]";

    /**
     * The XPath selector used to access the font name list box.
     */
    private static final String FONT_NAME_SELECTOR = "//select[@title=\"Font Name\"]";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Functional tests for font support inside the WYSIWYG editor.");
        suite.addTestSuite(FontSupportTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * Selects a plain text and applies a specific font size.
     * 
     * @see XWIKI-3295: Font size are not handled properly
     */
    public void testSetFontSizeOnAPlainTextSelection()
    {
        typeText("abc");
        select("XWE.body.firstChild", 1, "XWE.body.firstChild", 2);
        applyFontSize("24pt");
        assertWiki("a(% style=\"font-size: 24pt;\" %)b(%%)c");
    }

    /**
     * Selects a plain text and applies a specific font name.
     */
    public void testSetFontNameOnAPlainTextSelection()
    {
        typeText("abc");
        select("XWE.body.firstChild", 1, "XWE.body.firstChild", 2);
        applyFontName("georgia");
        assertWiki("a(% style=\"font-family: georgia;\" %)b(%%)c");
    }

    /**
     * Selects a plain text and applies a specific font name and font size.
     */
    public void testSetFontNameAndSizeOnAPlainTextSelection()
    {
        typeText("abc");
        select("XWE.body.firstChild", 1, "XWE.body.firstChild", 2);
        applyFontName("arial");
        applyFontSize("18pt");
        assertWiki("a(% style=\"font-family: arial; font-size: 18pt;\" %)b(%%)c");
    }

    /**
     * Test if the font size and font name are detected correctly.
     */
    public void testDetectFont()
    {
        setWikiContent("(% style=\"font-size: 24px; font-family: foo,verdana,sans-serif;\" %)\nabc");
        selectAllContent();
        assertDetectedFontSize("18pt");
        assertDetectedFontName("verdana");
    }

    /**
     * Selects a font size from the list box.
     * 
     * @param fontSize the font size to select from the list box
     */
    protected void applyFontSize(String fontSize)
    {
        getSelenium().select(FONT_SIZE_SELECTOR, fontSize);
    }

    /**
     * Selects a font name from the list box.
     * 
     * @param fontName the font name to select from the list box
     */
    protected void applyFontName(String fontName)
    {
        getSelenium().select(FONT_NAME_SELECTOR, fontName);
    }

    /**
     * Asserts if the detected font size equals the expected font size.
     * 
     * @param expectedFontSize the expected font size
     */
    protected void assertDetectedFontSize(String expectedFontSize)
    {
        assertEquals(expectedFontSize, getSelenium().getValue(FONT_SIZE_SELECTOR));
    }

    /**
     * Asserts if the detected font name equals the expected font name.
     * 
     * @param expectedFontName the expected font name
     */
    protected void assertDetectedFontName(String expectedFontName)
    {
        assertEquals(expectedFontName, getSelenium().getValue(FONT_NAME_SELECTOR));
    }
}
