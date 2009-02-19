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

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.xwiki.rest.Relations;
import org.xwiki.rest.model.jaxb.Comment;
import org.xwiki.rest.model.jaxb.Comments;
import org.xwiki.rest.model.jaxb.History;
import org.xwiki.rest.model.jaxb.HistorySummary;
import org.xwiki.rest.model.jaxb.Page;
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

        String commentsUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(CommentsResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME).toString();

        GetMethod getMethod = executeGet(commentsUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Comments comments = (Comments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        int numberOfComments = comments.getComments().size();

        Comment comment = objectFactory.createComment();
        comment.setText("Comment");

        PostMethod postMethod = executePostXml(commentsUri, comment, "Admin", "admin");
        assertEquals(HttpStatus.SC_CREATED, postMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(postMethod);

        getMethod = executeGet(commentsUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        comments = (Comments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        assertEquals(numberOfComments + 1, comments.getComments().size());
    }

    public void testGETComment() throws Exception
    {
        TestUtils.banner("testGetComment()");

        String commentsUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(CommentsResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME).toString();

        GetMethod getMethod = executeGet(commentsUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Comments comments = (Comments) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        for (Comment comment : comments.getComments()) {
            checkLinks(comment);
        }
    }

    public void testGETCommentsAtPreviousVersions() throws Exception
    {
        TestUtils.banner("testCommentsAtPreviousVersions");

        String pageHistoryUri =
            UriBuilder.fromUri(TestConstants.REST_API_ENTRYPOINT).path(PageHistoryResource.class).build(getWiki(),
                SPACE_NAME, PAGE_NAME).toString();

        GetMethod getMethod = executeGet(pageHistoryUri);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        History history = (History) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        for (HistorySummary historySummary : history.getHistorySummaries()) {
            getMethod = executeGet(getFirstLinkByRelation(historySummary, Relations.PAGE).getHref());
            assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
            TestUtils.printHttpMethodInfo(getMethod);

            Page page = (Page) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

            if (getFirstLinkByRelation(page, Relations.COMMENTS) != null) {
                getMethod = executeGet(getFirstLinkByRelation(page, Relations.COMMENTS).getHref());
                assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
                TestUtils.printHttpMethodInfo(getMethod);
            }
        }
    }

}
