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

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.XWikiTestSuite;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import junit.framework.Test;

/**
 * Verify the Backlinks feature.
 *
 * @version $Id$
 */
public class BacklinksTest extends AbstractXWikiTestCase
{
    private static final String SYNTAX = "xwiki/1.0";
    
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the Backlinks feature");
        suite.addTestSuite(BacklinksTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    public void testBacklinksCreation() throws Exception
    {
        loginAsAdmin();

        // Create page which will have a backlink to it
        createPage("Test", "BacklinkTargetTest", "#foreach ($link in $doc.getBacklinks())\n"
            + "$link\n"
            + "#end", SYNTAX);

        // Create page pointing to the backlinked page
        createPage("Test", "BacklinkSourceTest", "[backlink>Test.BacklinkTargetTest]", SYNTAX);

        open("Test", "BacklinkTargetTest");
        assertEquals("Test.BacklinkSourceTest", getSelenium().getText("xwikicontent"));
    }
}
