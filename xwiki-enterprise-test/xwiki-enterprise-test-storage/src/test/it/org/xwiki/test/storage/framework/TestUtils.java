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
package org.xwiki.test.storage.framework;

import java.util.Map;
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

/**
 * Test saving and downloading of attachments.
 *
 * @version $Id$
 * @since 3.2M1
 */
public final class TestUtils
{
    public static final String[] ADMIN_CREDENTIALS = new String[] {"Admin", "admin"};

    public static String getPageAsString(final String address) throws IOException
    {
        final HttpMethod ret = doPost(address, null, null);
        return new String(ret.getResponseBody(), "UTF-8");
    }

    /** Method to easily do a post request to the site. */
    public static HttpMethod doPost(final String address,
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

    public static HttpMethod doUpload(final String address,
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
