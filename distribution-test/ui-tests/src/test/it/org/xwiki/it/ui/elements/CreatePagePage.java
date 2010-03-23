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

import org.openqa.selenium.WebDriver;
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

    public CreatePagePage(WebDriver driver)
    {
        super(driver);
    }

    public WYSIWYGEditPage createPage(String spaceValue, String pageValue)
    {
        this.spaceTextField.sendKeys(spaceValue);
        this.pageTextField.sendKeys(pageValue);
        this.pageTextField.submit();
        return new WYSIWYGEditPage(getDriver());
    }
}
