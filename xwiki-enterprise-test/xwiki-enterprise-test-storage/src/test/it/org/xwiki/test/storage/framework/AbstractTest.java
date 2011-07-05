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

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethod;
import org.xwiki.test.integration.XWikiExecutor;

/**
 * To be extended by all Test Classes. Provides access to information such as the port number.
 *
 * @version $Id$
 * @since 3.0RC1
 */
public class AbstractTest
{
    private static XWikiExecutor executor;

    /** Used so that AllTests can set the executor. */
    public static void setExecutor(final XWikiExecutor executor)
    {
        AbstractTest.executor = executor;
    }

    protected short getPort()
    {
        return (short) executor.getPort();
    }

    protected String getAddressPrefix()
    {
        return "http://127.0.0.1:" + this.getPort() + "/xwiki/bin/";
    }

    protected HttpMethod doPostAsAdmin(final String space,
                                       final String page,
                                       final String filename,
                                       final String action,
                                       final String query,
                                       final Map<String, String> postParameters) throws IOException
    {
        String url = getURL(space, page, filename, action, addBasicauth(query));
        return TestUtils.doPost(url, TestUtils.ADMIN_CREDENTIALS, postParameters);
    }

    public HttpMethod doUploadAsAdmin(final String space,
                                      final String page,
                                      final Map<String, byte[]> uploads) throws IOException
    {
        String url = getURL(space, page, null, "upload", addBasicauth(null));
        return TestUtils.doUpload(url, TestUtils.ADMIN_CREDENTIALS, uploads);
    }

    /**
     * Adds basicauth=1 to the query string.
     * 
     * @param query the query string to update
     * @return new query string, never null
     */
    private String addBasicauth(final String query)
    {
        String basicauth = "basicauth=1";
        if (query == null || query.isEmpty()) {
            return basicauth;
        }
        return query + "&" + basicauth;
    }

    /**
     * Get the URL of an action on a page with a specified query string.
     * 
     * @param space the space in which the page resides.
     * @param page the name of the page.
     * @param filename the filename of the attachment to use, may be null or empty if not needed
     * @param action the action to do on the page.
     * @param queryString the query string to pass in the URL
     * @return the corresponding URL
     * @since 3.2M1
     */
    protected String getURL(String space, String page, String filename, String action, String queryString)
    {
        StringBuilder builder = new StringBuilder(getAddressPrefix());

        builder.append(action);
        builder.append('/');
        builder.append(TestUtils.escapeURL(space));
        builder.append('/');
        builder.append(TestUtils.escapeURL(page));
        if (filename != null && !filename.isEmpty()) {
            builder.append('/');
            builder.append(TestUtils.escapeURL(filename));
        }

        if (queryString != null && !queryString.isEmpty()) {
            builder.append('?');
            builder.append(queryString);
        }

        return builder.toString();
    }
}
