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

import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.xwiki.rest.Constants;
import org.xwiki.rest.Utils;
import org.xwiki.rest.model.Link;
import org.xwiki.rest.model.Page;
import org.xwiki.rest.model.PageSummary;
import org.xwiki.rest.model.Pages;
import org.xwiki.rest.model.Relations;
import org.xwiki.rest.model.Space;
import org.xwiki.rest.model.Spaces;
import org.xwiki.rest.model.Wiki;
import org.xwiki.rest.model.Wikis;
import org.xwiki.rest.resources.PageResource;
import org.xwiki.rest.resources.PageTranslationResource;
import org.xwiki.rest.resources.WikisResource;

public class PageResourceTest extends AbstractHttpTest
{
    private Page getPage() throws Exception
    {
        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(WikisResource.class))));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Wikis wikis = (Wikis) xstream.fromXML(getMethod.getResponseBodyAsString());
        assertTrue(wikis.getWikiList().size() > 0);

        Wiki wiki = wikis.getWikiList().get(0);
        Link link = wiki.getFirstLinkByRelation(Relations.SPACES);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Spaces spaces = (Spaces) xstream.fromXML(getMethod.getResponseBodyAsString());

        assertTrue(spaces.getSpaceList().size() > 0);

        Space space = spaces.getSpaceList().get(0);
        link = space.getFirstLinkByRelation(Relations.PAGES);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Pages pages = (Pages) xstream.fromXML(getMethod.getResponseBodyAsString());
        assertTrue(pages.getPageSummaryList().size() > 0);

        PageSummary pageSummary = pages.getPageSummaryList().get(0);
        link = pageSummary.getFirstLinkByRelation(Relations.PAGE);
        assertNotNull(link);

        getMethod = executeGet(link.getHref());
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Page page = (Page) xstream.fromXML(getMethod.getResponseBodyAsString());

        return page;
    }

    public String getWiki() throws Exception
    {
        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(WikisResource.class))));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Wikis wikis = (Wikis) xstream.fromXML(getMethod.getResponseBodyAsString());
        assertTrue(wikis.getWikiList().size() > 0);

        return wikis.getWikiList().get(0).getName();
    }

    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        Page page = getPage();

        Link link = page.getFirstLinkByRelation(Relations.SELF);
        assertNotNull(link);
    }

    public void testGETNotExistingPage() throws Exception
    {
        TestUtils.banner("testGETNotExistingPage()");

        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER, "NOTEXISTING",
                Constants.PAGE_NAME_PARAMETER, "NOTEXISTING")));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_NOT_FOUND);
        TestUtils.printHttpMethodInfo(getMethod);
    }

    public void testPUTPage() throws Exception
    {
        final String CONTENT = String.format("This is a content (%d)", System.currentTimeMillis());
        final String TITLE = String.format("Title (%d)", System.currentTimeMillis());

        TestUtils.banner("testPUTPage()");

        Page originalPage = getPage();

        Page newPage = new Page();
        newPage.setContent(CONTENT);
        newPage.setTitle(TITLE);

        Link link = originalPage.getFirstLinkByRelation(Relations.SELF);
        assertNotNull(link);

        PutMethod putMethod = executePut(link.getHref(), newPage, "Admin", "admin");
        assertTrue(putMethod.getStatusCode() == HttpStatus.SC_ACCEPTED);
        TestUtils.printHttpMethodInfo(putMethod);

        String body = putMethod.getResponseBodyAsString();
        Page modifiedPage = (Page) xstream.fromXML(body);

        assertEquals(modifiedPage.getContent(), CONTENT);
        assertEquals(modifiedPage.getTitle(), TITLE);
    }

    public void testPUTPageUnauthorized() throws Exception
    {
        TestUtils.banner("testPUTPageUnauthorized()");

        Page page = getPage();
        page.setContent("New content");

        Link link = page.getFirstLinkByRelation(Relations.SELF);
        assertNotNull(link);

        PutMethod putMethod = executePut(link.getHref(), page);
        assertTrue(putMethod.getStatusCode() == HttpStatus.SC_FORBIDDEN);
        TestUtils.printHttpMethodInfo(putMethod);
    }

    public void testPUTNonExistingPage() throws Exception
    {
        final String SPACE_NAME = "Test";
        final String PAGE_NAME = String.format("Test-%d", System.currentTimeMillis());
        final String CONTENT = String.format("Content %d", System.currentTimeMillis());
        final String TITLE = String.format("Title %d", System.currentTimeMillis());
        final String PARENT = "Main.WebHome";

        TestUtils.banner("testPUTNotExistingPage()");

        Page page = new Page();
        page.setContent(CONTENT);
        page.setTitle(TITLE);
        page.setParent(PARENT);

        PutMethod putMethod =
            executePut(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER, SPACE_NAME,
                Constants.PAGE_NAME_PARAMETER, PAGE_NAME)), page, "Admin", "admin");
        assertTrue(putMethod.getStatusCode() == HttpStatus.SC_CREATED);
        TestUtils.printHttpMethodInfo(putMethod);

        String body = putMethod.getResponseBodyAsString();
        Page modifiedPage = (Page) xstream.fromXML(body);

        assertEquals(modifiedPage.getContent(), CONTENT);
        assertEquals(modifiedPage.getTitle(), TITLE);
        assertEquals(modifiedPage.getParent(), PARENT);
    }

    public void testPUTWithInvalidRepresentation() throws Exception
    {
        TestUtils.banner("testPUTWithInvalidRepresentation()");

        Page page = getPage();
        Link link = page.getFirstLinkByRelation(Relations.SELF);

        PutMethod putMethod = executePut(link.getHref(), Utils.getResourceAsString(TestConstants.INVALID_PAGE_XML));
        assertTrue(putMethod.getStatusCode() == HttpStatus.SC_NOT_ACCEPTABLE);
        TestUtils.printHttpMethodInfo(putMethod);
    }

    private void createPageIfDoesntExist(String spaceName, String pageName, String content) throws Exception
    {
        String uri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER, spaceName,
                Constants.PAGE_NAME_PARAMETER, pageName));

        GetMethod getMethod = executeGet(uri);
        TestUtils.printHttpMethodInfo(getMethod);

        if (getMethod.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            System.out.format("Page %s.%s doesn't exist... Creating it\n", spaceName, pageName);

            Page page = new Page();
            page.setContent(content);

            PutMethod putMethod = executePut(uri, page, "Admin", "admin");
            assertTrue(putMethod.getStatusCode() == HttpStatus.SC_CREATED);
            TestUtils.printHttpMethodInfo(putMethod);

            getMethod = executeGet(uri);
            assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
            TestUtils.printHttpMethodInfo(getMethod);
            System.out.format("Page %s.%s created.\n", spaceName, pageName);
        } else {
            System.out.format("Page %s.%s exists. Good!\n", spaceName, pageName);
        }
    }

    public void testPUTTranslation() throws Exception
    {
        final String languageId = String.format("%d", random.nextLong());

        TestUtils.banner("testPUTTranslation()");

        createPageIfDoesntExist(TestConstants.TEST_SPACE_NAME, TestConstants.TRANSLATIONS_PAGE_NAME, "Translations");

        Page page = new Page();
        page.setContent(languageId);

        PutMethod putMethod =
            executePut(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageTranslationResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER,
                TestConstants.TEST_SPACE_NAME, Constants.PAGE_NAME_PARAMETER, TestConstants.TRANSLATIONS_PAGE_NAME,
                Constants.LANGUAGE_ID_PARAMETER, languageId)), page, "Admin", "admin");
        assertTrue(putMethod.getStatusCode() == HttpStatus.SC_CREATED);
        TestUtils.printHttpMethodInfo(putMethod);

        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageTranslationResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER,
                TestConstants.TEST_SPACE_NAME, Constants.PAGE_NAME_PARAMETER, TestConstants.TRANSLATIONS_PAGE_NAME,
                Constants.LANGUAGE_ID_PARAMETER, languageId)));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Page modifiedPage = (Page) xstream.fromXML(getMethod.getResponseBodyAsString());
        assertTrue(languageId.equals(modifiedPage.getLanguage()));
        assertTrue(languageId.equals(modifiedPage.getLanguage()));
    }

    public void testGETTranslations() throws Exception
    {
        TestUtils.banner("testGETTranslations()");

        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER,
                TestConstants.TEST_SPACE_NAME, Constants.PAGE_NAME_PARAMETER, TestConstants.TRANSLATIONS_PAGE_NAME)));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Page page = (Page) xstream.fromXML(getMethod.getResponseBodyAsString());

        List<Link> translationLinks = page.getTranslations().getLinksByRelation(Relations.TRANSLATION);
        assertTrue(translationLinks.size() > 0);

        for (Link translationLink : translationLinks) {
            System.out.format("Translation link: %s\n", translationLink.getHref());
            getMethod = executeGet(translationLink.getHref());
            assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
            TestUtils.printHttpMethodInfo(getMethod);

            page = (Page) xstream.fromXML(getMethod.getResponseBodyAsString());

            assertTrue(page.getLanguage().equals(translationLink.getHrefLang()));
        }
    }

    public void testGETNotExistingTranslation() throws Exception
    {
        TestUtils.banner("testGETNotExistingTranslation()");

        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER,
                TestConstants.TEST_SPACE_NAME, Constants.PAGE_NAME_PARAMETER, TestConstants.TRANSLATIONS_PAGE_NAME)));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageTranslationResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER,
                TestConstants.TEST_SPACE_NAME, Constants.PAGE_NAME_PARAMETER, TestConstants.TRANSLATIONS_PAGE_NAME,
                Constants.LANGUAGE_ID_PARAMETER, "NOTEXISTING")));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_NOT_FOUND);
        TestUtils.printHttpMethodInfo(getMethod);
    }

    public void testDELETEPage() throws Exception
    {
        final String pageName = String.format("Test-%d", random.nextLong());

        TestUtils.banner("testDELETEPage()");

        createPageIfDoesntExist(TestConstants.TEST_SPACE_NAME, pageName, "Test page");

        DeleteMethod deleteMethod =
            executeDelete(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER,
                TestConstants.TEST_SPACE_NAME, Constants.PAGE_NAME_PARAMETER, pageName)));
        assertTrue(deleteMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(deleteMethod);

        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageResource.class),
                Constants.WIKI_NAME_PARAMETER, getWiki(), Constants.SPACE_NAME_PARAMETER,
                TestConstants.TEST_SPACE_NAME, Constants.PAGE_NAME_PARAMETER, pageName)));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_NOT_FOUND);
        TestUtils.printHttpMethodInfo(getMethod);
    }

}
