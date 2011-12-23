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
package org.xwiki.test.ui;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.po.administration.GlobalRightsAdministrationSectionPage;
import org.xwiki.test.ui.po.CommentsTab;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.test.ui.po.EditRightsPane.Right;
import org.xwiki.test.ui.po.EditRightsPane.State;

/**
 * @version $Id$
 * @since 3.1M2
 */
public class CommentAsGuestTest extends AbstractAdminAuthenticatedTest
{
    private static final String SPACE_NAME = "TestSpace";

    private static final String DOC_NAME = "TestComments";

    private static final String CONTENT = "Some dummy Content";

    private static final String TITLE = "CommentsTest Page";

    private static final String COMMENT_CONTENT = "Some content";

    private static final String COMMENT_AUTHOR = "Anonymous";

    private static final String COMMENT_REPLY = "Comment Reply";

    private ViewPage vp;

    @Override
    @Before
    public void setUp()
    {
        super.setUp();

        // Ensure that guest user has comment permission
        setRightsOnGuest(Right.COMMENT, State.ALLOW);

        getUtil().deletePage(SPACE_NAME, DOC_NAME);
        this.vp = getUtil().createPage(SPACE_NAME, DOC_NAME, CONTENT, TITLE);

        // Set Guest user
        getUtil().forceGuestUser();

        // Important: we need to reload the page since forceGuestUser() simply removes the cookies and doesn't refresh
        // the page and we need it refreshed since there's an extra field for guest users when commenting (the user
        // name field).
        getDriver().navigate().refresh();
    }

    private void setRightsOnGuest(Right right, State state)
    {
        GlobalRightsAdministrationSectionPage globalRights = GlobalRightsAdministrationSectionPage.gotoPage();
        globalRights.getEditRightsPane().switchToUsers();
        globalRights.getEditRightsPane().setGuestRight(right, state);
    }

    @Test
    public void testPostCommentAsGuest()
    {
        CommentsTab commentsTab = this.vp.openCommentsDocExtraPane();

        commentsTab.postCommentAsGuest(COMMENT_CONTENT, COMMENT_AUTHOR, true);
        Assert.assertEquals(COMMENT_CONTENT, commentsTab.getCommentContentByID(0));
        Assert.assertEquals(COMMENT_AUTHOR, commentsTab.getCommentAuthorByID(0));
    }

    @Test
    public void testPostCommentAsGuestNoJs()
    {
        getUtil().gotoPage(SPACE_NAME, DOC_NAME, "view", "xpage=xpart&vm=commentsinline.vm");
        CommentsTab commentsTab = new CommentsTab();

        commentsTab.postComment(COMMENT_CONTENT, false);
        // This opens with ?viewer=comments, don't explicitly load the comments tab
        new ViewPage().waitUntilPageIsLoaded();
        Assert.assertEquals(COMMENT_CONTENT,
            commentsTab.getCommentContentByID(commentsTab.getCommentID(COMMENT_CONTENT)));
        Assert.assertEquals(COMMENT_AUTHOR,
            commentsTab.getCommentAuthorByID(commentsTab.getCommentID(COMMENT_CONTENT)));
    }

    @Test
    public void testReplyCommentAsAnonymous()
    {
        CommentsTab commentsTab = this.vp.openCommentsDocExtraPane();

        commentsTab.postCommentAsGuest(COMMENT_CONTENT, COMMENT_AUTHOR, true);
        commentsTab.replyToCommentByID(commentsTab.getCommentID(COMMENT_CONTENT), COMMENT_REPLY);
        Assert.assertEquals(COMMENT_REPLY,
            commentsTab.getCommentContentByID(commentsTab.getCommentID(COMMENT_REPLY)));
        Assert.assertEquals(COMMENT_AUTHOR,
            commentsTab.getCommentAuthorByID(commentsTab.getCommentID(COMMENT_REPLY)));
    }

    @Test
    public void testCannotEditCommentAsAnonymous()
    {
        CommentsTab commentsTab = this.vp.openCommentsDocExtraPane();
        commentsTab.postCommentAsGuest(COMMENT_CONTENT, COMMENT_AUTHOR, true);
        Assert.assertFalse(commentsTab.hasEditbuttonForCommentByID(commentsTab.getCommentID(COMMENT_CONTENT)));
    }
}
