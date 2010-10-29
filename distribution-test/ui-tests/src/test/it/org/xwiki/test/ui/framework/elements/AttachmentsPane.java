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

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.framework.elements.BaseElement;

/**
 * Represents the actions possible on the Attachment Pane at the bottom of a page.
 * 
 * @version $Id$
 * @since 2.5M1
 */
public class AttachmentsPane extends BaseElement
{
    @FindBy(id = "Attachmentspane")
    private WebElement pane;

    @FindBy(xpath = "//input[@value='Add another file']")
    private WebElement addAnotherFile;

    /**
     * Fills the URL with the specified file path.
     * This fills in the last field. To add more attachments, call addAnotherFile then setFileToUpload.
     * 
     * @param filePath the path to the file to upload in URL form (the file *must* exist in the target directory).
     */
    public void setFileToUpload(final String filePath)
    {
        final List<WebElement> inputs = this.pane.findElements(By.className("uploadFileInput"));
        inputs.get(inputs.size() - 1).sendKeys(filePath);
    }

    /**
     * Adds another input field for attaching a file.
     */
    public void addAnotherFile()
    {
        addAnotherFile.click();
    }

    public void clickAttachFiles()
    {
        // TODO Id tag this button.
        this.pane.findElement(By.xpath("//div/span/input[@class='button'][@type='submit'][@value='Attach']")).click();
    }

    public List<String> getAttachmentFilenames()
    {
        final ArrayList<String> names = new ArrayList<String>();
        for (WebElement el : this.pane.findElements(By.className("information"))) {
            names.add(el.findElement(By.className("name")).getText());
        }
        return names;
    }


    public List<WebElement> getAttachmentLinks()
    {
        final ArrayList<WebElement> links = new ArrayList<WebElement>();
        for (WebElement el : this.pane.findElements(By.className("information"))) {
            links.add(el.findElement(By.className("name")).findElement(By.tagName("a")));
        }
        return links;
    }

    /**
     * Deletes the corresponding file name.
     * 
     * @param attachmentName the name of the attachment to be deleted
     */
    public void deleteAttachmentByFileByName(String attachmentName)
    {
        waitUntilElementIsVisible(By.xpath("//li[@id='Attachmentstab']"));
        waitUntilElementIsVisible(By.xpath("//div[@id='attachmentscontent']//a[text()='" + attachmentName
            + "']/../../span[2]/a[@class='deletelink']"));
        getDriver().findElement(
            By.xpath("//div[@id='attachmentscontent']//a[text()='" + attachmentName
                + "']/../../span[2]/a[@class='deletelink']")).click();
        waitUntilElementIsVisible(By
            .xpath("//*[@class='xdialog-modal-container']/*[contains(@class, 'xdialog-box-confirmation')]"));
        getDriver().findElement(By.xpath("//*[@class='xdialog-modal-container']//input[@value='Yes']")).click();
        waitUntilElementDisappears(By
            .xpath("//*[@class='xdialog-modal-container']/*[contains(@class, 'xdialog-box-confirmation')]"));
        waitUntilElementDisappears(By.xpath("//div[@id='attachmentscontent']//a[text()='" + attachmentName + "']"));
        waitUntilElementIsVisible(By.xpath("//div[@id='Attachmentspane']"));
    }

    /**
     * Deletes the first attachment.
     */
    public void deleteFirstAttachment()
    {

        waitUntilElementIsVisible(By.xpath("//div[@id='Attachmentspane']"));
        String tmp =
            getDriver().findElement(
                By.xpath("//div[@id='_attachments']/*[1]/div[@class='information']/span[@class='name']")).getText();
        getDriver().findElement(
            By.xpath("//div[@id='attachmentscontent']//a[text()='" + tmp + "']/../../span[2]/a[@class='deletelink']"))
            .click();

        waitUntilElementIsVisible(By.className("xdialog-box-confirmation"));
        waitUntilElementIsVisible(
            By.xpath("//*[@class='xdialog-modal-container']/*[contains(@class, 'xdialog-box-confirmation')]"), 20);
        getDriver().findElement(By.xpath("//*[@class='xdialog-modal-container']//input[@value='Yes']")).click();
        waitUntilElementDisappears(By
            .xpath("//*[@class='xdialog-modal-container']/*[contains(@class, 'xdialog-box-confirmation')]"));
        waitUntilElementDisappears(By.xpath("//div[@id='attachmentscontent']//a[text()='" + tmp
            + "']/../../span[2]/a[@class='deletelink']"));
    }

    /**
     * @return the number of attachments in this document.
     */
    public int getNumberOfAttachments()
    {
        waitUntilElementIsVisible(By.xpath("//div[@id='Attachmentspane']"));
        By countLocator = By.cssSelector("#Attachmentstab .itemCount");
        return Integer.parseInt(getDriver().findElement(countLocator).getText().replaceAll("[()]", ""));
    }

    /**
     * Deletes ALL the attached files
     */
    public void deleteAllAttachments()
    {
        while (this.getNumberOfAttachments() > 0) {
            this.deleteFirstAttachment();
        }
    }

    public String getUploaderOfAttachment(String attachmentName)
    {
        waitUntilElementIsVisible(By.xpath("//div[@id='Attachmentspane']"));
        waitUntilElementIsVisible(By.xpath("//div[@id='attachmentscontent']//a[text()='" + attachmentName
            + "']/../../span[2]/a[@class='deletelink']"));
        return getDriver().findElement(
            By.xpath("//div[@id='attachmentscontent']//a[text()='" + attachmentName
                + "']/../../div[@class='meta']/span[@class='publisher']/span[@class='wikilink']")).toString();
    }

    public String getLatestVersionOfAttachment(String attachmentName)
    {
        waitUntilElementIsVisible(By.xpath("//div[@id='Attachmentspane']"));

        return getDriver().findElement(
            By.xpath("//div[@id='attachmentscontent']//a[text()= '" + attachmentName + "']/../../span[3]/a")).getText();
    }

    public String getSizeOfAttachment(String attachmentName)
    {
        waitUntilElementIsVisible(By.xpath("//div[@id='Attachmentspane']"));

        return getDriver()
            .findElement(
                By.xpath("//div[@id='attachmentscontent']//a[text()='" + attachmentName
                    + "']/../../div[@class='meta']/span[@class='size']")).toString().replaceAll("[()]", "");

    }

    public String getDateOfLastUpload(String attachmentName)
    {
        waitUntilElementIsVisible(By.xpath("//div[@id='Attachmentspane']"));
        return getDriver()
            .findElement(
                By.xpath("//div[@id='attachmentscontent']//a[text()='" + attachmentName
                    + "']/../../div[@class='meta']/span[@class='date']")).toString().replaceFirst("on", "");
    }

    public boolean attachmentExistsByFileName(String attachmentName)
    {
        try {
            getDriver().findElement(
                By.xpath("//a[@title='Download this attachment' and text()='" + attachmentName + "']"));
        } catch (NoSuchElementException e) {
            return Boolean.valueOf(false);
        }
        return Boolean.valueOf(true);
    }
}
