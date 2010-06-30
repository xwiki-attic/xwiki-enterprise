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
package org.xwiki.it.ui.administration.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.it.ui.framework.elements.InlinePage;

/**
 * Represents a template provider page in inline mode
 * 
 * @version $Id$
 * @since 2.4M2
 */
public class TemplateProviderInlinePage extends InlinePage
{
    @FindBy(name = "title")
    private WebElement nameInput;

    @FindBy(name = "XWiki.TemplateProviderClass_0_template")
    private WebElement templateInput;

    @FindBy(name = "XWiki.TemplateProviderClass_0_name")
    private WebElement templateNameInput;

    public String getTemplateName()
    {
        return templateNameInput.getValue();
    }

    public void setTemplateName(String value)
    {
        templateNameInput.clear();
        templateNameInput.sendKeys(value);
    }

    public String getTemplate()
    {
        return templateInput.getValue();
    }

    public void setTemplate(String value)
    {
        templateInput.clear();
        templateInput.sendKeys(value);
    }

    private List<WebElement> getSpacesInput()
    {
        return getDriver().findElements(By.name("XWiki.TemplateProviderClass_0_spaces"));
    }

    public List<String> getSpaces()
    {
        List<String> spaces = new ArrayList<String>();

        for (WebElement input : getSpacesInput()) {
            spaces.add(input.getValue());
        }

        return spaces;
    }

    public void setSpaces(List<String> spaces)
    {
        for (WebElement input : getSpacesInput()) {
            if (input.isSelected()) {
                input.toggle();
            }
            if (spaces.contains(input.getValue())) {
                input.toggle();
            }
        }
    }
}
