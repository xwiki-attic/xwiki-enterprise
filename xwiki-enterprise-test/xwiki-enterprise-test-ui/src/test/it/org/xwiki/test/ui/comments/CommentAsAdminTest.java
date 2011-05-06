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
package org.xwiki.test.ui.comments;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.comments.elements.CommentsTab;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Test comment and reply on XWiki Pages when logged as Administrator.
 * 
 * @version $Id$
 * @since 3.1M2
 */
public class CommentAsAdminTest extends AbstractAdminAuthenticatedTest
{
    private CommentsTab commentsTab;

    private static final String SPACE_NAME = "TestSpace";

    private static final String DOC_NAME = "TestComments";

    private static final String CONTENT = "Some dummy Content";

    private static final String TITLE = "CommentsTest Page";

    private static final String COMMENT_CONTENT = "Some content";

    private static final String COMMENT_REPLACED_CONTENT = "Some replaced content";

    private static final String ADMIN = "Administrator";

    private static final String COMMENT_REPLY = "Comment Reply";

    @Override
    @Before
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage(SPACE_NAME, DOC_NAME);
        getUtil().createPage(SPACE_NAME, DOC_NAME, CONTENT, TITLE);
        getUtil().gotoPage(SPACE_NAME, DOC_NAME);
        this.commentsTab = new CommentsTab();
        this.commentsTab.loadCommentsTab();
    }

    @Test
    public void testPostCommentAsAdmin()
    {
        Assert.assertTrue(this.commentsTab.isCommentFormShown());
        this.commentsTab.postComment(COMMENT_CONTENT, true);
        Assert.assertEquals(COMMENT_CONTENT, this.commentsTab.getCommentContentByID(0));
        Assert.assertEquals(ADMIN, this.commentsTab.getCommentAuthorByID(0));
    }

    @Test
    public void testReplyToCommentAsAdmin()
    {
        this.commentsTab.postComment(COMMENT_CONTENT, true);
        this.commentsTab.replyToCommentByID(0, COMMENT_REPLY);
    }

    @Test
    public void testDeleteCommentAsAdmin()
    {
        Assert.assertTrue(this.commentsTab.isCommentFormShown());
        this.commentsTab.postComment(COMMENT_CONTENT, true);
        this.commentsTab.deleteCommentByID(0);
    }

    @Test
    public void testEditCommentAsAdmin()
    {
        Assert.assertTrue(this.commentsTab.isCommentFormShown());
        this.commentsTab.postComment(COMMENT_CONTENT, true);
        this.commentsTab.editCommentByID(0, COMMENT_REPLACED_CONTENT);
        Assert.assertEquals(COMMENT_REPLACED_CONTENT, this.commentsTab.getCommentContentByID(0));
    }

    @Test
    public void testPostCommentAsAdminNoJs()
    {
        // In this test class, the only user who logs in is admin.
        getUtil().gotoPage(SPACE_NAME, DOC_NAME, "view", "xpage=xpart&vm=commentsinline.vm");
        this.commentsTab.postComment(COMMENT_CONTENT, false);
        ViewPage vp = new ViewPage();
        // This opens with ?viewer=comments, don't explicitly load the comments tab
        vp.waitUntilPageIsLoaded();
        Assert.assertEquals(COMMENT_CONTENT, this.commentsTab.getCommentContentByID(0));
        Assert.assertEquals(ADMIN, this.commentsTab.getCommentAuthorByID(0));
    }
}
