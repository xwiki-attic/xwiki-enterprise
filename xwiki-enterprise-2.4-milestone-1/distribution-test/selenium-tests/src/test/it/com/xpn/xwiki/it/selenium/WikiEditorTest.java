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

import java.io.IOException;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.ColibriSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Tests the wiki editor.
 * 
 * @version $Id$
 */
public class WikiEditorTest extends AbstractXWikiTestCase
{
    private static final String SYNTAX = "xwiki/1.0";
    
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests the wiki editor");
        suite.addTestSuite(WikiEditorTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    public void testEmptyLineAndSpaceCharactersBeforeSectionTitleIsNotRemoved()
    {
        createPage("Test", "WikiEdit", "\n  1.1 Section\n\ntext", SYNTAX);
        open("Test", "WikiEdit", "edit", "editor=wiki");
        assertEquals("\n  1.1 Section\n\ntext", getFieldValue("content"));
    }

    public void testBoldButton()
    {        
        editInWikiEditor("Test", "WikiBoldButton", SYNTAX);
        setFieldValue("content", "Here follows a bold text: ");
        clickWikiBoldButton();
        assertEquals("Failed to append bold marker", "Here follows a bold text: *Text in Bold*", 
            getFieldValue("content"));
        setFieldValue("content", "Here follows a bold text: \nAnd some content after...");
        getSelenium().setCursorPosition("id=content", "26");
        clickWikiBoldButton();
        assertEquals("Failed to insert bold marker",
            "Here follows a bold text: *Text in Bold*\nAnd some content after...", getFieldValue("content"));
        // TODO: We need to find out how to make a text selection in Selenium
    }

    public void testItalicsButton()
    {        
        editInWikiEditor("Test", "WikiItalicsButton", SYNTAX);
        setFieldValue("content", "Here follows an italics text: ");
        clickWikiItalicsButton();
        assertEquals("Failed to append italics marker", "Here follows an italics text: ~~Text in Italics~~",
            getFieldValue("content"));
        setFieldValue("content", "Here follows an italics text: \nAnd some content after...");
        getSelenium().setCursorPosition("id=content", "30");
        clickWikiItalicsButton();
        assertEquals("Failed to insert italics marker",
            "Here follows an italics text: ~~Text in Italics~~\nAnd some content after...", getFieldValue("content"));
        // TODO: We need to find out how to make a text selection in Selenium
    }

    public void testUnderlineButton()
    {        
        editInWikiEditor("Test", "WikiUnderlineButton", SYNTAX);
        setFieldValue("content", "Here follows an underlined text: ");
        clickWikiUnderlineButton();
        assertEquals("Failed to append underline marker", "Here follows an underlined text: __Text in Underline__",
            getFieldValue("content"));
        setFieldValue("content", "Here follows an underlined text: \nAnd some content after...");
        getSelenium().setCursorPosition("id=content", "33");
        clickWikiUnderlineButton();
        assertEquals("Failed to insert underline marker",
            "Here follows an underlined text: __Text in Underline__\nAnd some content after...",
            getFieldValue("content"));
        // TODO: We need to find out how to make a text selection in Selenium
    }

    public void testLinkButton()
    {
        editInWikiEditor("Test", "WikiLinkButton", SYNTAX);
        setFieldValue("content", "Here follows a link: ");
        clickWikiLinkButton();
        assertEquals("Failed to append link marker", "Here follows a link: [Link Example]", getFieldValue("content"));
        setFieldValue("content", "Here follows a link: \nAnd some content after...");
        getSelenium().setCursorPosition("id=content", "21");
        clickWikiLinkButton();
        assertEquals("Failed to insert link marker", "Here follows a link: [Link Example]\nAnd some content after...",
            getFieldValue("content"));
        // TODO: We need to find out how to make a text selection in Selenium
    }

    public void testHRButton()
    {
        editInWikiEditor("Test", "WikiHRButton", SYNTAX);
        setFieldValue("content", "Here follows a ruler: ");
        clickWikiHRButton();
        assertEquals("Failed to append ruler marker", "Here follows a ruler: \n----\n", getFieldValue("content"));
        setFieldValue("content", "Here follows a ruler: \nAnd some content after...");
        getSelenium().setCursorPosition("id=content", "22");
        clickWikiHRButton();
        assertEquals("Failed to insert ruler marker", "Here follows a ruler: \n----\n\nAnd some content after...",
            getFieldValue("content"));
    }

    public void testImageButton()
    {
        editInWikiEditor("Test", "WikiImageButton", SYNTAX);
        setFieldValue("content", "Here follows an image: ");
        clickWikiImageButton();
        assertEquals("Failed to append image marker", "Here follows an image: {image:example.jpg}",
            getFieldValue("content"));
        setFieldValue("content", "Here follows an image: \nAnd some content after...");
        getSelenium().setCursorPosition("id=content", "23");
        clickWikiImageButton();
        assertEquals("Failed to insert image marker",
            "Here follows an image: {image:example.jpg}\nAnd some content after...", getFieldValue("content"));
        // TODO: We need to find out how to make a text selection in Selenium
    }

    public void testSignatureButton()
    {
        editInWikiEditor("Test", "WikiSignatureButton", SYNTAX);
        setFieldValue("content", "Here follows a signature: ");
        clickWikiSignatureButton();
        assertEquals("Failed to append signature marker", "Here follows a signature: #sign(\"XWiki.Admin\")",
            getFieldValue("content"));
        setFieldValue("content", "Here follows a signature: \nAnd some content after...");
        getSelenium().setCursorPosition("id=content", "26");
        clickWikiSignatureButton();
        assertEquals("Failed to insert signature marker",
            "Here follows a signature: #sign(\"XWiki.Admin\")\nAnd some content after...", getFieldValue("content"));
        // TODO: We need to find out how to make a text selection in Selenium
    }

    /**
     * Tests that users can completely remove the content from a document (make the document empty). In previous
     * versions (pre-1.5M2), removing all content in page had no effect. See XWIKI-1007.
     */
    public void testEmptyDocumentContentIsAllowed()
    {
        createPage("Test", "EmptyWikiContent", "this is some content", SYNTAX);
        editInWikiEditor("Test", "EmptyWikiContent", SYNTAX);
        setFieldValue("content", "");
        clickEditSaveAndView();
        assertFalse(getSelenium().isAlertPresent());
        assertEquals(-1, getSelenium().getLocation().indexOf("/edit/"));
        assertTextNotPresent("this is some content");
    }

    /**
     * Test the ability to add edit comments and the ability to disable the edit comments feature.
     */
    public void testEditComment() throws IOException
    {
        try {
            editInWikiEditor("Test", "EditComment", SYNTAX);
            assertTrue(getSelenium().isVisible("comment"));

            // Test for XWIKI-2487: Hiding the edit comment field doesn't work
            setXWikiConfiguration("xwiki.editcomment.hidden=1");
            editInWikiEditor("Test", "EditComment", SYNTAX);
            assertFalse(getSelenium().isVisible("comment"));
        } finally {
            setXWikiConfiguration("xwiki.editcomment.hidden=0");
        }
    }

    /**
     * Verify that the preview works when the document content contains script requiring programming rights. See also
     * XWIKI-2490.
     */
    public void testPreviewModeWithContentRequiringProgrammingRights()
    {
        editInWikiEditor("Test", "PreviewMode", SYNTAX);
        setFieldValue("content", "$xwiki.hasAccessLevel('programming') $doc.author $doc.contentAuthor $doc.creator");
        clickEditPreview();
        assertTextPresent("true XWiki.Admin XWiki.Admin XWiki.Admin");
    }

    /**
     * Verify minor edit feature is working
     */
    public void testMinorEdit()
    {
        try {
            editInWikiEditor("Test", "MinorEdit", SYNTAX);
            // Note: Revision 2.1 is used since starting with 1.9-rc-1 editInWikiEditor creates an initial version to set the syntax.
            setFieldValue("content", "version=2.1");
            clickEditSaveAndContinue();
            setFieldValue("content", "version=3.1");
            clickEditSaveAndView();

            open("Test", "MinorEdit", "viewrev", "rev=3.1");
            assertTextPresent("version=3.1");

            editInWikiEditor("Test", "MinorEdit", SYNTAX);
            setFieldValue("content", "version=3.2");
            getSelenium().click("minorEdit");
            clickEditSaveAndView();

            open("Test", "MinorEdit", "viewrev", "rev=3.2");
            assertTextPresent("version=3.2");
        } finally {
            deletePage("Test", "MinorEdit");
        }
    }
}
