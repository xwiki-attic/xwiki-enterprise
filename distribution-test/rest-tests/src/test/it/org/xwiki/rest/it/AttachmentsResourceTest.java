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
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.xwiki.rest.Relations;
import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.Attachments;
import org.xwiki.rest.resources.attachments.AttachmentHistoryResource;
import org.xwiki.rest.resources.attachments.AttachmentResource;
import org.xwiki.rest.resources.attachments.AttachmentsAtPageVersionResource;
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
        TestUtils.banner("testPUTAttachment()");

        String attachmentName = String.format("%s.txt", UUID.randomUUID());
        String content = "ATTACHMENT CONTENT";

        String attachmentUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME, attachmentName).toString();

        GetMethod getMethod = executeGet(attachmentUri);
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());

        PutMethod putMethod = executePut(attachmentUri, content, MediaType.TEXT_PLAIN, "Admin", "admin");
        TestUtils.printHttpMethodInfo(putMethod);
        assertEquals(HttpStatus.SC_CREATED, putMethod.getStatusCode());

        getMethod = executeGet(attachmentUri);
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        assertEquals(content, getMethod.getResponseBodyAsString());
    }

    public void testPUTAttachmentNoRights() throws Exception
    {
        TestUtils.banner("testPUTAttachment()");

        String attachmentName = String.format("%s.txt", UUID.randomUUID());
        String content = "ATTACHMENT CONTENT";

        String attachmentUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME, attachmentName).toString();

        GetMethod getMethod = executeGet(attachmentUri);
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());

        PutMethod putMethod = executePut(attachmentUri, content, MediaType.TEXT_PLAIN);
        TestUtils.printHttpMethodInfo(putMethod);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, putMethod.getStatusCode());
    }

    public void testGETAttachments() throws Exception
    {
        TestUtils.banner("testGETAttachments()");

        String attachmentsUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentsResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME).toString();

        GetMethod getMethod = executeGet(attachmentsUri);
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Attachments attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertTrue(attachments.getAttachments().size() > 0);
    }

    public void testDELETEAttachment() throws Exception
    {
        TestUtils.banner("testDELETEAttachment()");

        String attachmentsUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentsResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME).toString();

        GetMethod getMethod = executeGet(attachmentsUri);
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Attachments attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertTrue(attachments.getAttachments().size() > 0);

        String attachmentName = attachments.getAttachments().get(0).getName();

        String attachmentUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME, attachmentName).toString();

        DeleteMethod deleteMethod = executeDelete(attachmentUri, "Admin", "admin");
        TestUtils.printHttpMethodInfo(deleteMethod);
        assertEquals(HttpStatus.SC_NO_CONTENT, deleteMethod.getStatusCode());

        getMethod = executeGet(attachmentUri);
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());
    }

    public void testDELETEAttachmentNoRights() throws Exception
    {
        TestUtils.banner("testDELETEAttachmentNoRights()");

        String attachmentName = String.format("%d.txt", System.currentTimeMillis());
        String content = "ATTACHMENT CONTENT";

        String attachmentUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME, attachmentName).toString();

        PutMethod putMethod = executePut(attachmentUri, content, MediaType.TEXT_PLAIN, "Admin", "admin");
        TestUtils.printHttpMethodInfo(putMethod);
        assertEquals(HttpStatus.SC_CREATED, putMethod.getStatusCode());

        DeleteMethod deleteMethod = executeDelete(attachmentUri);
        TestUtils.printHttpMethodInfo(deleteMethod);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, deleteMethod.getStatusCode());

        GetMethod getMethod = executeGet(attachmentUri);
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
    }

    public void testGETAttachmentsAtPageVersion() throws Exception
    {
        TestUtils.banner("testGETAttachmentsAtPageVersion");

        final int NUMBER_OF_ATTACHMENTS = 4;
        String[] attachmentNames = new String[NUMBER_OF_ATTACHMENTS];
        String[] pageVersions = new String[NUMBER_OF_ATTACHMENTS];

        for (int i = 0; i < NUMBER_OF_ATTACHMENTS; i++) {
            attachmentNames[i] = String.format("%s.txt", UUID.randomUUID());
        }

        String content = "ATTACHMENT CONTENT";

        Map<String, String> parametersMap;

        /* Create NUMBER_OF_ATTACHMENTS attachments */
        for (int i = 0; i < NUMBER_OF_ATTACHMENTS; i++) {
            String attachmentUri =
                UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentResource.class).build(getWiki(),
                    SPACE_NAME, PAGE_NAME, attachmentNames[i]).toString();

            PutMethod putMethod = executePut(attachmentUri, content, MediaType.TEXT_PLAIN, "Admin", "admin");
            TestUtils.printHttpMethodInfo(putMethod);
            assertEquals(HttpStatus.SC_CREATED, putMethod.getStatusCode());

            Attachment attachment = (Attachment) unmarshaller.unmarshal(putMethod.getResponseBodyAsStream());
            pageVersions[i] = attachment.getPageVersion();

            System.out.format("Attachment %s stored at page version %s\n", attachmentNames[i], pageVersions[i]);
        }

        /*
         * For each page version generated, check that the attachments that are supposed to be there are actually there.
         * We do the following: at pageVersion[i] we check that all attachmentNames[0..i] are there.
         */
        for (int i = 0; i < NUMBER_OF_ATTACHMENTS; i++) {
            String attachmentsUri =
                UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentsAtPageVersionResource.class)
                    .build(getWiki(), SPACE_NAME, PAGE_NAME, pageVersions[i]).toString();

            GetMethod getMethod = executeGet(attachmentsUri);
            TestUtils.printHttpMethodInfo(getMethod);
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

            Attachments attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

            /*
             * Check that all attachmentNames[0..i] are present in the list of attachments of page at version
             * pageVersions[i]
             */
            for (int j = 0; j <= i; j++) {
                System.out.format("Checking that %s is present in attachments list of the page at version %s... ",
                    attachmentNames[j], pageVersions[i]);

                boolean found = false;
                for (Attachment attachment : attachments.getAttachments()) {
                    if (attachment.getName().equals(attachmentNames[j])) {
                        if (attachment.getPageVersion().equals(pageVersions[i])) {
                            System.out.format("OK\n");
                            found = true;
                            break;
                        }
                    }
                }
                assertTrue(found);
            }

            /* Check links */
            for (Attachment attachment : attachments.getAttachments()) {
                checkLinks(attachment);
            }
        }
    }

    public void testGETAttachmentVersions() throws Exception
    {
        TestUtils.banner("testGETAttachmentVersions");

        final int NUMBER_OF_VERSIONS = 4;
        String attachmentName = String.format("%s.txt", UUID.randomUUID().toString());

        Map<String, String> versionToContentMap = new HashMap<String, String>();

        Map<String, String> parametersMap;

        /* Create NUMBER_OF_ATTACHMENTS attachments */
        for (int i = 0; i < NUMBER_OF_VERSIONS; i++) {
            String attachmentUri =
                UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentResource.class).build(getWiki(),
                    SPACE_NAME, PAGE_NAME, attachmentName).toString();

            String content = String.format("CONTENT %d", i);
            PutMethod putMethod = executePut(attachmentUri, content, MediaType.TEXT_PLAIN, "Admin", "admin");
            TestUtils.printHttpMethodInfo(putMethod);
            if (i == 0) {
                assertEquals(HttpStatus.SC_CREATED, putMethod.getStatusCode());
            } else {
                assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());
            }

            Attachment attachment = (Attachment) unmarshaller.unmarshal(putMethod.getResponseBodyAsStream());

            System.out.format("Attachment %s stored at version %s: %s\n", attachmentName, attachment.getVersion(),
                content);

            versionToContentMap.put(attachment.getVersion(), content);
        }

        String attachmentsUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentHistoryResource.class).build(
                getWiki(), SPACE_NAME, PAGE_NAME, attachmentName).toString();

        GetMethod getMethod = executeGet(attachmentsUri);
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Attachments attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        for (Attachment attachment : attachments.getAttachments()) {
            getMethod = executeGet(getFirstLinkByRelation(attachment, Relations.ATTACHMENT_DATA).getHref());
            TestUtils.printHttpMethodInfo(getMethod);
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

            assertEquals(versionToContentMap.get(attachment.getVersion()), getMethod.getResponseBodyAsString());
        }
    }

    public void testPOSTAttachment() throws Exception
    {
        TestUtils.banner("testPUTAttachment()");

        final String attachmentName = String.format("%s.txt", UUID.randomUUID());
        final String content = "ATTACHMENT CONTENT";

        String attachmentsUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(AttachmentsResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME, attachmentName).toString();

        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("Admin", "admin"));
        httpClient.getParams().setAuthenticationPreemptive(true);

        Part[] parts = new Part[1];

        ByteArrayPartSource baps = new ByteArrayPartSource(attachmentName, content.getBytes());
        parts[0] = new FilePart(attachmentName, baps);

        PostMethod postMethod = new PostMethod(attachmentsUri);
        MultipartRequestEntity mpre = new MultipartRequestEntity(parts, postMethod.getParams());
        postMethod.setRequestEntity(mpre);
        httpClient.executeMethod(postMethod);
        TestUtils.printHttpMethodInfo(postMethod);
        assertEquals(HttpStatus.SC_CREATED, postMethod.getStatusCode());

        Attachment attachment = (Attachment) unmarshaller.unmarshal(postMethod.getResponseBodyAsStream());

        Header location = postMethod.getResponseHeader("location");

        GetMethod getMethod = executeGet(location.getValue());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        assertEquals(content, getMethod.getResponseBodyAsString());
    }
}
