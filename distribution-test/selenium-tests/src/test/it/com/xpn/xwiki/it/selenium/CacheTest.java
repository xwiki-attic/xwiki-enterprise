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

import junit.framework.Test;

/**
 * Verify the caching features of XWiki.
 *
 * @version $Id: $
 */
public class CacheTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the caching features of XWiki");
        suite.addTestSuite(CacheTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    /**
     * Tests that the document dates are always of the type java.util.Date, as hibernate returns
     * java.sql.Timestamp, which is not entirely compatible with java.util.Date. 
     */
    public void testDateClass()
    {
        open("/xwiki/bin/edit/Main/TestDateClass?editor=wiki");
        setFieldValue("content", "$xwiki.flushCache()\n$xwiki.getDocument(\"Main.WebHome\").date.class");
        clickEditSaveAndView();
        assertTextPresent("java.util.Date");
    }
}
