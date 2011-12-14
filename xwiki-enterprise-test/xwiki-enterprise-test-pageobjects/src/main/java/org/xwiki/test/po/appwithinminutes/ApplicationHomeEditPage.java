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
package org.xwiki.test.po.appwithinminutes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.xwiki.test.po.platform.InlinePage;
import org.xwiki.test.po.platform.ViewPage;
import org.xwiki.test.po.platform.editor.wysiwyg.EditorElement;
import org.xwiki.test.po.platform.editor.wysiwyg.RichTextAreaElement;

/**
 * Represents the actions available when editing the application home page. This is also the third step of the App
 * Within Minutes wizard, in which the presentation of the application home page is customized.
 * 
 * @version $Id$
 * @since 3.3
 */
public class ApplicationHomeEditPage extends InlinePage
{
    @FindBy(xpath = "//a[. = 'Previous Step']")
    private WebElement previousStepButton;

    @FindBy(id = "wizard-next")
    private WebElement finishButton;

    /**
     * The form used to edit the application home page overwrites the save button because it needs to process the
     * submitted data. Otherwise the request is forwarded by the action filter to the save action.
     */
    @FindBy(name = "xaction_save")
    private WebElement saveButton;

    @FindBy(id = "availableColumns")
    private WebElement availableColumns;

    @FindBy(xpath = "//div[@class = 'columnPicker']/input[@type = 'image' and @alt = 'Add']")
    private WebElement addColumnButton;

    @FindBy(id = "inline")
    private WebElement form;

    /**
     * The WYSIWYG editor used to input the application description.
     */
    private final EditorElement descriptionEditor = new EditorElement("AppWithinMinutes.LiveTableClass_0_description");

    /**
     * Clicks on the Previous Step button.
     * 
     * @return the page that represents the previous step of the App Within Minutes wizard
     */
    public ApplicationClassEditPage clickPreviousStep()
    {
        previousStepButton.click();
        return new ApplicationClassEditPage();
    }

    /**
     * Clicks on the Finish button.
     * 
     * @return the page that represents the application home page
     */
    public ApplicationHomePage clickFinish()
    {
        finishButton.click();
        return new ApplicationHomePage();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewPage> T clickSaveAndView()
    {
        saveButton.click();
        return (T) new ApplicationHomePage();
    }

    /**
     * Sets the application description.
     * 
     * @param description the new application description
     */
    public void setDescription(String description)
    {
        descriptionEditor.waitToLoad();
        RichTextAreaElement descriptionTextArea = descriptionEditor.getRichTextArea();
        descriptionTextArea.clear();
        descriptionTextArea.sendKeys(description);
    }

    /**
     * Adds a new live table column.
     * 
     * @param columnLabel the label of the live table column to be added
     */
    public void addLiveTableColumn(String columnLabel)
    {
        Select select = new Select(availableColumns);
        select.selectByVisibleText(columnLabel);
        addColumnButton.click();
    }

    /**
     * Removes the live table column with the specified label.
     * 
     * @param columnLabel the label of the live table column to be removed
     */
    public void removeLiveTableColumn(String columnLabel)
    {
        String escapedColumnLabel = columnLabel.replace("\\", "\\\\").replace("'", "\\'");
        String xpath = "//ul[@class = 'hList']/li[starts-with(., '" + escapedColumnLabel + "')]";
        WebElement column = form.findElement(By.xpath(xpath));
        // FIXME: This doesn't trigger the :hover CSS pseudo class. The click still works because the delete X (text) is
        // not really hidden: it is displayed with white color (the page background-color).
        new Actions(getDriver().getWrappedDriver()).moveToElement(column).perform();
        column.findElement(By.className("delete")).click();
    }
}
