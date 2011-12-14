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
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.xwiki.test.po.platform.InlinePage;
import org.xwiki.test.po.platform.ViewPage;

/**
 * Represents the actions available when editing the application class. This is also the second step of the App Within
 * Minutes wizard, in which the application structure defined.
 * 
 * @version $Id$
 * @since 3.3
 */
public class ApplicationClassEditPage extends InlinePage
{
    @FindBy(id = "wizard-next")
    private WebElement nextStepButton;

    /**
     * The form used to edit the application class overwrites the save button because it needs to process the submitted
     * data. Otherwise the request is forwarded by the action filter to the save action.
     */
    @FindBy(name = "xaction_save")
    private WebElement saveButton;

    @FindBy(id = "palette")
    private WebElement palette;

    @FindBy(id = "fields")
    private WebElement fields;

    /**
     * Clicks on the Next Step button.
     * 
     * @return the page that represents the next step of the App Within Minutes wizard
     */
    public ApplicationHomeEditPage clickNextStep()
    {
        nextStepButton.click();
        return new ApplicationHomeEditPage();
    }

    @Override
    public <T extends ViewPage> T clickSaveAndView()
    {
        saveButton.click();
        return createViewPage();
    }

    /**
     * Drags a field of the specified type from the field palette to the field canvas.
     * 
     * @param fieldType the type of field to add, as displayed on the field palette
     */
    public ClassFieldEditPane addField(String fieldType)
    {
        String fieldXPath = "//span[@class = 'field' and normalize-space(.) = '%s']";
        WebElement field = palette.findElement(By.xpath(String.format(fieldXPath, fieldType)));
        new Actions(getDriver().getWrappedDriver()).dragAndDrop(field, fields).perform();
        final WebElement addedField = fields.findElement(By.xpath("li[last()]"));

        getUtil().waitUntilCondition(new ExpectedCondition<Boolean>()
        {
            public Boolean apply(WebDriver driver)
            {
                try {
                    return !addedField.getAttribute("class").contains("loading");
                } catch (NotFoundException e) {
                    return false;
                } catch (StaleElementReferenceException e) {
                    // The element was removed from DOM in the meantime
                    return false;
                }
            }
        });

        return new ClassFieldEditPane(addedField.getAttribute("id").substring("field-".length()));
    }
}
