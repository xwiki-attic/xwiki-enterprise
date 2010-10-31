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
package org.xwiki.test.ui;

import org.junit.Test;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;

import junit.framework.Assert;

/**
 * Test the section editing feature.
 *
 * @version $Id$
 * @since 2.6RC1
 */
public class SectionTest extends AbstractAdminAuthenticatedTest
{
    private ViewPage createTestPageSyntax10()
    {
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "SectionEditing");
        wep.setContent("1 Section1\nContent1\n\n"
            + "1 Section2\nContent2\n\n1.1 Section3\nContent3\n\n"
            + "1 Section4\nContent4");
        wep.setSyntaxId("xwiki/1.0");
        return wep.clickSaveAndView();
    }

    private ViewPage createTestPageSyntax20()
    {
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "SectionEditingIncluded");
        wep.setContent("== Section4 ==\n" +
            "Content4\n" +
            "\n" +
            "{{velocity wiki=true}}\n" +
            "#foreach($h in ['5', '6'])\n" +
            "== Section$h ==\n" +
            "Content$h\n" +
            "#end\n" +
            "{{velocity}}");
        wep.setSyntaxId("xwiki/2.0");
        wep.clickSaveAndView();

        wep.switchToEdit("Test", "SectionEditing20");
        wep.setContent("= Section1 =\nContent1\n\n"
            + "= Section2 =\nContent2\n\n== Section3 ==\nContent3\n\n"
            + "{{include document='Test.SectionEditingIncluded'/}}\n\n" + "= Section7 =\nContent7");
        wep.setSyntaxId("xwiki/2.0");

        return wep.clickSaveAndView();
    }

    /**
     * Verify edit section is working in both wiki and wysiwyg editors (xwiki/1.0).
     * See XWIKI-174: Sectional editing.
     */
    @Test
    public void testSectionEditInEditorWhenSyntax10()
    {
        ViewPage vp = createTestPageSyntax10();

        // Edit the second section in the wysiwyg editor
        WYSIWYGEditPage wysiwygEditPage = vp.editSection(2);
        Assert.assertEquals("Section2\n Content2 \n \n Section3\n Content3",
            wysiwygEditPage.getContent());

        // Edit the second section in the wiki editor
        WikiEditPage wikiEditPage = wysiwygEditPage.clickEditWiki();
        Assert.assertEquals("1 Section2 Content2 1.1 Section3 Content3",
            wikiEditPage.getContent());
        wikiEditPage.clickCancel();

        // Edit the third section in the wiki editor
        Assert.assertEquals("1.1 Section3 Content3",
            vp.editSection(3).clickEditWiki().getContent());
        wikiEditPage.clickCancel();

        // Edit the fourth section in the wiki editor
        Assert.assertEquals("1 Section4 Content4",
            vp.editSection(4).clickEditWiki().getContent());
    }

    /**
     * Verify edit section is working in both wiki and wysiwyg editors (xwiki/2.0).
     *
     * Note that we currently don't support section editing for included content (it would mean navigating to the
     * included page since it would change that page's content and not the currently page's content).
     *
     * See XWIKI-2881: Implement Section editing.
     */
    @Test
    public void testSectionEditInWikiEditorWhenSyntax20()
    {
        ViewPage vp = createTestPageSyntax20();

        // Edit the second section in the wysiwyg editor
        WYSIWYGEditPage wysiwygEditPage = vp.editSection(2);
        Assert.assertEquals("Section2\n" +
            "Content2\n" +
            "Section3\n" +
            "Content3\n" +
            "Section4\n" +
            "Content4\n" +
            "Section5\n" +
            "Content5\n" +
            "Section6\n" +
            "Content6",
            wysiwygEditPage.getContent());

        // Edit the second section in the wiki editor
        WikiEditPage wikiEditPage = wysiwygEditPage.clickEditWiki();
        Assert.assertEquals("= Section2 = Content2 == Section3 == Content3 "
            + "{{include document=\"Test.SectionEditingIncluded\"/}}", wikiEditPage.getContent());
        wikiEditPage.clickCancel();

        // Edit the third section in the wiki editor
        Assert.assertEquals("== Section3 == Content3 {{include document=\"Test.SectionEditingIncluded\"/}}",
            vp.editSection(3).clickEditWiki().getContent());
        wikiEditPage.clickCancel();

        // Edit the fourth section in the wiki editor
        // Note: we prove that included documents don't generate editable sections by checking that the fourth section
        // is "Section7".
        Assert.assertEquals("= Section7 = Content7",
            vp.editSection(4).clickEditWiki().getContent());
    }

    /**
     * Verify section save does not override the whole document content (xwiki/1.0).
     * See XWIKI-4033: When saving after section edit entire page is overwritten.
     */
    @Test
    public void testSectionSaveDoesNotOverwriteTheWholeContentWhenSyntax10()
    {
        ViewPage vp = createTestPageSyntax10();
        vp.editSection(3).clickEditWiki().clickSaveAndView();
        WikiEditPage wep = vp.clickEditWiki();
        Assert.assertEquals("1 Section1 Content1 1 Section2 Content2 1.1 Section3 Content3 1 Section4 Content4",
            wep.getContent());
    }

    /**
     * Verify section save does not override the whole document content (xwiki/2.0).
     * See XWIKI-4033: When saving after section edit entire page is overwritten.
     */
    @Test
    public void testSectionSaveDoesNotOverwriteTheWholeContentWhenSyntax20()
    {
        ViewPage vp = createTestPageSyntax20();
        vp.editSection(4).clickEditWiki().clickSaveAndView();
        WikiEditPage wep = vp.clickEditWiki();
        Assert.assertEquals("= Section1 = Content1 = Section2 = Content2 == Section3 == Content3 "
            + "{{include document=\"Test.SectionEditingIncluded\"/}} = Section7 = Content7", wep.getContent());
    }
}
