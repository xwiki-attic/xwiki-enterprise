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
package org.xwiki.test.cluster;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.Attachments;
import org.xwiki.rest.resources.attachments.AttachmentResource;
import org.xwiki.rest.resources.attachments.AttachmentsResource;
import org.xwiki.test.cluster.framework.AbstractClusterHttpTest;

/**
 * Verify the document cache update based on distributed events.
 * 
 * @version $Id$
 */
public class DocumentCacheTest extends AbstractClusterHttpTest
{
    @Test
    public void testDocumentCacheSync() throws Exception
    {
        // 1) edit a page on XWiki 0

        switchXWiki(0);
        setPageContent(getWiki(), "Test", "CacheSync", "content");
        Assert.assertEquals("content", getPageContent(getWiki(), "Test", "CacheSync"));

        // 2) modify content of the page on XWiki 1

        switchXWiki(1);
        setPageContent(getWiki(), "Test", "CacheSync", "modified content");
        Assert.assertEquals("modified content", getPageContent(getWiki(), "Test", "CacheSync"));

        // TODO: give some time to JGroups to send the message

        // ASSERT) the content in XWiki 0 should be the one set than in XWiki 1

        switchXWiki(0);
        Assert.assertEquals("modified content", getPageContent(getWiki(), "Test", "CacheSync"));
    }

    @Test
    public void testDocumentCacheSyncForAttachments() throws Exception
    {
        // 1) edit a page on XWiki 0

        switchXWiki(0);
        setPageContent(getWiki(), "Test", "AttachementCacheSync", "content");

        // 2) add attachment to the page on XWiki 1

        switchXWiki(1);
        String attachmentUri =
                getUriBuilder(AttachmentResource.class).build(getWiki(), "Test", "AttachementCacheSync", "file.ext").toString();
        PutMethod putMethod = executePut(attachmentUri, "content", MediaType.TEXT_PLAIN, "Admin", "admin");
        Assert.assertEquals(getHttpMethodInfo(putMethod), HttpStatus.SC_CREATED, putMethod.getStatusCode());

        // TODO: give some time to JGroups to send the message

        // ASSERT) the content in XWiki 0 should be the one set than in XWiki 1

        switchXWiki(0);
        String attachmentsUri =
                getUriBuilder(AttachmentsResource.class).build(getWiki(), "Test", "AttachementCacheSync").toString();

        GetMethod getMethod = executeGet(attachmentsUri);
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        boolean found = false;

        Attachments attachments = (Attachments) this.unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        for (Attachment attachment : attachments.getAttachments()) {
            System.out.println(attachment.getName());
            if (attachment.getName().equals("file.ext")) {
                found = true;
            }
        }

        Assert.assertTrue("Failed to find attachment", found);
    }
}
