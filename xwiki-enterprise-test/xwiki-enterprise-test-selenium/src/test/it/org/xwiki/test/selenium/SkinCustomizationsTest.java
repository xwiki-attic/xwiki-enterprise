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

import junit.framework.Test;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

/**
 * Verify the skin customization features available in the Administration (like changing the default CSS, etc).
 * 
 * @version $Id$
 */
public class SkinCustomizationsTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite =
            new XWikiTestSuite("Verify the skin customization features " + "available in the Administration");
        suite.addTestSuite(SkinCustomizationsTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    @Override
    protected void tearDown() throws Exception
    {
        // Ensure that we reset the style
        openAdministrationPage();
        clickLinkWithText("Presentation");
        setFieldValue("XWiki.XWikiPreferences_0_stylesheet", "style.css");
        clickEditSaveAndContinue();
    }

    public void testChangeDefaultStyleCss() throws Exception
    {
        openAdministrationPage();
        clickLinkWithText("Presentation");
        setFieldValue("XWiki.XWikiPreferences_0_stylesheet", "somestyle.css");
        clickEditSaveAndContinue();
        open("Main", "WebHome");
        assertTrue(getSelenium().isElementPresent(
            "xpath=//head/link[contains(@href,'/skin/skins/colibri/somestyle.css')]"));
    }
}
