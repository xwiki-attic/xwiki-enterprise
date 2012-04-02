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
package org.xwiki.test.ui.appwithinminutes;

import junit.framework.Assert;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
import org.xwiki.test.po.appwithinminutes.ApplicationCreatePage;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Tests the first step of the App Within Minutes wizard.
 * 
 * @version $Id$
 * @since 3.4M1
 */
public class ApplicationNameTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The error message displayed when we try to create an application with an empty name.
     */
    private static final String EMPTY_APP_NAME_ERROR_MESSAGE = "Please enter the application name.";

    /**
     * The error message displayed when we input an application name that can't be used to compute a valid class name.
     */
    private static final String INVALID_CLASS_NAME_ERROR_MESSAGE =
        "We can't extract a valid class name from the application name you entered.";

    /**
     * The warning message displayed when we input the name of an existing application.
     */
    public static final String APP_NAME_USED_WARNING_MESSAGE = "This application already exists.";

    /**
     * Try to create an application with an empty name using the next step button.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testEmptyAppNameWithNextStepButton()
    {
        ApplicationCreatePage appCreatePage = ApplicationCreatePage.gotoPage();
        Assert.assertFalse(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Try to move to the next step without typing the application name.
        appCreatePage.clickNextStep();
        appCreatePage.waitForApplicationNameError();
        Assert.assertTrue(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Type the application name.
        appCreatePage.setApplicationName("A");
        appCreatePage.waitForApplicationNamePreview();
        Assert.assertFalse(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Clear the application name using the Backspace key.
        appCreatePage.getApplicationNameInput().sendKeys(Keys.BACK_SPACE);
        appCreatePage.waitForApplicationNameError();
        Assert.assertTrue(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Try to create the application even if the error message is displayed.
        appCreatePage.clickNextStep();
        Assert.assertTrue(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Fix the application name and move to the next step.
        appCreatePage.setApplicationName(getTestMethodName());
        appCreatePage.waitForApplicationNamePreview();
        Assert.assertEquals("Class: " + getTestMethodName(), appCreatePage.clickNextStep().getDocumentTitle());
    }

    /**
     * Try to create an application with an empty name using the Enter key.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testEmptyAppNameWithEnter()
    {
        ApplicationCreatePage appCreatePage = ApplicationCreatePage.gotoPage();
        Assert.assertFalse(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Press Enter key without typing the application name.
        appCreatePage.getApplicationNameInput().sendKeys(Keys.RETURN);
        appCreatePage.waitForApplicationNameError();
        Assert.assertTrue(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Type the application name.
        appCreatePage.setApplicationName("B");
        appCreatePage.waitForApplicationNamePreview();
        Assert.assertFalse(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Clear the application name using the Backspace key.
        appCreatePage.getApplicationNameInput().sendKeys(Keys.BACK_SPACE);
        appCreatePage.waitForApplicationNameError();
        Assert.assertTrue(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Try to create the application even if the error message is displayed.
        appCreatePage.getApplicationNameInput().sendKeys(Keys.RETURN);
        Assert.assertTrue(appCreatePage.getContent().contains(EMPTY_APP_NAME_ERROR_MESSAGE));

        // Fix the application name and move to the next step using the Enter key.
        appCreatePage.setApplicationName(getTestMethodName());
        appCreatePage.waitForApplicationNamePreview();
        appCreatePage.getApplicationNameInput().sendKeys(Keys.RETURN);
        Assert.assertEquals("Class: " + getTestMethodName(), new ViewPage().getDocumentTitle());
    }

    /**
     * Try to create an application with a name that can't be used to compute a valid class name.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testInvalidAppName()
    {
        ApplicationCreatePage appCreatePage = ApplicationCreatePage.gotoPage();
        Assert.assertFalse(appCreatePage.getContent().contains(INVALID_CLASS_NAME_ERROR_MESSAGE));

        // Set an invalid application name.
        appCreatePage.setApplicationName("1");
        appCreatePage.waitForApplicationNameError();
        Assert.assertTrue(appCreatePage.getContent().contains(INVALID_CLASS_NAME_ERROR_MESSAGE));

        // Fix the application name.
        appCreatePage.getApplicationNameInput().sendKeys("Z");
        appCreatePage.waitForApplicationNamePreview();
        Assert.assertFalse(appCreatePage.getContent().contains(INVALID_CLASS_NAME_ERROR_MESSAGE));
        Assert.assertTrue(appCreatePage.getContent().contains("ZClass"));

        // Revert the fix.
        appCreatePage.getApplicationNameInput().sendKeys(Keys.BACK_SPACE);
        appCreatePage.waitForApplicationNameError();
        Assert.assertTrue(appCreatePage.getContent().contains(INVALID_CLASS_NAME_ERROR_MESSAGE));

        // Try to move to the next step even if the application name is invalid.
        appCreatePage.clickNextStep();
        Assert.assertTrue(appCreatePage.getContent().contains(INVALID_CLASS_NAME_ERROR_MESSAGE));

        // Test class name filtering.
        appCreatePage.setApplicationName("7\u0103?\u021B>/t:e-st_@28");
        appCreatePage.waitForApplicationNamePreview();
        Assert.assertEquals("Class: \u0103\u021Bte-st_28", appCreatePage.clickNextStep().getDocumentTitle());
    }

    /**
     * Try to input the name of an existing application.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testExistingAppName()
    {
        ApplicationCreatePage appCreatePage = ApplicationCreatePage.gotoPage();
        Assert.assertFalse(appCreatePage.getContent().contains(APP_NAME_USED_WARNING_MESSAGE));

        // Type the name of an existing space.
        appCreatePage.setApplicationName("Blog");
        appCreatePage.waitForApplicationNamePreview();
        Assert.assertTrue(appCreatePage.getContent().contains(APP_NAME_USED_WARNING_MESSAGE));

        // Proceed to the next step.
        Assert.assertTrue(appCreatePage.clickNextStep().hasBreadcrumbContent("The Wiki Blog", false));
    }
}
