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
    public void testLoginLogout()
    {
        HomePage homePage = new HomePage(this.driver);
        homePage.gotoHomePage();

        // Make sure we log out if we're already logged in since we're testing the log in...
        if (homePage.isLoggedIn()) {
            homePage.clickLogout();
        }

        LoginPage loginPage = homePage.clickLogin();
        loginPage.loginAsAdmin();

        // Verify that after logging in we're redirected to the page on which the login button was clicked, i.e. the
        // home page here.
        Assert.assertTrue(homePage.isOnHomePage());

        Assert.assertTrue(homePage.isLoggedIn());
        Assert.assertEquals("Administrator", homePage.getCurrentUser());

        // Test Logout and verify we stay on the home page
        homePage.clickLogout();
        Assert.assertFalse(homePage.isLoggedIn());
        Assert.assertTrue(homePage.isOnHomePage());
    }
}
