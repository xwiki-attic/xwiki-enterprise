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
 *
 */
package org.xwiki.xmlrpc;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.xwiki.xmlrpc.model.XWikiObject;
import org.xwiki.xmlrpc.model.XWikiObjectSummary;
import org.xwiki.xmlrpc.model.XWikiPage;

public class XWikiObjectsTest extends AbstractXWikiXmlRpcTest
{
    public void setUp() throws XmlRpcException, MalformedURLException
    {
        super.setUp();
        try {
            rpc.getPage(TestConstants.TEST_PAGE_WITH_OBJECTS);
        } catch (Exception e) {
            XWikiPage page = new XWikiPage();
            page.setId(TestConstants.TEST_PAGE_WITH_OBJECTS);
            page.setTitle("Test page with objects");
            page.setContent("Test page with objects");
            rpc.storePage(page);
        }
    }

    public void testCreateTagsObject() throws XmlRpcException
    {
        XWikiObject tagsObject =
            new XWikiObject();
        tagsObject.setPageId(TestConstants.TEST_PAGE_WITH_OBJECTS);
        tagsObject.setClassName("XWiki.TagClass");
        tagsObject.setPrettyName("PrettyName");

        List tags = new ArrayList();
        tags.add(String.format("New-%d", random.nextInt()));
        tagsObject.setProperty("tags", tags);

        tagsObject = rpc.storeObject(tagsObject);

        TestUtils.banner("createTagsObject()");
        System.out.format("%s\n", tagsObject);

        assertTrue(tagsObject.getId() != -1);
        assertEquals(tags, tagsObject.getProperty("tags"));
    }

    public void testGetXWikiObjects() throws XmlRpcException
    {
        List<XWikiObjectSummary> xwikiObjects =
            rpc.getObjects(TestConstants.TEST_PAGE_WITH_OBJECTS);

        TestUtils.banner("TEST: getXWikiObjects()");
        for (XWikiObjectSummary xwikiObjectSummary : xwikiObjects) {
            System.out.format("%s\n", xwikiObjectSummary);
        }

        assertFalse(xwikiObjects.isEmpty());
    }

    public void testGetXWikiTagObject() throws XmlRpcException
    {
        List<XWikiObjectSummary> xwikiObjects =
            rpc.getObjects(TestConstants.TEST_PAGE_WITH_OBJECTS);

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

    public void testSetTagsObject() throws XmlRpcException
    {
        List<XWikiObjectSummary> xwikiObjects =
            rpc.getObjects(TestConstants.TEST_PAGE_WITH_OBJECTS);

        XWikiObjectSummary tagsObjectSummary = null;
        for (XWikiObjectSummary xwikiObjectSummary : xwikiObjects) {
            if (xwikiObjectSummary.getClassName().equals("XWiki.TagClass")) {
                tagsObjectSummary = xwikiObjectSummary;
            }
        }

        XWikiObject object =
            rpc.getObject(tagsObjectSummary);

        TestUtils.banner("TEST: setTagsObject()");
        System.out.format("%s\n", object);

        Object value = object.getProperty("tags");
        /* Here we also check that properties that have a structured type are transfered in that form */
        assertTrue(value instanceof List);

        List tags = (List) value;
        tags.add((new Integer(random.nextInt()).toString()));

        rpc.storeObject(object);

        object = rpc.getObject(tagsObjectSummary);

        List newTags = (List) object.getProperty("tags");

        assertTrue(newTags.size() == tags.size());

        for (Object t : tags) {
            assertTrue(newTags.contains(t));
        }
    }

    public void testRemoveObject() throws XmlRpcException
    {
        XWikiObject tagsObject = new XWikiObject();
        tagsObject.setPageId(TestConstants.TEST_PAGE_WITH_OBJECTS);
        tagsObject.setClassName("XWiki.TagClass");
        tagsObject.setPrettyName("PrettyName");

        List tags = new ArrayList();
        tags.add(String.format("New-%d", random.nextInt()));
        tagsObject.setProperty("tags", tags);

        tagsObject = rpc.storeObject(tagsObject);

        Boolean result = rpc.removeObject(tagsObject);

        TestUtils.banner("removeObjectTest()");
        System.out.format("Object added: %s\n", tagsObject);
        System.out.format("Object removed = %b\n", result);

        List<XWikiObjectSummary> objects = rpc.getObjects(tagsObject.getPageId());
        boolean found = false;
        for (XWikiObjectSummary object : objects) {
            if (object.getClassName().equals(tagsObject.getClassName())
                && object.getId() == tagsObject.getId())
            {
                found = true;
                break;
            }
        }

        assertFalse(found);
    }
}
