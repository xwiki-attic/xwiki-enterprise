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
        XWikiTestSuite suite = new XWikiTestSuite("Verify the Accordions features");
        suite.addTestSuite(AccordionsTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    public void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    /**
     * Validate accordions features in wiki preferences edition.
     */
    public void testPreferencesEdition()
    {
        getSelenium().setSpeed("1000");
        open(getUrl("XWiki", "XWikiPreferences", "admin"));
        // Open Parameters tab and validate that its contents are displayed in less than
        // 1000 milliseconds
        Assert.assertTrue(getSelenium().getElementHeight("xwikiprefsparamsContent").intValue() > 0);
        Assert.assertTrue(getSelenium().getAttribute("xwikiprefsparamsContent@style")
            .indexOf("display: block;") > -1);
        // Close Parameters tab and validate that its content are not displayed
        getSelenium().click("xwikiprefsskinHeader");
        Assert
            .assertEquals(0, getSelenium().getElementHeight("xwikiprefsparamsContent").intValue());
        Assert.assertTrue(getSelenium().getAttribute("xwikiprefsparamsContent@style")
            .indexOf("display: none;") > -1);
        // Verify that Skin tab is open and its content is displayed
        Assert.assertTrue(getSelenium().getElementHeight("xwikiprefsskinContent").intValue() > 0);
        Assert.assertTrue(getSelenium().getAttribute("xwikiprefsskinContent@style")
            .indexOf("display: block;") > -1);
        getSelenium().setSpeed("0");
    }

    /**
     * Validate accordions features in XWiki.XWikiPreferences class edition.
     */
    public void testClassEdition()
    {
        getSelenium().setSpeed("1000");
        open(getUrl("XWiki", "XWikiPreferences", "edit", "editor=class"));
        // Open Skin tab and validate that its contents are displayed
        Assert.assertTrue(getSelenium().getElementHeight("field_skin_content").intValue() > 0);
        Assert.assertTrue(
            getSelenium().getAttribute("field_skin_content@style").indexOf("display: block;") > -1);
        // Close Skin tab and validate that its content is not displayed
        getSelenium().click("field_skin_title");
        Assert.assertEquals(0, getSelenium().getElementHeight("field_skin_content").intValue());
        Assert.assertTrue(
            getSelenium().getAttribute("field_skin_content@style").indexOf("display: none;") > -1);
        // Open last tab and verify its contents are displayed in less then 1000 milliseconds
        getSelenium().click("field_ldap_trylocal_title");
        Assert.assertTrue(
            getSelenium().getElementHeight("field_ldap_trylocal_content").intValue() > 0);
        Assert.assertTrue(getSelenium().getAttribute("field_ldap_trylocal_content@style")
            .indexOf("display: block;") > -1);
        getSelenium().setSpeed("0");
    }

    /**
     * Validate accordions features in XWiki.XWikiPreferences object edition.
     */
    public void testObjectEdition()
    {
        getSelenium().setSpeed("1000");
        open(getUrl("XWiki", "XWikiPreferences", "edit", "editor=object"));
        // Open XWikiPreferences tab and validate that its contents are displayed
        getSelenium().click("field_XWiki.XWikiPreferences_0_title");
        Assert.assertTrue(getSelenium()
            .getElementHeight("field_XWiki.XWikiPreferences_0_content").intValue() > 0);
        Assert.assertTrue(getSelenium()
            .getAttribute("field_XWiki.XWikiPreferences_0_content@style")
            .indexOf("display: block;") > -1);
        getSelenium().setSpeed("0");
    }
}
