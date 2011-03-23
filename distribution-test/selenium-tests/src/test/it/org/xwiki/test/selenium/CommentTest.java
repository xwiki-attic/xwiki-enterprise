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
package org.xwiki.test.selenium;

import junit.framework.Test;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

/**
 * Tries to post comments on a page as various users and with various rights.
 * 
 * @version $Id$
 */
public class CommentTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tries to post comments on a page.");
        suite.addTestSuite(CommentTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        // We start at localhost:4444/selenium-server... and isExistingPage reloads current location
        // so navigate to test page.
        if (getSelenium().getLocation().indexOf(getUrl("CommentTest", "PageWithSomeComments")) == -1) {
            open("CommentTest", "PageWithSomeComments");
            if (getSelenium().getLocation().indexOf("xpage=xpart&vm=commentsinline.vm") == -1
                && !isExistingPage("CommentTest", "PageWithSomeComments")) {
                loginAsAdmin();
                createPage("CommentTest", "PageWithSomeComments",
                    "This page is here to test comment functionality.", "xwiki/2.0");
                logout();
            }
        }
    }

    /* The Tests */

    public void testPostCommentAsAdmin()
    {
        // in this test class, the only user who logs in is admin.
        if (!isAuthenticated()) {
            loginAsAdmin();
        }
        postComment("This is the first comment by Administrator.", null, true);
    }

    public void testPostCommentAsAnonymous()
    {
        if (isAuthenticated()) {
            logout();
        }
        allowAnonymousCommenting();
        postComment("This is the first comment by Anonymous.", "John Anonymous Smith", true);
    }

    public void testReplyCommentAsAnonymous()
    {
        if (isAuthenticated()) {
            logout();
        }
        // If there is a reply, recreate the page to get rid of it.
        if (isElementPresent("//li[@class='reply']/div/ul/li[@class='reply']/div/div[@class='commentcontent']/p")) {
            loginAsAdmin();
            createPage("CommentTest", "PageWithSomeComments",
                "This page is here to test comment functionality.", "xwiki/2.0");
            logout();
        }
        allowAnonymousCommenting();
        if (!isElementPresent("//div[@class='commentcontent']/p")) {
            // Prepare a comment to reply to
            postComment("This is a comment for Anonymous to reply to.", null, false);
        }
        // Double check no replies.
        assertElementNotPresent("//li[@class='reply']/div/ul/li[@class='reply']/div/div[@class='commentcontent']/p");
        clickLinkWithLocator("//a[@class='commentreply']", false);
        postComment("This is a reply by Anonymous.", null, true);
        assertElementPresent("//li[@class='reply']/div/ul/li[@class='reply']/div/div[@class='commentcontent']/p");
    }

    public void testCannotEditCommentAsAnonymous()
    {
        if (isAuthenticated()) {
            logout();
        }
        allowAnonymousCommenting();
        // Prepare a comment to try to edit.
        postComment("This comment should not be able to be edited by Anonymous.", null, false);
        assertElementNotPresent("//a[@class='edit']");
    }

    public void testReplyCommentAsAdmin()
    {
        // in this test class, the only user who logs in is admin.
        if (!isAuthenticated()) {
            loginAsAdmin();
        }
        open("CommentTest", "PageWithSomeComments");
        loadCommentsTab();

        // Wait until comments load though ajax mechanism.
        waitForElement("//div[@id='_comments']/form[@id='AddComment']/fieldset[@id='commentform']");

        // If there is a reply, recreate the page to get rid of it.
        if (isElementPresent("//li[@class='reply']/div/ul/li[@class='reply']/div/div[@class='commentcontent']/p")) {
            // LoginAsAdmin returns before the page is completely loaded so put it below isElementPresent
            createPage("CommentTest", "PageWithSomeComments",
                "This page is here to test comment functionality.", "xwiki/2.0");
        }
        if (!isElementPresent("//div[@class='commentcontent']/p")) {
            // Prepare a comment to reply to
            postComment("This is a comment for Admin to reply to.", null, true);
        }
        // Double check no replies.
        assertElementNotPresent("//li[@class='reply']/div/ul/li[@class='reply']/div/div[@class='commentcontent']/p");
        clickLinkWithLocator("//a[@class='commentreply']", false);
        postComment("This is a reply by Admin.", null, true);
        waitForElement("//li[@class='reply']/div/ul/li[@class='reply']/div/div[@class='commentcontent']/p");
    }

    public void testAdminCanEditComment()
    {
        // in this test class, the only user who logs in is admin.
        if (!isAuthenticated()) {
            loginAsAdmin();
        }
        loadCommentsTab();
        if (!isElementPresent("//div[@class='commentheader']/div/span[@class='commentauthor']/span/a")) {
            postComment("This comment will be edited.", null, true);
        }
        clickLinkWithLocator("//a[@class='edit']", false);
        waitForElement("//li/form[contains(@class,'edit-xcomment')]//textarea");
        setFieldValue("//li/form[contains(@class,'edit-xcomment')]//textarea", "This comment has been edited.");
        clickLinkWithLocator("//form[contains(@class,'edit-xcomment')]//input[@name='action_save']", false);
        waitForTextPresent("//div[@class='commentcontent']/p", "This comment has been edited.");
    }

    public void testPostCommentAsAdminNoJs()
    {
        // in this test class, the only user who logs in is admin.
        if (!isAuthenticated()) {
            loginAsAdmin();
        }
        open("CommentTest", "PageWithSomeComments", "view", "xpage=xpart&vm=commentsinline.vm");
        postComment("This is the first comment by Administrator. With no js", null, true);
    }

    public void testPostCommentAsAnonymousNoJs()
    {
        if (isAuthenticated()) {
            logout();
        }
        allowAnonymousCommenting();
        open("CommentTest", "PageWithSomeComments", "view", "xpage=xpart&vm=commentsinline.vm");
        postComment("This is the first comment by Anonymous. With no js", "John Anonymous Smith", true);
    }

    /* Helper Methods */

    public void allowAnonymousCommenting()
    {
        if (!isCommentFormShown()) {
            loginAsAdmin();
            open("CommentTest", "PageWithSomeComments", "edit", "editor=rights");

            // So we can see what's going on...
            checkField("//*[@id='uorgu']");

            if (isElementPresent("//tr[@id='unregistered']/td[@id='tdcomment']/img"
                + "[@src='/xwiki/resources/js/xwiki/usersandgroups/img/none.png']")) {
                getSelenium().click("//tr[@id='unregistered']/td[@id='tdcomment']/img");
            } else if (isElementPresent("//tr[@id='unregistered']/td[@id='tdcomment']/img"
                + "[@src='/xwiki/resources/js/xwiki/usersandgroups/img/deny.png']")) {
                getSelenium().click("//tr[@id='unregistered']/td[@id='tdcomment']/img");
                waitForElement("//tr[@id='unregistered']/td[@id='tdcomment']/img"
                    + "[@src='/xwiki/resources/js/xwiki/usersandgroups/img/none.png']");
                getSelenium().click("//tr[@id='unregistered']/td[@id='tdcomment']/img");
            }
            waitForElement("//tr[@id='unregistered']/td[@id='tdcomment']/img"
                + "[@src='/xwiki/resources/js/xwiki/usersandgroups/img/allow.png']");
            open("CommentTest", "PageWithSomeComments");
            logout();
        }
    }

    public void postComment(String comment, String author, boolean doubleCheck)
    {
        loadCommentsTab();
        setCommentContent(comment);
        if (author != null) {
            setCommentAuthor(author);
        }
        clickPost();
        if (doubleCheck) {
            waitForElement("//li[@class='reply']/div/div[@class='commentcontent']/p");
            assertTextPresent(comment);
            if (author != null) {
                assertTextPresent(author);
            }
        }
    }

    public void setCommentAuthor(String author)
    {
        waitForElement("//input[@name='XWiki.XWikiComments_author']");
        getSelenium().fireEvent("//input[@name='XWiki.XWikiComments_author']", "focus");
        setFieldValue("//input[@name='XWiki.XWikiComments_author']", author);
    }

    public String getCommentAuthor()
    {
        return getSelenium().getAttribute("//input[@name='XWiki.XWikiComments_author']@value");
    }

    public void loadCommentsTab()
    {
        if (isElementPresent("//a[@id='Commentslink']")) {
            clickLinkWithXPath("//a[@id='Commentslink']", false);
            waitForCondition("selenium.browserbot.findElement(\"Commentspane\").className.indexOf(\"empty\") == -1");
        }
    }

    public void setCommentContent(String content)
    {
        waitForElement("//textarea[@id='XWiki.XWikiComments_comment']");
        getSelenium().fireEvent("//textarea[@id='XWiki.XWikiComments_comment']", "focus");
        setFieldValue("//textarea[@id='XWiki.XWikiComments_comment']", content);
    }

    public void clickPost()
    {
        if (getSelenium().getLocation().indexOf("xpage=xpart&vm=commentsinline.vm") != -1) {
            submit("//input[@type='submit'][@value='Add comment']");
        } else {
            String numComments = getSelenium().getText("//a[@id='Commentslink']/span");
            submit("//input[@type='submit'][@value='Add comment']", false);
            waitForCondition("window.document.getElementById('Commentslink').childNodes[1].childNodes[0].data != '"
                + numComments + "'");

        }
    }

    public boolean isCommentFormShown()
    {
        return isElementPresent("//form[@id='AddComment']/fieldset[@id='commentform']");
    }
}
