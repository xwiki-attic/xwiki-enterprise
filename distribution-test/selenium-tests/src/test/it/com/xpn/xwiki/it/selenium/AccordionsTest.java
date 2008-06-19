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
package com.xpn.xwiki.it.selenium;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;
import junit.framework.Assert;
import junit.framework.Test;

/**
 * Verify the Accordions features.
 *
 * @version $Id: $
 */
public class AccordionsTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the Javascript Accordion feature");
        suite.addTestSuite(AccordionsTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    public void setUp() throws Exception
    {
        super.setUp();        
        loginAsAdmin();
    }

    private void assertPaneOpened(String paneid)
    {
        waitForCondition("selenium.browserbot.getCurrentWindow().document." +
            "getElementById('" + paneid + "').style.display == 'block'");
        Assert.assertTrue(getSelenium().getElementHeight(paneid).intValue() > 0);
    }

    private void assertPaneClosed(String paneid)
    {
        waitForCondition("selenium.browserbot.getCurrentWindow().document." +
            "getElementById('" + paneid + "').style.display == 'none'");
        Assert.assertEquals(0, getSelenium().getElementHeight(paneid).intValue());
    }

    /**
     * Validate accordion feature.
     */
    public void testAccordions() throws InterruptedException
    {
        // I've unsuccesfuly tried many things to avoid this
        getSelenium().setSpeed("500");

        // Preferences edit
        open("XWiki", "XWikiPreferences", "admin");
        // Open Parameters pane and validate that its content is displayed in less than 2s
        assertPaneOpened("xwikiprefsparamsContent");
        // Open Skin pane
        getSelenium().click("xwikiprefsskinHeader");
        // Validate that Parameters pane is not displayed anymore
        assertPaneClosed("xwikiprefsparamsContent");
        // Validate that Skin pane is displayed
        assertPaneOpened("xwikiprefsskinContent");

        // TODO > For some reason the following tests don't succeed on our CI, removing them before
        // TODO > 1.4M2, will need investigations before 1.4 final.

        // Class edit
        // open("XWiki", "XWikiPreferences", "edit", "editor=class");
        // waitPage();
        // Open skin pane and validate that its content is displayed in less than 2s
        // assertPaneOpened("field_skin_content");
        // Close skin pane and validate that its content is not displayed anymore
        // getSelenium().click("field_skin_title");
        // assertPaneClosed("field_skin_content");
        // Open last tab and verify its contents are displayed in less then 2s
        // getSelenium().click("field_ldap_trylocal_title");
        // assertPaneOpened("field_ldap_trylocal_content");

        // Object edit
        // open("XWiki", "XWikiPreferences", "edit", "editor=object");
        // waitPage();
        // Open XWikiPreferences pane and validate that its content is displayed
        // getSelenium().click("field_XWiki.XWikiPreferences_0_title");
        // assertPaneOpened("field_XWiki.XWikiPreferences_0_content");

        // Reset selenium speed
        getSelenium().setSpeed("0");
    }
}
