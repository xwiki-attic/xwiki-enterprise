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
package org.xwiki.test.xmlrpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.xwiki.xmlrpc.model.XWikiClassSummary;
import org.xwiki.xmlrpc.model.XWikiObject;
import org.xwiki.xmlrpc.model.XWikiObjectSummary;
import org.xwiki.xmlrpc.model.XWikiPage;

/**
 * @version $Id$
 */
public class XWikiObjectsTest extends AbstractXWikiXmlRpcTest
{
    public void setUp() throws Exception
    {
        super.setUp();

        try {
            rpc.removePage(TestConstants.TEST_PAGE_WITH_OBJECTS);
        } catch (Exception e) {
            // Page doesn't exist.
        }

        XWikiPage page = new XWikiPage();
        page.setId(TestConstants.TEST_PAGE_WITH_OBJECTS);
        rpc.storePage(page);

        // Make sure the test page has at least one object.
        addTagObject(Arrays.asList("test", "XML", "RPC"));
    }

    private XWikiObject addTagObject(List<String> tags) throws Exception
    {
        XWikiObject tagObject = new XWikiObject();
        tagObject.setPageId(TestConstants.TEST_PAGE_WITH_OBJECTS);
        tagObject.setClassName("XWiki.TagClass");
        tagObject.setProperty("tags", tags);
        return rpc.storeObject(tagObject);
    }

    public void testCreateTagsObject() throws Exception
    {
        List<String> tags = new ArrayList<String>();
        tags.add(String.format("New-%d", random.nextInt()));

        XWikiObject tagsObject = addTagObject(tags);

        TestUtils.banner("createTagsObject()");
        System.out.format("%s\n", tagsObject);

        assertTrue(tagsObject.getId() != -1);
        assertEquals(tags, tagsObject.getProperty("tags"));
    }

    public void testGetXWikiObjects() throws Exception
    {
        List<XWikiObjectSummary> xwikiObjects = rpc.getObjects(TestConstants.TEST_PAGE_WITH_OBJECTS);

        TestUtils.banner("TEST: getXWikiObjects()");
        for (XWikiObjectSummary xwikiObjectSummary : xwikiObjects) {
            System.out.format("%s\n", xwikiObjectSummary);
        }

        assertFalse(xwikiObjects.isEmpty());
    }

    public void testGetXWikiTagObject() throws Exception
    {
        List<XWikiObjectSummary> xwikiObjects = rpc.getObjects(TestConstants.TEST_PAGE_WITH_OBJECTS);

        XWikiObjectSummary tagsObjectSummary = null;
        for (XWikiObjectSummary xwikiObjectSummary : xwikiObjects) {
            if (xwikiObjectSummary.getClassName().equals("XWiki.TagClass")) {
                tagsObjectSummary = xwikiObjectSummary;
            }
        }

        XWikiObject tagsObject = rpc.getObject(tagsObjectSummary);

        TestUtils.banner("TEST: getXWikiTagObject()");
        System.out.format("%s\n", tagsObject);

        assertEquals(tagsObjectSummary.getPageId(), tagsObject.getPageId());
        assertEquals(tagsObjectSummary.getId(), tagsObject.getId());
        assertEquals(tagsObjectSummary.getClassName(), tagsObject.getClassName());
    }

    public void testSetTagsObject() throws Exception
    {
        List<XWikiObjectSummary> xwikiObjects = rpc.getObjects(TestConstants.TEST_PAGE_WITH_OBJECTS);

        XWikiObjectSummary tagsObjectSummary = null;
        for (XWikiObjectSummary xwikiObjectSummary : xwikiObjects) {
            if (xwikiObjectSummary.getClassName().equals("XWiki.TagClass")) {
                tagsObjectSummary = xwikiObjectSummary;
            }
        }

        XWikiObject object = rpc.getObject(tagsObjectSummary);

        TestUtils.banner("TEST: setTagsObject()");
        System.out.format("%s\n", object);

        Object value = object.getProperty("tags");
        /* Here we also check that properties that have a structured type are transfered in that form */
        assertTrue(value instanceof List);

        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) value;
        tags.add((new Integer(random.nextInt()).toString()));

        rpc.storeObject(object);

        object = rpc.getObject(tagsObjectSummary);

        @SuppressWarnings("unchecked")
        List<String> newTags = (List<String>) object.getProperty("tags");

        assertTrue(newTags.size() == tags.size());

