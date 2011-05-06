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
package org.xwiki.test.ui.comments.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Page Object for Comments Tab.
 * 
 * @version $Id$
 * @since 3.1M2
 */
public class CommentsTab extends ViewPage
{

    @FindBy(xpath = "//input[@value='Add comment']")
    private WebElement buttonAddComment;

    @FindBy(xpath = "//input[@value='Save comment']")
    private WebElement buttonSaveComment;

    @FindBy(xpath = "//fieldset[@id='commentform']/label/span")
    private WebElement commentAuthor;

    @FindBy(id = "XWiki.XWikiComments_comment")
    private WebElement commentTextArea;

    @FindBy(id = "XWiki.XWikiComments_author")
    private WebElement setCommentAuthor;

    @FindBy(id = "Commentstab")
    private WebElement commentsTab;

    @FindBy(id = "Commentspane")
    private WebElement pane;

    CommentDeleteConfirmationModal confirmDelete;

    List<WebElement> commentsList;

    public void allowAnonymousCommenting()
    {
        /*
         * This is a little bit hard-core allowAnonymousCommenting()
         */

    }

    public void clickAddComment()
    {
        this.buttonAddComment.click();
    }

    public void clickSaveComment()
    {
        this.buttonSaveComment.click();
    }

    public String getCommentAuthor()
    {
        return commentAuthor.getValue();
    }

    public boolean isCommentFormShown()
    {

        RenderedWebElement commentForm =
            (RenderedWebElement) getDriver().findElement(
                By.xpath("//form[@id='AddComment']/fieldset[@id='commentform']"));
        return commentForm.isDisplayed();
    }

    public void loadCommentsTab()
    {
        this.commentsTab.click();
    }

    public void setCommentContent(String content)
    {
        this.commentTextArea.sendKeys(content);
    }

    public int getCommentID(String content)
    {
        commentsList = getDriver().findElements(By.className("reply"));
        WebElement comment;
        for (int i = 0; i < commentsList.size(); i++) {
            comment = commentsList.get(i);
            if (comment.findElement(By.xpath("//div[@class='commentcontent']")).getText().equals(content))
                return Integer.parseInt(comment.findElement(By.className("xwikicomment")).getAttribute("id")
                    .substring("xwikicomment_".length()));
        }
        return -1;
    }

    public int postComment(String content, boolean validation)
    {
        this.setCommentContent(content);
        this.clickAddComment();

        if (validation) {
            waitUntilElementIsVisible(By
                .xpath("//div[contains(@class,'xnotification-done') and text()='Comment posted']"));
            getDriver().findElement(
                By.xpath("//div[contains(@class,'xnotification-done') and text()='Comment posted']")).click();
            waitUntilElementIsVisible(By.xpath("//div[@id='commentscontent']"));
            waitUntilElementIsVisible(By.xpath("//div[@class='commentcontent']/p[contains(text(),'" + content + "')]"));
        }
        System.out.println("INDEX=  " + this.getCommentID(content));
        return this.getCommentID(content);

    }

    public void setCommentAuthor(String author)
    {
        this.setCommentAuthor.clear();
        this.setCommentAuthor(author);
    }

    public void deleteCommentByID(int id)
    {
        commentsList = getDriver().findElements(By.className("reply"));
        commentsList.get(id).findElement(By.className("delete")).click();
        confirmDelete = new CommentDeleteConfirmationModal();
        confirmDelete.clickOk();
        waitUntilElementIsVisible(By.xpath("//div[contains(@class,'xnotification-done') and text()='Comment deleted']"));
        getDriver().findElement(By.xpath("//div[contains(@class,'xnotification-done') and text()='Comment deleted']"))
            .click();
    }

    public void replyToCommentByID(int id, String replyContent)
    {
        commentsList = getDriver().findElements(By.className("reply"));
        commentsList.get(id).findElement(By.cssSelector("a.commentreply")).click();
        getDriver().findElement(By.id("XWiki.XWikiComments_comment")).sendKeys(replyContent);
        this.clickAddComment();
        waitUntilElementIsVisible(By.xpath("//div[contains(@class,'xnotification-done') and text()='Comment posted']"));
        getDriver().findElement(By.xpath("//div[contains(@class,'xnotification-done') and text()='Comment posted']"))
            .click();

    }

    public void editCommentByID(int id, String content)
    {
        commentsList = getDriver().findElements(By.className("reply"));
        commentsList.get(id).findElement(By.className("edit")).click();
        waitUntilElementIsVisible(By.id("XWiki.XWikiComments_" + id + "_comment"));
        getDriver().findElement(By.id("XWiki.XWikiComments_" + id + "_comment")).clear();
        getDriver().findElement(By.id("XWiki.XWikiComments_" + id + "_comment")).sendKeys(content);
        this.clickSaveComment();
        waitUntilElementIsVisible(By.xpath("//div[contains(@class,'xnotification-done') and text()='Comment posted']"));
        getDriver().findElement(By.xpath("//div[contains(@class,'xnotification-done') and text()='Comment posted']"))
            .click();
        waitUntilElementIsVisible(By.xpath("//div[@id='commentscontent']"));
        waitUntilElementIsVisible(By.xpath("//div[@class='commentcontent']/p[contains(text(),'" + content + "')]"));
    }

    public String getCommentAuthorByID(int id)
    {
        commentsList = getDriver().findElements(By.className("reply"));
        return commentsList.get(id).findElement(By.xpath("//span[@class='commentauthor']//a")).getText();
    }

    public String getCommentContentByID(int id)
    {
        commentsList = getDriver().findElements(By.className("reply"));
        return commentsList.get(id).findElement(By.xpath("//div[@class='commentcontent']/p")).getText();
    }

}
