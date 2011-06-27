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
package org.xwiki.test.ui.framework.elements;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.TimeoutException;

/**
 * Represents the actions possible on a livetable.
 * 
 * @version $Id$
 * @since 2.4M2
 */
public class LiveTableElement extends BaseElement
{
    private String livetableId;

    public LiveTableElement(String livetableId)
    {
        super();
        this.livetableId = livetableId;

        Assert.assertTrue("Invalid state, the livetable shouldn't be in a displayed state",
            getUtil().findElementsWithoutWaiting(getDriver(), By.id("uitest-livetable-status")).size() == 0);

        // Register a Javascript event observation since we need to know when the livetable has finished displaying
        // all its data before we can perform assertions and that livetable sends an event when it has done so.
        executeJavascript("document.observe('xwiki:livetable:" + this.livetableId + ":displayComplete', function() {"
            + "document.body.insert(new Element('div', {'id' : 'uitest-livetable-status' }).update('complete'));});");
    }

    /**
     * @return if the livetable has finished displaying and is ready for service
     */
    public boolean isReady()
    {
        boolean ready = false;
        List<WebElement> elements = getUtil().findElementsWithoutWaiting(getDriver(), By.id("uitest-livetable-status"));
        if (elements.size() > 0) {
            ready = "complete".equals(elements.get(0).getText());
        }
        return ready;
    }

    /**
     * Wait till the livetable has finished displaying all its rows (so that we can then assert the livetable content
     * without running the risk that the rows have not been updated yet).
     */
    public void waitUntilReady()
    {
        long t1 = System.currentTimeMillis();
        while ((System.currentTimeMillis() - t1 < getUtil().getTimeout() * 1000L)) {
            if (isReady()) {
                clearStatus();
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Do nothing just break out
                break;
            }
        }
        getUtil().takeScreenshot();
        throw new TimeoutException("Livetable isn't ready after the timeout has expired. Source ["
            + getDriver().getPageSource() + "]");
    }

    public boolean hasColumn(String columnTitle)
    {
        List<WebElement> elements = getUtil().findElementsWithoutWaiting(getDriver(),
            By.xpath("//th[contains(@class, 'xwiki-livetable-display-header-text') and normalize-space(.) = '"
            + columnTitle + "']"));
        return elements.size() > 0;
    }

    public void filterColumn(String inputId, String filterValue)
    {
        // Reset the livetable status since the filtering will cause a reload
        clearStatus();
        WebElement element = getDriver().findElement(By.id(inputId));
        element.sendKeys(filterValue);
        waitUntilReady();
    }

    /** Drop the element that signals that the livetable finished loading. */
    private void clearStatus()
    {
        executeJavascript("if ($('uitest-livetable-status')) $('uitest-livetable-status').remove();");
    }

    /**
     * @since 3.2M1
     */
    public void waitUntilRowCountGreaterThan(final int minimalExpectedRowCount)
    {
        final By by = By.xpath("//tbody[@id = '" + this.livetableId + "-display']//tr");
        getUtil().waitUntilCondition(new ExpectedCondition<Boolean>()
            {
                public Boolean apply(WebDriver driver)
                {
                    return driver.findElements(by).size() >= minimalExpectedRowCount;
                }
            }
        );
    }
}
