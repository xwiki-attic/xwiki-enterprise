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
package org.xwiki.test.xmlrpc.confluence;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.swizzle.confluence.Comment;
import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.Space;
import org.xwiki.test.xmlrpc.confluence.framework.AbstractXmlRpcTestCase;

public class CommentsTest extends AbstractXmlRpcTestCase
{
    private String spaceKey;

    private String pageTitle;

    private String pageId;

    public void setUp() throws Exception
    {
        super.setUp();

        spaceKey = "SomeContainerSpace";
        Space space = new Space();
        space.setKey(spaceKey);
        space.setName("Some Name");

        try {
            rpc.addSpace(space);
        } catch (Exception e) {
            // Now we throw exception if the space already exists
        }

        pageTitle = "SomeContainerPage";
        Page p = new Page();
        p.setSpace(spaceKey);
        p.setTitle(pageTitle);
        p.setContent("");
        // no id in pageProperties means storePage will add
        Page resultPage = rpc.storePage(p);
        pageId = resultPage.getId();

        // add and remove one comment (makes test more realistic)
        Comment comment = new Comment();
        comment.setPageId(pageId);
        comment.setContent("Dummy Comment");
        Comment commentResult = rpc.addComment(comment);
        rpc.removeComment(commentResult.getId());
    }

    public void tearDown() throws Exception
    {
        rpc.removePage(pageId);
        rpc.removeSpace(spaceKey);

        super.tearDown();
    }

    public void testAddGetComments() throws Exception
    {
        // first check that the page has no comments
        assertEquals(0, rpc.getComments(pageId).size());

        // then add some comments
        Comment comment = new Comment();
        comment.setPageId(pageId);
        comment.setContent("Comment1");
        Comment c1 = rpc.addComment(comment);
        assertNotNull(c1.getId());
        assertEquals(pageId, c1.getPageId());
        assertEquals("Comment1", c1.getContent());
        assertNotNull(c1.getUrl());
        comment.setContent("Comment2");
        Comment c2 = rpc.addComment(comment);
        assertNotNull(c2.getId());
        assertEquals(pageId, c2.getPageId());
        assertEquals("Comment2", c2.getContent());
        assertNotNull(c2.getUrl());

        // check that the page has the comments
        assertEquals(2, rpc.getComments(pageId).size());
        Comment c11 = rpc.getComment(c1.getId());
        assertEquals(c1.getId(), c11.getId());
        assertEquals(c1.getTitle(), c11.getTitle());
        assertEquals(c1.getPageId(), c11.getPageId());
        assertEquals(c1.getContent(), c11.getContent());
        assertEquals(c1.getCreated(), c11.getCreated());
        assertEquals(c1.getCreator(), c11.getCreator());
        assertURLEquals(c1.getUrl(), c11.getUrl());
        Comment c22 = rpc.getComment(c2.getId());
        assertEquals(c2.getId(), c22.getId());
        assertEquals(c2.getTitle(), c22.getTitle());
        assertEquals(c2.getPageId(), c22.getPageId());
        assertEquals(c2.getContent(), c22.getContent());
        assertEquals(c2.getCreated(), c22.getCreated());
        assertEquals(c2.getCreator(), c22.getCreator());
        assertURLEquals(c2.getUrl(), c22.getUrl());

        // delete 1st comment
        assertTrue(rpc.removeComment(c1.getId()));
        // check that 1st comment is still there
        assertEquals(1, rpc.getComments(pageId).size());
        assertNotNull(c2.getId());
        assertEquals(pageId, c2.getPageId());
        assertEquals("Comment2", c2.getContent());
        assertNotNull(c2.getUrl());
        // delete 2nd comment
        assertTrue(rpc.removeComment(c2.getId()));
        assertEquals(0, rpc.getComments(pageId).size());
    }

    /**
     * Compare two URLs, discarding any jsession id information in the URL.
     */
    private void assertURLEquals(String expectedURL, String url)
    {
        assertEquals(StringUtils.substringBefore(expectedURL, ";jsessionid"),
            StringUtils.substringBefore(url, ";jsessionid"));
    }
}
