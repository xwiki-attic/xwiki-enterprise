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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Represents the actions possible on the History Pane at the bottom of a page.
 *
 * @version $Id$
 * @since 2.3M1
 */
public class HistoryPane
{
    private WebDriver driver;

    @FindBy(id = "Historypane")
    private WebElement pane;

    public HistoryPane(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
    }

    public boolean hasVersionWithSummary(String summary)
    {
        List<WebElement> tableEntries = pane.findElements(By.xpath("//table/tbody/tr"));
        String commentVersionXpath;
        try {
            pane.findElement(By.xpath("//tr[2]/td/input"));
            commentVersionXpath = "//td[6]";
        }
        catch (NoSuchElementException e) {
            commentVersionXpath = "//td[4]";
        }
        for (WebElement tableEntry : tableEntries) {
            try {
                WebElement cell = tableEntry.findElement(By.xpath(commentVersionXpath));
                if (cell.getText().trim().contentEquals(summary)) {
                    return true;
                }
            }
            catch (NoSuchElementException e) {
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
            return pane.findElement(By.xpath("//node()[contains(@class, 'currentversion')]/td[4]/a")).getText();
        } catch (NoSuchElementException e) {
            // If we cound not find the radio button, there is less columns displayed and the version will be
            // in the second column
            return pane.findElement(By.xpath("//node()[contains(@class, 'currentversion')]/td[2]/a")).getText();
        }       
    }
}
