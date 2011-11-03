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
package org.xwiki.test.po.extension.client;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.po.administration.AdministrationSectionPage;

/**
 * Represents the actions possible on the Extension Manager Administration Page.
 * 
 * @version $Id$
 * @since 3.2M3
 */
public class ExtensionsAdminPage extends AdministrationSectionPage
{
    @FindBy(id = "extensionpattern")
    private WebElement extensionPattern;

    @FindBy(xpath = "//input[@type='submit'][@name='actionsearch']")
    private WebElement searchButton;

    @FindBy(id = "extensionid")
    private WebElement extensionId;

    @FindBy(id = "extensionversion")
    private WebElement extensionVersion;

    @FindBy(xpath = "//input[@type='submit'][@name='actionresolve']")
    private WebElement resolveButton;

    @FindBy(xpath = "//input[@type='submit'][@name='actioninstall']")
    private WebElement installButton;

    public static ExtensionsAdminPage gotoPage()
    {
        ExtensionsAdminPage page = new ExtensionsAdminPage();
        page.getDriver().get(page.getURL());

        return page;
    }

    public ExtensionsAdminPage()
    {
        super("Extensions");
    }

    public String getExtensionPattern()
    {
        return this.extensionPattern.getAttribute("value");
    }

    public void setExtensionPattern(String extensionPattern)
    {
        this.extensionPattern.clear();
        this.extensionPattern.sendKeys(extensionPattern);
    }

    public ExtensionsAdminPage clickSearchButton()
    {
        this.searchButton.click();
        
        return new ExtensionsAdminPage();
    }

    public String getExtensionId()
    {
        return this.extensionId.getAttribute("value");
    }

    public void setExtensionId(String extensionId)
    {
        this.extensionId.clear();
        this.extensionId.sendKeys(extensionId);
    }

    public String getExtensionVersion()
    {
        return this.extensionVersion.getAttribute("value");
    }

    public void setExtensionVersion(String extensionVersion)
    {
        this.extensionVersion.clear();
        this.extensionVersion.sendKeys(extensionVersion);
    }
    
    public ResolveExtensionsAdminPage clickResolveButton()
    {
        this.resolveButton.click();
        
        return new ResolveExtensionsAdminPage();
    }

    public ExtensionsAdminPage clickInstallButton()
    {
        this.installButton.click();
        
        return new ExtensionsAdminPage();
    }
}
