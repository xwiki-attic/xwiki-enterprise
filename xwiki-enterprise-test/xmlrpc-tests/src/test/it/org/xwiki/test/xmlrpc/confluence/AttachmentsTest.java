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
package org.xwiki.test.xmlrpc.confluence;

import java.util.List;

import org.codehaus.swizzle.confluence.Attachment;
import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.Space;
import org.xwiki.test.xmlrpc.confluence.framework.AbstractXmlRpcTestCase;

public class AttachmentsTest extends AbstractXmlRpcTestCase
{
    private String pageId;

    private String spaceKey;

    public void setUp() throws Exception
    {
        super.setUp();

        spaceKey = "SomeSpaceReally";
        Space space = new Space();
        space.setKey(spaceKey);
        space.setName("Some Name");

        rpc.addSpace(space);

        String pageTitle = "SomePage";
        Page p = new Page();
        p.setSpace(spaceKey);
        p.setTitle(pageTitle);
        p.setContent("Dummy Comment");
        Page resultPage = rpc.storePage(p);
        pageId = resultPage.getId();
    }

    public void tearDown() throws Exception
    {
        rpc.removePage(pageId);
        rpc.removeSpace(spaceKey);

        super.tearDown();
    }

    public void testAttachments() throws Exception
    {
        // Add attachment
        Attachment attach0 = new Attachment();
        String fileName = "test.txt";
        String contentType = "text/plain";
        attach0.setFileName(fileName);
        attach0.setPageId(pageId);
        attach0.setContentType(contentType);
        byte[] data0 = new byte[4];
        data0[0] = 't';
        data0[1] = 'e';
        data0[2] = 's';
        data0[3] = 't';
        Attachment attach00 = rpc.addAttachment(0, attach0, data0);
        assertEquals(pageId, attach00.getPageId());
        assertEquals(fileName, attach00.getFileName());
        assertEquals(fileName, attach00.getTitle());
        assertEquals("" + data0.length, attach00.getFileSize());
        assertEquals(contentType, attach00.getContentType());

        /*The Confluence API is broken for attachment versions. There is no way for knowing what versions of an attachment are available.
        Since we are using the Confluence client here, this test cannot be done. */
//        // Get attachment versions
//        List versions = rpc.getAttachmentVersions(pageId, fileName);
//        
//        // Get attachment
//        Attachment attach000 = rpc.getAttachment(pageId, fileName, (String)versions.get(0));
//        assertEquals(attach00.getId(), attach000.getId());
//        assertEquals(attach00.getPageId(), attach000.getPageId());
//        assertEquals(attach00.getTitle(), attach000.getTitle());
//        assertEquals(attach00.getFileName(), attach000.getFileName());
//        assertEquals(attach00.getFileSize(), attach000.getFileSize());
//        assertEquals(attach00.getContentType(), attach000.getContentType());
//        assertEquals(attach00.getCreated(), attach000.getCreated());
//        assertEquals(attach00.getCreator(), attach000.getCreator());
//        assertEquals(attach00.getUrl(), attach000.getUrl());
//        assertEquals(attach00.getComment(), attach000.getComment());

        // Get attachment data
        byte[] data00 = rpc.getAttachmentData(pageId, fileName, ""/*(String)versions.get(0)*/);
        assertEquals(data0.length, data00.length);
        for (int i = 0; i < data0.length; i++) {
            assertEquals(data0[i], data00[i]);
        }

        // Remove attachment
        rpc.removeAttachment(pageId, fileName);

        // Get attachments
        List list = rpc.getAttachments(pageId);
        assertTrue(list.isEmpty());

        // Add two attachments
        Attachment attach1 = new Attachment();
        String fileName1 = "file1";
        attach1.setFileName(fileName1);
        attach1.setPageId(pageId);
        attach1.setContentType("");
        byte[] data1 = new byte[0];
        rpc.addAttachment(0, attach1, data1);

        Attachment attach2 = new Attachment();
        String fileName2 = "file2";
        attach2.setFileName(fileName2);
        attach2.setPageId(pageId);
        attach2.setContentType("");
        byte[] data2 = new byte[0];
        rpc.addAttachment(0, attach2, data2);

        // Get attachments
        list = rpc.getAttachments(pageId);
        assertEquals(2, list.size());
        assertEquals(fileName1, ((Attachment) list.get(0)).getFileName());
        assertEquals(fileName2, ((Attachment) list.get(1)).getFileName());

        // Remove attachments
        rpc.removeAttachment(pageId, fileName1);
        rpc.removeAttachment(pageId, fileName2);

        // Get attachments
        list = rpc.getAttachments(pageId);
        assertTrue(list.isEmpty());
    }

    /*The Confluence API is broken for attachment versions. There is no way for knowing what versions of an attachment are available.
    Since we are using the Confluence client here, this test cannot be done. (there is no such a method getAttachmentVersions in the Confluence API
    */
//    public void testAttachmentsHistory() throws Exception
//    {
//        // Add attachment
//        Attachment attach = new Attachment();
//        String fileName = "test.txt";
//        String contentType = "text/plain";
//        attach.setFileName(fileName);
//        attach.setPageId(pageId);
//        attach.setContentType(contentType);
//        byte[] data0 = new byte[4];
//        data0[0] = 't'; data0[1] = 'e'; data0[2] = 's'; data0[3] = 't';
//        rpc.addAttachment(pageId, attach, data0);
//
//        // Overwrite attachment
//        byte[] data1 = new byte[4];
//        data1[0] = '0'; data1[1] = '1'; data1[2] = '2'; data1[3] = '3';
//        rpc.addAttachment(pageId, attach, data1);
//
//        // Get attachment versions
//        List versions = rpc.getAttachmentVersions(pageId, fileName);
//        
//        // Get attachments
//        byte[] data00 = rpc.getAttachmentData(pageId, fileName, (String)versions.get(0));
//        assertEquals(data0.length, data00.length);
//        for (int i = 0; i<data0.length; i++) {
//            assertEquals(data0[i], data00[i]);            
//        }
//        byte[] data11 = rpc.getAttachmentData(pageId, fileName, (String)versions.get(1));
//        assertEquals(data1.length, data11.length);
//        for (int i = 0; i<data1.length; i++) {
//            assertEquals(data1[i], data11[i]);            
//        }
//    }
}
