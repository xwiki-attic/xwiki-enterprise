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
package org.xwiki.rest.it;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.xwiki.rest.Constants;
import org.xwiki.rest.Utils;
import org.xwiki.rest.model.Attachments;
import org.xwiki.rest.resources.attachments.AttachmentResource;
import org.xwiki.rest.resources.attachments.AttachmentsResource;

public class AttachmentsResourceTest extends AbstractHttpTest
{
    private final String SPACE_NAME = "Main";

    private final String PAGE_NAME = "WebHome";

    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");
        /* Everything is done in test methods */
    }

    public void testPUTAttachment() throws Exception
    {
        TestUtils.banner("testCreateAttachment()");

        String attachmentName = String.format("%d.txt", System.currentTimeMillis());
        String content = "ATTACHMENT CONTENT";

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);
        parametersMap.put(Constants.ATTACHMENT_NAME_PARAMETER, attachmentName);

        String attachmentUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(AttachmentResource.class), parametersMap));

        GetMethod getMethod = executeGet(attachmentUri);
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        PutMethod putMethod = executePlainPut(attachmentUri, content, "Admin", "admin");
        assertEquals(HttpStatus.SC_CREATED, putMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(putMethod);

        getMethod = executeGet(attachmentUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        assertEquals(content, getMethod.getResponseBodyAsString());
    }

    public void testGETAttachments() throws Exception
    {
        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);

        String attachmentsUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(AttachmentsResource.class), parametersMap));

        GetMethod getMethod = executeGet(attachmentsUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Attachments attachments = (Attachments) xstream.fromXML(getMethod.getResponseBodyAsString());

        assertTrue(attachments.getAttachmentList().size() > 0);
    }

    public void testDELETEAttachment() throws Exception
    {
        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);

        String attachmentsUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(AttachmentsResource.class), parametersMap));

        GetMethod getMethod = executeGet(attachmentsUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Attachments attachments = (Attachments) xstream.fromXML(getMethod.getResponseBodyAsString());

        assertTrue(attachments.getAttachmentList().size() > 0);

        String attachmentName = attachments.getAttachmentList().get(0).getName();
        parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);
        parametersMap.put(Constants.ATTACHMENT_NAME_PARAMETER, attachmentName);

        String attachmentUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(AttachmentResource.class), parametersMap));

        DeleteMethod deleteMethod = executeDelete(attachmentUri, "Admin", "admin");
        assertEquals(HttpStatus.SC_NO_CONTENT, deleteMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(deleteMethod);

        getMethod = executeGet(attachmentUri);
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
    }

    public void testDELETEAttachmentNoRights() throws Exception
    {
        String attachmentName = String.format("%d.txt", System.currentTimeMillis());
        String content = "ATTACHMENT CONTENT";

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);
        parametersMap.put(Constants.ATTACHMENT_NAME_PARAMETER, attachmentName);

        String attachmentUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(AttachmentResource.class), parametersMap));
        
        PutMethod putMethod = executePlainPut(attachmentUri, content, "Admin", "admin");
        assertEquals(HttpStatus.SC_CREATED, putMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(putMethod);

        parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);
        parametersMap.put(Constants.ATTACHMENT_NAME_PARAMETER, attachmentName);

        attachmentUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(AttachmentResource.class), parametersMap));

        DeleteMethod deleteMethod = executeDelete(attachmentUri, "Guest", "guest");
        assertEquals(HttpStatus.SC_FORBIDDEN, deleteMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(deleteMethod);

        GetMethod getMethod = executeGet(attachmentUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
    }
}
