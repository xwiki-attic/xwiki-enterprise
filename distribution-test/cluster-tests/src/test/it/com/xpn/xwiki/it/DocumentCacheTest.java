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
package com.xpn.xwiki.it;

import org.xwiki.rest.it.framework.TestUtils;

import com.xpn.xwiki.it.framework.AbstractClusterHttpTest;

/**
 * Verify the document cache update based on distributed events.
 * 
 * @version $Id$
 */
public class DocumentCacheTest extends AbstractClusterHttpTest
{
    public void testDocumentCacheSync() throws Exception
    {
        TestUtils.banner("testDocumentCacheSync()");

        // 1) edit a page on XWiki 0

        switchXWiki(0);
        setPageContent(getWiki(), "Test", "CacheSync", "content");
        assertEquals("content", getPageContent(getWiki(), "Test", "CacheSync"));

        // 2) modify content of the page on XWiki 1

        switchXWiki(1);
        setPageContent(getWiki(), "Test", "CacheSync", "modified content");
        assertEquals("modified content", getPageContent(getWiki(), "Test", "CacheSync"));

        // TODO: give some time to JGroups to send the message

        // ASSERT) the content in XWiki 0 should be the one set than in XWiki 1

        switchXWiki(0);
        assertEquals("modified content", getPageContent(getWiki(), "Test", "CacheSync"));
    }
}
