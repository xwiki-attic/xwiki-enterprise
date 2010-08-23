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
package org.xwiki.it.ui.framework.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.it.ui.framework.elements.BaseElement;

/**
 * Represents the actions possible on the Attachment Pane at the bottom of a page.
 * 
 * @version $Id:$
 * @since 2.5M1
 */
public class AttachmentsPane extends BaseElement
{
    @FindBy(id = "Attachmentspane")
    private WebElement pane;

    public void addFileToAttach(final String filePath)
    {
        List<WebElement> inputs = this.pane.findElements(By.id("xwikiuploadfile"));
        if (inputs.size() > 0 && inputs.get(0).getValue().length() == 0) {
            inputs.get(0).sendKeys(filePath);
            return;
        }

        this.pane.findElement(By.className("add-file-input")).click();

        inputs = this.pane.findElements(By.className("uploadFileInput"));
        for (WebElement input : inputs) {
            if (input.getValue().length() == 0) {
                input.sendKeys(filePath);
            }
        }
    }

    public void clickAttachFiles()
    {
        // TODO Id tag this button.
        this.pane.findElement(By.xpath("//div/span/input[@class='button'][@type='submit'][@value='Attach']")).click();
    }

    public List<WebElement> getAttachmentLinks()
    {
        final ArrayList<WebElement> links = new ArrayList<WebElement>();
        for (WebElement el : this.pane.findElements(By.className("information"))) {
            links.add(el.findElement(By.className("name")).findElement(By.tagName("a")));
        }
        return links;
    }
}
