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
package org.xwiki.test.ui.administration.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the actions possible on the Annotations Administration Page.
 * 
 * @version $Id&
 * @since 2.5M2
 */

public class AnnotationsPage extends AdministrationSectionPage
{
    public AnnotationsPage()
    {
        super("Annotations");
    }

    @FindBy(id = "AnnotationCode.AnnotationConfig_AnnotationCode.AnnotationConfig_0_activated")
    private WebElement annotationsAreActivatedYes;

    @FindBy(id = "AnnotationCode.AnnotationConfig_AnnotationCode.AnnotationConfig_0_activated_false")
    private WebElement annotationsAreActivatedNo;

    @FindBy(id = "AnnotationCode.AnnotationConfig_AnnotationCode.AnnotationConfig_0_displayed")
    private WebElement displayAnnotationsByDefaultYes;

    @FindBy(id = "AnnotationCode.AnnotationConfig_AnnotationCode.AnnotationConfig_0_displayed_false")
    private WebElement displayAnnotationsByDefaultNo;

    @FindBy(id = "AnnotationCode.AnnotationConfig_AnnotationCode.AnnotationConfig_0_displayHighlight")
    private WebElement displayAnnotationsHighlightByDefaultYes;

    @FindBy(id = "AnnotationCode.AnnotationConfig_AnnotationCode.AnnotationConfig_0_displayHighlight_false")
    private WebElement displayAnnotationsHighlightByDefaultNo;

    public void gotoPage()
    {
        getUtil().gotoPage("XWiki", "XWikiPreferences", "admin", "section=Annotations");
    }

    public void activateAnnotations()
    {
        this.annotationsAreActivatedYes.click();
    }

    public void deactivateAnnotations()
    {
        this.annotationsAreActivatedNo.click();
    }

    public void displayAnnotationsByDefault()
    {
        this.displayAnnotationsByDefaultYes.click();
    }

    public void hideAnnotationsByDefault()
    {
        this.displayAnnotationsByDefaultNo.click();
    }

    public void displayAnnotationsHighlightByDefault()
    {
        this.displayAnnotationsHighlightByDefaultYes.click();
    }

    public void hideAnnotationsHighlightByDefault()
    {
        this.displayAnnotationsHighlightByDefaultNo.click();
    }
}
