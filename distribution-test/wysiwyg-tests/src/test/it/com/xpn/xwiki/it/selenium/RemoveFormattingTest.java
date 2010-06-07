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

/**
 * Functional tests for remove formatting support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class RemoveFormattingTest extends AbstractWysiwygTestCase
{
    /**
     * Tests if formatting markers are removed properly.
     */
    public void testRemoveFormattingMarkers()
    {
        switchToSource();
        setSourceText("==== **//__--abc--__//** ====");
        switchToWysiwyg();
        selectAllContent();
        clickRemoveFormattingButton();
        switchToSource();
        assertSourceText("==== abc ====");
    }

    /**
     * Tests if in-line style is removed properly.
     */
    public void testRemoveInlineStyle()
    {
        switchToSource();
        setSourceText("a(% style=\"color:red;\" %)b(% style=\"font-size:36pt;\" %)c(%%)d(%%)e");
        switchToWysiwyg();
        selectAllContent();
        clickRemoveFormattingButton();
        switchToSource();
        assertSourceText("abcde");
    }

    /**
     * Tests if the formatting is removed properly when the selection spans across block-level elements.
     */
    public void testRemoveFormattingFromCrossBlockSelection()
    {
        switchToSource();
        setSourceText("= a(% style=\"color:green\" %)b**cd**(%%)e =\n\nf(% style=\"font-size:36pt\" %)g//hi//(%%)j");
        switchToWysiwyg();
        select("XWE.body.getElementsByTagName('strong')[0].firstChild.firstChild", 1,
            "XWE.body.getElementsByTagName('em')[0].firstChild.firstChild", 1);
        clickRemoveFormattingButton();
        switchToSource();
        assertSourceText("= a(% style=\"color: green\" %)b**c**(%%)de =\n\nfgh(% style=\"font-size: 36pt\" %)//i//(%%)j");
    }

    /**
     * Tests if the anchors are kept after removing the formatting.
     */
    public void testRemoveFormattingKeepsTheAnchorsIntact()
    {
        // Selection includes the anchor.
        switchToSource();
        setSourceText("a**b[[c//d//e>>http://www.xwiki.org]]f**g");
        switchToWysiwyg();
        selectAllContent();
        clickRemoveFormattingButton();
        switchToSource();
        assertSourceText("ab[[cde>>http://www.xwiki.org]]fg");

        // Selection is included in the anchor.
        setSourceText("1**2[[3//456//7>>http://www.xwiki.org]]8**9");
        switchToWysiwyg();
        select(getDOMLocator("getElementsByTagName('em')[0].firstChild"), 1,
            getDOMLocator("getElementsByTagName('em')[0].firstChild"), 2);
        clickRemoveFormattingButton();
        switchToSource();
        assertSourceText("1**2**[[**3//4//**5**//6//7**>>http://www.xwiki.org]]**8**9");
    }

    /**
     * Clicks on the tool bar button for removing the in-line formatting of the current selection.
     */
    protected void clickRemoveFormattingButton()
    {
        pushToolBarButton("Clear Formatting");
    }
}
