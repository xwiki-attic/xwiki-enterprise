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
package org.xwiki.test.rest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.rest.Relations;
import org.xwiki.rest.model.jaxb.Attachment;
import org.xwiki.rest.model.jaxb.Attachments;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.Page;
import org.xwiki.rest.model.jaxb.PageSummary;
import org.xwiki.rest.model.jaxb.Pages;
import org.xwiki.rest.model.jaxb.SearchResult;
import org.xwiki.rest.model.jaxb.SearchResults;
import org.xwiki.rest.model.jaxb.Wiki;
import org.xwiki.rest.model.jaxb.Wikis;
import org.xwiki.rest.resources.pages.PageResource;
import org.xwiki.rest.resources.wikis.WikiAttachmentsResource;
import org.xwiki.rest.resources.wikis.WikiPagesResource;
import org.xwiki.rest.resources.wikis.WikiResource;
import org.xwiki.rest.resources.wikis.WikiSearchQueryResource;
import org.xwiki.rest.resources.wikis.WikiSearchResource;
import org.xwiki.rest.resources.wikis.WikisResource;
import org.xwiki.rest.resources.wikis.WikisSearchQueryResource;
import org.xwiki.test.rest.framework.AbstractHttpTest;
import org.xwiki.test.ui.TestUtils;

public class WikisResourceTest extends AbstractHttpTest
{
    @Override
    @Test
    public void testRepresentation() throws Exception
    {
        GetMethod getMethod = executeGet(getFullUri(WikisResource.class));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Wikis wikis = (Wikis) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        Assert.assertTrue(getHttpMethodInfo(getMethod), wikis.getWikis().size() > 0);

        for (Wiki wiki : wikis.getWikis()) {
            Link link = getFirstLinkByRelation(wiki, Relations.SPACES);
            Assert.assertNotNull(link);

            link = getFirstLinkByRelation(wiki, Relations.CLASSES);
            Assert.assertNotNull(link);

            link = getFirstLinkByRelation(wiki, Relations.MODIFICATIONS);
            Assert.assertNotNull(link);

            link = getFirstLinkByRelation(wiki, Relations.SEARCH);
            Assert.assertNotNull(link);

            link = getFirstLinkByRelation(wiki, Relations.QUERY);
            Assert.assertNotNull(link);

            checkLinks(wiki);
        }
    }

