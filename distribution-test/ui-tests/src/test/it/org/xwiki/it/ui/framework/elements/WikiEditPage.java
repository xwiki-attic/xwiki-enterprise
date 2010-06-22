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
package org.xwiki.it.ui.framework.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the common actions possible on all Pages when using the "edit" action with "wiki" editor
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class WikiEditPage extends EditPage
{
    @FindBy(id = "xwikidoctitleinput")
    private WebElement titleInput;

    @FindBy(id = "content")
    private WebElement contentText;

    @FindBy(name = "minorEdit")
    private WebElement minorEditCheckBox;

    @FindBy(name = "comment")
    private WebElement commentInput;
    
    /**
     * Get the <code>title</code> of the page
     * 
     */
    public String getTitle()
    {
        return titleInput.getValue();
    }

    /**
     * Set the <code>title</code> of the page
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        titleInput.clear();
        titleInput.sendKeys(title);
    }
    
    /**
     * Get the <code>content</code> of the page
     * 
     */
    public String getContent()
    {
        return contentText.getText();
    }

    /**
     * Set the <code>content</code> of the page
     * 
     * @param content
     */
    public void setContent(String content)
    {
        contentText.clear();
        contentText.sendKeys(content);
    }

    /**
     * Set the minor edit check box value
     * 
     * @param value
     */
    public void setMinorEdit(boolean value)
    {
        if (minorEditCheckBox.isSelected() != value)
            minorEditCheckBox.toggle();
    }

    /**
     * Set <code>comment</code> for this change
     * 
     * @param comment
     */
    public void setEditComment(String comment)
    {
        commentInput.clear();
        commentInput.sendKeys(comment);
    }

    /**
     * Start editing page, create first if needed
     * 
     * @param space
     * @param page
     */
    public void switchToEdit(String space, String page)
    {
        getUtil().gotoPage(space, page, "edit", "editor=wiki");
    }
}
