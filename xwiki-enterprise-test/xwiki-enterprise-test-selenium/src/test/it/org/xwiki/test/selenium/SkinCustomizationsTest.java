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
package org.xwiki.test.selenium;

import org.junit.After;
import org.junit.Test;
import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;

import static org.junit.Assert.*;

/**
 * Verify the skin customization features available in the Administration (like changing the default CSS, etc).
 * 
 * @version $Id$
 */
public class SkinCustomizationsTest extends AbstractXWikiTestCase
{
    @After
    public void tearDown()
    {
        // Ensure that we reset the style
        openAdministrationPage();
        clickLinkWithText("Presentation");
        setFieldValue("XWiki.XWikiPreferences_0_stylesheet", "style.css");
        clickEditSaveAndContinue();
    }

    @Test
    public void testChangeDefaultStyleCss() throws Exception
    {
        openAdministrationPage();
        clickLinkWithText("Presentation");
        // style.less.vm is not a valid CSS file but it will confirm that the default style has been changed.
        // we need to set an existing file otherwise the generated URL could be messy.
        setFieldValue("XWiki.XWikiPreferences_0_stylesheet", "less/style.less.vm");
        clickEditSaveAndContinue();
        open("Main", "WebHome");
        assertTrue(getSelenium().isElementPresent(
            "xpath=//head/link[contains(@href,'/skin/skins/flamingo/less/style.less.vm')]"));

    }
}
