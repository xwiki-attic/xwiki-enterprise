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
package org.xwiki.test.xmlrpc;

import java.util.Date;

import org.xwiki.xmlrpc.model.XWikiPage;

/**
 * 
 * @version $Id$
 */
public class PagesTestWithGuest extends AbstractXWikiXmlRpcTest
{
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        try {
            this.rpc.getPage(TestConstants.TEST_PAGE);
        } catch (Exception e) {
            XWikiPage page = new XWikiPage();
            page.setId(TestConstants.TEST_PAGE);
            page.setTitle("Test page");
            String content =
                String.format("Modified by org.xwiki.xmlrpc @ %s (This will be version: %d)\n", new Date(),
                    page.getVersion() + 1);
            page.setContent(content);
            this.rpc.storePage(page);
        }

        try {
            this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        } catch (Exception e) {
            XWikiPage page = new XWikiPage();
            page.setId(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
            page.setTitle("Test page");
            String content =
                String.format("Modified by org.xwiki.xmlrpc @ %s (This will be version: %d)\n", new Date(),
                    page.getVersion() + 1);
            page.setContent(content);
            this.rpc.storePage(page);
        }

        this.rpc.logout();
    }

    public void testRenderContent() throws Exception
    {
        TestUtils.banner("TEST: renderContent()");
        
        // Test rendering document content
        String html = this.rpc.renderContent(TestConstants.TEST_SPACE, TestConstants.TEST_PAGE, "");
        System.out.format("Rendered content: '%s'\n", html);

        assertTrue(html.length() != 0);

        // Test rendering provided content
        html =
            this.rpc.renderContent(TestConstants.TEST_SPACE, TestConstants.TEST_PAGE,
                "{{groovy}}print 'programming'{{/groovy}}");
        System.out.format("Rendered content: '%s'\n", html);

        assertTrue(html.length() != 0);
        assertTrue(!"<p>programming</p>".equals(html));
    }
}
