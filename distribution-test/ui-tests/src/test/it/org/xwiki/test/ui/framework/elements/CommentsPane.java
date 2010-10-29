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
package org.xwiki.test.ui.framework.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the actions possible on the History Pane at the bottom of a page.
 * 
 * @version $Id$
 * @since 2.4
 */
public class CommentsPane extends BaseElement
{
    @FindBy(id = "Commentspane")
    private WebElement pane;

    public int postComment(String comment)
    {
        By countLocator = By.cssSelector("#Commentstab .itemCount");
        int initialCount = Integer.parseInt(getDriver().findElement(countLocator).getText().replaceAll("[()]", ""));
        this.pane.findElement(By.id("XWiki.XWikiComments_comment")).sendKeys(comment);
        this.pane.findElement(By.cssSelector("input[type='submit']")).click();
        String waitingFor = "(" + (++initialCount) + ")";
        this.waitUntilElementHasTextContent(countLocator, waitingFor);
        List<WebElement> comments = this.pane.findElements(By.className("xwikicomment"));
        WebElement lastComment = comments.get(comments.size() - 1);
        return Integer.parseInt(lastComment.getAttribute("id").substring("xwikicomment_".length()));
    }

    public void deleteComment(int number)
    {
        this.pane.findElement(By.id("xwikicomment_" + number)).findElement(By.className("delete")).click();
        waitUntilElementIsVisible(By.className("xdialog-box-confirmation"));
        getDriver().findElement(By.cssSelector(".xdialog-box-confirmation input[value='Yes']")).click();
        waitUntilElementDisappears(By.id("xwikicomment_" + number));
    }
}
