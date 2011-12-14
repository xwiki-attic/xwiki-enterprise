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
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.po.platform.InlinePage;

/**
 * Represents the actions possible when editing an application entry.
 * 
 * @version $Id$
 * @since 3.3
 */
public class EntryEditPage extends InlinePage
{
    /**
     * The XPath that locates the label of a form field.
     * <p>
     * NOTE: We use a workaround for the fact that ends-with XPath function is not implemented:
     * 
     * <pre>
     * {@code substring(@id, string-length(@id) - string-length(suffix) + 1)}
     * </pre>
     */
    private static final String LABEL_XPATH_FORMAT = "//label[substring(@for, string-length(@for) - %s - 2) = '_0_%s']";

    /**
     * The XPath that locates a form field.
     */
    private static final String FIELD_XPATH_FORMAT = "//*[substring(@id, string-length(@id) - %s - 2) = '_0_%s']";

    @FindBy(id = "inline")
    private WebElement form;

    /**
     * Retrieves the label of the specified form field.
     * 
     * @param fieldName the name of a form field
     * @return the label of the specified form field
     */
    public String getLabel(String fieldName)
    {
        WebElement label = form.findElement(By.xpath(String.format(LABEL_XPATH_FORMAT, fieldName.length(), fieldName)));
        return label.getText();
    }

    /**
     * Retrieves the value of the specified form field
     * 
     * @param fieldName the name of a form field
     * @return the value of the specified form field
     */
    public String getValue(String fieldName)
    {
        WebElement field = form.findElement(By.xpath(String.format(FIELD_XPATH_FORMAT, fieldName.length(), fieldName)));
        return field.getAttribute("value");
    }

    /**
     * Sets the value of the specified form field
     * 
     * @param fieldName the name of a form field
     * @param fieldValue the new value for the specified form field
     */
    public void setValue(String fieldName, String fieldValue)
    {
        WebElement field = form.findElement(By.xpath(String.format(FIELD_XPATH_FORMAT, fieldName.length(), fieldName)));
        field.clear();
        field.sendKeys(fieldValue);
    }
}