    @Test
    public void testSearch() throws Exception
    {
        GetMethod getMethod =
            executeGet(String.format("%s?q=easy-to-edit", getUriBuilder(WikiSearchResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        int resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize >= 1);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }

        getMethod =
            executeGet(String.format("%s?q=WebHome&scope=name", getUriBuilder(WikiSearchResource.class)
                .build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize >= 3);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }

        // Note: we use $services.<service> a bit everywhere in our title for the moment... The search is a search in the DB
        // and not on the rendered content. Thus for our tests we search on services...
        getMethod =
            executeGet(String.format("%s?q=services&scope=title", getUriBuilder(WikiSearchResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), searchResults.getSearchResults().size() >= 1);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }

        /* Check search for space names. Here we should get at least Sandbox as a result */
        getMethod =
            executeGet(String.format("%s?q=db&scope=spaces", getUriBuilder(WikiSearchResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize >= 1);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }
    }

    @Test
    public void testObjectSearchNotAuthenticated() throws Exception
    {
        /* Check search for an object containing XWiki.Admin (i.e., the admin profile) */
        GetMethod getMethod =
            executeGet(String.format("%s?q=XWiki.Admin&scope=objects",
                getUriBuilder(WikiSearchResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        int resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize == 0);
    }

    @Test
    public void testObjectSearchAuthenticated() throws Exception
    {
        /* Check search for an object containing XWiki.Admin (i.e., the admin profile) */
        GetMethod getMethod =
            executeGet(String.format("%s?q=XWiki.Admin&scope=objects",
                getUriBuilder(WikiSearchResource.class).build(getWiki())), TestUtils.ADMIN_CREDENTIALS.getUserName(),
                TestUtils.ADMIN_CREDENTIALS.getPassword());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        /*
         * We get more results because previous tests have also created comments on behalf of XWiki.Admin. They will
         * appear in the results.
         */
        int resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize >= 1);
    }

    @Test
    public void testPages() throws Exception
    {
        // Get all pages
        GetMethod getMethod = executeGet(String.format("%s", getUriBuilder(WikiPagesResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Pages pages = (Pages) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        Assert.assertTrue(pages.getPageSummaries().size() > 0);

        for (PageSummary pageSummary : pages.getPageSummaries()) {
            checkLinks(pageSummary);
        }

        // Get all pages having a document name that contains "WebHome" (for all spaces)
        getMethod =
            executeGet(String.format("%s?name=WebHome", getUriBuilder(WikiPagesResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        pages = (Pages) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        List<PageSummary> pageSummaries = pages.getPageSummaries();
        Assert.assertTrue(pageSummaries.size() > 0);
        // Verify that some WebHomes we expect are found.
        int foundCounter = 0;
        List<String> expectedWebHomes = Arrays.asList("Main.WebHome", "Sandbox.WebHome", "XWiki.WebHome");
        for (PageSummary pageSummary : pages.getPageSummaries()) {
            if (expectedWebHomes.contains(pageSummary.getFullName())) {
                foundCounter++;
            }
            Assert.assertTrue(pageSummary.getFullName().endsWith(".WebHome"));
            checkLinks(pageSummary);
        }
        // Note: since we can have translations, the number of found pages can be greater than the expected size.
        Assert.assertTrue("Some WebHome pages were not found!", foundCounter >= expectedWebHomes.size());

        // Get all pages having a document name that contains "WebHome" and a space with an "s" in its name.
        getMethod =
            executeGet(String
                .format("%s?name=WebHome&space=s", getUriBuilder(WikiPagesResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        pages = (Pages) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        pageSummaries = pages.getPageSummaries();
        Assert.assertTrue(pageSummaries.size() > 0);
        // Verify that some WebHomes we expect are found.
        foundCounter = 0;
        expectedWebHomes =
            Arrays.asList("ColorThemes.WebHome", "Stats.WebHome", "Sandbox.WebHome", "Panels.WebHome",
                "Scheduler.WebHome", "Sandbox.WebHome");
        for (PageSummary pageSummary : pages.getPageSummaries()) {
            if (expectedWebHomes.contains(pageSummary.getFullName())) {
                foundCounter++;
            }
            Assert.assertTrue(pageSummary.getFullName().endsWith(".WebHome"));
            checkLinks(pageSummary);
        }
        // Note: since we can have translations, the number of found pages can be greater than the expected size.
        Assert.assertTrue("Some WebHome pages were not found!", foundCounter >= expectedWebHomes.size());
    }

    @Test
    public void testAttachments() throws Exception
    {
        // Verify there are attachments in the whole wiki
        GetMethod getMethod = executeGet(getUriBuilder(WikiAttachmentsResource.class).build(getWiki()).toString());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Attachments attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        Assert.assertTrue(attachments.getAttachments().size() > 0);

        for (Attachment attachment : attachments.getAttachments()) {
            checkLinks(attachment);
        }

        // Verify we can search for a specific attachment name in the whole wiki
        // Matches Sandbox.WebHome@XWikiLogo.png
        getMethod =
            executeGet(String.format("%s?name=iLogo", getUriBuilder(WikiAttachmentsResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        Assert.assertEquals(getAttachmentsInfo(attachments), 1, attachments.getAttachments().size());

        for (Attachment attachment : attachments.getAttachments()) {
            checkLinks(attachment);
        }

        // Verify we can search for all attachments in a given space (sandbox)
        // Also verify that a space can be looked up independtly of its case ("sandbox" will match the "Sandbox" space)
        getMethod =
            executeGet(String.format("%s?space=sandbox", getUriBuilder(WikiAttachmentsResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        Assert.assertEquals(getAttachmentsInfo(attachments), 1, attachments.getAttachments().size());

        for (Attachment attachment : attachments.getAttachments()) {
            checkLinks(attachment);
        }

        // Verify we can search for an attachment in a given space (sandbox)
        getMethod =
            executeGet(String.format("%s?name=Logo&space=Sandbox",
                getUriBuilder(WikiAttachmentsResource.class).build(getWiki())));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        attachments = (Attachments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        Assert.assertEquals(getAttachmentsInfo(attachments), 1, attachments.getAttachments().size());

        for (Attachment attachment : attachments.getAttachments()) {
            checkLinks(attachment);
        }
    }

    @Test
    public void testHQLQuerySearch() throws Exception
    {
        GetMethod getMethod =
            executeGet(URIUtil.encodeQuery(String.format(
                "%s?q=where doc.name='WebHome' order by doc.space desc&type=hql",
                getUriBuilder(WikiSearchQueryResource.class).build(getWiki()))));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        int resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize >= 1);

        // Verify that some WebHomes we expect are found.
        int foundCounter = 0;
        List<String> expectedWebHomes =
            Arrays.asList("ColorThemes.WebHome", "Stats.WebHome", "Sandbox.WebHome", "Panels.WebHome",
                "Scheduler.WebHome", "Sandbox.WebHome", "XWiki.WebHome");
        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);

            if (expectedWebHomes.contains(searchResult.getPageFullName())) {
                foundCounter++;
            }

            Assert.assertTrue(searchResult.getPageFullName().endsWith(".WebHome"));
        }

        // Note: since we can have translations, the number of found pages can be greater than the expected size.
        Assert.assertTrue("Some WebHome pages were not found!", foundCounter >= expectedWebHomes.size());

        Assert.assertEquals("XWiki.WebHome", searchResults.getSearchResults().get(0).getPageFullName());
    }

    @Test
    public void testHQLQuerySearchWithClassnameAuthenticated() throws Exception
    {
        GetMethod getMethod =
            executeGet(URIUtil.encodeQuery(String.format(
                "%s?q=where doc.space='XWiki' and doc.name='Admin'&type=hql&className=XWiki.XWikiUsers",
                getUriBuilder(WikiSearchQueryResource.class).build(getWiki()))),
                TestUtils.ADMIN_CREDENTIALS.getUserName(), TestUtils.ADMIN_CREDENTIALS.getPassword());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        int resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize == 1);

        Assert.assertNotNull(searchResults.getSearchResults().get(0).getObject());
    }

    @Test
    public void testHQLQuerySearchWithClassnameNotAuthenticated() throws Exception
    {
        GetMethod getMethod =
            executeGet(URIUtil.encodeQuery(String.format(
                "%s?q=where doc.space='XWiki' and doc.name='Admin'&type=hql&classname=XWiki.XWikiUsers",
                getUriBuilder(WikiSearchQueryResource.class).build(getWiki()))));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        int resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize == 1);

        Assert.assertNull(searchResults.getSearchResults().get(0).getObject());
    }

    @Test
    public void testLuceneSearch() throws Exception
    {
        GetMethod getMethod =
            executeGet(URIUtil.encodeQuery(String.format("%s?q=\"easy-to-edit\"&type=lucene",
                getUriBuilder(WikiSearchQueryResource.class).build(getWiki()))));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        int resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize == 1);

        Assert.assertEquals("Main.Welcome", searchResults.getSearchResults().get(0).getPageFullName());
    }

    @Test
    public void testGlobalLuceneSearch() throws Exception
    {
        GetMethod getMethod =
            executeGet(URIUtil.encodeQuery(String.format("%s?q=\"easy-to-edit\"&type=lucene&wikis=xwiki",
                getUriBuilder(WikisSearchQueryResource.class).build(getWiki()))));
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        int resultSize = searchResults.getSearchResults().size();
        Assert.assertTrue(String.format("Found %s results", resultSize), resultSize == 1);

        Assert.assertEquals("Main.Welcome", searchResults.getSearchResults().get(0).getPageFullName());
    }

    @Test
    public void testImportXAR() throws Exception
    {
        InputStream is = this.getClass().getResourceAsStream("/Main.Foo.xar");
        String wiki = getWiki();

        PostMethod postMethod =
            executePost(getUriBuilder(WikiResource.class).build(wiki).toString(), is,
                TestUtils.ADMIN_CREDENTIALS.getUserName(), TestUtils.ADMIN_CREDENTIALS.getPassword());
        Assert.assertEquals(getHttpMethodInfo(postMethod), HttpStatus.SC_OK, postMethod.getStatusCode());

        GetMethod getMethod =
            executeGet(getUriBuilder(PageResource.class).build(wiki, "Main", "Foo").toString(),
                TestUtils.ADMIN_CREDENTIALS.getUserName(), TestUtils.ADMIN_CREDENTIALS.getPassword());
        Assert.assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        Assert.assertEquals(wiki, page.getWiki());
        Assert.assertEquals("Main", page.getSpace());
        Assert.assertEquals("Foo", page.getName());
        Assert.assertEquals("Foo", page.getContent());
    }
}
