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

import org.apache.commons.lang.RandomStringUtils;
import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

/**
 * Tests the inline editor.
 * 
 * @version $Id$
 */
public class InlineEditorTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests the inline editor");
        suite.addTestSuite(InlineEditorTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    /**
     * Tests that pages can override the default property display mode using $context.setDisplayMode. See XWIKI-2436.
     */
    public void testEditModeCanBeSet()
    {
        String initialContent = null;
        try {
            editInWikiEditor("XWiki", "Admin");
            initialContent = getFieldValue("content");
            typeInWiki("{{velocity}}$xcontext.setDisplayMode('edit'){{/velocity}}\n" + initialContent);
            clickEditSaveAndView();
            assertElementPresent("XWiki.XWikiUsers_0_last_name");
        } finally {
            if (initialContent != null) {
                open("XWiki", "Admin", "save", "content=" + initialContent);
            }
        }
    }
}
