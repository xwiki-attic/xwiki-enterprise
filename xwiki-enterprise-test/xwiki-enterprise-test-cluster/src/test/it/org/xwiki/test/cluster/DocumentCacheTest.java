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

import java.util.Arrays;
import java.util.List;

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
    private final static List<String> TEST_SPACE = Arrays.asList("Test");
    
    @Test
    public void testDocumentCacheSync() throws Exception
    {
        // 1) Edit a page on XWiki 0
        switchXWiki(0);
        setPageContent(getWiki(), TEST_SPACE, "CacheSync", "content");
        Assert.assertEquals("content", getPageContent(getWiki(), TEST_SPACE, "CacheSync"));

        // 2) Modify content of the page on XWiki 1
        switchXWiki(1);
        setPageContent(getWiki(), TEST_SPACE, "CacheSync", "modified content");
        Assert.assertEquals("modified content", getPageContent(getWiki(), TEST_SPACE, "CacheSync"));

        // ASSERT) The content in XWiki 0 should be the one set than in XWiki 1
        // Since it can take time for the Cluster to propagate the change, we need to wait and set up a timeout.
        switchXWiki(0);
        long t1 = System.currentTimeMillis();
        long t2;
        String result;
        while (!(result = getPageContent(getWiki(), TEST_SPACE, "CacheSync")).equalsIgnoreCase("modified content")) {
            t2 = System.currentTimeMillis();
            if (t2 - t1 > 10000L) {
                Assert.fail("Content should have been [modified content] but was [" + result + "]");
            }
            Thread.sleep(100);
        }
    }

    @Test
    public void testDocumentCacheSyncForAttachments() throws Exception
    {
        // 1) Edit a page on XWiki 0
        switchXWiki(0);
        setPageContent(getWiki(), TEST_SPACE, "AttachementCacheSync", "content");

        // 2) Add attachment to the page on XWiki 1
        switchXWiki(1);
        String attachmentUri = buildURI(AttachmentResource.class, getWiki(), TEST_SPACE,
            "AttachementCacheSync", "file.ext").toString();
        PutMethod putMethod = executePut(attachmentUri, "content", MediaType.TEXT_PLAIN, "Admin", "admin");
        Assert.assertEquals(getHttpMethodInfo(putMethod), HttpStatus.SC_CREATED, putMethod.getStatusCode());

        // ASSERT) The content in XWiki 0 should be the one set than in XWiki 1
        // Since it can take time for the Cluster to propagate the change, we need to wait and set up a timeout.
        switchXWiki(0);
        String attachmentsUri = buildURI(AttachmentsResource.class, getWiki(), TEST_SPACE,
            "AttachementCacheSync").toString();

        long t1 = System.currentTimeMillis();
        long t2;
        while (!hasAttachment(attachmentsUri)) {
            t2 = System.currentTimeMillis();
            if (t2 - t1 > 10000L) {
                Assert.fail("Failed to find attachment");
            }
            Thread.sleep(100);
        }
    }

    private boolean hasAttachment(String attachmentsUri) throws Exception
    {
        GetMethod getMethod = executeGet(attachmentsUri);
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        boolean found = false;

        Attachments attachments = (Attachments) this.unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        for (Attachment attachment : attachments.getAttachments()) {
            System.out.println(attachment.getName());
            if (attachment.getName().equals("file.ext")) {
                found = true;
                break;
            }
        }

        return found;
    }
}
