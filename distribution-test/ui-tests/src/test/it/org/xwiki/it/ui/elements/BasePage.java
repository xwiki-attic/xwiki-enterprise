package org.xwiki.it.ui.elements;

import org.openqa.selenium.By;
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
    private WebElement registerLink;

    @FindBy(xpath = "//div[@id='tmLogin']/a")
    private WebElement loginLink;

    @FindBy(xpath = "//div[@id='tmLogout']/a")
    private WebElement logoutLink;

    @FindBy(xpath = "//div[@id='tmUser']//a")
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

    public LoginPage clickLogin()
    {
        this.loginLink.click();
        return new LoginPage(getDriver());
    }

    public boolean isLoggedIn()
    {
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

    protected boolean isOnPage(String space, String page)
    {
        return getDriver().getCurrentUrl().equals(getURLForPage(space, page));
    }

    protected void gotoPage(String space, String page)
    {
        String url = getURLForPage(space, page);

        // Verify if we're already on the correct page and if so don't do anything
        if (!getDriver().getCurrentUrl().equals(url)) {
            getDriver().get(url);
        }
    }

    private String getURLForPage(String space, String page)
    {
        return "http://localhost:8080/xwiki/bin/view/" + space + "/" + page;
    }
}
