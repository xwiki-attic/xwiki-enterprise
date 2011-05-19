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
package org.xwiki.test.ui.history.elements;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.framework.elements.BaseElement;

/**
 * Represents the actions possible on the History Pane at the bottom of a page.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class HistoryTab extends BaseElement
{
    @FindBy(id = "Historypane")
    private WebElement pane;

    @FindBy(id = "Historylink")
    private WebElement historyTab;

    public boolean hasVersionWithSummary(String summary)
    {
        List<WebElement> tableEntries = pane.findElements(By.xpath(".//table/tbody/tr"));
        By commentVersionXPath;
        try {
            pane.findElement(By.xpath(".//tr[2]/td/input"));
            commentVersionXPath = By.xpath(".//td[6]");
        } catch (NoSuchElementException e) {
            commentVersionXPath = By.xpath(".//td[4]");
        }
        for (WebElement tableEntry : tableEntries) {
            try {
                WebElement cell = tableEntry.findElement(commentVersionXPath);
                if (cell.getText().trim().contentEquals(summary)) {
                    return true;
                }
            } catch (NoSuchElementException e) {
                // Ignore, better luck next time.
            }
        }
        return false;
    }

    public String getCurrentVersion()
    {
        try {
            // Try to find a radio button. This will mean there are several revisions in the table
            // and we'll find the version written down in the 3rd column
            pane.findElement(By.xpath("//tr[2]/td/input"));
            return pane.findElement(By.xpath("//node()[contains(@class, 'currentversion')]/td[3]/a")).getText();
        } catch (NoSuchElementException e) {
            // If we cound not find the radio button, there is less columns displayed and the version will be
            // in the first column
            return pane.findElement(By.xpath("//node()[contains(@class, 'currentversion')]/td[1]/a")).getText();
        }
    }

    public String getCurrentVersionComment()
    {
        try {
            // Try to find a radio button. This will mean there are several revisions in the table
            // and we'll find the version comment written down in the 6th column
            pane.findElement(By.xpath("//tr[2]/td/input"));
            return pane.findElement(By.xpath("//node()[contains(@class, 'currentversion')]/td[6]")).getText();
        } catch (NoSuchElementException e) {
            // If we cound not find the radio button, there is less columns displayed and the version comment will be
            // in the 4th column
            return pane.findElement(By.xpath("//node()[contains(@class, 'currentversion')]/td[4]")).getText();
        }
    }

    public String getCurrentAuthor()
    {
        try {
            // Try to find a radio button. This will mean there are several revisions in the table
            // and we'll find the author written down in the 4th column
            pane.findElement(By.xpath("//tr[2]/td/input"));
            return pane.findElement(By.xpath("//node()[contains(@class, 'currentversion')]/td[4]")).getText();
        } catch (NoSuchElementException e) {
            // If we cound not find the radio button, there is less columns displayed and the version will be
            // in the second column
            return pane.findElement(By.xpath("//node()[contains(@class, 'currentversion')]/td[2]")).getText();
        }
    }

    public void loadHistoryTab()
    {
        this.historyTab.click();
        waitUntilElementIsVisible(By.id("Historypane"));
    }

    public void rollbackToVersion(String version)
    {
        getDriver().findElement(
            By.xpath("//table[@class='xwikidatatable']//tr[contains(., '" + version
                + "')]//td[@class='xwikibuttonlink']/a[contains(.,'Rollback')]")).click();
        Alert alert = getDriver().switchTo().alert();
        alert.accept();
    }

    public void deleteVersion(String version)
    {
        getDriver().findElement(
            By.xpath("//table[@class='xwikidatatable']//tr[contains(., '" + version
                + "')]//td[@class='xwikibuttonlink']/a[contains(.,'Delete')]")).click();
        Alert alert = getDriver().switchTo().alert();
        alert.accept();
    }
}
