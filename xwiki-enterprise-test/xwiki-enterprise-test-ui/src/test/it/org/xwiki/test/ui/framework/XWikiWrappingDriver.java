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
package org.xwiki.test.ui.framework;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Extends an existing Selenium Driver to generate more debug information (takes screenshot on find failures, show page
 * source).
 *
 * @since 3.2M1
 */
public class XWikiWrappingDriver implements WebDriver, JavascriptExecutor
{
    private TestUtils utils;

    private WebDriver driver;

    public XWikiWrappingDriver(WebDriver wrappedDriver, TestUtils utils)
    {
        this.driver = wrappedDriver;
        this.utils = utils;
    }

    public WebDriver getWrappedDriver()
    {
        return this.driver;
    }

    @Override
    public void close()
    {
        getWrappedDriver().close();
    }

    @Override
    public List<WebElement> findElements(By by)
    {
        return getWrappedDriver().findElements(by);
    }

    @Override
    public void get(String s)
    {
        getWrappedDriver().get(s);
    }

    @Override
    public String getCurrentUrl()
    {
        return getWrappedDriver().getCurrentUrl();
    }

    @Override
    public String getPageSource()
    {
        return getWrappedDriver().getPageSource();
    }

    @Override
    public String getTitle()
    {
        return getWrappedDriver().getTitle();
    }

    @Override
    public String getWindowHandle()
    {
        return getWrappedDriver().getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles()
    {
        return getWrappedDriver().getWindowHandles();
    }

    @Override
    public Options manage()
    {
        return getWrappedDriver().manage();
    }

    @Override
    public Navigation navigate()
    {
        return getWrappedDriver().navigate();
    }

    @Override
    public void quit()
    {
        getWrappedDriver().quit();
    }

    @Override
    public TargetLocator switchTo()
    {
        return getWrappedDriver().switchTo();
    }

    @Override
    public WebElement findElement(By by)
    {
        try {
            return getWrappedDriver().findElement(by);
        } catch (NoSuchElementException e) {
            this.utils.takeScreenshot();
            throw new NoSuchElementException("Failed to locate element from page source [" + getPageSource()
                + "]", e);
        }
    }

    @Override
    public Object executeAsyncScript(String s, Object... objects)
    {
        checkIsJavascriptExecutor();
        return ((JavascriptExecutor) getWrappedDriver()).executeAsyncScript(s, objects);
    }

    @Override
    public Object executeScript(String s, Object... objects)
    {
        checkIsJavascriptExecutor();
        return ((JavascriptExecutor) getWrappedDriver()).executeScript(s, objects);
    }

    @Override
    public boolean isJavascriptEnabled()
    {
        checkIsJavascriptExecutor();
        return ((JavascriptExecutor) getWrappedDriver()).isJavascriptEnabled();
    }

    private void checkIsJavascriptExecutor()
    {
        if (!(getWrappedDriver() instanceof JavascriptExecutor)) {
            throw new RuntimeException("Currently used web driver [" + getWrappedDriver().getClass()
                + "] does not support JavaScript execution");
        }
    }
}
