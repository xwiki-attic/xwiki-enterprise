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
import java.io.ByteArrayOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.test.storage.framework.AbstractTest;
import org.apache.commons.io.IOUtils;

/**
 * Test saving and downloading of attachments.
 *
 * @version $Id$
 * @since 3.0RC1
 */
public class AttachmentTest extends AbstractTest
{
    private static final String ATTACHMENT_CONTENT = "This is content for a very small attachment.";

    private static final String FILENAME = "littleAttachment.txt";

    private static final String[] ADMIN_CREDENTIALS = new String[] {"Admin", "admin"};

    private final String addressPrefix = "http://127.0.0.1:" + this.getPort() + "/xwiki/bin/";

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

        // Delete the document if it exists.
        doPost(this.addressPrefix + "delete/Test/Attachment?confirm=1&basicauth=1",
               ADMIN_CREDENTIALS,
               null);

        // Create a document.
        doPost(this.addressPrefix + "save/Test/Attachment",
               ADMIN_CREDENTIALS,
               new HashMap<String, String>() {{
            put("basicauth", "1");
            put("content", test);
        }});

        HttpMethod ret = null;

        // Test getAttachment()
        ret = doPost(this.addressPrefix + "view/Test/Attachment?xpage=plain", null, null);
        Assert.assertEquals("<p>" + ATTACHMENT_CONTENT + "</p>", ret.getResponseBodyAsString());

        // Test downloadAction.
        ret = doPost(this.addressPrefix + "download/Test/Attachment/" + FILENAME, null, null);
        Assert.assertEquals(ATTACHMENT_CONTENT, new String(ret.getResponseBody(), "UTF-8"));
        Assert.assertEquals(200, ret.getStatusCode());

        // Make sure there is exactly 1 version of this attachment.
        ret = doPost(this.addressPrefix + "preview/Test/Attachment?xpage=plain",
                     ADMIN_CREDENTIALS,
                     new HashMap<String, String>() {{
            put("basicauth", "1");
            put("content", "{{velocity}}$doc.getAttachment('"
                           + FILENAME + "').getVersions().size(){{/velocity}}");
        }});
        Assert.assertEquals("<p>1</p>", ret.getResponseBodyAsString());

