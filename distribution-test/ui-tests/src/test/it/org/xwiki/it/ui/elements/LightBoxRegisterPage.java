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
import java.util.ArrayList;

import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;

/**
 * Represents a registertion page in a lightbox
 *
 * @version $Id$
 * @since 2.3M1
 */
public class LightBoxRegisterPage extends RegisterPage
{
    @FindBy(xpath = "//div/form[@id='register']/div/span[1]/input[@value='Save']")
    private WebElement submitButton;

    public void gotoRegisterPage()
    {
        HomePage homePage = new HomePage();
        homePage.gotoHomePage();

        homePage.loginAsAdmin();
        AdministrationPage adminPage = homePage.administorWiki();
        UsersPage usersPage = adminPage.clickUsersSection();

        usersPage.clickAddNewUser();
        waitUntilElementIsVisible(By.id("register_first_name"));
    }

    /** @return true if registration is successful, false if user couldn't be registered. */
    public boolean register()
    {
        submitButton.click();
        
        waitUntilElementsAreVisible(
            new By[] {By.xpath("//td[@class='username']/a[@href='/xwiki/bin/view/XWiki/JohnSmith']"),
                      By.xpath("//dd/span[@class='LV_validation_message LV_invalid']")
            },
            false
        );

        return !getDriver()
                .findElements(
                  By.xpath("//td[@class='username']/a[@href='/xwiki/bin/view/XWiki/JohnSmith']"))
                    .isEmpty();
    }

    public boolean liveValidationEnabled()
    {
        return !getDriver().findElements(By.xpath("//div[@id='lb']/div[@id='lb-content']/script[3]")).isEmpty();
    }
}
