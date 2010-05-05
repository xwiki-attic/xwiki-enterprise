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
package org.xwiki.it.ui.elements;

import java.util.Map;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.By;

/**
 * Represents a Form.
 *
 * @version $Id$
 * @since 2.4M1
 */
public class FormPage extends BasePage
{
    private final WebElement form;

    public FormPage(WebElement form, WebDriver driver)
    {
        super(driver);
        this.form = form;
    }

    public void fillFieldsByName(Map<String, String> valuesByNames)
    {
        Map valuesByElements = new HashMap<WebElement, String>((int) (valuesByNames.size() / 0.75));

        for (String name : valuesByNames.keySet()) {
            valuesByElements.put(this.form.findElement(By.name(name)), valuesByNames.get(name));
        }
        fillFieldsByElements(valuesByElements);
    }


    public void fillFieldsByElements(Map<WebElement, String> valuesByElements)
    {
        for (WebElement el : valuesByElements.keySet()) {
            setFieldValue(el, valuesByElements.get(el));
        }
    }

    public void setFieldValue(By findElementBy, String value)
    {
        setFieldValue(this.form.findElement(findElementBy), value);
    }

    public void setFieldValue(WebElement fieldElement, String value)
    {
        if (!fieldElement.getTagName().equals("input") && !fieldElement.getTagName().equals("textbox")) {
            throw new WebDriverException("You can only fill in input and textbox elements.");
        }
        if (fieldElement.getAttribute("type").equals("checkbox")) {
            setCheckBox(fieldElement, value.equals("true"));
        } else {
            fieldElement.clear();            
            fieldElement.sendKeys(value);
        }
    }

    public void setCheckBox(WebElement checkBoxElement, boolean checked)
    {
        int x = 0;
        while (checkBoxElement.isSelected() != checked) {
            checkBoxElement.toggle();
            if (x == 100) {
                throw new WebDriverException("Unable to set checkbox at "
                                             + checkBoxElement.getAttribute("name") + " to " + checked);
            }
            x++;
        }

    }
}
