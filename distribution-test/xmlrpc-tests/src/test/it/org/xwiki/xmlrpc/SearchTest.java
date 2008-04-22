package org.xwiki.xmlrpc;

import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.SearchResult;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

public class SearchTest extends AbstractXWikiXmlRpcTest
{
    public void testSearch() throws XmlRpcException
    {
        List<SearchResult> result = rpc.search("a", 10);

        TestUtils.banner("TEST: search()");
        System.out.format("%s\n", result);

        assertFalse(result.isEmpty());
    }

    public void testSearchAllPageIds() throws XmlRpcException
    {
        List<SearchResult> result = rpc.searchAllPagesIds();

        TestUtils.banner("TEST: searchAllPageIds()");
        System.out.format("%s\n", result);

        List<SpaceSummary> spaces = rpc.getSpaces();
        for (SpaceSummary spaceSummary : spaces) {
            List<XWikiPageSummary> pages = rpc.getPages(spaceSummary);
            for (XWikiPageSummary page : pages) {
                assertTrue(checkMembership(page.getId(), result));
            }
        }
    }

    private boolean checkMembership(String pageId, List<SearchResult> searchResults)
    {
        for (SearchResult searchResult : searchResults) {
            if (searchResult.getId().equals(pageId)) {
                return true;
            }
        }

        System.out.format("%s not in result\n", pageId);
        return false;
    }
}
