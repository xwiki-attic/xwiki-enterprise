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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.it.ui.framework.elements.ViewPage;

/**
 * Represents the actions possible on the main Administration Page.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class AdministrationPage extends ViewPage
{
    @FindBy(xpath = "//li[@class='Import']/a/span/img")
    WebElement importLink;

    @FindBy(xpath = "//li[@class='Registration']/a/span/img")
    WebElement registrationLink;

    @FindBy(xpath = "//li[@class='Users']/a/span/img")
    WebElement usersLink;

    @FindBy(xpath = "//li[@class='Rights']/a/span/img")
    WebElement rightsLink;

    public void gotoPage()
    {
        getUtil().gotoPage("XWiki", "XWikiPreferences", "admin");
    }

    public ImportPage clickImportSection()
    {
        this.importLink.click();
        return new ImportPage();
    }

    public AdminSectionPage clickRegistrationSection()
    {
        this.registrationLink.click();
        return new AdminSectionPage("register");
    }

    public UsersPage clickUsersSection()
    {
        this.usersLink.click();
        return new UsersPage();
    }

    public GlobalRightsPage clickGlobalRightsSection()
    {
        this.rightsLink.click();
        return new GlobalRightsPage();
    }
}
