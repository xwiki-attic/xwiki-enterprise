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
import org.xwiki.test.po.platform.BaseElement;

/**
 * Represents the pane used to edit a class field.
 * 
 * @version $Id$
 * @since 3.3
 */
public class ClassFieldEditPane extends BaseElement
{
    /**
     * The name of the edited field.
     */
    private final String fieldName;

    /**
     * The element that wraps the class field edit pane.
     */
    private final WebElement container;

    /**
     * Creates a new instance that can be used to edit the specified class field.
     * 
     * @param fieldName the field name
     */
    public ClassFieldEditPane(String fieldName)
    {
        this.fieldName = fieldName;
        container = getDriver().findElement(By.id("field-" + fieldName));
    }

    /**
     * Sets the field pretty name.
     * 
     * @param prettyName the new field pretty name
     */
    public void setPrettyName(String prettyName)
    {
        WebElement prettyNameInput = container.findElement(By.id(String.format("field-%s_prettyName", fieldName)));
        prettyNameInput.clear();
        prettyNameInput.sendKeys(prettyName);
    }

    /**
     * Sets the field default value
     * 
     * @param defaultValue the new field default value
     */
    public void setDefaultValue(String defaultValue)
    {
        // Workaround for the fact that ends-with XPath function is not implemented.
        // substring(@id, string-length(@id) - string-length(suffix) + 1)
        String xpath = "//*[substring(@id, string-length(@id) - %s - 2) = '_0_%s']";
        WebElement input = container.findElement(By.xpath(String.format(xpath, fieldName.length(), fieldName)));
        input.clear();
        input.sendKeys(defaultValue);
    }

    /**
     * Opens the field configuration panel.
     */
    public void openConfigPanel()
    {
        // This doesn't trigger the :hover CSS pseudo class so we're forced to manually set the display of the tool box.
        new Actions(getDriver().getWrappedDriver()).moveToElement(container).perform();

        // FIXME: The following two lines are a hack to overcome the fact that the previous line doesn't trigger the
        // :hover CSS pseudo class on the field container (even if the mouse if moved over it).
        WebElement toolBox = container.findElement(By.className("toolBox"));
        getDriver().executeScript("arguments[0].style.display = 'block';", toolBox);

        container.findElement(By.xpath("//div[@class = 'toolBox']/img[@title = 'Configure']")).click();

        // Reset the tool box display.
        getDriver().executeScript("arguments[0].style.display = '';", toolBox);
    }

    /**
     * Sets the field name
     * 
     * @param fieldName the new field name
     */
    public void setName(String fieldName)
    {
        WebElement nameInput = container.findElement(By.id(String.format("field-%s_name", this.fieldName)));
        nameInput.clear();
        nameInput.sendKeys(fieldName);
    }
}
