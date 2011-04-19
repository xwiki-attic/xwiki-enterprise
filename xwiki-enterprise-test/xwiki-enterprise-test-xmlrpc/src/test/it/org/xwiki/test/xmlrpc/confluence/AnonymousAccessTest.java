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
package org.xwiki.test.xmlrpc.confluence;

import java.util.List;

import junit.framework.TestCase;
import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.SpaceSummary;

public class AnonymousAccessTest extends TestCase
{
    private Confluence rpc; // xml-rpc proxy

    public void setUp() throws Exception
    {
        super.setUp();

        // no login  = anonymous access 
        rpc = new Confluence("http://127.0.0.1:8080/xwiki/xmlrpc");
    }

    public void testReadSomePagesWhenNotLoggedIn() throws Exception
    {
        List spaces = rpc.getSpaces();
        for (int i = 0; i < spaces.size(); i++) {
            SpaceSummary spaceSummary = (SpaceSummary) spaces.get(i);
            String key = spaceSummary.getKey();

            // Only read pages from the Main space in this test since we're sure Guest users
            // are allowed to read them.
            if (key.equals("Main")) {
                List pages = rpc.getPages(key);
                for (int j = 0; j < pages.size(); j++) {
                    PageSummary pageSummary = (PageSummary) pages.get(j);
                    String id = pageSummary.getId();
                    rpc.getPage(id);
                }
            }
        }
    }

    public void testReadUnauthorizedPage()
    {
        try {
            rpc.getPage("Scheduler.WebHome");
            fail("Should have thrown an exception here");
        } catch (Exception expected) {
            assertTrue(expected.getMessage().contains(
                "Page 'Scheduler.WebHome' cannot be accessed"));
        }
    }
}
