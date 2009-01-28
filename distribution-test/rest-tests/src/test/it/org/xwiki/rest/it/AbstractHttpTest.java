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

import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.restlet.data.MediaType;
import org.xwiki.rest.XWikiResource;
import org.xwiki.rest.model.Link;
import org.xwiki.rest.model.LinkCollection;
import org.xwiki.rest.model.Wikis;
import org.xwiki.rest.model.XStreamFactory;
import org.xwiki.rest.resources.wikis.WikisResource;

import com.thoughtworks.xstream.XStream;
import com.xpn.xwiki.test.AbstractXWikiComponentTestCase;

public abstract class AbstractHttpTest extends AbstractXWikiComponentTestCase
{
    protected HttpClient httpClient;

    protected XStream xstream;

    protected Random random;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        httpClient = new HttpClient();
        xstream = XStreamFactory.getXStream();
        random = new Random();
    }

    protected String getFullUri(String relativeUri)
    {
        return String.format("%s%s", TestConstants.REST_API_ENTRYPOINT, relativeUri);
    }

    public abstract void testRepresentation() throws Exception;

    protected GetMethod executeGet(String uri) throws Exception
    {
        GetMethod getMethod = new GetMethod(uri);
        httpClient.executeMethod(getMethod);

        return getMethod;
    }

    protected GetMethod executeGet(String uri, String userName, String password) throws Exception
    {
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        return executeGet(uri);
    }

    protected PostMethod executePost(String uri, Object object) throws Exception
    {
        PostMethod postMethod = new PostMethod(uri);
        RequestEntity entity =
            new StringRequestEntity(xstream.toXML(object), MediaType.APPLICATION_XML.toString(), "UTF-8");
        postMethod.setRequestEntity(entity);

        httpClient.executeMethod(postMethod);

        return postMethod;
    }

    protected PostMethod executePost(String uri, Object object, String userName, String password) throws Exception
    {
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        return executePost(uri, object);
    }

    protected PutMethod executePut(String uri, Object object) throws Exception
    {
        PutMethod putMethod = new PutMethod(uri);
        RequestEntity entity =
            new StringRequestEntity(xstream.toXML(object), MediaType.APPLICATION_XML.toString(), "UTF-8");
        putMethod.setRequestEntity(entity);

        httpClient.executeMethod(putMethod);

        return putMethod;
    }

    protected PutMethod executePut(String uri, Object object, String userName, String password) throws Exception
    {
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        return executePut(uri, object);
    }

    protected DeleteMethod executeDelete(String uri) throws Exception
    {
        DeleteMethod deleteMethod = new DeleteMethod(uri);
        httpClient.executeMethod(deleteMethod);

        return deleteMethod;
    }

    protected DeleteMethod executeDelete(String uri, String userName, String password) throws Exception
    {
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        return executeDelete(uri);
    }

    protected String getUriPatternForResource(Class< ? extends XWikiResource> resourceClass) throws Exception
    {
        XWikiResource resource =
            (XWikiResource) getComponentManager().lookup(XWikiResource.class.getName(), resourceClass.getName());

        String uriPattern = resource.getUriPattern();

        getComponentManager().release(resource);

        return uriPattern;
    }

    public String getWiki() throws Exception
    {
        GetMethod getMethod = executeGet(getFullUri(getUriPatternForResource(WikisResource.class)));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Wikis wikis = (Wikis) xstream.fromXML(getMethod.getResponseBodyAsString());
        assertTrue(wikis.getWikiList().size() > 0);

        return wikis.getWikiList().get(0).getName();
    }

    public void checkLinks(LinkCollection linkCollection) throws Exception
    {
        System.out.format("Checking links...\n");
        if (linkCollection.getLinks() != null) {
            for (Link link : linkCollection.getLinks()) {
                System.out.format("Relation '%s': ", link.getRel());
                GetMethod getMethod = executeGet(link.getHref());
                assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
                TestUtils.printHttpMethodInfo(getMethod);
            }
        }
    }
}
