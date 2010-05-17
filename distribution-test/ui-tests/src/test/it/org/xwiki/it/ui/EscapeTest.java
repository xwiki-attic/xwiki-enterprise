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
package org.xwiki.it.ui;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.it.ui.framework.TestUtils;

/**
 * Test various character escaping bugs.
 * <p>
 * NOTE: {@link WebDriver#getPageSource()} XML-escapes the output in some cases, but not inside HTML comments
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class EscapeTest extends AbstractAdminAuthenticatedTest
{
    /** XML significant characters */
    private static final String XML_CHARS = "<>'&\"";

    @Test
    public void testEditReflectedXSS()
    {
        // tests for XWIKI-4758, XML symbols should be escaped
        String page = "<!-- " + XML_CHARS + " -->";
        TestUtils.gotoPage("Main", TestUtils.escapeURL(page), "edit", getDriver());
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testErrorTraceEscaping()
    {
        // tests for XWIKI-5170, XML symbols in the error trace should be escaped
        String rev = "</pre><!-- " + XML_CHARS + " -->";
        TestUtils.gotoPage("Main", "WebHome", "viewrev", "rev=" + TestUtils.escapeURL(rev), getDriver());
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testEditorEscaping()
    {
        // tests for XWIKI-5164, XML symbols in editor parameter should be escaped
        String str = "\"<!-- " + XML_CHARS + " -->";

        TestUtils.gotoPage("Main", "Page", "edit", "editor=" + TestUtils.escapeURL(str), getDriver());
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);

        TestUtils.gotoPage("Main", "Page", "edit", "editor=wysiwyg&section=" + TestUtils.escapeURL(str), getDriver());
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);

        TestUtils.gotoPage("Main", "Page", "edit", "editor=wiki&x-maximized=" + TestUtils.escapeURL(str), getDriver());
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    /**
     * Go to a working page after each test run to prevent failures in {@link #setUp()}
     */
    @After
    public void tearDown()
    {
        TestUtils.gotoPage("Main", "WebHome", getDriver());
    }
}
