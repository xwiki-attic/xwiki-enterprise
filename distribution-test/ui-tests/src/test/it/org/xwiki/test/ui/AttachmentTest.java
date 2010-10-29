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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.AttachmentsPane;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;

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

    private final String docName = "AttachmentTest";

    private final String docFullName = "Test.AttachmentTest";

    private final String smallAttachmentString = "This is content for a very small attachment.";

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage("Test", docName);
    }

    /**
     * Tests that XWIKI-5405 remains fixed.
     * This test proves that when an attachment is saved using Document.addAttachment and then Document.save()
     * the attachment is actually persisted to the database.
     * TODO: How can this be made into a unit test?
     * Answer: Just verify that the store interface method for saving the attachment has been called using an
     * expectation. And only have only *one* functional test to prove that the store save attachment method works.
     */
    @Test
    public void testDocumentAddAttachment()
    {
        final String filename = "littleAttachment.txt";

        final String attacher =
            "{{velocity}}\n"
          + "#set($attachmentContent = '" + this.smallAttachmentString + "')\n"
          + "#set($discard = $doc.addAttachment('" + filename + "', $attachmentContent.getBytes('UTF-8')))\n"
          + "$doc.save()\n"
          + "{{/velocity}}\n";

        final String attachValidator =
            "{{groovy}}\n"
          + "println(new String(xwiki.search(\"select content.content from XWikiAttachmentContent content, "
          + "XWikiAttachment attach, XWikiDocument doc where content.id=attach.id and attach.docId=doc.id and "
          + "attach.filename='" + filename + "' and doc.fullName='" + docFullName + "'\").get(0), "
          + "\"UTF-8\"));\n"
          + "{{/groovy}}";

        // Create a page, add content which automatically adds an attachment to itself.
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", docName);
        wep.setTitle("AttachmentTest#testDocumentAddAttachment()");
        wep.setContent(attacher + attachValidator);
        ViewPage vp = wep.clickSaveAndView();

        Assert.assertEquals(this.smallAttachmentString, vp.getContent());
        Assert.assertEquals("1.1", vp.openAttachmentsDocExtraPane().getLatestVersionOfAttachment(filename));
    }

    /**
     * Make sure Document.addAttachment can be used twice.
     */
    @Test
    public void testDocumentAddTwoAttachments()
    {
        final String filename1 = "attach1.txt";

        final String filename2 = "attach2.txt";

        final String attacher =
            "{{velocity}}\n"
          + "#set($attachmentContent = '" + this.smallAttachmentString + "')\n"
          + "#set($discard = $doc.addAttachment('" + filename1 + "', $attachmentContent.getBytes('UTF-8')))\n"
          + "#set($discard = $doc.addAttachment('" + filename2 + "', $attachmentContent.getBytes('UTF-8')))\n"
          + "$doc.save()\n"
          + "{{/velocity}}\n";

        final String attachValidator =
            "{{groovy}}\n"
          + "list = xwiki.search(\"select content.content from XWikiAttachmentContent content, "
          + "XWikiAttachment attach, XWikiDocument doc where content.id=attach.id and attach.docId=doc.id and "
          + "(attach.filename='" + filename1 + "' or attach.filename='" + filename2 + "') and doc.fullName='"
          + docFullName + "'\")\n"
          + "println(new String(list.get(0), \"UTF-8\") + \"\\n\" + new String(list.get(1), \"UTF-8\"));\n"
          + "{{/groovy}}";

        // Create a page, add content which automatically adds an attachment to itself.
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", docName);
        wep.setTitle("AttachmentTest#testDocumentAddTwoAttachments()");
        wep.setContent(attacher + attachValidator);
        ViewPage vp = wep.clickSaveAndView();

        Assert.assertEquals(this.smallAttachmentString + "\n" + this.smallAttachmentString, vp.getContent());
        AttachmentsPane ap = vp.openAttachmentsDocExtraPane();
        Assert.assertEquals("1.1", ap.getLatestVersionOfAttachment(filename1));
        Assert.assertEquals("1.1", ap.getLatestVersionOfAttachment(filename2));
    }

    @Test
    public void testUploadDownloadAttachment()
    {
        // this mess is here to make absolutely sure the attachment goes into the database and is not being
        // stored in a cache or on the hard disk.
        final String attachValidator =
            "{{groovy}}\n"
          + "println(new String(xwiki.search(\"select content.content from XWikiAttachmentContent content, "
          + "XWikiAttachment attach, XWikiDocument doc where content.id=attach.id and attach.docId=doc.id and "
          + "attach.filename='" + this.testAttachment + "' and doc.fullName='" + docFullName + "'\").get(0), "
          + "\"UTF-8\"));\n"
          + "{{/groovy}}";

        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", docName);
        wep.setTitle("AttachmentTest#testUploadDownloadAttachment()");
        wep.setContent(attachValidator);
        ViewPage vp = wep.clickSaveAndView();

        AttachmentsPane ap = vp.openAttachmentsDocExtraPane();
        ap.setFileToUpload(this.getClass().getResource("/" + this.testAttachment).getPath());
        ap.clickAttachFiles();

        this.getDriver().navigate().refresh();

        Assert.assertEquals("This is a small attachment.", vp.getContent());
        Assert.assertEquals("1.1", ap.getLatestVersionOfAttachment(this.testAttachment));

        ap.getAttachmentLinks().get(0).click();
        Assert.assertEquals("This is a small attachment.", getDriver().findElement(By.tagName("html")).getText());
    }

    @Test
    public void testUploadDownloadTwoAttachments()
    {
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", docName);
        wep.setTitle("AttachmentTest#testUploadDownloadTwoAttachments()");
        ViewPage vp = wep.clickSaveAndView();

        AttachmentsPane ap = vp.openAttachmentsDocExtraPane();
        ap.setFileToUpload(this.getClass().getResource("/" + this.testAttachment).getPath());
        ap.addAnotherFile();
        ap.setFileToUpload(this.getClass().getResource("/" + this.testAttachment2).getPath());
        ap.clickAttachFiles();

        Assert.assertEquals("1.1", ap.getLatestVersionOfAttachment(this.testAttachment));

        // This is breaking because of a bug. TODO: fix.
        //Assert.assertEquals("1.1", ap.getLatestVersionOfAttachment(this.testAttachment2));

        List<WebElement> links = ap.getAttachmentLinks();
        links.get(0).click();
        // This test does not prove that the attachments will be shown in any particular order.
        String content = getDriver().findElement(By.tagName("html")).getText();
        int firstAttachNum = 0;
        if (content.equals("This is a small attachment.")) {
            firstAttachNum = 1;
        } else {
            Assert.assertEquals("This is another small attachment.", content);
            firstAttachNum = 2;
        }

        getDriver().navigate().back();
        links.get(1).click();
        content = getDriver().findElement(By.tagName("html")).getText();

        if (firstAttachNum == 2) {
            Assert.assertEquals("This is a small attachment.", content);
        } else {
            Assert.assertEquals("This is another small attachment.", content);
        }
    }

    /**
     * Tests that XWIKI-5436 remains fixed.
     * This test proves that when an attachment is saved using Document.addAttachment and then the document is saved
     * a number of times after, the attachment verstion is not incremented.
     * It also checks that XWikiAttachment.isContentDirty() is false unless the attachment has just been modified.
     */
    @Test
    public void testAttachmentContentDirty()
    {
        final String attacher =
            "{{velocity}}\n"
          + "#set($attachmentContent = '" + this.smallAttachmentString + "')\n"
          + "#set($discard = $doc.addAttachment('" + this.testAttachment + "', $attachmentContent.getBytes('UTF-8')))\n"
          + "$doc.getDocument().getAttachmentList().get(0).isContentDirty()\n"
          + "$doc.save()\n"
          + "$xwiki.getDocument($doc.getFullName()).getDocument().getAttachmentList().get(0).isContentDirty()\n"
          + "{{/velocity}}\n";

        // Create a page, add content which automatically adds an attachment to itself and validates that the content
        // is dirty until the ocument is saved.
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", docName);
        wep.setTitle("AttachmentTest#testAttachmentContentDirty()");
        wep.setContent(attacher);
        ViewPage vp = wep.clickSaveAndView();
        Assert.assertEquals("true\nfalse", vp.getContent());

        // Resave the doc and:
        // #1 ensure that on load the attach content isn't dirty.
        // #2 make sure the version of the attachment has not been incremented.
        wep.switchToEdit("Test", docName);
        wep.setContent(
            "{{velocity}}\n"
          + "$xwiki.getDocument($doc.getFullName()).getDocument().getAttachmentList().get(0).isContentDirty()\n"
          + "{{/velocity}}\n");
        vp = wep.clickSaveAndView();
        Assert.assertEquals("false", vp.getContent());
        AttachmentsPane ap = vp.openAttachmentsDocExtraPane();
        Assert.assertEquals("1.1", ap.getLatestVersionOfAttachment(this.testAttachment));

        // Reupload the attachment and make sure the version is incremented.
        ap.setFileToUpload(this.getClass().getResource("/" + this.testAttachment).getPath());
        ap.clickAttachFiles();
        this.getDriver().navigate().refresh();
        Assert.assertEquals("1.2", ap.getLatestVersionOfAttachment(this.testAttachment));
    }
}
