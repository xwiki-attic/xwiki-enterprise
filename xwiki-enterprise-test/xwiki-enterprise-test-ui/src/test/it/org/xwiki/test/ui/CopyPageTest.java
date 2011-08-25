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
import org.xwiki.test.po.AbstractAdminAuthenticatedTest;
import org.xwiki.test.po.platform.CopyConfirmationPage;
import org.xwiki.test.po.platform.CopyPage;
import org.xwiki.test.po.platform.ViewPage;

/**
 * Test the Copy menu action to copy one page to another location.
 * 
 * @version $Id$
 * @since 3.0M2
 */
public class CopyPageTest extends AbstractAdminAuthenticatedTest
{
    private static final String PAGE_CONTENT = "This page is used for copying purposes";

    private static final String PAGE_TITLE = "Page title that will be copied";

    private static final String COPY_SUCCESSFUL = "successfully copied to";

    @Test
    public void testCopyPage()
    {
        // Delete page that may already exist
        String copiedPageName = getTestMethodName() + "1";
        getUtil().deletePage(getTestClassName(), getTestMethodName());
        getUtil().deletePage(getTestClassName(), copiedPageName);

        // Create a new page that will be copied.
        ViewPage viewPage = getUtil().createPage(getTestClassName(), getTestMethodName(), PAGE_CONTENT, PAGE_TITLE);

        // Click on Copy from the Page top menu.
        CopyPage copyPage = viewPage.copy();

        // Fill the target destination the page to be copied to.
        copyPage.setTargetSpaceName(getTestClassName());
        copyPage.setTargetPageName(copiedPageName);
        CopyConfirmationPage copyConfirmationPage = copyPage.clickCopyButton();
        Assert.assertTrue(copyConfirmationPage.getInfoMessage().contains(COPY_SUCCESSFUL));
        viewPage = copyConfirmationPage.goToNewPage();
        Assert.assertEquals(PAGE_TITLE, viewPage.getDocumentTitle());
        Assert.assertEquals(PAGE_CONTENT, viewPage.getContent());
    }
}
