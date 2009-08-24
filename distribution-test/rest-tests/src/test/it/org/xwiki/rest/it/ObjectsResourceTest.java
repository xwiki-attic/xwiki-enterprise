package org.xwiki.rest.it;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.xwiki.rest.Relations;
import org.xwiki.rest.it.framework.AbstractHttpTest;
import org.xwiki.rest.it.framework.TestUtils;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.Object;
import org.xwiki.rest.model.jaxb.ObjectSummary;
import org.xwiki.rest.model.jaxb.Objects;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.Property;
import org.xwiki.rest.resources.objects.ObjectAtPageVersionResource;
import org.xwiki.rest.resources.objects.ObjectResource;
import org.xwiki.rest.resources.objects.ObjectsResource;
import org.xwiki.rest.resources.pages.PageResource;

public class ObjectsResourceTest extends AbstractHttpTest
{
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), "Main", "WebHome").toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Link link = getFirstLinkByRelation(page, Relations.OBJECTS);

        /* Create a tag object if it doesn't exist yet */
        if (link == null) {
            Object object = objectFactory.createObject();
            object.setClassName("XWiki.TagClass");

            PostMethod postMethod =
                executePostXml(getUriBuilder(ObjectsResource.class).build(getWiki(), "Main", "WebHome").toString(),
                    object, "Admin", "admin");
            TestUtils.printHttpMethodInfo(postMethod);
            assertEquals(HttpStatus.SC_CREATED, postMethod.getStatusCode());
        }
    }

    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), "Main", "WebHome").toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Link link = getFirstLinkByRelation(page, Relations.OBJECTS);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Objects objects = (Objects) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertFalse(objects.getObjectSummaries().isEmpty());

        for (ObjectSummary objectSummary : objects.getObjectSummaries()) {
            link = getFirstLinkByRelation(objectSummary, Relations.OBJECT);
            getMethod = executeGet(link.getHref());
            TestUtils.printHttpMethodInfo(getMethod);
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

            Object object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

            checkLinks(objectSummary);

            for (Property property : object.getProperties()) {
                checkLinks(property);
            }
        }
    }

    public void testGETNotExistingObject() throws Exception
    {
        TestUtils.banner("testGETNotExistingObject()");

        GetMethod getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome", "NOTEXISTING", 0).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());

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
            executePostXml(getUriBuilder(ObjectsResource.class).build(getWiki(), "Main", "WebHome").toString(), object,
                "Admin", "admin");
        TestUtils.printHttpMethodInfo(postMethod);
        assertEquals(HttpStatus.SC_CREATED, postMethod.getStatusCode());

        object = (Object) unmarshaller.unmarshal(postMethod.getResponseBodyAsStream());

        assertEquals(TAG_VALUE, getProperty(object, "tags").getValue());

        GetMethod getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome", object.getClassName(),
                object.getNumber()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

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
            executePostXml(getUriBuilder(ObjectsResource.class).build(getWiki(), "Main", "WebHome").toString(), object,
                "Admin", "admin");
        TestUtils.printHttpMethodInfo(postMethod);
        assertEquals(HttpStatus.SC_BAD_REQUEST, postMethod.getStatusCode());

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
            executePostXml(getUriBuilder(ObjectsResource.class).build(getWiki(), "Main", "WebHome").toString(), object);
        TestUtils.printHttpMethodInfo(postMethod);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, postMethod.getStatusCode());

    }

    public void testPUTObject() throws Exception
    {
        final String TAG_VALUE = UUID.randomUUID().toString();

        TestUtils.banner("testPUTObject()");

        Object objectToBePut = getObject("XWiki.TagClass");

        GetMethod getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBePut.getClassName(), objectToBePut.getNumber()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Object objectSummary = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        getProperty(objectSummary, "tags").setValue(TAG_VALUE);

        PutMethod putMethod =
            executePutXml(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBePut.getClassName(), objectToBePut.getNumber()).toString(), objectSummary, "Admin", "admin");
        TestUtils.printHttpMethodInfo(putMethod);
        assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());

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
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBePut.getClassName(), objectToBePut.getNumber()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Object object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        String originalTagValue = getProperty(object, "tags").getValue();
        getProperty(object, "tags").setValue(TAG_VALUE);

        PutMethod putMethod =
            executePutXml(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBePut.getClassName(), objectToBePut.getNumber()).toString(), object);

        TestUtils.printHttpMethodInfo(putMethod);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, putMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBePut.getClassName(), objectToBePut.getNumber()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertEquals(originalTagValue, getProperty(object, "tags").getValue());
    }

    public void testDELETEObject() throws Exception
    {
        TestUtils.banner("testDELETEObject()");

        Object objectToBeDeleted = getObject("XWiki.TagClass");

        DeleteMethod deleteMethod =
            executeDelete(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBeDeleted.getClassName(), objectToBeDeleted.getNumber()).toString(), "Admin", "admin");
        TestUtils.printHttpMethodInfo(deleteMethod);
        assertEquals(HttpStatus.SC_NO_CONTENT, deleteMethod.getStatusCode());

        GetMethod getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBeDeleted.getClassName(), objectToBeDeleted.getNumber()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());

    }

    public void testDELETEObjectUnAuthorized() throws Exception
    {
        TestUtils.banner("testDELETEObjectUnAuthorized()");

        Object objectToBeDeleted = getObject("XWiki.TagClass");

        DeleteMethod deleteMethod =
            executeDelete(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBeDeleted.getClassName(), objectToBeDeleted.getNumber()).toString());
        TestUtils.printHttpMethodInfo(deleteMethod);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, deleteMethod.getStatusCode());

        GetMethod getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBeDeleted.getClassName(), objectToBeDeleted.getNumber()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

    }

    public void testPUTProperty() throws Exception
    {
        TestUtils.banner("testPUTProperty()");

        final String TAG_VALUE = UUID.randomUUID().toString();

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), "Main", "WebHome").toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Link link = getFirstLinkByRelation(page, Relations.OBJECTS);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Objects objects = (Objects) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertFalse(objects.getObjectSummaries().isEmpty());

        Object currentObject = null;

        for (ObjectSummary objectSummary : objects.getObjectSummaries()) {
            if (objectSummary.getClassName().equals("XWiki.TagClass")) {
                link = getFirstLinkByRelation(objectSummary, Relations.OBJECT);
                assertNotNull(link);
                getMethod = executeGet(link.getHref());
                TestUtils.printHttpMethodInfo(getMethod);
                assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

                currentObject = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
                break;
            }
        }

        assertNotNull(currentObject);

        Property tagsProperty = getProperty(currentObject, "tags");

        assertNotNull(tagsProperty);

        Link tagsPropertyLink = getFirstLinkByRelation(tagsProperty, Relations.SELF);

        assertNotNull(tagsPropertyLink);

        Property newTags = objectFactory.createProperty();
        newTags.setValue(TAG_VALUE);

        PutMethod putMethod = executePutXml(tagsPropertyLink.getHref(), newTags, "Admin", "admin");
        TestUtils.printHttpMethodInfo(putMethod);
        assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                currentObject.getClassName(), currentObject.getNumber()).toString());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        currentObject = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        tagsProperty = getProperty(currentObject, "tags");

        assertEquals(TAG_VALUE, tagsProperty.getValue());

    }

    public void testPUTPropertyWithTextPlain() throws Exception
    {
        TestUtils.banner("testPUTProperty()");

        final String TAG_VALUE = UUID.randomUUID().toString();

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), "Main", "WebHome").toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Link link = getFirstLinkByRelation(page, Relations.OBJECTS);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Objects objects = (Objects) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertFalse(objects.getObjectSummaries().isEmpty());

        Object currentObject = null;

        for (ObjectSummary objectSummary : objects.getObjectSummaries()) {
            if (objectSummary.getClassName().equals("XWiki.TagClass")) {
                link = getFirstLinkByRelation(objectSummary, Relations.OBJECT);
                assertNotNull(link);
                getMethod = executeGet(link.getHref());
                TestUtils.printHttpMethodInfo(getMethod);
                assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

                currentObject = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
                break;
            }
        }

        assertNotNull(currentObject);

        Property tagsProperty = getProperty(currentObject, "tags");

        assertNotNull(tagsProperty);

        Link tagsPropertyLink = getFirstLinkByRelation(tagsProperty, Relations.SELF);

        assertNotNull(tagsPropertyLink);

        PutMethod putMethod = executePut(tagsPropertyLink.getHref(), TAG_VALUE, MediaType.TEXT_PLAIN, "Admin", "admin");
        TestUtils.printHttpMethodInfo(putMethod);
        assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                currentObject.getClassName(), currentObject.getNumber()).toString());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        currentObject = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        tagsProperty = getProperty(currentObject, "tags");

        assertEquals(TAG_VALUE, tagsProperty.getValue());

    }

    private Object getObject(String className) throws Exception
    {
        GetMethod getMethod =
            executeGet(getUriBuilder(ObjectsResource.class).build(getWiki(), "Main", "WebHome").toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Objects objects = (Objects) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertFalse(objects.getObjectSummaries().isEmpty());

        for (ObjectSummary objectSummary : objects.getObjectSummaries()) {
            if (objectSummary.getClassName().equals(className)) {
                Link link = getFirstLinkByRelation(objectSummary, Relations.OBJECT);
                assertNotNull(link);
                getMethod = executeGet(link.getHref());
                TestUtils.printHttpMethodInfo(getMethod);
                assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

                Object object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

                return object;
            }

        }

        /* If no object of that class is found, then create a new one */
        Object object = objectFactory.createObject();
        object.setClassName(className);

        PostMethod postMethod =
            executePostXml(getUriBuilder(ObjectsResource.class).build(getWiki(), "Main", "WebHome").toString(), object,
                "Admin", "admin");
        TestUtils.printHttpMethodInfo(postMethod);
        assertEquals(HttpStatus.SC_CREATED, postMethod.getStatusCode());

        object = (Object) unmarshaller.unmarshal(postMethod.getResponseBodyAsStream());

        return object;
    }

    public void testPUTObjectFormUrlEncoded() throws Exception
    {
        final String TAG_VALUE = UUID.randomUUID().toString();

        TestUtils.banner("testPUTObjectFormUrlEncoded()");

        Object objectToBePut = getObject("XWiki.TagClass");

        GetMethod getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                objectToBePut.getClassName(), objectToBePut.getNumber()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Object object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        NameValuePair[] nameValuePairs = new NameValuePair[1];
        nameValuePairs[0] = new NameValuePair("property#tags", TAG_VALUE);

        PostMethod postMethod =
            executePostForm(String.format("%s?method=PUT", getUriBuilder(ObjectResource.class).build(getWiki(), "Main",
                "WebHome", objectToBePut.getClassName(), objectToBePut.getNumber()).toString()), nameValuePairs,
                "Admin", "admin");

        TestUtils.printHttpMethodInfo(postMethod);
        assertEquals(HttpStatus.SC_ACCEPTED, postMethod.getStatusCode());

        Object updatedObjectSummary = (Object) unmarshaller.unmarshal(postMethod.getResponseBodyAsStream());

        assertEquals(TAG_VALUE, getProperty(updatedObjectSummary, "tags").getValue());
        assertEquals(object.getClassName(), updatedObjectSummary.getClassName());
        assertEquals(object.getNumber(), updatedObjectSummary.getNumber());
    }

    public void testPOSTObjectFormUrlEncoded() throws Exception
    {
        TestUtils.banner("testPOSTObject()");

        final String TAG_VALUE = "TAG";

        NameValuePair[] nameValuePairs = new NameValuePair[2];
        nameValuePairs[0] = new NameValuePair("className", "XWiki.TagClass");
        nameValuePairs[1] = new NameValuePair("property#tags", TAG_VALUE);

        PostMethod postMethod =
            executePostForm(getUriBuilder(ObjectsResource.class).build(getWiki(), "Main", "WebHome").toString(),
                nameValuePairs, "Admin", "admin");
        TestUtils.printHttpMethodInfo(postMethod);
        assertEquals(HttpStatus.SC_CREATED, postMethod.getStatusCode());

        Object object = (Object) unmarshaller.unmarshal(postMethod.getResponseBodyAsStream());

        assertEquals(TAG_VALUE, getProperty(object, "tags").getValue());

        GetMethod getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome", object.getClassName(),
                object.getNumber()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        object = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertEquals(TAG_VALUE, getProperty(object, "tags").getValue());
    }

    public void testPUTPropertyFormUrlEncoded() throws Exception
    {
        TestUtils.banner("testPUTPropertyFormUrlEncoded()");

        final String TAG_VALUE = UUID.randomUUID().toString();

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), "Main", "WebHome").toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Link link = getFirstLinkByRelation(page, Relations.OBJECTS);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Objects objects = (Objects) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertFalse(objects.getObjectSummaries().isEmpty());

        Object currentObject = null;

        for (ObjectSummary objectSummary : objects.getObjectSummaries()) {
            if (objectSummary.getClassName().equals("XWiki.TagClass")) {
                link = getFirstLinkByRelation(objectSummary, Relations.OBJECT);
                assertNotNull(link);
                getMethod = executeGet(link.getHref());
                TestUtils.printHttpMethodInfo(getMethod);
                assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

                currentObject = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
                break;
            }
        }

        assertNotNull(currentObject);

        Property tagsProperty = getProperty(currentObject, "tags");

        assertNotNull(tagsProperty);

        Link tagsPropertyLink = getFirstLinkByRelation(tagsProperty, Relations.SELF);

        assertNotNull(tagsPropertyLink);

        NameValuePair[] nameValuePairs = new NameValuePair[1];
        nameValuePairs[0] = new NameValuePair("property#tags", TAG_VALUE);

        PostMethod postMethod =
            executePostForm(String.format("%s?method=PUT", tagsPropertyLink.getHref()), nameValuePairs, "Admin",
                "admin");
        TestUtils.printHttpMethodInfo(postMethod);
        assertEquals(HttpStatus.SC_ACCEPTED, postMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                currentObject.getClassName(), currentObject.getNumber()).toString());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        currentObject = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        tagsProperty = getProperty(currentObject, "tags");

        assertEquals(TAG_VALUE, tagsProperty.getValue());
    }

    public void testGETObjectAtPageVersion() throws Exception
    {
        TestUtils.banner("testPUTObject()");

        Object objectToBePut = getObject("XWiki.TagClass");

        Map<String, String> versionToValueMap = new HashMap<String, String>();
        for (int i = 0; i < 5; i++) {
            String value = String.format("Value%d", i);

            Property property = getProperty(objectToBePut, "tags");
            property.setValue(value);

            PutMethod putMethod =
                executePutXml(getUriBuilder(ObjectResource.class).build(getWiki(), "Main", "WebHome",
                    objectToBePut.getClassName(), objectToBePut.getNumber()).toString(), objectToBePut, "Admin",
                    "admin");
            TestUtils.printHttpMethodInfo(putMethod);
            assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());

            GetMethod getMethod =
                executeGet(getUriBuilder(PageResource.class).build(getWiki(), "Main", "WebHome").toString());
            TestUtils.printHttpMethodInfo(getMethod);
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

            Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

            versionToValueMap.put(page.getVersion(), value);
        }

        for (String version : versionToValueMap.keySet()) {
            GetMethod getMethod =
                executeGet(getUriBuilder(ObjectAtPageVersionResource.class).build(getWiki(), "Main", "WebHome",
                    version, objectToBePut.getClassName(), objectToBePut.getNumber()).toString());
            TestUtils.printHttpMethodInfo(getMethod);
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

            Object currentObject = (Object) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

            Property property = getProperty(currentObject, "tags");

            assertEquals(versionToValueMap.get(version), property.getValue());

            checkLinks(currentObject);
            for (Property p : currentObject.getProperties()) {
                checkLinks(p);
            }
        }

    }

}
