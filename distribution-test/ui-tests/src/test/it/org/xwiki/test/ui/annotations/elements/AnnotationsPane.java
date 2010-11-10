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
package org.xwiki.test.ui.annotations.elements;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * @version $Id$
 * @since 2.7RC1
 */
public class AnnotationsPane extends ViewPage
{
    private boolean isVisible = false;

    /**
     * Toggles visible/invisible the Annotations Tab.
     */
    public void toggleAnnotationsPane()
    {
        WebElement annotationsPane = getDriver().findElement(By.xpath("//div[@id='tmAnnotations']//a[@class='tme']"));
        annotationsPane.click();
        if (this.isVisible == true) {
            this.isVisible = false;
        }
        if (this.isVisible == false) {
            this.isVisible = true;
        }
    }

    /**
     * Checks the "Show Annotations" check box.
     */
    public void showAnnotations()
    {
        if (this.isVisible == false) {
            Assert.fail("Annotation Pane is hidden");
        }
        waitUntilElementIsVisible(By.className("annotationsettings"));
        WebElement checkBox = getDriver().findElement(By.id("annotationsdisplay"));
        if (checkBox.isSelected() == false) {
            checkBox.setSelected();
        }
    }

    /**
     * Un-Checks the "Show Annotations" checkbox.
     */
    public void hideAnnotations()
    {
        if (this.isVisible == false) {
            Assert.fail("Annotation Pane is hidden");
        }
        waitUntilElementIsVisible(By.className("annotationsettings"));
        WebElement checkBox = getDriver().findElement(By.id("annotationsdisplay"));
        if (checkBox.isSelected() == true) {
            checkBox.toggle();
        }
    }
}
