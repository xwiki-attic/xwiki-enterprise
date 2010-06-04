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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the part of the home page that lists the existing spaces and allows the user to create a new space.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class SpacesPane extends BaseElement
{
    /**
     * The element that needs to be clicked to show/hide the space creation form.
     */
    @FindBy(linkText = "Create a new space")
    private WebElement spaceCreateFormToggleSwitch;

    /**
     * The text field used to input the space name.
     */
    @FindBy(id = "spSpaceCreateTextInput")
    private WebElement spaceNameTextField;

    /**
     * Shows the space creation form, fills the space name text field with the given space name and submits the form.
     * 
     * @param spaceName the name of the space to create
     * @return the WYSIWYG edit page for the space home page
     */
    public WYSIWYGEditPage createSpace(String spaceName)
    {
        spaceCreateFormToggleSwitch.click();
        this.spaceNameTextField.clear();
        this.spaceNameTextField.sendKeys(spaceName);
        this.spaceNameTextField.submit();
        return new WYSIWYGEditPage();
    }
}
