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
package com.xpn.xwiki.it.webdav;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

/**
 * Abstract test class for all webdav tests.
 * 
 * @version $Id$
 * @since 1.8RC1
 */
public class AbstractWebDAVTest extends TestCase
{
    /**
     * Root webdav view.
     */
    public static final String ROOT = "http://localhost:8080/xwiki/webdav";
    
    /**
     * location of the home view.
     */
    public static final String HOME = ROOT + "/home";
    
    /**
     * location of the spaces view.
     */
    public static final String SPACES = ROOT + "/spaces";        
    
    /**
     * location of the attachments view.
     */
    public static final String ATTACHMENTS = ROOT + "/attachments";

    /**
     * location of the orphans view.
     */
    public static final String ORPHANS = ROOT + "/orphans";
    
    /**
     * location of the whatsnew view.
     */
    public static final String WHATSNEW = ROOT + "/whatsnew";
    
    /**
     * The {@link HttpClient} used to invoke various methods on the webdav server.
     */
    private HttpClient client;

    /**
     * Initializes the http client.
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        client = new HttpClient();
        client.getState().setCredentials(
            new AuthScope(AuthScope.ANY_HOST,
                AuthScope.ANY_PORT,
                AuthScope.ANY_REALM,
                AuthScope.ANY_SCHEME), new UsernamePasswordCredentials("Admin", "admin"));
        client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
    }
    
    /**
     * @return the {@link HttpClient}.
     */
    protected HttpClient getHttpClient()
    {
        return client;
    }
}
