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
package org.xwiki.it.ui;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.it.ui.framework.elements.AttachmentsPane;
import org.xwiki.it.ui.framework.elements.ViewPage;
import org.xwiki.it.ui.framework.elements.editor.WikiEditPage;


/**
 * Test saving and downloading of attachments.
 * 
 * @version $Id:$
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
     * @Ignored because the bug isn't fixed yet.
     */
    @Test
    @Ignore
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
        wep.setContent(attacher + attachValidator);
        ViewPage vp = wep.clickSaveAndView();

        Assert.assertEquals(this.smallAttachmentString, vp.getContent());
    }

    /**
     * Make sure Document.addAttachment can be used twice.
     * @Ignored because the bug isn't fixed yet.
     */
    @Test
    @Ignore
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
        wep.setContent(attacher + attachValidator);
        ViewPage vp = wep.clickSaveAndView();

        Assert.assertEquals(this.smallAttachmentString + "\n" + this.smallAttachmentString, vp.getContent());
    }

    @Test
    public void testUploadDownloadAttachment()
    {
        final String attachValidator =
            "{{groovy}}\n"
          + "println(new String(xwiki.search(\"select content.content from XWikiAttachmentContent content, "
          + "XWikiAttachment attach, XWikiDocument doc where content.id=attach.id and attach.docId=doc.id and "
          + "attach.filename='" + this.testAttachment + "' and doc.fullName='" + docFullName + "'\").get(0), "
          + "\"UTF-8\"));\n"
          + "{{/groovy}}";

        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", docName);
        wep.setContent(attachValidator);
        ViewPage vp = wep.clickSaveAndView();

        AttachmentsPane ap = vp.openAttachmentsDocExtraPane();
        ap.addFileToAttach(this.getClass().getResource("/" + this.testAttachment).getPath());
        ap.clickAttachFiles();

        this.getDriver().navigate().refresh();

        Assert.assertEquals("This is a small attachment.", vp.getContent());

        ap.getAttachmentLinks().get(0).click();
        Assert.assertEquals("This is a small attachment.", getDriver().findElement(By.tagName("html")).getText());
    }

    @Test
    public void testUploadDownloadTwoAttachments()
    {
        final String attachValidator =
            "{{groovy}}\n"
          + "list = xwiki.search(\"select content.content from XWikiAttachmentContent content, "
          + "XWikiAttachment attach, XWikiDocument doc where content.id=attach.id and attach.docId=doc.id and "
          + "(attach.filename='" + this.testAttachment + "' or attach.filename='" + this.testAttachment2 + "') "
          + "and doc.fullName='" + docFullName + "'\");\n"
          + "println(new String(list.get(0), \"UTF-8\") + \"\\n\" + new String(list.get(1), \"UTF-8\"));\n"
          + "{{/groovy}}";

        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", docName);
        wep.setContent(attachValidator);
        ViewPage vp = wep.clickSaveAndView();

        AttachmentsPane ap = vp.openAttachmentsDocExtraPane();
        ap.addFileToAttach(this.getClass().getResource("/" + this.testAttachment).getPath());
        ap.addFileToAttach(this.getClass().getResource("/" + this.testAttachment2).getPath());
        ap.clickAttachFiles();

        this.getDriver().navigate().refresh();

        Assert.assertTrue(vp.getContent().contains("This is a small attachment."));
        Assert.assertTrue(vp.getContent().contains("This is another small attachment."));

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
}
