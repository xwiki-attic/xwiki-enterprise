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
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import junit.framework.Test;

public class StandardFeaturesTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests wysiwyg essentials features");
        suite.addTestSuite(StandardFeaturesTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    public void testEmptyWysiwyg()
    {
        assertXHTML(WYSIWYG_DEFAULT_CONTENT);
    }

    public void testTypingAndDeletion()
    {
        typeText("foobar");
        assertXHTML("foobar<br>");
        typeBackspaces("foobar".length());
        assertXHTML(WYSIWYG_DEFAULT_CONTENT);
    }

    public void testParagraphs()
    {
        typeTextThenEnter("a");
        typeTextThenEnter("b");
        typeText("c");
        assertXHTML("<p>a</p><p>b</p><p>c<br></p>");
    }

    public void testBold()
    {
        typeTextThenEnter("foobar");
        selectElement("p", 1);
        clickBoldButton();
        assertXHTML("<p><strong>foobar</strong></p><p><br></p>");
    }

    public void testItalics()
    {
        typeTextThenEnter("foobar");
        selectElement("p", 1);
        clickItalicsButton();
        assertXHTML("<p><em>foobar</em></p><p><br></p>");
    }

    public void testUnderline()
    {
        typeTextThenEnter("foobar");
        selectElement("p", 1);
        clickUnderlineButton();
        assertXHTML("<p><ins>foobar</ins></p><p><br></p>");
    }

    public void testStrikethrough()
    {
        typeTextThenEnter("foobar");
        selectElement("p", 1);
        clickStrikethroughButton();
        assertXHTML("<p><del>foobar</del></p><p><br></p>");
    }

    public void testSubscript()
    {
        typeTextThenEnter("foobar");
        selectElement("p", 1);
        clickSubscriptButton();
        assertXHTML("<p><sub>foobar</sub></p><p><br></p>");
    }

    public void testSuperscript()
    {
        typeTextThenEnter("foobar");
        selectElement("p", 1);
        clickSuperscriptButton();
        assertXHTML("<p><sup>foobar</sup></p><p><br></p>");
    }

    public void testUnorderedList()
    {
        // Create a list with 3 items
        typeTextThenEnter("a");
        typeTextThenEnter("b");
        typeText("c");
        selectAllContent();
        clickUnorderedListButton();
        assertXHTML("<ul><li>a</li><li>b</li><li>c<br></li></ul>");

        // Undo
        clickUnorderedListButton();
        assertXHTML("a<br>b<br>c<br>");

        // Create a list with 1 item and delete it
        resetContent();
        typeText("a");
        selectAllContent();
        clickUnorderedListButton();
        typeBackspaces(2);
        assertXHTML("<br>");

        // Create a list with 1 item and delete the bullet
        // FIXME : this should be working.
        /* resetContent();
        typeText("a");
        selectAllContent();        
        clickUnorderedListButton();
        resetSelection();
        typeLeftArrow();
        typeBackspace();
        assertXHTML("a"); */
    }

    public void testOrderedList()
    {
        // Create a list with 3 items
        typeTextThenEnter("a");
        typeTextThenEnter("b");
        typeText("c");
        selectAllContent();
        clickOrderedListButton();
        assertXHTML("<ol><li>a</li><li>b</li><li>c<br></li></ol>");

        // Undo
        clickOrderedListButton();
        assertXHTML("a<br>b<br>c<br>");

        // Create a list with 1 item and delete it
        resetContent();
        typeText("a");
        selectAllContent();
        clickOrderedListButton();
        typeBackspaces(2);
        assertXHTML("<br>");

        // Create a list with 1 item and delete the bullet
        // FIXME : this should be working.
        /* resetContent();
        typeText("a");
        selectAllContent();
        clickOrderedListButton();
        resetSelection();
        typeLeftArrow();
        typeBackspace();
        assertXHTML("a"); */
    }

    public void testStyle()
    {
        typeText("foobar");
        selectAllContent();
        assertXHTML("foobar<br>");

        applyStyleTitle1();
        assertXHTML("<h1>foobar</h1>");

        applyStyleTitle2();
        assertXHTML("<h2>foobar</h2>");

        applyStyleTitle3();
        assertXHTML("<h3>foobar</h3>");

        applyStyleTitle4();
        assertXHTML("<h4>foobar</h4>");

        applyStyleTitle5();
        assertXHTML("<h5>foobar</h5>");

        // FIXME : fix this behavior (foobar<br> becomes <p>foobar</p>)
        applyStyleNormal();
        assertXHTML("<p>foobar</p>");
    }

    public void testHR()
    {
        typeText("foobar");
        clickHRButton();
        assertXHTML("foobar<hr><br>");
    }
}
