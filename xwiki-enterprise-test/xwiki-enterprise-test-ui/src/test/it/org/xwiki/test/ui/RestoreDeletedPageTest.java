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

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.xwiki.test.ui.po.AttachmentsPane;
import org.xwiki.test.ui.po.DeletePageOutcomePage;
import org.xwiki.test.ui.po.HistoryPane;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Functional tests for restoring a deleted document from recycle bin.
 * 
 * @version $Id$
 * @since 5.2M2
 */
public class RestoreDeletedPageTest extends AbstractTest
{
    @Rule
    public AdminAuthenticationRule adminAuthenticationRule = new AdminAuthenticationRule(getUtil());

    /**
     * @see "XWIKI-9421: Attachment version is incremented when a document is restored from recycle bin"
     */
    @Test
    public void restore()
    {
        // Clean up.
        getUtil().deletePage(getTestClassName(), getTestMethodName());

        // Create a new page.
        ViewPage page = getUtil().createPage(getTestClassName(), getTestMethodName(), "Once upon a time..", "A story");

        // Add an attachment.
        page.openAttachmentsDocExtraPane().setFileToUpload(getClass().getResource("/SmallAttachment.txt").getPath());

        // Delete the page.
        page.delete().clickYes();

        // Restore the page.
        page = new DeletePageOutcomePage().clickRestore();

        // Check the page title and content.
        assertEquals("A story", page.getDocumentTitle());
        assertEquals("Once upon a time..", page.getContent());

        // Check document version/history.
        HistoryPane historyPane = page.openHistoryDocExtraPane();
        assertEquals("3.1", historyPane.getCurrentVersion());
        assertEquals("Restored from recycle bin", historyPane.getCurrentVersionComment());

        // Check the attachment.
        AttachmentsPane attachmentsPane = page.openAttachmentsDocExtraPane();
        assertEquals(1, attachmentsPane.getNumberOfAttachments());
        assertEquals("1.1", attachmentsPane.getLatestVersionOfAttachment("SmallAttachment.txt"));

        // Check the attachment content.
        attachmentsPane.getAttachmentLink("SmallAttachment.txt").click();
        Assert.assertEquals("This is a small attachment.", getDriver().findElement(By.tagName("html")).getText());
    }
}
