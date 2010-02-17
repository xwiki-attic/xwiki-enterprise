package org.xwiki.it.ui.elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage
{
    @FindBy(id = "j_username")
    private WebElement usernameText;

    @FindBy(id = "j_password")
    private WebElement passwordText;

    @FindBy(id = "rememberme")
    private WebElement rememberMeCheckbox;

    @FindBy(xpath = "//input[@type='submit' and @value='Log-in']")
    private WebElement submitButton;

    public LoginPage(WebDriver driver)
    {
        super(driver);
    }

    public void loginAsAdmin()
    {
        loginAs("Admin", "admin", true);        
    }

    public void loginAs(String username, String password, boolean rememberMe)
    {
        // In order to have good performance, don't log in again if the user is already logged-in.
        if (!isAuthenticated() || !getCurrentUser().equals(username)) {
            this.usernameText.sendKeys(username);
            this.passwordText.sendKeys(password);
            if (rememberMe) {
                this.rememberMeCheckbox.setSelected();
            }
            this.submitButton.click();
        }
    }

    public void loginAs(String username, String password)
    {
        loginAs(username, password);
    }
}
