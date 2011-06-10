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

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.test.ui.administration.elements.GlobalRightsAdministrationSectionPage;
import org.xwiki.test.ui.framework.elements.CommentsTab;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.EditRightsPane.Right;
import org.xwiki.test.ui.framework.elements.EditRightsPane.State;
import org.xwiki.test.ui.framework.elements.ViewPage;

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

    @Override
    @Before
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage(SPACE_NAME, DOC_NAME);
        getUtil().createPage(SPACE_NAME, DOC_NAME, CONTENT, TITLE);
    }

    private void setRightsOnGuest(Right right, State state)
    {
        GlobalRightsAdministrationSectionPage globalRights = new GlobalRightsAdministrationSectionPage();
        globalRights.gotoPage();
        globalRights.getEditRightsPane().switchToUsers();
        globalRights.getEditRightsPane().setGuestRight(right, state);
    }

    @Test
    public void testPostCommentAsAnonymous()
    {
        setRightsOnGuest(Right.COMMENT, State.ALLOW);
        getUtil().forceGuestUser();

        ViewPage vp = getUtil().gotoPage(SPACE_NAME, DOC_NAME);
        CommentsTab commentsTab = vp.openCommentsDocExtraPane();

        commentsTab.postCommentAsGuest(COMMENT_CONTENT, COMMENT_AUTHOR, true);
        Assert.assertEquals(COMMENT_CONTENT, commentsTab.getCommentContentByID(0));
        Assert.assertEquals(COMMENT_AUTHOR, commentsTab.getCommentAuthorByID(0));
    }

    @Test
    public void testPostCommentAsAnonymousNoJs()
    {
        setRightsOnGuest(Right.COMMENT, State.ALLOW);
        getUtil().forceGuestUser();
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
        setRightsOnGuest(Right.COMMENT, State.ALLOW);
        getUtil().forceGuestUser();

        ViewPage vp = getUtil().gotoPage(SPACE_NAME, DOC_NAME);
        CommentsTab commentsTab = vp.openCommentsDocExtraPane();

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
        setRightsOnGuest(Right.COMMENT, State.ALLOW);
        getUtil().forceGuestUser();

        ViewPage vp = getUtil().gotoPage(SPACE_NAME, DOC_NAME);
        CommentsTab commentsTab = vp.openCommentsDocExtraPane();

        commentsTab.postCommentAsGuest(COMMENT_CONTENT, COMMENT_AUTHOR, true);
        List<WebElement> editButton;

        editButton =
            getDriver().findElements(
                By.xpath("//div[@id='xwikicomment_" + commentsTab.getCommentID(COMMENT_CONTENT)
                    + "']//a[@class='edit']"));

        Assert.assertEquals(0, editButton.size());
    }
}
