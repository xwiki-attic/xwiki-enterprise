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

import org.junit.Assert;
import org.junit.Test;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.po.AttachmentsPane;
import org.xwiki.test.ui.po.CopyConfirmationPage;
import org.xwiki.test.ui.po.CopyOverwritePromptPage;
import org.xwiki.test.ui.po.CopyPage;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Test the Copy menu action to copy one page to another location.
 * 
 * @version $Id$
 * @since 3.0M2
 */
public class CopyPageTest extends AbstractAdminAuthenticatedTest
{
    private static final String PAGE_CONTENT = "This page is used for copying purposes";

    private static final String OVERWRITTEN_PAGE_CONTENT = "This page is used for overwritten copy purposes";

    private static final String COPY_SUCCESSFUL = "successfully copied to";

    private static final String OVERWRITE_PROMPT1 = "The document ";

    private static final String OVERWRITE_PROMPT2 = " already exists. Are you sure you want to overwrite it (all its content would be lost)?";

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason = "See http://jira.xwiki.org/browse/XE-1146")
    public void testCopyPage()
    {
        String sourceSpaceName = getTestClassName();
        String sourcePageName = getTestMethodName();
        String targetSpaceName = getTestClassName() + "Copy";
        String targetPageName = getTestMethodName() + "Copy";

        // Delete page that may already exist
        getUtil().deletePage(sourceSpaceName, sourcePageName);
        getUtil().deletePage(targetSpaceName, targetPageName);

        // Create a new page that will be copied.
        ViewPage viewPage =
            getUtil().createPage(sourceSpaceName, sourcePageName, PAGE_CONTENT, sourcePageName);

        // Add an attachment to verify that it's version is not incremented in the target document (XWIKI-8157).
        // FIXME: Remove the following wait when XWIKI-6688 is fixed.
        viewPage.waitForDocExtraPaneActive("comments");
        AttachmentsPane attachmentsPane = viewPage.openAttachmentsDocExtraPane();
        attachmentsPane.setFileToUpload(getClass().getResource("/image.gif").getPath());
        attachmentsPane.waitForUploadToFinish("image.gif");
        Assert.assertEquals("1.1", attachmentsPane.getLatestVersionOfAttachment("image.gif"));

        // Click on Copy from the Page top menu.
        CopyPage copyPage = viewPage.copy();

        // Check form content
        Assert.assertEquals(sourceSpaceName, copyPage.getSourceSpaceName());
        Assert.assertEquals(sourcePageName, copyPage.getSourcePageName());
        Assert.assertEquals(sourceSpaceName, copyPage.getTargetSpaceName());
        Assert.assertEquals(sourcePageName, copyPage.getTargetPageName());

        // Fill the target destination the page to be copied to.
        copyPage.setTargetSpaceName(targetSpaceName);
        copyPage.setTargetPageName(targetPageName);

        // Click copy button
        CopyConfirmationPage copyConfirmationPage = copyPage.clickCopyButton();

        // Check successful copy confirmation
        Assert.assertTrue(copyConfirmationPage.getInfoMessage().contains(COPY_SUCCESSFUL));
        viewPage = copyConfirmationPage.goToNewPage();

        // Verify that the copied title is modified to be the new page name since it was set to be the page name
        // originally.
        Assert.assertEquals(targetPageName, viewPage.getDocumentTitle());
        Assert.assertEquals(PAGE_CONTENT, viewPage.getContent());

        // Verify the attachment version is the same (XWIKI-8157).
        // FIXME: Remove the following wait when XWIKI-6688 is fixed.
        viewPage.waitForDocExtraPaneActive("comments");
        attachmentsPane = viewPage.openAttachmentsDocExtraPane();
        Assert.assertEquals("1.1", attachmentsPane.getLatestVersionOfAttachment("image.gif"));
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason = "See http://jira.xwiki.org/browse/XE-1146")
    public void testCopyOverwritePage()
    {
        String sourceSpaceName = getTestClassName();
        String sourcePageName = getTestMethodName();
        String targetSpaceName = getTestClassName() + "Copy";
        String targetPageName = getTestMethodName() + "Copy";

        // Delete page that may already exist
        getUtil().deletePage(sourceSpaceName, sourcePageName);
        getUtil().deletePage(targetSpaceName, targetPageName);

        // Create a new page that will be overwritten.
        getUtil().createPage(targetSpaceName, targetPageName, OVERWRITTEN_PAGE_CONTENT, targetPageName);

        // Create a new page that will be copied.
        ViewPage viewPage =
            getUtil().createPage(sourceSpaceName, sourcePageName, PAGE_CONTENT, sourcePageName);

        // Click on Copy from the Page top menu.
        CopyPage copyPage = viewPage.copy();

        // Fill the target destination the page to be copied to.
        copyPage.setTargetSpaceName(targetSpaceName);
        copyPage.setTargetPageName(targetPageName);

        // Click copy button
        CopyOverwritePromptPage copyOverwritePrompt = copyPage.clickCopyButtonExpectingOverwritePrompt();

        // Check overwrite warning
        Assert.assertEquals(OVERWRITE_PROMPT1 +  targetSpaceName + '.' + targetPageName + OVERWRITE_PROMPT2,
            copyOverwritePrompt.getWarningMessage());

        // Cancel the copy
        viewPage = copyOverwritePrompt.clickCancelButton();

        // Verify that we have been sent back to the original page
        Assert.assertEquals(sourcePageName, viewPage.getDocumentTitle());

        // Click on Copy from the Page top menu.
        copyPage = viewPage.copy();

        // Fill the target destination the page to be copied to.
        copyPage.setTargetSpaceName(targetSpaceName);
        copyPage.setTargetPageName(targetPageName);

        // Click copy button
        copyOverwritePrompt = copyPage.clickCopyButtonExpectingOverwritePrompt();

        // Click change target
        copyPage = copyOverwritePrompt.clickChangeTargetButton();

        // Check form content
        Assert.assertEquals(sourceSpaceName, copyPage.getSourceSpaceName());
        Assert.assertEquals(sourcePageName, copyPage.getSourcePageName());
        Assert.assertEquals(sourceSpaceName, copyPage.getTargetSpaceName());
        Assert.assertEquals(sourcePageName, copyPage.getTargetPageName());

        // Fill the target destination the page to be copied to.
        copyPage.setTargetSpaceName(targetSpaceName);
        copyPage.setTargetPageName(targetPageName);

        // Copy and confirm overwrite
        copyOverwritePrompt = copyPage.clickCopyButtonExpectingOverwritePrompt();
        CopyConfirmationPage copyConfirmationPage = copyOverwritePrompt.clickCopyButton();

        // Check successful copy confirmation
        Assert.assertTrue(copyConfirmationPage.getInfoMessage().contains(COPY_SUCCESSFUL));
        viewPage = copyConfirmationPage.goToNewPage();

        // Verify that the copied title is modified to be the new page name since it was set to be the page name
        // originally.
        Assert.assertEquals(targetPageName, viewPage.getDocumentTitle());
        Assert.assertEquals(PAGE_CONTENT, viewPage.getContent());
    }
}
