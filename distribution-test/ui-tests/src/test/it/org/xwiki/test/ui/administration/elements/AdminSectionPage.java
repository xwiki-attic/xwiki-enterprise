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
package org.xwiki.test.ui.administration.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.framework.elements.FormElement;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Represents the actions possible on the main Administration Page.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class AdminSectionPage extends ViewPage
{
    @FindBy(xpath = "//input[@type='submit'][@name='formactionsac']")
    private WebElement saveButton;

    // The admin-page-content div is being treated as a form since it may contain multiple forms and we want to be able
    // to access elements in them all.
    @FindBy(xpath = "//div[@id='admin-page-content']")
    private WebElement form;

    private final String section;

    public AdminSectionPage(String section)
    {
        this.section = section;
    }

    public void gotoPage()
    {
        getDriver().get(getURL());
    }

    public String getURL()
    {
        return getUtil().getURL("XWiki", "XWikiPreferences", "admin", "section=" + section);
    }

    public void clickSave()
    {
        saveButton.click();
    }

    public FormElement getForm()
    {
        return new FormElement(form);
    }
}
