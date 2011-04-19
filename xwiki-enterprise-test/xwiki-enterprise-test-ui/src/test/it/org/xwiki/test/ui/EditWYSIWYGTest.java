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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.framework.elements.editor.wysiwyg.UploadImagePane;

/**
 * Test WYSIWYG content editing.
 * 
 * @version $Id$
 * @since 3.0M2
 */
public class EditWYSIWYGTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The object used to get the test name.
     */
    @Rule
    public TestName testName = new TestName();

    /**
     * The edited page.
     */
    private WYSIWYGEditPage editPage;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();

        editPage = new WYSIWYGEditPage();
        editPage.switchToEdit(this.getClass().getSimpleName(), testName.getMethodName());
        editPage.getContentEditor().waitToLoad();
    }

    /**
     * Tests that images are uploaded fine after a preview.
     * 
     * @see XWIKI-5895: Adding an image in the WYSIWYG editor and previewing it without saving the page first makes the
     *      XWiki page corrupt.
     **/
    @Test
    public void testUploadImageAfterPreview()
    {
        editPage.clickPreview().clickBackToEdit();
        editPage.getContentEditor().waitToLoad();
        UploadImagePane uploadImagePane = editPage.insertImage().selectFromCurrentPage().uploadImage();
        uploadImagePane.setImageToUpload(this.getClass().getResource("/administration/avatar.png").getPath());
        // Fails if the image configuration step doesn't load in a decent amount of time.
        uploadImagePane.clickUploadImage();
    }
}
