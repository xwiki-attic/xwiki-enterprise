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
 *
 */
package org.xwiki.xmlrpc;

import java.util.List;

import org.codehaus.swizzle.confluence.Comment;
import org.xwiki.xmlrpc.model.XWikiPage;

/**
 * @version $Id$
 */
public class CommentsTest extends AbstractXWikiXmlRpcTest
{
    public void setUp() throws Exception
    {
        super.setUp();

        try {
            rpc.getPage(TestConstants.TEST_PAGE_WITH_COMMENTS);
        } catch (Exception e) {
            XWikiPage page = new XWikiPage();
            page.setId(TestConstants.TEST_PAGE_WITH_COMMENTS);
            page.setTitle("Test page");
            page.setContent("Test page");
            rpc.storePage(page);
        }
    }

    public void testAddComment() throws Exception
    {
        String content = String.format("This is a new comment!!! %s", random.nextInt());
        Comment comment = new Comment();
        comment.setPageId(TestConstants.TEST_PAGE_WITH_COMMENTS);
        comment.setContent(content);

        comment = rpc.addComment(comment);
        TestUtils.banner("TEST: getComments()");
        System.out.format("%s\n", comment);

        assertNotSame("NEW", comment.getId());
        assertEquals(content, comment.getContent());
        assertEquals(String.format("XWiki.%s", TestConstants.USERNAME), comment.getCreator());
    }

    public void testGetComments() throws Exception
    {
        List<Comment> comments = rpc.getComments(TestConstants.TEST_PAGE_WITH_COMMENTS);

        TestUtils.banner("TEST: getComments()");
        for (Comment comment : comments) {
            System.out.format("%s\n", comment);
        }

        assertFalse(comments.isEmpty());
    }

    public void testRemoveComment() throws Exception
    {
        List<Comment> comments = rpc.getComments(TestConstants.TEST_PAGE_WITH_COMMENTS);
        Comment comment = comments.get(random.nextInt(comments.size()));

        Boolean result = rpc.removeComment(comment.getId());
        TestUtils.banner("TEST: removeComment()");
        System.out.format("Comment removed = %b\n", result);

        comments = rpc.getComments(TestConstants.TEST_PAGE_WITH_COMMENTS);
        boolean found = false;
        for (Comment c : comments) {
            if (c.getId().equals(comment.getId())) {
                found = true;
                break;
            }
        }

        assertFalse(found);
    }
}
