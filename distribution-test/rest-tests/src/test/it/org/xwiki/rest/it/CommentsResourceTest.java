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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.xwiki.rest.Constants;
import org.xwiki.rest.Utils;
import org.xwiki.rest.model.Comment;
import org.xwiki.rest.model.Comments;
import org.xwiki.rest.model.History;
import org.xwiki.rest.model.HistorySummary;
import org.xwiki.rest.model.Page;
import org.xwiki.rest.model.Relations;
import org.xwiki.rest.resources.comments.CommentsResource;
import org.xwiki.rest.resources.pages.PageHistoryResource;

public class CommentsResourceTest extends AbstractHttpTest
{
    private final String SPACE_NAME = "Main";

    private final String PAGE_NAME = "WebHome";

    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");
        /* Everything is done in test methods */
    }

    public void testPOSTComment() throws Exception
    {
        TestUtils.banner("testCreateComments()");

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);

        String commentsUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(CommentsResource.class), parametersMap));

        GetMethod getMethod = executeGet(commentsUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Comments comments = (Comments) xstream.fromXML(getMethod.getResponseBodyAsString());

        int numberOfComments = 0;
        if (comments.getCommentList() != null) {
            numberOfComments = comments.getCommentList().size();
        }

        Comment comment = new Comment();
        comment.setText("Comment");

        PostMethod postMethod = executePost(commentsUri, comment, "Admin", "admin");
        assertEquals(HttpStatus.SC_CREATED, postMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(postMethod);

        getMethod = executeGet(commentsUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        comments = (Comments) xstream.fromXML(getMethod.getResponseBodyAsString());

        assertEquals(numberOfComments + 1, comments.getCommentList().size());
    }

    public void testGETComment() throws Exception
    {
        TestUtils.banner("testGetComment()");

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);

        String commentsUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(CommentsResource.class), parametersMap));

        GetMethod getMethod = executeGet(commentsUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Comments comments = (Comments) xstream.fromXML(getMethod.getResponseBodyAsString());

        if (comments.getCommentList() != null) {
            for (Comment comment : comments.getCommentList()) {
                checkLinks(comment);
            }
        }
    }

    public void testGETCommentsAtPreviousVersions() throws Exception
    {
        TestUtils.banner("testCommentsAtPreviousVersions");

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);

        String pageHistoryUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(PageHistoryResource.class), parametersMap));

        GetMethod getMethod = executeGet(pageHistoryUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        History history = (History) xstream.fromXML(getMethod.getResponseBodyAsString());

        for (HistorySummary historySummary : history.getHistorySummaryList()) {
            getMethod = executeGet(historySummary.getFirstLinkByRelation(Relations.PAGE).getHref());
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
            TestUtils.printHttpMethodInfo(getMethod);

            Page page = (Page) xstream.fromXML(getMethod.getResponseBodyAsString());
            getMethod = executeGet(page.getFirstLinkByRelation(Relations.COMMENTS).getHref());
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
            TestUtils.printHttpMethodInfo(getMethod);
        }

    }

}
