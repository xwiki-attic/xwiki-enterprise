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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.it.ui.framework.elements.editor.ChangePasswordPage;
import org.xwiki.it.ui.framework.elements.editor.PreferencesEditPage;

/** User profile, the preferences pane, view mode. */
public class PreferencesPage extends ViewPage
{
    @FindBy(xpath = "//a[@href='/xwiki/bin/inline/XWiki/Admin?category=preferences']")
    private WebElement editPreferences;

    @FindBy(xpath = "//div[@class='userPreferences']/dl[2]/dd")
    private WebElement userType;

    @FindBy(xpath = "//div[@class='userPreferences']/dl[1]/dd")
    private WebElement defaultEditorToUse;

    @FindBy(xpath = "//a[@id='changePassword']")
    private WebElement changePassword;

    public String getDefaultEditor()
    {
        return this.defaultEditorToUse.getText();
    }

    public String getUserType()
    {
        return this.userType.getText();
    }

    public PreferencesEditPage editPreferences()
    {
        this.editPreferences.click();
        return new PreferencesEditPage();
    }

    public ChangePasswordPage changePassword()
    {
        this.changePassword.click();
        return new ChangePasswordPage();
    }
}
