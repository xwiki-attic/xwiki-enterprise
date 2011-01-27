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
package org.xwiki.test.ui.framework.elements.editor.wysiwyg;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Models the image upload wizard step that is accessible when inserting or editing an image with the WYSIWYG editor.
 * 
 * @version $Id$
 * @since 3.0M2
 */
public class UploadImagePane extends WizardStepElement
{
    /**
     * The file input used to specify the path to the image to upload.
     */
    @FindBy(name = "filepath")
    private WebElement fileInput;

    /**
     * The upload button.
     */
    @FindBy(xpath = "//button[. = 'Upload']")
    private WebElement uploadButton;

    /**
     * {@inheritDoc}
     * 
     * @see WizardStepElement#waitToLoad()
     */
    @Override
    public UploadImagePane waitToLoad()
    {
        super.waitToLoad();
        waitUntilElementIsVisible(By.className("xUploadPanel"));
        return this;
    }

    /**
     * Fills the URL with the specified image path.
     * 
     * @param imagePath the path to the image to upload in URL form
     */
    public void setImageToUpload(String imagePath)
    {
        fileInput.clear();
        fileInput.sendKeys(imagePath);
    }

    /**
     * Clicks on the upload button and waits for the image to be uploaded.
     * 
     * @return the pane used to configure the uploaded image before inserting it into the content
     */
    public ImageConfigPane clickUploadImage()
    {
        uploadButton.click();
        return new ImageConfigPane().waitToLoad();
    }
}
