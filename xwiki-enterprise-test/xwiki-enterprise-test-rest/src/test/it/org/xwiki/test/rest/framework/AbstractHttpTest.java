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
package org.xwiki.test.rest.framework;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;
import org.junit.Before;
import org.restlet.data.MediaType;
import org.xwiki.rest.internal.resources.pages.PageResource;
import org.xwiki.rest.internal.resources.wikis.WikisResource;
import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.Attachments;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.LinkCollection;
import org.xwiki.rest.model.jaxb.ObjectFactory;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Pages;
import org.xwiki.rest.model.jaxb.Wikis;
import org.xwiki.test.AbstractComponentTestCase;
import org.xwiki.test.integration.XWikiExecutor;

public abstract class AbstractHttpTest extends AbstractComponentTestCase
{
    protected Random random;

    protected Marshaller marshaller;

    protected Unmarshaller unmarshaller;

    protected ObjectFactory objectFactory;

    protected int port = Integer.valueOf(XWikiExecutor.DEFAULT_PORT);

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        random = new Random();

        JAXBContext context = JAXBContext.newInstance("org.xwiki.rest.model.jaxb");
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        objectFactory = new ObjectFactory();

    }

    public void setPort(int port)
    {
        this.port = port;
    }

    protected Link getFirstLinkByRelation(LinkCollection linkCollection, String relation)
    {
        if (linkCollection.getLinks() == null) {
            return null;
        }

        for (Link link : linkCollection.getLinks()) {
            if (link.getRel().equals(relation)) {
                return link;
            }
        }

        return null;
    }

    protected List<Link> getLinksByRelation(LinkCollection linkCollection, String relation)
    {
        List<Link> result = new ArrayList<Link>();

        if (linkCollection.getLinks() == null) {
            return result;
        }

        for (Link link : linkCollection.getLinks()) {
            if (link.getRel().equals(relation)) {
                result.add(link);
            }
        }

        return result;
    }

    protected String getBaseURL()
    {
        return "http://localhost:" + this.port + TestConstants.RELATIVE_REST_API_ENTRYPOINT;
    }

    protected String getFullUri(Class< ? > resourceClass)
    {
        return String.format("%s%s", getBaseURL(), UriBuilder.fromResource(resourceClass).build());
    }

    public abstract void testRepresentation() throws Exception;

    protected GetMethod executeGet(String uri) throws Exception
    {
        HttpClient httpClient = new HttpClient();

        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
        httpClient.executeMethod(getMethod);

        return getMethod;
    }

    protected GetMethod executeGet(String uri, String userName, String password) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
        httpClient.executeMethod(getMethod);

        return getMethod;
    }

    protected PostMethod executePostXml(String uri, Object object) throws Exception
    {
        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod(uri);
        postMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());

        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);

        RequestEntity entity =
            new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
        postMethod.setRequestEntity(entity);

        httpClient.executeMethod(postMethod);

        return postMethod;
    }

    protected PostMethod executePostXml(String uri, Object object, String userName, String password) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        PostMethod postMethod = new PostMethod(uri);
        postMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());

        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);

        RequestEntity entity =
            new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
        postMethod.setRequestEntity(entity);

        httpClient.executeMethod(postMethod);

        return postMethod;
    }

    protected PostMethod executePost(String uri, String string, String mediaType, String userName, String password)
        throws Exception
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        PostMethod postMethod = new PostMethod(uri);
        postMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());

        RequestEntity entity = new StringRequestEntity(string, mediaType, "UTF-8");
        postMethod.setRequestEntity(entity);

        httpClient.executeMethod(postMethod);

        return postMethod;
    }

    protected PostMethod executePostForm(String uri, NameValuePair[] nameValuePairs, String userName, String password)
        throws Exception
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        PostMethod postMethod = new PostMethod(uri);
        postMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
        postMethod.addRequestHeader("Content-type", MediaType.APPLICATION_WWW_FORM.toString());

        postMethod.setRequestBody(nameValuePairs);

        httpClient.executeMethod(postMethod);

        return postMethod;
    }

    protected PutMethod executePutXml(String uri, Object object) throws Exception
    {
        HttpClient httpClient = new HttpClient();

        PutMethod putMethod = new PutMethod(uri);
        putMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());

        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);

        RequestEntity entity =
            new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
        putMethod.setRequestEntity(entity);

        httpClient.executeMethod(putMethod);

        return putMethod;
    }

    protected PutMethod executePutXml(String uri, Object object, String userName, String password) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        PutMethod putMethod = new PutMethod(uri);
        putMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());

        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);

        RequestEntity entity =
            new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
        putMethod.setRequestEntity(entity);

        httpClient.executeMethod(putMethod);

        return putMethod;
    }

    protected PutMethod executePut(String uri, String string, String mediaType) throws Exception
    {
        HttpClient httpClient = new HttpClient();

        PutMethod putMethod = new PutMethod(uri);
        RequestEntity entity = new StringRequestEntity(string, mediaType, "UTF-8");
        putMethod.setRequestEntity(entity);

        httpClient.executeMethod(putMethod);

        return putMethod;
    }

    protected PutMethod executePut(String uri, String string, String mediaType, String userName, String password)
        throws Exception
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        PutMethod putMethod = new PutMethod(uri);
        RequestEntity entity = new StringRequestEntity(string, mediaType, "UTF-8");
        putMethod.setRequestEntity(entity);

        httpClient.executeMethod(putMethod);

        return putMethod;
    }

    protected DeleteMethod executeDelete(String uri) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        DeleteMethod deleteMethod = new DeleteMethod(uri);
        httpClient.executeMethod(deleteMethod);

        return deleteMethod;
    }

    protected DeleteMethod executeDelete(String uri, String userName, String password) throws Exception
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);

        DeleteMethod deleteMethod = new DeleteMethod(uri);
        httpClient.executeMethod(deleteMethod);

        return deleteMethod;
    }

    protected String getWiki() throws Exception
    {
        GetMethod getMethod = executeGet(getFullUri(WikisResource.class));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Wikis wikis = (Wikis) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Assert.assertTrue(wikis.getWikis().size() > 0);

        return wikis.getWikis().get(0).getName();
    }

    protected void checkLinks(LinkCollection linkCollection) throws Exception
    {
        if (linkCollection.getLinks() != null) {
            for (Link link : linkCollection.getLinks()) {
                GetMethod getMethod = executeGet(link.getHref());
                if (getMethod.getStatusCode() != HttpStatus.SC_UNAUTHORIZED) {
                    Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());
                }
            }
        }
    }

    protected UriBuilder getUriBuilder(Class< ? > resource)
    {
        return UriBuilder.fromUri(getBaseURL()).path(resource);
    }

    private Page getPage(String wikiName, String spaceName, String pageName) throws Exception
    {
        String uri = getUriBuilder(PageResource.class).build(wikiName, spaceName, pageName).toString();

        GetMethod getMethod = executeGet(uri);

        return (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
    }

    protected String getPageContent(String wikiName, String spaceName, String pageName) throws Exception
    {
        Page page = getPage(wikiName, spaceName, pageName);

        return page.getContent();
    }

    protected int setPageContent(String wikiName, String spaceName, String pageName, String content) throws Exception
    {
        String uri = getUriBuilder(PageResource.class).build(wikiName, spaceName, pageName).toString();

        PutMethod putMethod = executePut(uri, content, javax.ws.rs.core.MediaType.TEXT_PLAIN, "Admin", "admin");

        int code = putMethod.getStatusCode();
        Assert.assertTrue(String.format("Failed to set page content, %s", getHttpMethodInfo(putMethod)),
            code == HttpStatus.SC_ACCEPTED || code == HttpStatus.SC_CREATED);

        return code;
    }

    protected String getHttpMethodInfo(HttpMethod method) throws Exception
    {
        return String.format("\nName: %s\nURI: %s\nStatus code: %d\nStatus text: %s", method.getName(),
            method.getURI(), method.getStatusCode(), method.getStatusText());
    }

    protected String getAttachmentsInfo(Attachments attachments)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("Attachments: %d\n", attachments.getAttachments().size()));
        for (Attachment attachment : attachments.getAttachments()) {
            sb.append(String.format("* %s\n", attachment.getName()));
        }

        return sb.toString();
    }

    protected String getPagesInfo(Pages pages)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("Pages: %d\n", pages.getPageSummaries().size()));
        for (PageSummary pageSummary : pages.getPageSummaries()) {
            sb.append(String.format("* %s\n", pageSummary.getFullName()));
        }

        return sb.toString();
    }
}
