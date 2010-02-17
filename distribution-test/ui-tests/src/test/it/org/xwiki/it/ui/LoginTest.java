package org.xwiki.it.ui;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
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
        HomePage homePage = new HomePage(this.driver);
        LoginPage loginPage = homePage.clickLogin();
        loginPage.loginAsAdmin();
        Assert.assertTrue(loginPage.isLoggedIn());
        Assert.assertEquals("Administrator", loginPage.getCurrentUser());
    }
}
