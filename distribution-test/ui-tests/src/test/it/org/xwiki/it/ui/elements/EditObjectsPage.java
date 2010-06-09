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

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the common actions possible editing objects on a page.
 * 
 * @version $Id$
 * @since 2.4M2
 */
public class EditObjectsPage extends EditPage
{
    public String getURL(String space, String page)
    {
        return getUtil().getURL(space, page, "edit", "editor=object");
    }

    public void clickPreview()
    {
        throw new WebDriverException("Preview not available in object editor");
    }

    /** className will look something like "XWiki.XWikiRights" */
    public List<FormElement> getObjectsOfClass(String className)
    {
        List<WebElement> titles = getDriver().findElement(By.id("xclass_" + className))
                                        .findElements(By.className("xobject-title"));
        List<WebElement> elements = getDriver().findElement(By.id("xclass_" + className))
                                        .findElements(By.className("xobject-content"));
        List<FormElement> forms = new ArrayList<FormElement>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            WebElement element = elements.get(i);
            // Make sure all forms are displayed otherwise we can't interact with them.
            if (element instanceof RenderedWebElement && !((RenderedWebElement) element).isDisplayed()) {
                titles.get(i).click();
            }
            forms.add(new FormElement(element));
        }
        return forms;
    }

    /** 
     * Adding an object is easy, you simply say:
     * page.getAddPanel().selectClass("XWiki.XWikiRights").clickAdd();
     */
    public AddObjectPanel getAddPanel()
    {
        return this.new AddObjectPanel();
    }

    public class AddObjectPanel
    {
        private WebElement form;

        private FormElement.SelectElement classSelector;

        private WebElement getForm()
        {
            if (form == null) {
                form = getDriver().findElement(By.id("objectadd"));
            }
            return form;
        }

        private FormElement.SelectElement getClassSelector()
        {
            if (classSelector == null) {
                classSelector = new FormElement(getForm()).getSelectElement(By.name("classname"));
            }
            return classSelector;
        }

        public AddObjectPanel selectClass(String name)
        {
            getClassSelector().select(name);
            return this;
        }

        public Set<String> getOptions()
        {
            return getClassSelector().getOptions();
        }

        public void clickAdd()
        {
            getForm().findElement(By.className("button")).click();
        }
    }


}
