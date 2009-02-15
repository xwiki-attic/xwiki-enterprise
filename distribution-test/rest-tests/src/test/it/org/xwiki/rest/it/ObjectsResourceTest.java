package org.xwiki.rest.it;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.jackrabbit.uuid.UUID;
import org.xwiki.rest.Constants;
import org.xwiki.rest.Utils;
import org.xwiki.rest.model.Link;
import org.xwiki.rest.model.ObjectSummary;
import org.xwiki.rest.model.Objects;
import org.xwiki.rest.model.Page;
import org.xwiki.rest.model.Property;
import org.xwiki.rest.model.Relations;
import org.xwiki.rest.resources.objects.ObjectResource;
import org.xwiki.rest.resources.objects.ObjectsResource;
import org.xwiki.rest.resources.pages.PageResource;

public class ObjectsResourceTest extends AbstractHttpTest
{
    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, "Main");
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, "WebHome");

        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageResource.class), parametersMap)));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Page page = (Page) xstream.fromXML(getMethod.getResponseBodyAsString());
        Link link = page.getFirstLinkByRelation(Relations.OBJECTS);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Objects objects = (Objects) xstream.fromXML(getMethod.getResponseBodyAsString());

        assertFalse(objects.getObjectSummaryList().isEmpty());

        for (ObjectSummary objectSummary : objects.getObjectSummaryList()) {
            link = objectSummary.getFirstLinkByRelation(Relations.SELF);
            getMethod = executeGet(link.getHref());
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
            TestUtils.printHttpMethodInfo(getMethod);

            ObjectSummary object = (ObjectSummary) xstream.fromXML(getMethod.getResponseBodyAsString());
        }
    }

    public void testGETNotExistingObject() throws Exception
    {
        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, "Main");
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, "WebHome");
        parametersMap.put(Constants.CLASS_NAME_PARAMETER, "NOTEXISTING");
        parametersMap.put(Constants.OBJECT_NUMBER_PARAMETER, "0");

        GetMethod getMethod =
            executeGet(getFullUri(Utils
                .formatUriTemplate(getUriPatternForResource(ObjectResource.class), parametersMap)));
        assertEquals(HttpStatus.SC_NOT_FOUND, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);
    }

    public void testPOSTObject() throws Exception
    {
        final String TAG_VALUE = "TAG";

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, "Main");
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, "WebHome");

        Property property = new Property();
        property.setName("tags");
        property.setValue(TAG_VALUE);
        ObjectSummary objectSummary = new ObjectSummary();
        objectSummary.setClassName("XWiki.TagClass");
        objectSummary.getPropertyList().addProperty(property);

        PostMethod postMethod =
            executePost(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(ObjectsResource.class),
                parametersMap)), objectSummary, "Admin", "admin");
        assertEquals(HttpStatus.SC_CREATED, postMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(postMethod);

        objectSummary = (ObjectSummary) xstream.fromXML(postMethod.getResponseBodyAsString());

        assertEquals(TAG_VALUE, objectSummary.getPropertyValue("tags"));

        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, "Main");
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, "WebHome");
        parametersMap.put(Constants.CLASS_NAME_PARAMETER, objectSummary.getClassName());
        parametersMap.put(Constants.OBJECT_NUMBER_PARAMETER, String.format("%d", objectSummary.getNumber()));

        GetMethod getMethod =
            executeGet(getFullUri(Utils
                .formatUriTemplate(getUriPatternForResource(ObjectResource.class), parametersMap)));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        objectSummary = (ObjectSummary) xstream.fromXML(postMethod.getResponseBodyAsString());

        assertEquals(TAG_VALUE, objectSummary.getPropertyValue("tags"));
    }

    public void testPOSTObjectNotAuthorized() throws Exception
    {
        final String TAG_VALUE = "TAG";

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, "Main");
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, "WebHome");

        Property property = new Property();
        property.setName("tags");
        property.setValue(TAG_VALUE);
        ObjectSummary objectSummary = new ObjectSummary();
        objectSummary.setClassName("XWiki.TagClass");
        objectSummary.getPropertyList().addProperty(property);

        PostMethod postMethod =
            executePost(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(ObjectsResource.class),
                parametersMap)), objectSummary);
        assertEquals(HttpStatus.SC_FORBIDDEN, postMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(postMethod);
    }

    public void testPUTObject() throws Exception
    {
        final String TAG_VALUE = UUID.randomUUID().toString();

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, "Main");
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, "WebHome");
        parametersMap.put(Constants.CLASS_NAME_PARAMETER, "XWiki.TagClass");
        parametersMap.put(Constants.OBJECT_NUMBER_PARAMETER, "0");

        GetMethod getMethod =
            executeGet(getFullUri(Utils
                .formatUriTemplate(getUriPatternForResource(ObjectResource.class), parametersMap)));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        ObjectSummary objectSummary = (ObjectSummary) xstream.fromXML(getMethod.getResponseBodyAsString());

        objectSummary.getProperty("tags").setValue(TAG_VALUE);

        PutMethod putMethod =
            executePut(getFullUri(Utils
                .formatUriTemplate(getUriPatternForResource(ObjectResource.class), parametersMap)), objectSummary,
                "Admin", "admin");
        assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(putMethod);

        ObjectSummary updatedObjectSummary = (ObjectSummary) xstream.fromXML(putMethod.getResponseBodyAsString());

        assertEquals(TAG_VALUE, updatedObjectSummary.getPropertyValue("tags"));
        assertEquals(objectSummary.getClassName(), updatedObjectSummary.getClassName());
        assertEquals(objectSummary.getNumber(), updatedObjectSummary.getNumber());

    }

}
