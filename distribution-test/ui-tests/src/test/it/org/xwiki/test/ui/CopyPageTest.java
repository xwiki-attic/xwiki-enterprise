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

import org.junit.Test;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.CopyConfirmationPage;
import org.xwiki.test.ui.framework.elements.CopyPage;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;
import org.xwiki.test.ui.xe.elements.HomePage;

/**
 * Test the Copy menu action to copy one page to another location.
 * 
 * @version $Id$
 * @since 3.0M2
 */
public class CopyPageTest extends AbstractAdminAuthenticatedTest
{
    private HomePage homePage;

    private static final String SPACE_VALUE = "Main";

    private static final String PAGE_VALUE = "CopyPageTest";

    private static final String SPACE_VALUE_COPY = "Main";

    private static final String PAGE_VALUE_COPY = "CopyPageTest1";

    private static final String PAGE_CONTENT = "This page is used for copying purposes";

    private static final String PAGE_TITLE = "Page title that will be copied";

    private static final String COPY_SUCCESSFUL = "successfully copied to";

    public void setUp()
    {
        // Delete page is already exists
        super.setUp();
        getUtil().deletePage(SPACE_VALUE, PAGE_VALUE);
        getUtil().deletePage(SPACE_VALUE_COPY, PAGE_VALUE_COPY);
        homePage = new HomePage();
        homePage.gotoPage();
    }

    @Test
    public void testCopyPage()
    {
        // Create a new Page that will be copied
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit(SPACE_VALUE, PAGE_VALUE);
        wep.setTitle(PAGE_TITLE);
        wep.setContent(PAGE_CONTENT);
        ViewPage viewPage = wep.clickSaveAndView();

        // Click on Copy Page from Top Menu
        CopyPage copyPage = viewPage.copy();

        // Fill the Target destination of the page to be copied to
        copyPage.setTargetPage(SPACE_VALUE_COPY + "." + PAGE_VALUE_COPY);
        CopyConfirmationPage copyConfirmationPage = copyPage.clickCopyButton();
        Assert.assertTrue(copyConfirmationPage.getInfoMessage().contains(COPY_SUCCESSFUL));
        viewPage = copyConfirmationPage.goToNewPage();
        Assert.assertEquals(PAGE_TITLE, viewPage.getDocumentTitle());
        Assert.assertEquals(PAGE_CONTENT, viewPage.getContent());
    }
}
