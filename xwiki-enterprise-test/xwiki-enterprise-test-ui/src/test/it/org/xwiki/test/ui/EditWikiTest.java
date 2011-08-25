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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.po.AbstractAdminAuthenticatedTest;
import org.xwiki.test.po.platform.editor.WYSIWYGEditPage;
import org.xwiki.test.po.platform.editor.WikiEditPage;
import org.xwiki.test.po.platform.editor.EditPage.Editor;

/**
 * Test wiki editing.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class EditWikiTest extends AbstractAdminAuthenticatedTest
{
    /** Page used for testing: Test.EditWikiTest */
    private WikiEditPage editPage;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        this.editPage = new WikiEditPage();
        getUtil().deletePage("Test", "EditWikiTest");
    }

    /** Test that save and continue saves as a minor version. */
    @Test
    public void testSaveAndContinueSavesAsMinorEdit()
    {
        this.editPage.switchToEdit("Test", "EditWikiTest");
        Assert.assertTrue(this.editPage.isNewDocument());
        this.editPage.setContent("abc1");
        this.editPage.clickSaveAndView();
        Assert.assertEquals("1.1", this.editPage.getMetaDataValue("version"));

        this.editPage.switchToEdit("Test", "EditWikiTest");
        Assert.assertFalse(this.editPage.isNewDocument());
        this.editPage.setContent("abc2");
        this.editPage.setMinorEdit(false);
        this.editPage.clickSaveAndContinue();
        this.editPage.clickCancel();
        Assert.assertEquals("1.2", this.editPage.getMetaDataValue("version"));
    }

    /**
     * Tests that the warning about loosing some of the page content when switching to the WYSIWYG editor is not
     * displayed if the page syntax is xwiki/2.0.
     */
    @Test
    public void testSwitchToWysiwygWithAdvancedContent()
    {
        editPage.switchToEdit("Test", "EditWikiTest");
        // Place some HTML in the page content.
        editPage.setContent("{{html}}<hr/>{{/html}}");
        // If we are asked to confirm the editor switch then we choose to remain on the wiki editor.
        editPage.makeConfirmDialogSilent(false);
        // Switch to WYSIWYG editor.
        WYSIWYGEditPage wysiwygEditPage = editPage.editWYSIWYG();
        // Check that we are indeed in WYSIWYG edit mode.
        Assert.assertEquals(Editor.WYSIWYG, wysiwygEditPage.getEditor());
    }
}
