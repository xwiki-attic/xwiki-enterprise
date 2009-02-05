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
 * Tests for the custom list support, to handle and generate valid XHTML lists in the wysiwyg. At the moment, this class
 * tests processing the rendered lists rather than creating new lists from the wysiwyg, to ensure that valid rendered
 * lists are managed correctly.
 * 
 * @version $Id$
 */
public class ListSupportTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests valid XHTML list support in the wysiwyg editor.");
        suite.addTestSuite(ListSupportTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * @see XWIKI-2734: Cannot edit the outer list item. The test is not deeply relevant as we are positioning the range
     *      programatically. The correct test would prove that the caret can be positioned there with the keys.
     */
    public void testEmptyListItemsEditable()
    {
        switchToWikiEditor();
        setFieldValue("content", "** rox");
        switchToWysiwygEditor();
        // Check that a br is added in the parent list item so that it becomes editable
        assertXHTML("<ul><li><br class=\"spacer\"><ul><li>rox</li></ul></li></ul>");
        // Place the caret in the first list item
        moveCaret("XWE.body.firstChild.firstChild", 0);
        typeText("x");
        assertXHTML("<ul><li>x<br class=\"spacer\"><ul><li>rox</li></ul></li></ul>");
    }

    /**
     * Test the case when hitting enter in a list item before a sublist, that it creates an editable list item under.
     * The test is not deeply relevant since we are positioning the range in the item under programatically. The correct
     * test would prove that the caret can be positioned there with the keys.
     */
    public void testEnterBeforeSublist()
    {
        switchToWikiEditor();
        setFieldValue("content", "* x\n** rox");
        switchToWysiwygEditor();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 1);
        typeEnter();
        // Check the created item is editable
        assertXHTML("<ul><li>x</li><li><br class=\"spacer\"><ul><li>rox</li></ul></li></ul>");
        moveCaret("XWE.body.firstChild.childNodes[1]", 0);
        typeText("w");
        assertXHTML("<ul><li>x</li><li>w<br class=\"spacer\"><ul><li>rox</li></ul></li></ul>");
    }

    /**
     * Test the midas bug which causes the list items in a list to be replaced with an empty list and the caret to be
     * left inside the ul, not editable.
     */
    public void testEnterOnEntireList()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n* bar");
        switchToWysiwygEditor();
        // Set the selection around the list
        select("XWE.body.firstChild.firstChild.firstChild", 0, "XWE.body.firstChild.lastChild.firstChild", 3);
        typeEnter();
        typeText("foobar");
        assertXHTML("<p><br class=\"spacer\"></p>foobar");
    }

    /**
     * Test delete works fine inside a list item, and before another element (such as bold).
     */
    public void testDeleteInsideItem()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo**bar**\n** far");
        switchToWysiwygEditor();
        // Set the selection inside the foo text
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 1);
        typeDelete();
        assertXHTML("<ul><li>fo<strong>bar</strong><ul><li>far</li></ul></li></ul>");

        // set the selection just before the bold text but inside the text before
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 2);
        typeDelete();
        assertXHTML("<ul><li>fo<strong>ar</strong><ul><li>far</li></ul></li></ul>");
    }

    /**
     * Test backspace works fine inside a list item, and after another element (such as italic).
     */
    public void testBackspaceInsideItem()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n** b//arf//ar");
        switchToWysiwygEditor();
        // Set the selection in the "ar" text in the second list item
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.lastChild", 1);
        typeBackspace();
        assertXHTML("<ul><li>foo<ul><li>b<em>arf</em>r</li></ul></li></ul>");
        // delete again, now it should delete inside the em
        typeBackspace();
        assertXHTML("<ul><li>foo<ul><li>b<em>ar</em>r</li></ul></li></ul>");
    }

    /**
     * Test that the delete at the end of the list works fine
     */
    public void testDeleteInSameList()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n* bar");
        switchToWysiwygEditor();
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foobar</li></ul>");

    }

    /**
     * Test that the backspace at the beginning of the second item in a list moves the items together in the first list
     * item.
     */
    public void testBackspaceInSameList()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n* bar");
        switchToWysiwygEditor();
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foobar</li></ul>");

    }

    /**
     * Test that delete at the end of a list item moves the next list item (in another list) in it.
     */
    public void testDeleteInDifferentLists()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n\n* bar");
        switchToWysiwygEditor();
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foobar</li></ul>");
    }

    /**
     * Test that backspace at the beginning of a list item moves it to the previous list item (in another list).
     */
    public void testBackspaceInDifferentLists()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n\n* bar");
        switchToWysiwygEditor();
        // Set the selection at the end of the first item
        moveCaret("XWE.body.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foobar</li></ul>");
    }

    /**
     * Tests that the delete moves the first item on another level in the item in which is executed.
     */
    public void testDeleteBeforeSublist()
    {
        // 1/ with only one item -> the sublist should be removed
        switchToWikiEditor();
        setFieldValue("content", "* foo\n** bar\n");
        switchToWysiwygEditor();
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foobar</li></ul>");

        // 2/ with more than one item -> only the first item should be moved to the list above
        switchToWikiEditor();
        setFieldValue("content", "* foo\n** bar\n** far");
        switchToWysiwygEditor();
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foobar<ul><li>far</li></ul></li></ul>");
    }

    /**
     * Test that backspace at the beginning of an item in a sublist moves the item in the list item before, on a lower
     * list level.
     */
    public void testBackspaceBeginSublist()
    {
        // 1/ with only one item -> the sublist should be deleted
        switchToWikiEditor();
        setFieldValue("content", "* foo\n** bar\n");
        switchToWysiwygEditor();
        // Set the selection at beginning of the first item in sublist
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foobar</li></ul>");

        // 2/ with more than one item -> only the first item should be moved to the list above
        switchToWikiEditor();
        setFieldValue("content", "* foo\n** bar\n** far");
        switchToWysiwygEditor();
        // Set the selection at beginning of the first item in sublist
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foobar<ul><li>far</li></ul></li></ul>");
    }

    /**
     * Test that deleting at the end of a list item with a sublist with another sublist inside, moves the first sublist
     * and the elements on level 3 are moved to level 2.
     */
    public void testDeleteDecreasesLevelWithEmptyItem()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n*** bar\n");
        switchToWysiwygEditor();
        // Set the selection at beginning of the first item in sublist
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foo<br class=\"spacer\"><ul><li>bar</li></ul></li></ul>");
    }

    /**
     * Test that hitting backspace at the beginning of a list item with a sublist moves this element in its parent list
     * item and decreases the level of the subitems.
     */
    public void testBackspaceDecreasesLevelWithEmptyItem()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n*** bar\n");
        switchToWysiwygEditor();
        // Set the selection at beginning of the first item in sublist
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foo<br class=\"spacer\"><ul><li>bar</li></ul></li></ul>");
    }

    /**
     * Test delete at the end of a sublist item on a higher level moves the list item on the lower level inside it.
     * XWIKI-3114: Backspace is ignored at the beginning of a list item if the previous list item is on a lower level.
     */
    public void testDeleteBeforePreviousLevelItem()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n** bar\n* bar minus one");
        switchToWysiwygEditor();
        // Set the selection at the end of "bar"
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foo<ul><li>barbar minus one</li></ul></li></ul>");
    }

    /**
     * Test backspace at the beginning of a sublist item before a sublist moves the item on the lower level to the item
     * on the higher level. XWIKI-3114: Backspace is ignored at the beginning of a list item if the previous list item
     * is on a lower level.
     */
    public void testBackspaceAfterPreviousLevelItem()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n** bar\n* bar minus one");
        switchToWysiwygEditor();
        // Set the selection at the end of "bar"
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foo<ul><li>barbar minus one</li></ul></li></ul>");
    }

    /**
     * Test deleting the last piece of text inside a list item with sublists, keeps the remaining list item empty but
     * editable. The test is weak, since we position the range programatically. The true test should try to navigate to
     * the list item.
     */
    public void testDeleteAllTextInListItem()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n* b\n** ar");
        switchToWysiwygEditor();

        // Set the selection at the beginning of the text in the second list item
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 0);
        typeDelete();
        // test that the list structure is correct: two items one with a sublist
        assertXHTML("<ul><li>foo</li><li><br class=\"spacer\"><ul><li>ar</li></ul></li></ul>");
        // type in the empty list item
        moveCaret("XWE.body.firstChild.lastChild", 0);
        typeText("bar");
        assertXHTML("<ul><li>foo</li><li>bar<br class=\"spacer\"><ul><li>ar</li></ul></li></ul>");
        // now delete, to test that it jumps the <br>
        typeDelete();
        assertXHTML("<ul><li>foo</li><li>barar</li></ul>");
    }

    /**
     * Test backspacing the last piece of text inside a list item with sublists, keeps the remaining list item empty but
     * editable. The test is weak, since we position the range programatically. The true test should try to navigate to
     * the list item.
     */
    public void testBackspaceAllTextInListItem()
    {
        switchToWikiEditor();
        setFieldValue("content", "* foo\n* b\n** ar");
        switchToWysiwygEditor();

        // Set the selection at the end of the text in the second list item
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 1);
        typeBackspace();
        // test that the list structure is correct: two items one with a sublist
        assertXHTML("<ul><li>foo</li><li><br class=\"spacer\"><ul><li>ar</li></ul></li></ul>");
        // type in the empty list item
        moveCaret("XWE.body.firstChild.lastChild", 0);
        typeText("bar");
        assertXHTML("<ul><li>foo</li><li>bar<br class=\"spacer\"><ul><li>ar</li></ul></li></ul>");
        // Put the caret at the beginning of the sublist
        moveCaret("XWE.body.firstChild.lastChild.lastChild.firstChild.firstChild", 0);
        // now backspace, to test that it jumps the <br>
        typeBackspace();
        assertXHTML("<ul><li>foo</li><li>barar</li></ul>");
    }

    /**
     * Test delete before text outside lists.
     */
    public void testDeleteBeforeParagraph()
    {
        switchToWikiEditor();
        setFieldValue("content", "* one\n* two\n\nFoobar");
        switchToWysiwygEditor();

        // Set the selection at the end of the "two" list item
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>one</li><li>twoFoobar</li></ul>");

        // now run the case with delete in a sublist
        switchToWikiEditor();
        setFieldValue("content", "* one\n** two\n\nFoobar");
        switchToWysiwygEditor();

        // Set the selection at the end of the "two" list item
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>one<ul><li>twoFoobar</li></ul></li></ul>");
    }

    /**
     * Test backspace at the beginning of list item, after text outside lists.
     */
    public void testBackspaceAfterParagraph()
    {
        switchToWikiEditor();
        setFieldValue("content", "Foobar\n\n* one\n* two");
        switchToWysiwygEditor();

        // Set the selection at the beginning of the "one" list item
        moveCaret("XWE.body.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<p>Foobarone</p><ul><li>two</li></ul>");

        // Now test the case when the list has a sublist, in which case FF keeps the sublist parent, as empty and
        // editable
        // Note that this behaves differently on Internet Explorer, unwrapping the sublist
        switchToWikiEditor();
        setFieldValue("content", "Foobar\n\n* one\n** two");
        switchToWysiwygEditor();

        // Set the selection at the beginning of the "one" list item
        moveCaret("XWE.body.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<p>Foobarone</p><ul><li><br class=\"spacer\"><ul><li>two</li></ul></li></ul>");
    }

    /**
     * Test deleting the whole selection on a list, on multiple list levels keeps the list valid. Test that the parents
     * of the indented list items that stay are editable.
     */
    public void testDeleteSelectionPreserveSublists()
    {
        switchToWikiEditor();
        setFieldValue("content", "* one\n** two\n** three\n*** four\n*** five");
        switchToWysiwygEditor();

        // Set the selection starting in the one element and ending in the four element
        select("XWE.body.firstChild.firstChild.firstChild", 2,
            "XWE.body.firstChild.firstChild.lastChild.lastChild.lastChild.firstChild.firstChild", 2);
        typeDelete();
        assertXHTML("<ul><li>on<ul><li><br class=\"spacer\"><ul><li>ur</li><li>five</li></ul></li></ul></li></ul>");
    }

    /**
     * Test deleting the whole selection on a list, on multiple list levels deletes all the fully enclosed list items
     * and lists, and keeps the result in a single list item if the selection ends are on the same list level.
     */
    public void testDeleteSelectionDeletesEnclosedSublists()
    {
        switchToWikiEditor();
        setFieldValue("content", "* one\n** two\n** three\n*** four\n** five\n* six");
        switchToWysiwygEditor();

        // Set the selection starting in the one element and ending in the six element
        select("XWE.body.firstChild.firstChild.firstChild", 2, "XWE.body.firstChild.lastChild.firstChild", 1);
        typeDelete();
        assertXHTML("<ul><li>onix</li></ul>");
    }
}
