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
package org.xwiki.it.ui.administration.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the actions possible on the Global Rights Administration Page.
 * 
 * @version $Id$
 * @since 2.4RC1
 */
public class GlobalRightsPage extends AdminSectionPage
{
    public GlobalRightsPage()
    {
        super("Rights");
    }

    @FindBy(id = "authenticate_view")
    WebElement forceAuthenticatedViewLink;

    /** Checks the "always authenticate user for view" option. */
    public void forceAuthenticatedView()
    {
        if (!this.forceAuthenticatedViewLink.getAttribute("alt").equals("yes")) {
            this.forceAuthenticatedViewLink.click();
            waitUntilElementHasAttributeValue(By.id(this.forceAuthenticatedViewLink.getAttribute("id")), "alt", "yes");
        }
    }

    /** Unchecks the "always authenticate user for view" option. */
    public void unforceAuthenticatedView()
    {
        if (!this.forceAuthenticatedViewLink.getAttribute("alt").equals("no")) {
            this.forceAuthenticatedViewLink.click();
            waitUntilElementHasAttributeValue(By.id(this.forceAuthenticatedViewLink.getAttribute("id")), "alt", "no");
        }
    }
}
