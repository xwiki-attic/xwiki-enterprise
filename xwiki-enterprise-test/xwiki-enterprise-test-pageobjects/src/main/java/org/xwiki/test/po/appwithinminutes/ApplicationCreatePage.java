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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.xwiki.test.po.platform.ViewPage;

/**
 * Represents the actions possible on the first step of the App Within Minutes wizard.
 * 
 * @version $Id$
 * @since 3.3
 */
public class ApplicationCreatePage extends ViewPage
{
    @FindBy(id = "appName")
    private WebElement appNameInput;

    @FindBy(id = "wizard-next")
    private WebElement nextStepButton;

    /**
     * Types the given string into the application name input.
     * 
     * @param appName the application name
     */
    public void setApplicationName(String appName)
    {
        appNameInput.clear();
        appNameInput.sendKeys(appName);
    }

    /**
     * Waits until the preview for the currently inputed application name is displayed.
     */
    public void waitForApplicationNamePreview()
    {
        final String appName = appNameInput.getAttribute("value");
        getUtil().waitUntilCondition(new ExpectedCondition<Boolean>()
        {
            public Boolean apply(WebDriver driver)
            {
                List<WebElement> previews = driver.findElements(By.className("appName-preview"));
                return previews.size() == 1 && previews.get(0).getText().contains(appName);
            }
        });
    }

    /**
     * Clicks on the Next Step button.
     * 
     * @return the page that represents the next step of the App Within Minutes wizard
     */
    public ApplicationClassEditPage clickNextStep()
    {
        nextStepButton.click();
        return new ApplicationClassEditPage();
    }
}
