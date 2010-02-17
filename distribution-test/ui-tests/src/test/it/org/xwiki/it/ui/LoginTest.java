package org.xwiki.it.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.xwiki.it.ui.elements.HomePage;
import org.xwiki.it.ui.elements.LoginPage;

public class LoginTest
{
    private WebDriver driver;

    @Before
    public void setUp()
    {
        this.driver = new FirefoxDriver();
    }

    @After
    public void tearDown()
    {
        this.driver.close();
    }

    @Test
    public void testLogin()
    {
        this.driver.get("http://localhost:8080/xwiki/bin/view/Main/WebHome");
        HomePage homePage = new HomePage(this.driver);
        LoginPage loginPage = homePage.clickLogin();
        loginPage.loginAsAdmin();
        // TODO: check that the user is logged in
    }
}
