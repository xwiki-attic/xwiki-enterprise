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

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.browser.IgnoreBrowsers;
import org.xwiki.test.ui.po.AttachmentsPane;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Test saving and downloading of attachments.
 *
 * A number of these tests paste code into the document and expect an output. This is wrong IMO but the way we have
 * presently to test components which require the database to function.
 * 
 * @version $Id$
 * @since 2.5M1
 */
public class AttachmentTest extends AbstractAdminAuthenticatedTest
{
    private final String testAttachment = "SmallAttachment.txt";

    private final String testAttachment2 = "SmallAttachment2.txt";

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage(getTestClassName(), getTestMethodName());
    }

    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void testUploadDownloadTwoAttachments()
    {
        ViewPage vp = getUtil().createPage(getTestClassName(), getTestMethodName(), null,
            getTestClassName() + "#" + getTestMethodName());

        // TODO: Remove when XWIKI-6688 (Possible race condition when clicking on a tab at the bottom of a page in
        // view mode) is fixed.
        vp.waitForDocExtraPaneActive("comments");

        AttachmentsPane ap = vp.openAttachmentsDocExtraPane();
        ap.setFileToUpload(this.getClass().getResource("/" + this.testAttachment).getPath());
        ap.addAnotherFile();
        ap.setFileToUpload(this.getClass().getResource("/" + this.testAttachment2).getPath());
        ap.clickAttachFiles();

        Assert.assertEquals("1.1", ap.getLatestVersionOfAttachment(this.testAttachment));
        Assert.assertEquals("1.1", ap.getLatestVersionOfAttachment(this.testAttachment2));

        // Verify attachment contents

        ap.getAttachmentLink(this.testAttachment).click();

        Assert.assertEquals("This is a small attachment.", getDriver().findElement(By.tagName("html")).getText());
        getDriver().navigate().back();
        ap.getAttachmentLink(this.testAttachment2).click();
        Assert.assertEquals("This is another small attachment.", getDriver().findElement(By.tagName("html")).getText());
    }

    /**
     * See XWIKI-5896: The image handling in the WYSIWYG-editor with GIF images is buggy.
     */
    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void testAttachAndViewGifImage()
    {
        // Prepare the page to display the GIF image. We explicitly set the width to a value greater than the actual
        // image width because we want the code that resizes the image on the server side to be executed (even if the
        // image is not actually resized).
        ViewPage viewPage = getUtil().createPage(getClass().getSimpleName(), getTestMethodName(),
            String.format("[[image:image.gif||width=%s]]", (20 + RandomUtils.nextInt(200))), getTestClassName());

        // Attach the GIF image.
        AttachmentsPane attachmentsPane = viewPage.openAttachmentsDocExtraPane();
        attachmentsPane.setFileToUpload(getClass().getResource("/image.gif").getPath());
        attachmentsPane.clickAttachFiles();
        // clickAttachFiles should wait for the page to load but it doesn't..
        viewPage.waitUntilPageIsLoaded();
        // XWIKI-5896 shows that the file name becomes image.png
        attachmentsPane = viewPage.openAttachmentsDocExtraPane();
        Assert.assertTrue(attachmentsPane.attachmentExistsByFileName("image.gif"));
    }
}
