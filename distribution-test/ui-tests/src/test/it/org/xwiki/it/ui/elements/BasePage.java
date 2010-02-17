package org.xwiki.it.ui.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BasePage
{
    @FindBy(xpath = "//div[@id='tmRegister']/a")
    private WebElement registerLink;

    @FindBy(xpath = "//div[@id='tmLogin']/a")
    private WebElement loginLink;

    @FindBy(xpath = "//div[@id='tmLogout']/a")
    private WebElement logoutLink;

    public LoginPage clickLogin()
    {
        this.loginLink.click();
        return new LoginPage();
    }

    public void clickLogout()
    {
        this.logoutLink.click();        
    }

    public RegisterPage clickRegister()
    {
        this.registerLink.click();
        return new RegisterPage();
    }
}