        for (Object t : tags) {
            assertTrue(newTags.contains(t));
        }
    }

    public void testRemoveObject() throws Exception
    {
        List<String> tags = new ArrayList<String>();
        tags.add(String.format("New-%d", random.nextInt()));

        XWikiObject tagsObject = addTagObject(tags);
        Boolean result = rpc.removeObject(tagsObject);

        TestUtils.banner("removeObjectTest()");
        System.out.format("Object added: %s\n", tagsObject);
        System.out.format("Object removed = %b\n", result);

        List<XWikiObjectSummary> objects = rpc.getObjects(tagsObject.getPageId());
        boolean found = false;
        for (XWikiObjectSummary object : objects) {
            if (object.getClassName().equals(tagsObject.getClassName()) && object.getId() == tagsObject.getId()) {
                found = true;
                break;
            }
        }

        assertFalse(found);
    }

    public void testCreateTagsObjectWithCheckVersion() throws Exception
    {
        List<String> tags = new ArrayList<String>();
        tags.add(String.format("New-%d", random.nextInt()));

        XWikiObject tagsObject = addTagObject(tags);
        tagsObject.setPageId(TestConstants.TEST_PAGE_WITH_OBJECTS);
        tagsObject.setClassName("XWiki.TagClass");
        tagsObject.setPrettyName("PrettyName");
        tagsObject.setProperty("tags", tags);

        XWikiObject storedTagsObject = rpc.storeObject(tagsObject);

        TestUtils.banner("createTagsObjectWithCheckVersion()");
        System.out.format("%s\n", storedTagsObject);

        assertTrue(storedTagsObject.getId() != -1);
        assertEquals(tags, storedTagsObject.getProperty("tags"));

        /* Try to store the object again */
        storedTagsObject = rpc.storeObject(tagsObject, true);
        assertTrue(storedTagsObject.getPageId().equals(""));
    }

    @SuppressWarnings("unchecked")
    public void testGetObjectAtPreviousVersion() throws Exception
    {
        List<String> tags = new ArrayList<String>();
        tags.add(String.format("VERSION1", random.nextInt()));

        XWikiObject tagsObject = new XWikiObject();
        tagsObject.setPageId(TestConstants.TEST_PAGE_WITH_OBJECTS);
        tagsObject.setClassName("XWiki.TagClass");
        tagsObject.setPrettyName("PrettyName");
        tagsObject.setProperty("tags", tags);

        TestUtils.banner("getObjectAtPreviousVersion()");

        XWikiObject storedTagsObject1 = rpc.storeObject(tagsObject);
        System.out.format("%s %s\n", storedTagsObject1.getProperty("tags"), storedTagsObject1);

        tags = new ArrayList<String>();
        tags.add(String.format("VERSION2", random.nextInt()));
        storedTagsObject1.setProperty("tags", tags);

        XWikiObject storedTagsObject2 = rpc.storeObject(storedTagsObject1);
        System.out.format("%s %s\n", storedTagsObject2.getProperty("tags"), storedTagsObject2);

        XWikiObject object =
            rpc.getObject(storedTagsObject1.getPageId(), storedTagsObject1.getClassName(), storedTagsObject1.getId(),
                storedTagsObject1.getPageVersion(), storedTagsObject1.getPageMinorVersion());
        System.out.format("%s %s\n", object.getProperty("tags"), object);

        tags = (List<String>) object.getProperty("tags");
        assertTrue(tags.contains("VERSION1"));
    }

    public void testGetObjectByGuid() throws XmlRpcException
    {
        List<XWikiObjectSummary> objectSummaries = rpc.getObjects(TestConstants.TEST_PAGE_WITH_OBJECTS);

        TestUtils.banner("getObjectByGuid()");
        System.out.format("%s\n", objectSummaries);

        XWikiObject object = rpc.getObject(TestConstants.TEST_PAGE_WITH_OBJECTS, objectSummaries.get(0).getGuid());
        System.out.format("Guid '%s': %s\n", objectSummaries.get(0).getGuid(), object);

        assertTrue(object.getGuid().equals(objectSummaries.get(0).getGuid()));
    }

    public void testOverrideObjectGuid() throws XmlRpcException
    {
        final String GUID = "overridden-guid";

        List<XWikiObjectSummary> objectSummaries = rpc.getObjects(TestConstants.TEST_PAGE_WITH_OBJECTS);

        TestUtils.banner("getOverrideObjectGuid()");

        XWikiObject object = rpc.getObject(TestConstants.TEST_PAGE_WITH_OBJECTS, objectSummaries.get(0).getGuid());
        object.setGuid(GUID);
        rpc.storeObject(object);

        object = rpc.getObject(TestConstants.TEST_PAGE_WITH_OBJECTS, GUID);

        System.out.format("%s\n", object);

        assertTrue(object.getGuid().equals(GUID));
    }

    public void testCreateEmptyObjectsFromAllClasses() throws XmlRpcException
    {
        TestUtils.banner("createEmptyObjectsFromAllClasses()");

        List<XWikiClassSummary> xwikiClasses = rpc.getClasses();
        for (XWikiClassSummary cs : xwikiClasses) {
            System.out.format("Storing object for class %s\n", cs.getId());
            XWikiObject object = new XWikiObject();
            object.setPageId(TestConstants.TEST_PAGE_WITH_OBJECTS);
            object.setClassName(cs.getId());
            rpc.storeObject(object);
        }
    }
}
