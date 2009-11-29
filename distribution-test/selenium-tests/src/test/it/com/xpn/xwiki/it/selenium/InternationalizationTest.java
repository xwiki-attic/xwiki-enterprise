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

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.ColibriSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Validates the support for non-ASCII characters.
 * 
 * @version $Id$
 */
public class InternationalizationTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Validates the support for non-ASCII characters");
        suite.addTestSuite(InternationalizationTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        loginAsAdmin();
        deletePage("\u0219", "WebHome");
        deletePage("Main", "\u0219");
    }

    @Override
    public void tearDown() throws Exception
    {
        deletePage("\u0219", "WebHome");
        deletePage("Main", "\u0219");
        logout();
    }

    /**
     * Checks that non-ASCII characters are allowed in the space name.
     */
    public void testCreateNonAsciiSpace()
    {
        open("Main", "WebHome");
        clickLinkWithLocator("link=Create Space");
        setFieldValue("title", "\u0219");
        submit();
        // Check the title field
        assertElementPresent("//input[@name='title' and @value='\u0219']");
        // Check the document space in the metadata
        assertElementPresent("//meta[@name='space' and @content='\u0219']");
        // Save the document
        clickEditSaveAndView();
        // Check the document space in the metadata
        assertElementPresent("//meta[@name='space' and @content='\u0219']");
    }

    /**
     * Checks that non-ASCII characters are allowed in the page name.
     */
    public void testCreateNonAsciiPage()
    {
        open("Main", "WebHome");
        clickLinkWithLocator("link=Create Page");
        setFieldValue("title", "\u0219");
        submit();
        // Check the title field
        assertElementPresent("//input[@name='title' and @value='\u0219']");
        // Check the document name in the metadata
        assertElementPresent("//meta[@name='page' and @content='\u0219']");
        // Save the document
        clickEditSaveAndView();
        // Check the document name in the metadata
        assertElementPresent("//meta[@name='page' and @content='\u0219']");
    }
}
