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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the common actions possible on all Pages when using the "edit" action with the "class" editor.
 *
 * @version $Id$
 * @since 2.4M2
 */
// TODO: Fix the fact that this class should extend EditPage but the id of the save action is incorrectly different...
public class ClassEditPage extends BasePage
{
    @FindBy(name = "action_saveandcontinue")
    private WebElement saveandcontinue;

    @FindBy(name = "action_propupdate")
    private WebElement saveandview;

    @FindBy(name = "action_cancel")
    private WebElement cancel;

    @FindBy(id ="propupdate")
    private WebElement propertyForm;

    @FindBy(id = "propname")
    private WebElement propertyNameField;

    @FindBy(id = "proptype")
    private WebElement propertyTypeField;

    @FindBy(name = "action_propadd")
    private WebElement propertySubmit;

    private FormElement form;

    public void addProperty(String propertyName, String propertyType)
    {
        getForm().setFieldValue(this.propertyNameField, propertyName);
        getForm().setFieldValue(this.propertyTypeField, propertyType);
        this.propertySubmit.click();
    }

    private FormElement getForm()
    {
        if (this.form == null) {
            this.form = new FormElement(this.propertyForm);
        }
        return this.form;
    }

    /**
     * Start editing the page using the Class editor.
     *
     * @param space
     * @param page
     */
    public void switchToEdit(String space, String page)
    {
        getUtil().gotoPage(space, page, "edit", "editor=class");
    }

    public DatabaseListClassEditElement getDatabaseListClassEditElement(String propertyName)
    {
        // Make the element visible before returning it
        By locator = By.id("xproperty_" + propertyName + "_title");
        waitUntilElementIsVisible(locator);
        getDriver().findElement(locator).click();        
        return new DatabaseListClassEditElement(getForm(), propertyName);
    }

    public void clickSaveAndContinue()
    {
        this.saveandcontinue.click();

        // Wait until the page is really saved
        waitUntilElementIsVisible(By.xpath("//div[contains(@class,'xnotification-done') and text()='Saved']"));
    }

    public ViewPage clickSaveAndView()
    {
        this.saveandview.click();
        return new ViewPage();
    }

    public ViewPage clickCancel()
    {
        this.cancel.click();
        return new ViewPage();
    }
}
