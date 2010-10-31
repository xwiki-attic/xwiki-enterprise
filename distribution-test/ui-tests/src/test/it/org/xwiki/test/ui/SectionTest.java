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
        wep.setContent("1 First section\nSection 1 content\n\n"
            + "1 Second section\nSection 2 content\n\n1.1 Subsection\nSubsection content\n\n"
            + "1 Third section\nSection 3 content");
        wep.setSyntaxId("xwiki/1.0");
        return wep.clickSaveAndView();
    }

    private ViewPage createTestPageSyntax20()
    {
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "SectionEditingIncluded");
        wep.setContent("== Included section ==\nFirst Included section content\n{{velocity wiki=true}}\n"
            + "#foreach($h in ['First', 'Second'])\n== $h generated section ==\n\n$h generated paragraph\n"
            + "#end\n{{velocity}}\n");
        wep.setSyntaxId("xwiki/2.0");
        wep.clickSaveAndView();

        wep.switchToEdit("Test", "SectionEditing20");
        wep.setContent("= First section =\nSection 1 content\n\n"
            + "= Second section =\nSection 2 content\n\n== Subsection ==\nSubsection content\n\n"
            + "{{include document='Test.SectionEditingIncluded'/}}\n\n" + "= Third section =\nSection 3 content");
        wep.setSyntaxId("xwiki/2.0");

        return wep.clickSaveAndView();
    }

    /**
     * Verify edit section is working in both wiki and wysiwyg editors (xwiki/1.0).
     * See XWIKI-174: Sectional editing.
     */
    @Test
    public void testSectionEditInWikiEditorWhenSyntax10()
    {
        ViewPage vp = createTestPageSyntax10();

        // Edit the second section in the wysiwyg editor
        WYSIWYGEditPage wysiwygEditPage = vp.editSection(2);
        Assert.assertEquals("Second section\n Section 2 content \n \n Subsection\n Subsection content",
            wysiwygEditPage.getContent());

        // Edit the second section in the wiki editor
        WikiEditPage wikiEditPage = wysiwygEditPage.clickEditWiki();
        Assert.assertEquals("1 Second section Section 2 content 1.1 Subsection Subsection content",
            wikiEditPage.getContent());
    }

    /**
     * Verify edit section is working in wiki editor (xwiki/2.0). XWIKI-2881 : Implement Section editing.
     */
    @Test
    public void testSectionEditInWikiEditorWhenSyntax20()
    {
        ViewPage vp = createTestPageSyntax20();
        WikiEditPage wikiEditPage = vp.editSection(2).clickEditWiki();
        Assert.assertEquals("= Second section = Section 2 content == Subsection == Subsection content "
            + "{{include document=\"Test.SectionEditingIncluded\"/}}", wikiEditPage.getContent());
    }
}
