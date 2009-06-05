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
 * Functional tests for remove formatting support inside the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class RemoveFormattingSupportTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite =
            new XWikiTestSuite("Functional tests for remove formatting support inside the WYSIWYG editor.");
        suite.addTestSuite(RemoveFormattingSupportTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * Tests if formatting markers are removed properly.
     */
    public void testRemoveFormattingMarkers()
    {
        setWikiContent("==== **//__--abc--__//** ====");
        selectAllContent();
        clickRemoveFormattingButton();
        assertWiki("==== abc ====");
    }

    /**
     * Tests if in-line style is removed properly.
     */
    public void testRemoveInlineStyle()
    {
        setWikiContent("a(% style=\"color:red;\" %)b(% style=\"font-size:36pt;\" %)c(%%)d(%%)e");
        selectAllContent();
        clickRemoveFormattingButton();
        assertWiki("abcde");
    }

    /**
     * Tests if the formatting is removed properly when the selection spans across block-level elements.
     */
    public void testRemoveFormattingFromCrossBlockSelection()
    {
        setWikiContent("= a(% style=\"color:green\" %)b**cd**(%%)e =\n\nf(% style=\"font-size:36pt\" %)g//hi//(%%)j");
        select("XWE.body.getElementsByTagName('strong')[0].firstChild.firstChild", 1,
            "XWE.body.getElementsByTagName('em')[0].firstChild.firstChild", 1);
        clickRemoveFormattingButton();
        assertWiki("= a(% style=\"color: green;\" %)b**c**(%%)de =\n\nfgh(% style=\"font-size: 36pt;\" %)//i//(%%)j");
    }

    /**
     * Tests if the anchors are kept after removing the formatting.
     */
    public void testRemoveFormattingKeepsTheAnchorsIntact()
    {
        // Selection includes the anchor.
        setWikiContent("a**b[[c//d//e>>http://www.xwiki.org]]f**g");
        selectAllContent();
        clickRemoveFormattingButton();
        assertWiki("ab[[cde>>http://www.xwiki.org]]fg");

        // Selection is included in the anchor.
        setWikiContent("1**2[[3//456//7>>http://www.xwiki.org]]8**9");
        select(getDOMLocator("getElementsByTagName('em')[0].firstChild"), 1,
            getDOMLocator("getElementsByTagName('em')[0].firstChild"), 2);
        clickRemoveFormattingButton();
        assertWiki("1**2**[[**3//4//**5**//6//7**>>http://www.xwiki.org]]**8**9");
    }

    /**
     * Clicks on the tool bar button for removing the in-line formatting of the current selection.
     */
    protected void clickRemoveFormattingButton()
    {
        pushToolBarButton("Remove formatting");
    }
}
