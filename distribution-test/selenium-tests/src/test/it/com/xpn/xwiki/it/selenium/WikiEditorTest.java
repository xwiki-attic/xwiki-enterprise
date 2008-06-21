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
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Tests the wiki editor.
 * 
 * @version $Id: $
 */
public class WikiEditorTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests the wiki editor");
        suite.addTestSuite(WikiEditorTest.class, AlbatrossSkinExecutor.class);
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
        createPage("Test", "WikiEdit", "\n  1.1 Section\n\ntext");
        open("/xwiki/bin/edit/Test/WikiEdit?editor=wiki");
        assertEquals("\n  1.1 Section\n\ntext", getFieldValue("content"));
    }

    public void testBoldButton()
    {
        open("/xwiki/bin/edit/Test/WikiBoldButton?editor=wiki");
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
        open("/xwiki/bin/edit/Test/WikiItalicsButton?editor=wiki");
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
        open("/xwiki/bin/edit/Test/WikiUnderlineButton?editor=wiki");
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
        open("/xwiki/bin/edit/Test/WikiLinkButton?editor=wiki");
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
        open("/xwiki/bin/edit/Test/WikiHRButton?editor=wiki");
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
        open("/xwiki/bin/edit/Test/WikiImageButton?editor=wiki");
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
        open("/xwiki/bin/edit/Test/WikiSignatureButton?editor=wiki");
        setFieldValue("content", "Here follows a signature: ");
        clickWikiSignatureButton();
        assertEquals("Failed to append signature marker", "Here follows a signature: #sign(\"XWiki.Admin\")sigtext",
            getFieldValue("content"));
        setFieldValue("content", "Here follows a signature: \nAnd some content after...");
        getSelenium().setCursorPosition("id=content", "26");
        clickWikiSignatureButton();
        assertEquals("Failed to insert signature marker",
            "Here follows a signature: #sign(\"XWiki.Admin\")sigtext\nAnd some content after...",
            getFieldValue("content"));
        // TODO: We need to find out how to make a text selection in Selenium
    }

    /**
     * Tests that users can completely remove the content from a document (make the document empty). In previous
     * versions (pre-1.5M2), removing all content in page had no effect. See XWIKI-1007.
     */
    public void testEmptyDocumentContentIsAllowed()
    {
        createPage("Test", "EmptyWikiContent", "this is some content");
        open("/xwiki/bin/edit/Test/EmptyWikiContent?editor=wiki");
        setFieldValue("content", "");
        clickEditSaveAndView();
        assertFalse(getSelenium().isAlertPresent());
        assertEquals(-1, getSelenium().getLocation().indexOf("/edit/"));
        assertTextNotPresent("this is some content");
    }

    /**
     * Test for edit comment feature.
     */
    public void testEditComment() throws IOException
    {
        // Test for XWIKI-2487: Hiding the edit comment field doesn't work
        editInWikiEditor("Test", "EditComment");
        assertTrue( getSelenium().isVisible("comment") );

        setXWikiConfiguration("xwiki.editcomment.hidden=1");
        editInWikiEditor("Test", "EditComment");
        assertFalse( getSelenium().isVisible("comment") );

        setXWikiConfiguration("xwiki.editcomment.hidden=0");
    }

    public void testPreviewMode() {
        // Test for XWIKI-2490: Preview Action doesn't update contentAuthor
        editInWikiEditor("Test", "PreviewMode");
        setFieldValue("content", "$xwiki.hasAccessLevel('programming') $doc.author $doc.contentAuthor $doc.creator");
        clickEditPreview();
        assertTextPresent("true XWiki.Admin XWiki.Admin XWiki.Admin");        
    }
}
