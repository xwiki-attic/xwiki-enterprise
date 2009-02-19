package org.xwiki.rest.it;

import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.xwiki.rest.Relations;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.Object;
import org.xwiki.rest.model.jaxb.Objects;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.Property;
import org.xwiki.rest.resources.objects.ObjectResource;
import org.xwiki.rest.resources.objects.ObjectsResource;
import org.xwiki.rest.resources.pages.PageResource;

public class ObjectsResourceTest extends AbstractHttpTest
{
    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(PageResource.class).build(getWiki(),
                "Main", "WebHome").toString());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Link link = getFirstLinkByRelation(page, Relations.OBJECTS);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Objects objects = (Objects) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertFalse(objects.getObjects().isEmpty());

        for (Object object : objects.getObjects()) {
            link = getFirstLinkByRelation(object, Relations.SELF);
            getMethod = executeGet(link.getHref());
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
            TestUtils.printHttpMethodInfo(getMethod);

            object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
            
            checkLinks(object);
            
            for(Property property : object.getProperties()) {
                checkLinks(property);
            }
        }
    }

    public void testGETNotExistingObject() throws Exception
    {
        TestUtils.banner("testGETNotExistingObject()");

        GetMethod getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(
                getWiki(), "Main", "WebHome", "NOTEXISTING", 0).toString());
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
    }

    public Property getProperty(Object object, String propertyName)
    {
        for (Property property : object.getProperties()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }

        return null;
    }

    public void testPOSTObject() throws Exception
    {
        TestUtils.banner("testPOSTObject()");

        final String TAG_VALUE = "TAG";

        Property property = new Property();
        property.setName("tags");
        property.setValue(TAG_VALUE);
        Object object = objectFactory.createObject();
        object.setClassName("XWiki.TagClass");
        object.getProperties().add(property);

        PostMethod postMethod =
            executePostXml(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectsResource.class).build(
                getWiki(), "Main", "WebHome").toString(), object, "Admin", "admin");
        assertEquals(HttpStatus.SC_CREATED, postMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(postMethod);

        object = (Object) unmarshaller.unmarshal(postMethod.getResponseBodyAsStream());

        assertEquals(TAG_VALUE, getProperty(object, "tags").getValue());

        GetMethod getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(
                getWiki(), "Main", "WebHome", object.getClassName(), object.getNumber()).toString());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertEquals(TAG_VALUE, getProperty(object, "tags").getValue());
    }

    public void testPOSTInvalidObject() throws Exception
    {
        TestUtils.banner("testPOSTInvalidObject()");

        final String TAG_VALUE = "TAG";

        Property property = new Property();
        property.setName("tags");
        property.setValue(TAG_VALUE);
        Object object = objectFactory.createObject();
        object.getProperties().add(property);

        PostMethod postMethod =
            executePostXml(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectsResource.class).build(
                getWiki(), "Main", "WebHome").toString(), object, "Admin", "admin");
        assertEquals(HttpStatus.SC_BAD_REQUEST, postMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(postMethod);
    }

    public void testPOSTObjectNotAuthorized() throws Exception
    {
        final String TAG_VALUE = "TAG";

        TestUtils.banner("testPOSTObject()");
        
        Property property = new Property();
        property.setName("tags");
        property.setValue(TAG_VALUE);
        Object object = objectFactory.createObject();
        object.setClassName("XWiki.TagClass");
        object.getProperties().add(property);

        PostMethod postMethod =
            executePostXml(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectsResource.class).build(
                getWiki(), "Main", "WebHome").toString(), object);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, postMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(postMethod);
    }

    public void testPUTObject() throws Exception
    {
        final String TAG_VALUE = UUID.randomUUID().toString();

        TestUtils.banner("testPUTObject()");
        
        Object objectToBePut = getObject("XWiki.TagClass");
        
        GetMethod getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(
                getWiki(), "Main", "WebHome", objectToBePut.getClassName(), objectToBePut.getNumber()).toString());        
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
        
        Object objectSummary = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        getProperty(objectSummary, "tags").setValue(TAG_VALUE);
        
        PutMethod putMethod =
            executePutXml(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(
                getWiki(), "Main", "WebHome", objectToBePut.getClassName(), objectToBePut.getNumber()).toString(), objectSummary,
                "Admin", "admin");
        assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(putMethod);
        
        Object updatedObjectSummary = (Object) unmarshaller.unmarshal(putMethod.getResponseBodyAsStream());

        assertEquals(TAG_VALUE, getProperty(updatedObjectSummary, "tags").getValue());
        assertEquals(objectSummary.getClassName(), updatedObjectSummary.getClassName());
        assertEquals(objectSummary.getNumber(), updatedObjectSummary.getNumber());
    }
    
    public void testPUTObjectUnauthorized() throws Exception
    {
        final String TAG_VALUE = UUID.randomUUID().toString();

        TestUtils.banner("testPUTObject()");
        
        Object objectToBePut = getObject("XWiki.TagClass");
        
        GetMethod getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(
                getWiki(), "Main", "WebHome", objectToBePut.getClassName(), objectToBePut.getNumber()).toString());        
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
        
        Object object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        String originalTagValue = getProperty(object, "tags").getValue();        
        getProperty(object, "tags").setValue(TAG_VALUE);
        
        PutMethod putMethod =
            executePutXml(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(
                getWiki(), "Main", "WebHome", objectToBePut.getClassName(), objectToBePut.getNumber()).toString(), object);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, putMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(putMethod);        
        
        getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(
                getWiki(), "Main", "WebHome", objectToBePut.getClassName(), objectToBePut.getNumber()).toString());        
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
        
        object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        
        assertEquals(originalTagValue, getProperty(object, "tags").getValue());
    }

    public void testDELETEObject() throws Exception {
        TestUtils.banner("testDELETEObject()");

        Object objectToBeDeleted = getObject("XWiki.TagClass");
        
        DeleteMethod deleteMethod = executeDelete(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(getWiki(),
            "Main", "WebHome", objectToBeDeleted.getClassName(), objectToBeDeleted.getNumber()).toString(), "Admin", "admin");
        assertEquals(HttpStatus.SC_NO_CONTENT, deleteMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(deleteMethod);
        
        GetMethod getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(getWiki(),
                "Main", "WebHome", objectToBeDeleted.getClassName(), objectToBeDeleted.getNumber()).toString());
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
    }
    
    public void testDELETEObjectUnAuthorized() throws Exception {
        TestUtils.banner("testDELETEObjectUnAuthorized()");

        Object objectToBeDeleted = getObject("XWiki.TagClass");
        
        DeleteMethod deleteMethod = executeDelete(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(getWiki(),
            "Main", "WebHome", objectToBeDeleted.getClassName(), objectToBeDeleted.getNumber()).toString());
        assertEquals(HttpStatus.SC_UNAUTHORIZED, deleteMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(deleteMethod);
        
        GetMethod getMethod = executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(getWiki(),
            "Main", "WebHome", objectToBeDeleted.getClassName(), objectToBeDeleted.getNumber()).toString());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
    }
    
    public void testPUTProperty() throws Exception {
        TestUtils.banner("testPUTProperty()");
        
        final String TAG_VALUE = UUID.randomUUID().toString();
        
        GetMethod getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(PageResource.class).build(getWiki(),
                "Main", "WebHome").toString());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Link link = getFirstLinkByRelation(page, Relations.OBJECTS);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Objects objects = (Objects) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertFalse(objects.getObjects().isEmpty());

        Object currentObject = null;
        
        for (Object object : objects.getObjects()) {
            if(object.getClassName().equals("XWiki.TagClass")) {
                currentObject = object;
                break;
            }
        }
        
        assertNotNull(currentObject);
        
        Property tagsProperty = getProperty(currentObject, "tags");
        
        assertNotNull(tagsProperty);
        
        Link tagsPropertyLink = getFirstLinkByRelation(tagsProperty, Relations.PROPERTY);
        
        assertNotNull(tagsPropertyLink);
        
        Property newTags = objectFactory.createProperty();
        newTags.setValue(TAG_VALUE);
        
        PutMethod putMethod = executePutXml(tagsPropertyLink.getHref(), newTags, "Admin", "admin");
        assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(putMethod);
        
        getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectResource.class).build(getWiki(),
                "Main", "WebHome", currentObject.getClassName(), currentObject.getNumber()).toString());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        
        currentObject = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        
        tagsProperty = getProperty(currentObject, "tags");
        
        assertEquals(TAG_VALUE, tagsProperty.getValue());
        
    }
    
    private Object getObject(String className) throws Exception {
        GetMethod getMethod =
            executeGet(UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(ObjectsResource.class).build(getWiki(),
                "Main", "WebHome").toString());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
        
        Objects objects = (Objects) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertFalse(objects.getObjects().isEmpty());
        
        for(Object object : objects.getObjects()) {
            if(object.getClassName().equals(className)) {
                return object;
            }
            
        }
        
        return null;
    }
}
