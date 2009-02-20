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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.LinkCollection;
import org.xwiki.rest.model.jaxb.ObjectFactory;
import org.xwiki.rest.model.jaxb.Wikis;
import org.xwiki.rest.resources.wikis.WikisResource;

import com.xpn.xwiki.test.AbstractXWikiComponentTestCase;

public abstract class AbstractHttpTest extends AbstractXWikiComponentTestCase
{   
    protected Random random;

    protected Marshaller marshaller;

    protected Unmarshaller unmarshaller;

    protected ObjectFactory objectFactory;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();        
        random = new Random();

        JAXBContext context = JAXBContext.newInstance("org.xwiki.rest.model.jaxb");
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        objectFactory = new ObjectFactory();

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

    protected String getFullUri(Class resourceClass)
    {
        return String.format("%s%s", TestConstants.REST_API_ENTRYPOINT, UriBuilder.fromResource(resourceClass).build());
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
        
        RequestEntity entity = new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
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
        
        RequestEntity entity = new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
        postMethod.setRequestEntity(entity);
        
        httpClient.executeMethod(postMethod);

        return postMethod;
    }

    protected PostMethod executePost(String uri, String string, String mediaType, String userName, String password) throws Exception
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
    
    protected PutMethod executePutXml(String uri, Object object) throws Exception
    {
        HttpClient httpClient = new HttpClient();

        PutMethod putMethod = new PutMethod(uri);
        putMethod.addRequestHeader("Accept", MediaType.APPLICATION_XML.toString());
        
        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        
        RequestEntity entity = new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
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
        
        RequestEntity entity = new StringRequestEntity(writer.toString(), MediaType.APPLICATION_XML.toString(), "UTF-8");
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

    protected PutMethod executePut(String uri, String string, String mediaType, String userName, String password) throws Exception
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
   
    public String getWiki() throws Exception
    {
        GetMethod getMethod = executeGet(getFullUri(WikisResource.class));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Wikis wikis = (Wikis) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        assertTrue(wikis.getWikis().size() > 0);

        return wikis.getWikis().get(0).getName();
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
