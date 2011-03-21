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
package org.xwiki.test.storage;

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.MultipartPostMethod;

/**
 * Test saving and downloading of attachments.
 *
 * @version $Id$
 * @since 3.0M3
 */
public class AttachmentTest
{
    private static final String ADDRESS_PREFIX = "http://127.0.0.1:8080/xwiki/bin/";

    private static final String ATTACHMENT_CONTENT = "This is content for a very small attachment.";

    private static final String FILENAME = "littleAttachment.txt";

    /**
     * Tests that XWIKI-5405 remains fixed.
     * This test proves that when an attachment is saved using Document.addAttachment and
     * then Document.save() the attachment is actually persisted to the database.
     */
    @Test
    public void testDocumentAddAttachment() throws Exception
    {
        final String test =
            "{{groovy}}\n"
          // First the attacher.
          + "doc.addAttachment('" + FILENAME + "', '" + ATTACHMENT_CONTENT
          + "'.getBytes('UTF-8'));\n"
          + "doc.saveAsAuthor();"
          // then the validator.
          + "println(xwiki.getDocument(doc.getDocumentReference()).getAttachment('" + FILENAME
          + "').getContentAsString());"
          + "{{/groovy}}";

        // Create the document if it exists.
        doPost(ADDRESS_PREFIX + "delete/Test/Attachment?confirm=1&basicauth=1",
               new String[] {"Admin", "admin"}, null);

        // Create a document.
        doPost(ADDRESS_PREFIX + "save/Test/Attachment",
               new String[] {"Admin", "admin"},
               new HashMap<String, String>() {{
            put("basicauth", "1");
            put("content", test);
        }});

        HttpMethod ret = null;

        // Test getAttachment()
        ret = doPost(ADDRESS_PREFIX + "view/Test/Attachment?xpage=plain", null, null);
        Assert.assertEquals("<p>" + ATTACHMENT_CONTENT + "</p>", ret.getResponseBodyAsString());

        // Test downloadAction.
        ret = doPost(ADDRESS_PREFIX + "download/Test/Attachment/" + FILENAME, null, null);
        Assert.assertEquals(ATTACHMENT_CONTENT, new String(ret.getResponseBody(), "UTF-8"));

        // Make sure there is exactly 1 version of this attachment.
        ret = doPost(ADDRESS_PREFIX + "preview/Test/Attachment?xpage=plain",
               new String[] {"Admin", "admin"},
               new HashMap<String, String>() {{
            put("basicauth", "1");
            put("content", "{{velocity}}$doc.getAttachment('"
                           + FILENAME + "').getVersions().size(){{/velocity}}");
        }});
        Assert.assertEquals("<p>1</p>", ret.getResponseBodyAsString());

        // Make sure that version contains the correct content.
        ret = doPost(ADDRESS_PREFIX + "preview/Test/Attachment?xpage=plain",
                     new String[] {"Admin", "admin"},
                     new HashMap<String, String>() {{
            put("basicauth", "1");
            put("content", "{{velocity}}$doc.getAttachment('" + FILENAME
                           + "').getAttachmentRevision('1.1').getContentAsString(){{/velocity}}");
        }});
        Assert.assertEquals("<p>" + ATTACHMENT_CONTENT + "</p>", ret.getResponseBodyAsString());
    }

    @Test
    public void testImportDocumentWithAttachment() throws Exception
    {
        HttpMethod ret = null;
        // Upload the XAR to import.
        ret = doUpload(ADDRESS_PREFIX + "upload/XWiki/XWikiPreferences?basicauth=1",
                       new String[] {"Admin", "admin"},
                       new HashMap<String, File>() {{
            put("filepath", new File(this.getClass().getResource("/Test.Attachment2.xar").toURI()));
        }});

        // Do the import.
        ret = doPost(ADDRESS_PREFIX + "import/XWiki/XWikiPreferences",
                     new String[] {"Admin", "admin"},
                     new HashMap<String, String>() {{
            put("basicauth", "1");
            put("action", "import");
            put("name", "Test.Attachment2.xar");
            put("historyStrategy", "add");
            put("pages", "Test.Attachment2");
        }});

        // Check for attachment content.
        ret = doPost(ADDRESS_PREFIX + "download/Test/Attachment2/" + FILENAME, null, null);
        Assert.assertEquals(ATTACHMENT_CONTENT, new String(ret.getResponseBody(), "UTF-8"));
    }

    /** Method to easily do a post request to the site. */
    private static HttpMethod doPost(final String address,
                                     final String[] userNameAndPassword,
                                     final Map<String, String> parameters)
        throws IOException
    {
        final HttpClient client = new HttpClient();
        final PostMethod method = new PostMethod(address);

        if (userNameAndPassword != null && userNameAndPassword.length == 2) {
            client.getState().setCredentials(null,
                                             null,
                                             new UsernamePasswordCredentials(userNameAndPassword[0],
                                                                             userNameAndPassword[1]));
        }

        if (parameters != null) {
            for (Map.Entry e : parameters.entrySet()) {
                method.addParameter((String) e.getKey(), (String) e.getValue());
            }
        }
        client.executeMethod(method);
        return method;
    }

    private static HttpMethod doUpload(final String address,
                                       final String[] userNameAndPassword,
                                       final Map<String, File> files)
        throws IOException
    {
        final HttpClient client = new HttpClient();
        final MultipartPostMethod method = new MultipartPostMethod(address);

        if (userNameAndPassword != null && userNameAndPassword.length == 2) {
            client.getState().setCredentials(null,
                                             null,
                                             new UsernamePasswordCredentials(userNameAndPassword[0],
                                                                             userNameAndPassword[1]));
        }

        if (files != null) {
            for (Map.Entry e : files.entrySet()) {
                method.addParameter((String) e.getKey(), (File) e.getValue());
            }
        }
        client.executeMethod(method);
        return method;
    }
}
