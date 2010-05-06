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
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Represents the common actions possible on all Pages.
 *
 * @version $Id$
 * @since 2.3M1
 */
public class BasePage
{
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

    public String getPageTitle()
    {
        return getDriver().getTitle();
    }

    public String getPageURL()
    {
        return getDriver().getCurrentUrl();
    }

    public String getMetaDataValue(String metaName)
    {
        return getDriver().findElement(By.xpath("//meta[@name='" + metaName + "']")).getAttribute("content");
    }

    /**
     * Wait until the element given by the locator is displayed. Give up after 10 seconds.
     *
     * @param locator the locator for the element to look for.
     */    
    public void waitUntilElementIsVisible(final By locator)
    {
        this.waitUntilElementIsVisible(locator, 10);
    }

    /**
     * Wait until the element given by the locator is displayed.
     *
     * @param locator the locator for the element to look for.
     * @param timeout how long to wait in seconds before giving up.
     */
    public void waitUntilElementIsVisible(final By locator, int timeout)
    {
        waitUntilElementsAreVisible(new By[] {locator}, timeout, true);
    }

    /**
     * Wait until one or all of a array of element locators are displayed.
     *
     * @param locators the array of element locators to look for.
     * @param all if true then don't return until all elements are found. Otherwise return after finding one.
     */
    public void waitUntilElementsAreVisible(final By[] locators, final boolean all)
    {
        waitUntilElementsAreVisible(locators, 10, all);
    }

    /**
     * Wait until one or all of a array of element locators are displayed.
     *
     * @param locators the array of element locators to look for.
     * @param timeout how long to wait in seconds before giving up.
     * @param all if true then don't return until all elements are found. Otherwise return after finding one.
     */
    public void waitUntilElementsAreVisible(final By[] locators, int timeout, final boolean all)
    {
        Wait<WebDriver> wait = new WebDriverWait(driver, timeout);
        wait.until(new ExpectedCondition<WebElement>()
        {
            public WebElement apply(WebDriver driver)
            {
                RenderedWebElement element = null;
                for (int i = 0; i < locators.length; i++) {
                    try {
                        element = (RenderedWebElement) driver.findElement(locators[i]);
                    } catch (NotFoundException e) {
                        // This exception is caught by WebDriverWait
                        // but it returns null which is not necessarily what we want.
                        if (all) {
                            return null;
                        }
                        continue;
                    }
                    if (element.isDisplayed()) {
                        if (!all) {
                            return element;
                        }
                    } else if (all) {
                        return null;
                    }
                }
                return element;
            }
        });
    }

    public void waitUntilElementDisappears(final By locator)
    {
        waitUntilElementDisappears(locator, 10);
    }

    /**
     * Waits until the given element is either hidden or deleted.
     * 
     * @param locator
     * @param timeout
     */
    public void waitUntilElementDisappears(final By locator, int timeout)
    {
        Wait<WebDriver> wait = new WebDriverWait(driver, timeout);
        wait.until(new ExpectedCondition<Boolean>()
        {
            public Boolean apply(WebDriver driver)
            {
                try {
                    RenderedWebElement element = (RenderedWebElement) driver.findElement(locator);
                    return Boolean.valueOf(!element.isDisplayed());
                } catch (NotFoundException e) {
                    return Boolean.TRUE;
                }
            }
        });
    }

    /**
     * Shows hidden elements, as if they would be shown on hover.
     * 
     * Currently implemented using JavaScript. Will throw a {@link RuntimeException} if the web driver
     * does not support JavaScript or JavaScript is disabled.
     * 
     * @param locator  locator used to find the element, in case multiple elements are found, the first is used
     */
    public void makeElementVisible(By locator)
    {
        // RenderedWebElement.hover() don't seem to work, workarounded using JavaScript call
        if (!(getDriver() instanceof JavascriptExecutor)) {
            throw new RuntimeException("Currently used web driver (" + getDriver().getClass()
                + ") does not support JavaScript execution");
        }
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        if (!js.isJavascriptEnabled()) {
            throw new RuntimeException("JavaScript is disabled");
        }
        js.executeScript("arguments[0].style.visibility='visible'", getDriver().findElement(locator));
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
