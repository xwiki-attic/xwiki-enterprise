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

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.rest.Relations;
import org.xwiki.rest.it.framework.AbstractHttpTest;
import org.xwiki.rest.it.framework.TestUtils;
import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.Attachments;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Pages;
import org.xwiki.rest.model.jaxb.SearchResult;
import org.xwiki.rest.model.jaxb.SearchResults;
import org.xwiki.rest.model.jaxb.Wiki;
import org.xwiki.rest.model.jaxb.Wikis;
import org.xwiki.rest.resources.wikis.WikiAttachmentsResource;
import org.xwiki.rest.resources.wikis.WikiPagesResource;
import org.xwiki.rest.resources.wikis.WikiSearchResource;
import org.xwiki.rest.resources.wikis.WikisResource;

public class WikisResourceTest extends AbstractHttpTest
{
    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod = executeGet(getFullUri(WikisResource.class));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Wikis wikis = (Wikis) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        assertTrue(wikis.getWikis().size() > 0);

        for (Wiki wiki : wikis.getWikis()) {
            Link link = getFirstLinkByRelation(wiki, Relations.SPACES);
            assertNotNull(link);

            link = getFirstLinkByRelation(wiki, Relations.CLASSES);
            assertNotNull(link);

            link = getFirstLinkByRelation(wiki, Relations.MODIFICATIONS);
            assertNotNull(link);

            link = getFirstLinkByRelation(wiki, Relations.SEARCH);
            assertNotNull(link);

            checkLinks(wiki);
        }
    }

    public void testSearch() throws Exception
    {
        GetMethod getMethod =
            executeGet(String.format("%s?q=easy-to-edit", getUriBuilder(WikiSearchResource.class).build(getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        int resultSize = searchResults.getSearchResults().size();
        assertTrue("Found " + resultSize + " result", resultSize >= 1);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }

        getMethod =
            executeGet(String.format("%s?q=WebHome&scope=name",
                getUriBuilder(WikiSearchResource.class).build(getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertTrue(searchResults.getSearchResults().size() >= 3);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }

        // Note: we use $msg.get(...) a bit everywhere in our title for the moment... The search is a search in the DB
        // and not on the rendered content. Thus for our tests we search on msg...
        getMethod =
            executeGet(String.format("%s?q=msg&scope=title", getUriBuilder(WikiSearchResource.class).build(getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertTrue(searchResults.getSearchResults().size() >= 1);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }

    }

    public void testPages() throws Exception
    {
        TestUtils.banner("testWikiPages()");

        GetMethod getMethod = executeGet(String.format("%s", getUriBuilder(WikiPagesResource.class).build(getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Pages pages = (Pages) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertTrue(pages.getPageSummaries().size() > 0);

        for (PageSummary pageSummary : pages.getPageSummaries()) {
            checkLinks(pageSummary);
        }

        getMethod =
            executeGet(String.format("%s?name=WebHome", getUriBuilder(WikiPagesResource.class).build(getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        pages = (Pages) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        System.out.format("Pages: %d\n", pages.getPageSummaries().size());
        for (PageSummary pageSummary : pages.getPageSummaries()) {
            System.out.format("* %s\n", pageSummary.getFullName());
        }

        assertEquals(8, pages.getPageSummaries().size());

        for (PageSummary pageSummary : pages.getPageSummaries()) {
            checkLinks(pageSummary);
        }

        getMethod =
            executeGet(String.format("%s?name=WebHome&space=s", getUriBuilder(WikiPagesResource.class).build(getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        pages = (Pages) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        System.out.format("Pages: %d\n", pages.getPageSummaries().size());
        for (PageSummary pageSummary : pages.getPageSummaries()) {
            System.out.format("* %s\n", pageSummary.getFullName());
        }

        assertEquals(5, pages.getPageSummaries().size());

        for (PageSummary pageSummary : pages.getPageSummaries()) {
            checkLinks(pageSummary);
        }

    }

    public void testAttachments() throws Exception
    {
        TestUtils.banner("testWikiAttachments()");

        GetMethod getMethod =
            executeGet(String.format("%s", getUriBuilder(WikiAttachmentsResource.class).build(getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Attachments attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertTrue(attachments.getAttachments().size() > 0);

        for (Attachment attachment : attachments.getAttachments()) {
            checkLinks(attachment);
        }

        getMethod =
            executeGet(String.format("%s?name=bl", getUriBuilder(WikiAttachmentsResource.class).build(getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        System.out.format("Attachments: %d\n", attachments.getAttachments().size());
        for (Attachment attachment : attachments.getAttachments()) {
            System.out.format("* %s\n", attachment.getName());
        }

        assertEquals(3, attachments.getAttachments().size());

        for (Attachment attachment : attachments.getAttachments()) {
            checkLinks(attachment);
        }

        getMethod =
            executeGet(String.format("%s?space=blog", getUriBuilder(WikiAttachmentsResource.class).build(getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertEquals(7, attachments.getAttachments().size());

        for (Attachment attachment : attachments.getAttachments()) {
            checkLinks(attachment);
        }

        getMethod =
            executeGet(String.format("%s?name=de&space=blog", getUriBuilder(WikiAttachmentsResource.class).build(
                getWiki())));
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertEquals(3, attachments.getAttachments().size());

        for (Attachment attachment : attachments.getAttachments()) {
            checkLinks(attachment);
        }

    }

}
