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
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Common page object for all Profile page tabs (profile tab, preferences tab, watchlist tab).
 * 
 * @version $Id$
 * @since 3.0M1
 */
public class AbstractUserProfilePage extends ViewPage
{
    @FindBy(xpath = "//a[@href='?category=profile']")
    private WebElement profile;

    @FindBy(xpath = "//a[@href='?category=preferences']")
    private WebElement preferences;

    @FindBy(xpath = "//div[@id='preferencesPane']/div[1]/div/dl[2]/dd[1]")
    private WebElement defaultEditorToUse;

    @FindBy(xpath = "//div[@id='preferencesPane']/div[1]/div/dl[2]/dd[2]")
    private WebElement userType;

    protected final String targetUsername;

    public AbstractUserProfilePage(String username)
    {
        this.targetUsername = username;
    }

    public PreferencesUserProfilePage switchToPreferences()
    {
        this.preferences.click();
        return new PreferencesUserProfilePage(this.targetUsername);
    }

    public ProfileUserProfilePage switchToProfile()
    {
        this.profile.click();
        return new ProfileUserProfilePage(this.targetUsername);
    }

    public String getDefaultEditorToUse()
    {
        return this.defaultEditorToUse.getText();
    }

    public String getUserType()
    {
        return this.userType.getText();
    }
}
