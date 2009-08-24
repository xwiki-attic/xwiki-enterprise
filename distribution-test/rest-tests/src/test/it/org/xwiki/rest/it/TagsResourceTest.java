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

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.jackrabbit.uuid.UUID;
import org.xwiki.rest.Relations;
import org.xwiki.rest.it.framework.AbstractHttpTest;
import org.xwiki.rest.it.framework.TestConstants;
import org.xwiki.rest.it.framework.TestUtils;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Pages;
import org.xwiki.rest.model.jaxb.Tag;
import org.xwiki.rest.model.jaxb.Tags;
import org.xwiki.rest.resources.pages.PageResource;
import org.xwiki.rest.resources.pages.PageTagsResource;
import org.xwiki.rest.resources.tags.PagesForTagsResource;
import org.xwiki.rest.resources.tags.TagsResource;

/**
 * @version $Id$
 */
public class TagsResourceTest extends AbstractHttpTest
{
    private void createPageIfDoesntExist(String spaceName, String pageName, String content) throws Exception
    {
        String uri = getUriBuilder(PageResource.class).build(getWiki(), spaceName, pageName).toString();

        GetMethod getMethod = executeGet(uri);
        TestUtils.printHttpMethodInfo(getMethod);

        if (getMethod.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            System.out.format("Page %s.%s doesn't exist... Creating it\n", spaceName, pageName);

            Page page = objectFactory.createPage();
            page.setContent(content);

            PutMethod putMethod = executePutXml(uri, page, "Admin", "admin");
            TestUtils.printHttpMethodInfo(putMethod);
            assertEquals(HttpStatus.SC_CREATED, putMethod.getStatusCode());

            getMethod = executeGet(uri);
            TestUtils.printHttpMethodInfo(getMethod);
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

            System.out.format("Page %s.%s created.\n", spaceName, pageName);
        } else {
            System.out.format("Page %s.%s exists. Good!\n", spaceName, pageName);
        }
    }

    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        String tagName = UUID.randomUUID().toString();

        createPageIfDoesntExist(TestConstants.TEST_SPACE_NAME, TestConstants.TEST_PAGE_NAME, "Test");

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Tags tags = objectFactory.createTags();
        Tag tag = objectFactory.createTag();
        tag.setName(tagName);
        tags.getTags().add(tag);

        PutMethod putMethod =
            executePutXml(getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString(), tags, "Admin", "admin");
        TestUtils.printHttpMethodInfo(putMethod);
        assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        tags = (Tags) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        boolean found = false;
        for (Tag t : tags.getTags()) {
            if (tagName.equals(t.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found);

        getMethod = executeGet(getUriBuilder(TagsResource.class).build(getWiki()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        tags = (Tags) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        found = false;
        for (Tag t : tags.getTags()) {
            if (tagName.equals(t.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found);

        getMethod = executeGet(getUriBuilder(PagesForTagsResource.class).build(getWiki(), tagName).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Pages pages = (Pages) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        found = false;
        for (PageSummary pageSummary : pages.getPageSummaries()) {
            if (pageSummary.getFullName().equals(
                String.format("%s.%s", TestConstants.TEST_SPACE_NAME, TestConstants.TEST_PAGE_NAME))) {
                found = true;
            }
        }
        assertTrue(found);

        getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Link tagsLink = getFirstLinkByRelation(page, Relations.TAGS);
        assertNotNull(tagsLink);
    }

    public void testPUTTagsWithTextPlain() throws Exception
    {
        TestUtils.banner("testPUTTagsWithTextPlain()");

        createPageIfDoesntExist(TestConstants.TEST_SPACE_NAME, TestConstants.TEST_PAGE_NAME, "Test");

        String tagName = UUID.randomUUID().toString();

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        PutMethod putMethod =
            executePut(getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString(), tagName, MediaType.TEXT_PLAIN, "Admin", "admin");
        TestUtils.printHttpMethodInfo(putMethod);
        assertEquals(HttpStatus.SC_ACCEPTED, putMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Tags tags = (Tags) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        boolean found = false;
        for (Tag t : tags.getTags()) {
            if (tagName.equals(t.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    public void testPUTTagsFormUrlEncoded() throws Exception
    {
        TestUtils.banner("testPUTTagsFormUrlEncoded()");

        createPageIfDoesntExist(TestConstants.TEST_SPACE_NAME, TestConstants.TEST_PAGE_NAME, "Test");

        String tagName = UUID.randomUUID().toString();

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        NameValuePair[] nameValuePairs = new NameValuePair[1];
        nameValuePairs[0] = new NameValuePair("tags", tagName);

        PostMethod postMethod =
            executePostForm(String.format("%s?method=PUT", getUriBuilder(PageTagsResource.class).build(getWiki(),
                TestConstants.TEST_SPACE_NAME, TestConstants.TEST_PAGE_NAME).toString()), nameValuePairs, "Admin",
                "admin");
        TestUtils.printHttpMethodInfo(postMethod);
        assertEquals(HttpStatus.SC_ACCEPTED, postMethod.getStatusCode());

        getMethod =
            executeGet(getUriBuilder(PageTagsResource.class).build(getWiki(), TestConstants.TEST_SPACE_NAME,
                TestConstants.TEST_PAGE_NAME).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Tags tags = (Tags) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        boolean found = false;
        for (Tag t : tags.getTags()) {
            if (tagName.equals(t.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

}
