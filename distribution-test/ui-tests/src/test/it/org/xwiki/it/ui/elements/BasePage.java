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
public class BasePage extends BaseElement
{
    private final String baseURL = "http://localhost:8080/xwiki/bin/";

    public String getPageTitle()
    {
        return getDriver().getTitle();
    }

    public String getPageURL()
    {
        return getDriver().getCurrentUrl();
    }

    public String getURL(String space, String page)
    {
        return getURL(space, page, "view", null);
    }

    public String getURL(String space, String page, String action)
    {
        return getURL(space, page, action, null);
    }

    public String getURL(String space, String page, String action, String queryString)
    {
        return baseURL + action + "/" + space + "/" + page + (queryString == null ? "" : "?" + queryString);
    }

    public String getMetaDataValue(String metaName)
    {
        return getDriver().findElement(By.xpath("//meta[@name='" + metaName + "']")).getAttribute("content");
    }

    /**
     * @return true if we are currently logged in, false otherwise
     */
    public boolean isAuthenticated()
    {
        // Note that we cannot test if the userLink field is accessible since we're using an AjaxElementLocatorFactory
        // and thus it would wait 15 seconds before considering it's not accessible.
        return !getDriver().findElements(By.id("tmUser")).isEmpty();
    }

    /**
     * Determine if the current page is a new document.
     * 
     * @return true if the document is new, false otherwise
     */
    public boolean isNewDocument()
    {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        return (Boolean) js.executeScript("return XWiki.docisnew");
    }

    /**
     * Emulate mouse over on a top menu entry.
     * 
     * @param menuId Menu to emulate the mouse over on
     */
    protected void hoverOverMenu(String menuId)
    {
        // We need to hover over the Wiki menu so that the a menu entry is visible before we can click on
        // it. The normal way to implement it is to do something like this:
        //
        // @FindBy(id = "tmWiki")
        // private WebElement spaceMenuDiv;
        // ...
        // ((RenderedWebElement) spaceMenuDiv).hover();
        //
        // However it seems that currently Native Events don't work in FF 3.5+ versions and it seems to be only working
        // on Windows. Thus for now we have to simulate the hover using JavaSCript.
        //
        // In addition there's a second bug where a WebElement retrieved using a @FindBy annotation cannot be used
        // as a parameter to JavascriptExecutor.executeScript().
        // See http://code.google.com/p/selenium/issues/detail?id=256
        // Thus FTM we have to use getDriver().findElement().

        if (getDriver() instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            WebElement spaceMenuDiv = getDriver().findElement(By.id(menuId));
            js.executeScript("showsubmenu(arguments[0])", spaceMenuDiv);
        } else {
            throw new RuntimeException("This test only works with a Javascript-enabled Selenium2 Driver");
        }
    }

    /**
     * Perform a click on a "content menu" top entry.
     * 
     * @param id The id of the entry to follow
     */
    protected void clickContentMenuTopEntry(String id)
    {
        getDriver().findElement(By.xpath("//div[@id='" + id + "']//strong")).click();
    }

    /**
     * Perform a click on a "content menu" sub-menu entry.
     * 
     * @param id The id of the entry to follow
     */
    protected void clickContentMenuEditSubMenuEntry(String id)
    {
        hoverOverMenu("tmEdit");
        getDriver().findElement(By.xpath("//a[@id='" + id + "']")).click();
    }

    /**
     * Performs a click on the "edit" entry of the content menu.
     */
    public void clickEdit()
    {
        clickContentMenuTopEntry("tmEdit");
    }

    /**
     * Performs a click on the "edit wiki" entry of the content menu.
     */
    public void clickEditWiki()
    {
        clickContentMenuEditSubMenuEntry("tmEditWiki");
    }

    /**
     * Performs a click on the "edit wysiwyg" entry of the content menu.
     */
    public void clickEditWysiwyg()
    {
        clickContentMenuEditSubMenuEntry("tmEditWysiwyg");
    }

    /**
     * Performs a click on the "edit inline" entry of the content menu.
     */
    public void clickEditInline()
    {
        clickContentMenuEditSubMenuEntry("tmEditInline");
    }

    /**
     * Performs a click on the "edit acces rights" entry of the content menu.
     */
    public void clickEditRights()
    {
        clickContentMenuEditSubMenuEntry("tmEditRights");
    }

    /**
     * Performs a click on the "edit objects" entry of the content menu.
     */
    public void clickEditObjects()
    {
        clickContentMenuEditSubMenuEntry("tmEditObjects");
    }

    /**
     * Performs a click on the "edit class" entry of the content menu.
     */
    public void clickEditClass()
    {
        clickContentMenuEditSubMenuEntry("tmEditClass");
    }
}
