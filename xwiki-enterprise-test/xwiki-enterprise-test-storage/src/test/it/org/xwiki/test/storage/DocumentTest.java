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

import org.apache.commons.httpclient.HttpMethod;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xwiki.test.storage.framework.AbstractTest;
import org.xwiki.test.storage.framework.TestUtils;
import org.apache.commons.io.IOUtils;

/**
 * Test saving and downloading of attachments.
 *
 * @version $Id$
 * @since 3.2M1
 */
public class DocumentTest extends AbstractTest
{
    @Test
    public void testRollback() throws Exception
    {
        final String docName = "/DocumentTest/testRollback";

        final String versionOne = "This is version one";
        final String versionTwo = "This is version two";

        final String pageURL = this.getAddressPrefix() + "get" + docName + "?xpage=plain";

        // Delete the document if it exists.
        TestUtils.doPost(this.getAddressPrefix() + "delete" + docName + "?confirm=1&basicauth=1",
                         TestUtils.ADMIN_CREDENTIALS,
                         null);

        // Create a document. v1.1
        TestUtils.doPost(this.getAddressPrefix() + "save" + docName + "?basicauth=1",
                         TestUtils.ADMIN_CREDENTIALS,
                         new HashMap<String, String>() {{
            put("content", versionOne);
        }});

        // Change the document v2.1
        TestUtils.doPost(this.getAddressPrefix() + "save" + docName + "?basicauth=1",
                         TestUtils.ADMIN_CREDENTIALS,
                         new HashMap<String, String>() {{
            put("content", versionTwo);
        }});

        // Make sure it's version 2.
        Assert.assertEquals("<p>" + versionTwo + "</p>", TestUtils.getPageAsString(pageURL));

        // Do a rollback. v3.1
        TestUtils.doPost(this.getAddressPrefix() + "rollback" + docName + "?rev=1.1&basicauth=1&confirm=1",
                         TestUtils.ADMIN_CREDENTIALS,
                         null);

        // Make sure it's the same as version 1.
        Assert.assertEquals("<p>" + versionOne + "</p>", TestUtils.getPageAsString(pageURL));

        // Make sure the latest current version is actually v3.1
        HttpMethod ret =
             TestUtils.doPost(this.getAddressPrefix() + "preview" + docName + "?basicauth=1&xpage=plain",
                              TestUtils.ADMIN_CREDENTIALS,
                              new HashMap<String, String>() {{
                 put("content", "{{velocity}}$doc.getVersion(){{/velocity}}");
             }});
        Assert.assertEquals("<p>3.1</p>", new String(ret.getResponseBody(), "UTF-8"));
    }
}
