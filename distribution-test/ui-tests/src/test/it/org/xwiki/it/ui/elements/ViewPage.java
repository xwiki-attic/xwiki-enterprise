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

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

/**
 * Represents the common actions possible on all Pages when using the "view" action.
 *
 * @version $Id$
 * @since 2.3M1
 */
public class ViewPage extends BasePage
{
    @FindBys({@FindBy(id = "tmRegister"), @FindBy(tagName = "a")})
    private WebElement registerLink;

    @FindBys({@FindBy(id = "tmLogin"), @FindBy(tagName = "a")})
    private WebElement loginLink;

    @FindBys({@FindBy(id = "tmLogout"), @FindBy(tagName = "a")})
    private WebElement logoutLink;

    @FindBys({@FindBy(id = "tmUser"), @FindBy(tagName = "a")})
    private WebElement userLink;

    @FindBy(id = "tmCreatePage")
    private WebElement createPageMenuLink;

    @FindBy(id = "tmCreateSpace")
    private WebElement createSpaceMenuLink;

    public ViewPage(WebDriver driver)
    {
        super(driver);
    }

    /**
     * Logs in the Admin user (move to the home page if the current page has no log in link).
     */
    public void loginAsAdmin()
    {
        if (!isAuthenticated()) {
            // If there's no login link then go to the home page
            if (!hasLoginLink()) {
                HomePage homePage = new HomePage(getDriver());
                homePage.gotoHomePage();
            }
            clickLogin().loginAsAdmin();
        }
    }

    public boolean hasLoginLink()
    {
        // Note that we cannot test if the loginLink field is accessible since we're using an AjaxElementLocatorFactory
        // and thus it would wait 15 seconds before considering it's not accessible.
        return !getDriver().findElements(By.id("tmLogin")).isEmpty();
    }

    public LoginPage clickLogin()
    {
        this.loginLink.click();
        return new LoginPage(getDriver());
    }

    public boolean isAuthenticated()
    {
        // Note that we cannot test if the userLink field is accessible since we're using an AjaxElementLocatorFactory
        // and thus it would wait 15 seconds before considering it's not accessible.
        return !getDriver().findElements(By.id("tmUser")).isEmpty();
    }

    public String getCurrentUser()
    {
        return this.userLink.getText();
    }

    public void clickLogout()
    {
        this.logoutLink.click();
    }

    public RegisterPage clickRegister()
    {
        this.registerLink.click();
        return new RegisterPage(getDriver());
    }

    public CreatePagePage createPage()
    {
        hoverOverMenu("tmSpace");
        this.createPageMenuLink.click();
        return new CreatePagePage(getDriver());
    }

    public CreateSpacePage createSpace()
    {
        hoverOverMenu("tmWiki");
        this.createSpaceMenuLink.click();
        return new CreateSpacePage(getDriver());
    }

    // TODO: I don't think we should go through the menus, it's probably faster to to as deletePage() does
    public void deleteCurrentPage()
    {
        getDriver().findElement(By.partialLinkText("More actions")).click();
        getDriver().findElement(By.linkText("Delete")).click();

        getDriver().findElement(By.xpath("//input[@value='yes']")).click();

        // Purge from trash bin
        makeConfirmDialogSilent(); // temporary, see #makeConfirmDialogSilent
        getDriver().findElement(By.partialLinkText("Delete")).click();
    }

    public HistoryPane openHistoryDocExtraPane()
    {
        this.getDriver().findElement(By.id("Historylink")).click();
        this.waitUntilElementIsVisible(By.id("historycontent"));

        return new HistoryPane(getDriver());
    }
    
    private void hoverOverMenu(String menuId)
    {
        // We need to hover over the Wiki menu so that the "Create Space" menu entry is visible before we can click on
        // it. The normal way to implement it is to do something like this:
        //
        // @FindBy(id = "tmWiki")
        // private WebElement spaceMenuDiv;
        // ...
        // ((RenderedWebElement) spaceMenuDiv).hover();
        //
        // However it seems that currently Native Events don't work in FF 3.5+ versions and it seems to be only working
        // on Windows. Thus for now we have to simulate the hover using JavaSCript.
        //
        // In addition there's a second bug where a WebElement retrieved using a @FindBy annotation cannot be used
        // as a parameter to JavascriptExecutor.executeScript().
        // See http://code.google.com/p/selenium/issues/detail?id=256
        // Thus FTM we have to use getDriver().findElement().

        if (getDriver() instanceof JavascriptExecutor) {
           JavascriptExecutor js = (JavascriptExecutor) getDriver();
           WebElement spaceMenuDiv = getDriver().findElement(By.id(menuId));
           js.executeScript("showsubmenu(arguments[0])", spaceMenuDiv);
        } else {
            throw new RuntimeException("This test only works with a Javascript-enabled Selenium2 Driver");
        }
    }    
}
