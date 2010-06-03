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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the actions possible on the Create Page template page.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class CreatePagePage extends ViewPage
{
    @FindBy(id = "space")
    private WebElement spaceTextField;

    @FindBy(id = "page")
    private WebElement pageTextField;

    public String getSpace()
    {
        return spaceTextField.getValue();
    }

    public void setSpace(String space)
    {
        this.spaceTextField.clear();
        this.spaceTextField.sendKeys(space);
    }

    public String getPage()
    {
        return pageTextField.getValue();
    }

    public void setPage(String page)
    {
        this.pageTextField.clear();
        this.pageTextField.sendKeys(page);
    }

    public boolean areTemplatesAvailable()
    {
        List<WebElement> templates = getDriver().findElements(By.name("template"));
        // When there's no template available a hidden input with a blank value remains.
        return templates.size() > 1;
    }

    public void setTemplate(String template)
    {
        List<WebElement> templates = getDriver().findElements(By.name("template"));
        for (WebElement templateInput : templates) {
            if (templateInput.getValue().equals(template)) {
                templateInput.setSelected();
            }
        }

    }

    public void clickCreate()
    {
        this.pageTextField.submit();
    }

    public WYSIWYGEditPage createPage(String spaceValue, String pageValue)
    {
        setSpace(spaceValue);
        setPage(pageValue);
        clickCreate();
        return new WYSIWYGEditPage();
    }

    public WYSIWYGEditPage createPageFromTemplate(String spaceValue, String pageValue, String templateValue)
    {
        setSpace(spaceValue);
        setPage(pageValue);
        setTemplate(templateValue);
        clickCreate();
        return new WYSIWYGEditPage();
    }
}
