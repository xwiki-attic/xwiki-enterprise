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
package org.xwiki.it.ui.framework.elements.editor;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.it.ui.framework.elements.FormElement;
import org.xwiki.it.ui.framework.elements.editor.EditPage;

/**
 * Represents the common actions possible on all Pages when using the "edit" action with the "object" editor.
 *
 * @version $Id$
 * @since 2.4M2
 */
public class ObjectEditPage extends EditPage
{
    @FindBy(id ="update")
    private WebElement objectForm;

    @FindBy(id = "classname")
    private WebElement classNameField;

    @FindBy(name = "action_objectadd")
    private WebElement classNameSubmit;

    private FormElement form;

    public FormElement addObject(String className)
    {
        getForm().setFieldValue(this.classNameField, className);
        this.classNameSubmit.click();

        // Make sure we wait for the element to appear since there's no page refresh.
        waitUntilElementIsVisible(By.id("xclass_" + className));

        List<FormElement> objects = getObjectsOfClass(className);
        return objects.get(objects.size() - 1);
    }

    private FormElement getForm()
    {
        if (this.form == null) {
            this.form = new FormElement(this.objectForm);
        }
        return this.form;
    }

    public String getURL(String space, String page)
    {
        return getUtil().getURL(space, page, "edit", "editor=object");
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

    public void switchToEdit(String space, String page)
    {
        getUtil().gotoPage(space, page, "edit", "editor=object");
    }
}
