package org.xwiki.rest.it;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.rest.Relations;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.SearchResult;
import org.xwiki.rest.model.jaxb.SearchResults;
import org.xwiki.rest.model.jaxb.Wiki;
import org.xwiki.rest.model.jaxb.Wikis;
import org.xwiki.rest.resources.wikis.WikiSearchResource;
import org.xwiki.rest.resources.wikis.WikisResource;

public class WikisResourceTest extends AbstractHttpTest
{
    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod = executeGet(getFullUri(WikisResource.class));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

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
            executeGet(String.format("%s?q=welcome", UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(
                WikiSearchResource.class).build(getWiki())));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        SearchResults searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertTrue(searchResults.getSearchResults().size() >= 1);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }

        getMethod =
            executeGet(String.format("%s?q=WebHome&scope=name", UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT)
                .path(WikiSearchResource.class).build(getWiki())));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertTrue(searchResults.getSearchResults().size() >= 3);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }

        getMethod =
            executeGet(String.format("%s?q=your&scope=title", UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT)
                .path(WikiSearchResource.class).build(getWiki())));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        searchResults = (SearchResults) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertTrue(searchResults.getSearchResults().size() >= 1);

        for (SearchResult searchResult : searchResults.getSearchResults()) {
            checkLinks(searchResult);
        }

    }

}
