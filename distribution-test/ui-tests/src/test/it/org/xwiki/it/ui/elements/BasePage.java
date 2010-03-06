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
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xwiki.it.ui.framework.TestUtils;

/**
 * Represents the common actions possible on all Pages.
 *
 * @version $Id$
 * @since 2.3M1
 */
public class BasePage
{
    @FindBys({@FindBy(id = "tmRegister"), @FindBy(tagName = "a")})
    private WebElement registerLink;

    @FindBys({@FindBy(id = "tmLogin"), @FindBy(tagName = "a")})
    private WebElement loginLink;

    @FindBys({@FindBy(id = "tmLogout"), @FindBy(tagName = "a")})
    private WebElement logoutLink;

    @FindBys({@FindBy(id = "tmUser"), @FindBy(tagName = "a")})
    private WebElement userLink;

    private WebDriver driver;

    public BasePage(WebDriver driver)
    {
        this.driver = driver;
        ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, 15);
        PageFactory.initElements(finder, this);
    }

    protected WebDriver getDriver()
    {
        return this.driver;
    }

    public void waitUntilElementIsVisible(final By locator)
    {
        this.waitUntilElementIsVisible(locator, 10);
    }

    public void waitUntilElementIsVisible(final By locator, int timeout)
    {
        Wait<WebDriver> wait = new WebDriverWait(driver, timeout);
        wait.until(new ExpectedCondition<WebElement>()
        {
            public WebElement apply(WebDriver driver)
            {
                RenderedWebElement element = (RenderedWebElement) driver.findElement(locator);
                return element.isDisplayed() ? element : null;
            }
        });
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

        return new HistoryPane(driver);
    }

    /**
     * There is no easy support for alert/confirm window methods yet, see -
     * http://code.google.com/p/selenium/issues/detail?id=27 -
     * http://www.google.com/codesearch/p?hl=en#2tHw6m3DZzo/branches
     * /merge/common/test/java/org/openqa/selenium/AlertsTest.java The aim is : <code>
     * Alert alert = driver.switchTo().alert();
     * alert.accept();
     * </code> Until then, the following hack does override the confirm method in Javascript to always return true.
     */
    protected void makeConfirmDialogSilent()
    {
        ((JavascriptExecutor) driver).executeScript("window.confirm = function() { return true; }");
    }
}
