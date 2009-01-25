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
import org.xwiki.rest.resources.comments.CommentsResource;

public class CommentsResourceTest extends AbstractHttpTest
{
    private final String SPACE_NAME = "Main";

    private final String PAGE_NAME = "WebHome";

    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");
        /* Everything is done in testCreateComments() */
    }

    public void testCreateComments() throws Exception
    {
        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);

        String commentsUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(CommentsResource.class), parametersMap));

        GetMethod getMethod = executeGet(commentsUri);
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Comments comments = (Comments) xstream.fromXML(getMethod.getResponseBodyAsString());

        int numberOfComments = 0;
        if (comments.getCommentList() != null) {
            numberOfComments = comments.getCommentList().size();
        }

        Comment comment = new Comment();
        comment.setText("Comment");

        PostMethod postMethod = executePost(commentsUri, comment, "Admin", "admin");
        assertTrue(postMethod.getStatusCode() == HttpStatus.SC_CREATED);
        TestUtils.printHttpMethodInfo(postMethod);

        getMethod = executeGet(commentsUri);
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        comments = (Comments) xstream.fromXML(getMethod.getResponseBodyAsString());

        assertTrue(comments.getCommentList().size() == numberOfComments + 1);
    }

    public void testGetComment() throws Exception
    {
        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());
        parametersMap.put(Constants.SPACE_NAME_PARAMETER, SPACE_NAME);
        parametersMap.put(Constants.PAGE_NAME_PARAMETER, PAGE_NAME);

        String commentsUri =
            getFullUri(Utils.formatUriTemplate(getUriPatternForResource(CommentsResource.class), parametersMap));

        GetMethod getMethod = executeGet(commentsUri);
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        Comments comments = (Comments) xstream.fromXML(getMethod.getResponseBodyAsString());

        if (comments.getCommentList() != null) {
            for (Comment comment : comments.getCommentList()) {
                checkLinks(comments);
            }
        }
    }

}
