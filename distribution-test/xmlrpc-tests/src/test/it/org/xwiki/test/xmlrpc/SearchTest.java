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

import java.util.List;

import org.codehaus.swizzle.confluence.SearchResult;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

/**
 * @version $Id$
 */
public class SearchTest extends AbstractXWikiXmlRpcTest
{
    public void testSearch() throws Exception
    {
        List<SearchResult> result = rpc.search("a", 10);

        TestUtils.banner("TEST: search()");
        System.out.format("%s\n", result);

        assertFalse(result.isEmpty());
    }

    public void testSearchAllPageIds() throws Exception
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
