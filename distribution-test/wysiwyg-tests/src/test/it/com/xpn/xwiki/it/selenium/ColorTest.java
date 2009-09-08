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
 * Functional tests for color support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class ColorTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Functional tests for color support inside the WYSIWYG editor.");
        suite.addTestSuite(ColorTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * Tests if the text color can be changed.
     */
    public void testChangeTextColor()
    {
        typeText("abc");

        // Select 'b'.
        select("XWE.body.firstChild", 1, "XWE.body.firstChild", 2);

        // Change the text color to red.
        clickForegroundColorButton();
        selectColor("rgb(255, 0, 0)");

        // Check the XWiki syntax.
        assertWiki("a(% style=\"color: rgb(255, 0, 0);\" %)b(%%)c");

        // Place the caret after 'b' in order to check if the current color is detected.
        moveCaret("XWE.body.getElementsByTagName('span')[0].firstChild", 1);

        // Check if the editor detects the right color.
        clickForegroundColorButton();
        assertSelectedColor("rgb(255, 0, 0)");
        hideColorPicker();
    }

    /**
     * Tests if the background color can be changed.
     */
    public void testChangeBackgroundColor()
    {
        typeText("abc");

        // Select 'b'.
        select("XWE.body.firstChild", 1, "XWE.body.firstChild", 2);

        // Change the text color to red.
        clickBackgroundColorButton();
        selectColor("rgb(255, 0, 0)");

        // Check the XWiki syntax.
        assertWiki("a(% style=\"background-color: rgb(255, 0, 0);\" %)b(%%)c");

        // Place the caret after 'b' in order to check if the current color is detected.
        moveCaret("XWE.body.getElementsByTagName('span')[0].firstChild", 1);

        // Check if the editor detects the right color.
        clickBackgroundColorButton();
        assertSelectedColor("rgb(255, 0, 0)");
        hideColorPicker();
    }

    /**
     * Tests if both the text color and the background color can be changed on the current selection.
     */
    public void testChangeTextAndBackgroudColor()
    {
        setWikiContent("(% style=\"color: red; background-color:#777;\" %)\nfoo");

        // Select the text.
        selectNodeContents("XWE.body.firstChild");

        // Change the text color.
        clickForegroundColorButton();
        selectColor("rgb(0, 255, 0)");

        // Change the background color.
        clickBackgroundColorButton();
        selectColor("rgb(252, 229, 205)");

        assertWiki("(% style=\"color: rgb(0, 255, 0); background-color: rgb(252, 229, 205);\" %)\nfoo");
    }

    /**
     * Makes a text bold, changes its color and then removes the bold style.
     */
    public void testRemoveBoldStyleFromAColoredText()
    {
        // Type some text and make it bold.
        typeText("bar");
        selectAllContent();
        clickBoldButton();

        // Change the text color.
        clickForegroundColorButton();
        selectColor("rgb(0, 0, 255)");

        // Remove the bold style.
        clickBoldButton();

        // Check the XWiki syntax.
        assertWiki("(% style=\"color: rgb(0, 0, 255);\" %)bar");
    }

    /**
     * Types two words in different colors, selects both and tries to change their color.
     * 
     * @see XWIKI-3564: Cannot change the text color after selecting text with different colors in IE
     */
    public void testChangeTextColorAfterSelectingTextWithDifferentColors()
    {
        // Type the two words.
        typeText("foo bar");

        // Select the first word and change its color to red.
        select("XWE.body.firstChild", 0, "XWE.body.firstChild", 3);
        clickForegroundColorButton();
        selectColor("rgb(255, 0, 0)");

        // Select the second word and change its color to blue.
        select("XWE.body.childNodes[1]", 1, "XWE.body.childNodes[1]", 4);
        clickForegroundColorButton();
        selectColor("rgb(0, 0, 255)");

        // Select both words and change their color to green.
        selectAllContent();
        clickForegroundColorButton();
        selectColor("rgb(0, 255, 0)");

        // Check the XWiki syntax.
        assertWiki("(% style=\"color: rgb(0, 255, 0);\" %)foo bar");
    }

    /**
     * Clicks on the tool bar button for changing the text color.
     */
    protected void clickForegroundColorButton()
    {
        pushToolBarButton("Font Color");
    }

    /**
     * Clicks on the tool bar button for changing the text background color.
     */
    protected void clickBackgroundColorButton()
    {
        pushToolBarButton("Background Color");
    }

    /**
     * Selects the specified color from the color picker.
     * 
     * @param rgbColor the RGB color to select
     */
    protected void selectColor(String rgbColor)
    {
        getSelenium().click("//div[@class = 'colorCell' and @style = 'background-color: " + rgbColor + ";']");
    }

    /**
     * Asserts that the color selected by the color picker equals the given color.
     * 
     * @param rgbColor the expected selected color
     */
    protected void assertSelectedColor(String rgbColor)
    {
        assertElementPresent("//div[@class = 'colorCell-selected' and @style = 'background-color: " + rgbColor + ";']");
    }

    /**
     * Hides the color picker by clicking outside.
     */
    protected void hideColorPicker()
    {
        pushButton(getDOMLocator("body"));
    }
}
