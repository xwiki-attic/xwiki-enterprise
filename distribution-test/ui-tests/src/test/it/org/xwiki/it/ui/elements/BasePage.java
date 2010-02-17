package org.xwiki.it.ui.elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class BasePage
{
    @FindBy(xpath = "//div[@id='tmRegister']/a")
    @CacheLookup
    private WebElement registerLink;

    @FindBy(xpath = "//div[@id='tmLogin']/a")
    @CacheLookup
    private WebElement loginLink;

    @FindBy(xpath = "//div[@id='tmLogout']/a")
    @CacheLookup
    private WebElement logoutLink;

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

    public LoginPage clickLogin()
    {
        this.loginLink.click();
        return new LoginPage(getDriver());
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
}
