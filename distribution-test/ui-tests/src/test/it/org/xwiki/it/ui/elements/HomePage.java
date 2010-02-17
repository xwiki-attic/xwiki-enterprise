package org.xwiki.it.ui.elements;

import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage
{
    public HomePage(WebDriver driver)
    {
        super(driver);
        openPage("Main", "WebHome");
    }
}