        // Make sure that version contains the correct content.
        ret = doPost(this.addressPrefix + "preview/Test/Attachment?xpage=plain",
                     ADMIN_CREDENTIALS,
                     new HashMap<String, String>() {{
            put("basicauth", "1");
            put("content", "{{velocity}}$doc.getAttachment('" + FILENAME
                           + "').getAttachmentRevision('1.1').getContentAsString(){{/velocity}}");
        }});
        Assert.assertEquals("<p>" + ATTACHMENT_CONTENT + "</p>", ret.getResponseBodyAsString());
    }

    /**
     * XWIKI-6126
     */
    @Test
    public void testImportDocumentWithAttachment() throws Exception
    {
        final String docName = "/Test/Attachment2";

        final String attachURL = this.addressPrefix + "download" + docName + "/" + FILENAME;

        // Delete the document if it exists.
        doPost(this.addressPrefix + "delete" + docName + "?confirm=1&basicauth=1",
               ADMIN_CREDENTIALS, null);

        HttpMethod ret = null;
        // Upload the XAR to import.
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(this.getClass().getResourceAsStream("/Test.Attachment2.xar"), baos);
        ret = doUpload(this.addressPrefix + "upload/XWiki/XWikiPreferences?basicauth=1",
                       ADMIN_CREDENTIALS,
                       new HashMap<String, byte[]>() {{
            put("Test.Attachment2.xar", baos.toByteArray());
        }});

        // Do the import.
        ret = doPost(this.addressPrefix + "import/XWiki/XWikiPreferences",
                     ADMIN_CREDENTIALS,
                     new HashMap<String, String>() {{
            put("basicauth", "1");
            put("action", "import");
            put("name", "Test.Attachment2.xar");
            put("historyStrategy", "add");
            put("pages", "Test.Attachment2");
        }});

        // Check for attachment content.
        Assert.assertEquals(ATTACHMENT_CONTENT, getPageAsString(attachURL));
    }

    @Test
    public void testRollbackAfterDeleteAttachment() throws Exception
    {
        final String docName = "/Test/testRollbackAfterDeleteAttachment";

        final String attachURL = this.addressPrefix + "download" + docName + "/" + FILENAME;

        // Delete the document if it exists.
        doPost(this.addressPrefix + "delete" + docName + "?confirm=1&basicauth=1", ADMIN_CREDENTIALS, null);

        // Create a document.
        doPost(this.addressPrefix + "save" + docName + "?basicauth=1", ADMIN_CREDENTIALS, null);

        HttpMethod ret = null;
        // Upload the attachment
        ret = doUpload(this.addressPrefix + "upload" + docName + "?basicauth=1",
                       ADMIN_CREDENTIALS,
                       new HashMap<String, byte[]>() {{
            put(FILENAME, ATTACHMENT_CONTENT.getBytes());
        }});

        // Make sure it's there.
        Assert.assertEquals(ATTACHMENT_CONTENT, getPageAsString(attachURL));

        // Delete it
        doPost(this.addressPrefix + "delattachment" + docName + "/" + FILENAME + "?basicauth=1",
               ADMIN_CREDENTIALS, null);

        // Make sure it's nolonger there.
        Assert.assertFalse(ATTACHMENT_CONTENT.equals(getPageAsString(attachURL)));

        // Do a rollback.
        doPost(this.addressPrefix + "rollback" + docName + "?rev=2.1&basicauth=1&confirm=1",
               ADMIN_CREDENTIALS,
               null);

        // Make sure the content is back again.
        Assert.assertEquals(ATTACHMENT_CONTENT, getPageAsString(attachURL));
    }

    @Test
    public void testRollbackAfterNewAttachment() throws Exception
    {
        final String docName = "/Test/testRollbackAfterNewAttachment";

        final String attachURL = this.addressPrefix + "download" + docName + "/" + FILENAME;

        // Delete the document if it exists.
        doPost(this.addressPrefix + "delete" + docName + "?confirm=1&basicauth=1", ADMIN_CREDENTIALS, null);

        // Create a document.
        doPost(this.addressPrefix + "save" + docName + "?basicauth=1", ADMIN_CREDENTIALS, null);

        HttpMethod ret = null;
        // Upload the attachment
        ret = doUpload(this.addressPrefix + "upload" + docName + "?basicauth=1",
                       ADMIN_CREDENTIALS,
                       new HashMap<String, byte[]>() {{
            put(FILENAME, ATTACHMENT_CONTENT.getBytes());
        }});

        // Make sure it's there.
        Assert.assertEquals(ATTACHMENT_CONTENT, getPageAsString(attachURL));

        // Do a rollback.
        doPost(this.addressPrefix + "rollback" + docName + "?rev=1.1&basicauth=1&confirm=1",
               ADMIN_CREDENTIALS,
               null);

        // Make sure it's nolonger there.
        ret = doPost(this.addressPrefix + "download" + docName + "/" + FILENAME, null, null);
        Assert.assertFalse(ATTACHMENT_CONTENT.equals(new String(ret.getResponseBody(), "UTF-8")));
        Assert.assertEquals(404, ret.getStatusCode());
    }

    @Test
    public void testRollbackAfterUpdateOfAttachment() throws Exception
    {
        final String docName = "/Test/testRollbackAfterUpdateOfAttachment";

        final String versionTwo = "This is some different content";

        final String attachURL = this.addressPrefix + "download" + docName + "/" + FILENAME;

        // Delete the document if it exists.
        doPost(this.addressPrefix + "delete" + docName + "?confirm=1&basicauth=1", ADMIN_CREDENTIALS, null);

        // Create a document.
        doPost(this.addressPrefix + "save" + docName + "?basicauth=1", ADMIN_CREDENTIALS, null);

        HttpMethod ret = null;
        // Upload the attachment
        ret = doUpload(this.addressPrefix + "upload" + docName + "?basicauth=1",
                       ADMIN_CREDENTIALS,
                       new HashMap<String, byte[]>() {{
            put(FILENAME, ATTACHMENT_CONTENT.getBytes());
        }});

        // Make sure it's there.
        Assert.assertEquals(ATTACHMENT_CONTENT, getPageAsString(attachURL));

        // Overwrite
        ret = doUpload(this.addressPrefix + "upload" + docName + "?basicauth=1",
                       ADMIN_CREDENTIALS,
                       new HashMap<String, byte[]>() {{
            put(FILENAME, versionTwo.getBytes());
        }});

        // Make sure it is now version2
        Assert.assertEquals(versionTwo, getPageAsString(attachURL));

        // Do a rollback.
        doPost(this.addressPrefix + "rollback" + docName + "?rev=2.1&basicauth=1&confirm=1",
               ADMIN_CREDENTIALS,
               null);

        // Make sure it is version1
        Assert.assertEquals(ATTACHMENT_CONTENT, getPageAsString(attachURL));
    }

    /**
     * If the user saves an attachment, then deletes it, then saves another with the same name,
     * then rolls the version back, the new attachment should be trashed and the old attachment should be
     * restored.
     */
    @Test
    public void testRollbackAfterSaveDeleteSaveAttachment() throws Exception
    {
        final String docName = "/Test/testRollbackAfterSaveDeleteSaveAttachment";

        final String versionTwo = "This is some different content";

        final String attachURL = this.addressPrefix + "download" + docName + "/" + FILENAME;

        // Delete the document if it exists.
        doPost(this.addressPrefix + "delete" + docName + "?confirm=1&basicauth=1", ADMIN_CREDENTIALS, null);

        // Create a document. v1.1
        doPost(this.addressPrefix + "save" + docName + "?basicauth=1", ADMIN_CREDENTIALS, null);

        HttpMethod ret = null;
        // Upload the attachment v2.1
        ret = doUpload(this.addressPrefix + "upload" + docName + "?basicauth=1",
                       ADMIN_CREDENTIALS,
                       new HashMap<String, byte[]>() {{
            put(FILENAME, ATTACHMENT_CONTENT.getBytes());
        }});

        // Make sure it's there.
        Assert.assertEquals(ATTACHMENT_CONTENT, getPageAsString(attachURL));

        // Delete it v3.1
        doPost(this.addressPrefix + "delattachment" + docName + "/" + FILENAME + "?basicauth=1",
               ADMIN_CREDENTIALS, null);

        // Upload again v4.1
        ret = doUpload(this.addressPrefix + "upload" + docName + "?basicauth=1",
                       ADMIN_CREDENTIALS,
                       new HashMap<String, byte[]>() {{
            put(FILENAME, versionTwo.getBytes());
        }});

        // Make sure it's there.
        Assert.assertEquals(versionTwo, getPageAsString(attachURL));

        // Do a rollback. v5.1
        doPost(this.addressPrefix + "rollback" + docName + "?rev=2.1&basicauth=1&confirm=1",
               ADMIN_CREDENTIALS,
               null);

        // Make sure it is version1
        Assert.assertEquals(ATTACHMENT_CONTENT, getPageAsString(attachURL));

        // Do rollback to version2. v6.1
        doPost(this.addressPrefix + "rollback" + docName + "?rev=4.1&basicauth=1&confirm=1",
               ADMIN_CREDENTIALS,
               null);

        // Make sure it is version2
        Assert.assertEquals(versionTwo, getPageAsString(attachURL));

        // Make sure the latest current version is actually v6.1
        ret = doPost(this.addressPrefix + "preview" + docName + "?basicauth=1&xpage=plain",
                     ADMIN_CREDENTIALS,
                     new HashMap<String, String>() {{
            put("content", "{{velocity}}$doc.getVersion(){{/velocity}}");
        }});
        Assert.assertEquals("<p>6.1</p>", new String(ret.getResponseBody(), "UTF-8"));
    }


    private static String getPageAsString(final String address) throws IOException
    {
        final HttpMethod ret = doPost(address, null, null);
        return new String(ret.getResponseBody(), "UTF-8");
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
                                       final Map<String, byte[]> uploads)
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

        for (Map.Entry e : uploads.entrySet()) {
            method.addPart(new FilePart("filepath",
                           new ByteArrayPartSource((String) e.getKey(), (byte[]) e.getValue())));
        }
        client.executeMethod(method);
        return method;
    }
}
