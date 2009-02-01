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
import org.apache.jackrabbit.uuid.UUID;
import org.xwiki.rest.Constants;
import org.xwiki.rest.Utils;
import org.xwiki.rest.model.Attachment;
import org.xwiki.rest.model.Attachments;
import org.xwiki.rest.model.Relations;
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
        TestUtils.banner("testGETAttachments()");

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
        TestUtils.banner("testDELETEAttachment()");

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
        TestUtils.banner("testDELETEAttachmentNoRights()");

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
            parametersMap = new HashMap<String, String>();
            parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
            parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
            parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);
            parametersMap.put(Constants.ATTACHMENT_NAME_PARAMETER, attachmentNames[i]);

            String attachmentUri =
                getFullUri(Utils.formatUriTemplate(getUriPatternForResource(AttachmentResource.class), parametersMap));

            PutMethod putMethod = executePlainPut(attachmentUri, content, "Admin", "admin");
            assertEquals(HttpStatus.SC_CREATED, putMethod.getStatusCode());
            TestUtils.printHttpMethodInfo(putMethod);

            Attachment attachment = (Attachment) xstream.fromXML(putMethod.getResponseBodyAsString());
            pageVersions[i] = attachment.getPageVersion();

            System.out.format("Attachment %s stored at page version %s\n", attachmentNames[i], pageVersions[i]);
        }

        /*
         * For each page version generated, check that the attachments that are supposed to be there are actually there.
         * We do the following: at pageVersion[i] we check that all attachmentNames[0..i] are there.
         */
        for (int i = 0; i < NUMBER_OF_ATTACHMENTS; i++) {
            parametersMap = new HashMap<String, String>();
            parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
            parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
            parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);
            parametersMap.put(Constants.PAGE_VERSION_PARAMETER, pageVersions[i]);

            String attachmentsUri =
                getFullUri(Utils.formatUriTemplate(getUriPatternForResource(AttachmentsAtPageVersionResource.class),
                    parametersMap));

            GetMethod getMethod = executeGet(attachmentsUri);
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
            TestUtils.printHttpMethodInfo(getMethod);

            Attachments attachments = (Attachments) xstream.fromXML(getMethod.getResponseBodyAsString());

            /*
             * Check that all attachmentNames[0..i] are present in the list of attachments of page at version
             * pageVersions[i]
             */
            for (int j = 0; j <= i; j++) {
                System.out.format("Checking that %s is present in attachments list of the page at version %s... ",
                    attachmentNames[j], pageVersions[i]);

                boolean found = false;
                for (Attachment attachment : attachments.getAttachmentList()) {
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
            for (Attachment attachment : attachments.getAttachmentList()) {
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
            parametersMap = new HashMap<String, String>();
            parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
            parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
            parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);
            parametersMap.put(Constants.ATTACHMENT_NAME_PARAMETER, attachmentName);

            String attachmentUri =
                getFullUri(Utils.formatUriTemplate(getUriPatternForResource(AttachmentResource.class), parametersMap));

            String content = String.format("CONTENT %d", i);
            PutMethod putMethod = executePlainPut(attachmentUri, content, "Admin", "admin");
            if (i == 0) {
                assertEquals(HttpStatus.SC_CREATED, putMethod.getStatusCode());
            } else {
                assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());
            }
            TestUtils.printHttpMethodInfo(putMethod);

            Attachment attachment = (Attachment) xstream.fromXML(putMethod.getResponseBodyAsString());

            System.out.format("Attachment %s stored at version %s: %s\n", attachmentName, attachment.getVersion(),
                content);

            versionToContentMap.put(attachment.getVersion(), content);
        }

        parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);
        parametersMap.put(Constants.ATTACHMENT_NAME_PARAMETER, attachmentName);

        String attachmentsUri =
            getFullUri(Utils
                .formatUriTemplate(getUriPatternForResource(AttachmentHistoryResource.class), parametersMap));

        GetMethod getMethod = executeGet(attachmentsUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Attachments attachments = (Attachments) xstream.fromXML(getMethod.getResponseBodyAsString());

        for (Attachment attachment : attachments.getAttachmentList()) {
            getMethod = executeGet(attachment.getFirstLinkByRelation(Relations.ATTACHMENT_DATA).getHref());
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
            TestUtils.printHttpMethodInfo(getMethod);

            assertEquals(versionToContentMap.get(attachment.getVersion()), getMethod.getResponseBodyAsString());
        }

    }
}
