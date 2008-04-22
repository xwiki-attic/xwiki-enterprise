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
package com.xpn.xwiki.it.xmlrpc;

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.Page;

/**
 * Verifies that all pages in the default wiki have a parent
 *
 * @version $Id: $
 */
public class OrphanedPageTest extends TestCase
{
    private String fullPageName;

    private Confluence rpc;

    public OrphanedPageTest(String fullPageName)
    {
        super("testPageIsOrphaned");

        this.fullPageName = fullPageName;
    }

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();

        String path =
            System.getProperty("localRepository") + "/" + System.getProperty("pathToXWikiXar");

        String patternFilter = System.getProperty("documentsToTest");

        List pageNames = XhtmlValidityTest.readXarContents(path, patternFilter);
        Iterator it = pageNames.iterator();
        while (it.hasNext()) {
            suite.addTest(new OrphanedPageTest((String) it.next()));
        }

        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        rpc = new Confluence("http://127.0.0.1:8080/xwiki/xmlrpc");
        rpc.login("Admin", "admin");

        // TODO Until we find a way to incrementally display the result of tests this stays
        System.out.println(getName());
    }

    protected void tearDown() throws Exception
    {
        rpc.logout();

        super.tearDown();
    }

    public String getName()
    {
        return "Checking orphans for " + fullPageName;
    }

    public void testPageIsOrphaned() throws Exception
    {
        Page page = rpc.getPage(fullPageName);
        assertTrue("Page " + page.getId() + " is orphaned!", isRoot(page) || hasParent(page));
    }

    public static boolean isRoot(Page page)
    {
        String pageId = page.getId();
        String pageName = pageId.substring(pageId.lastIndexOf(".") + 1);
        return pageName.equals("WebHome");
    }

    public static boolean hasParent(Page page)
    {
        String parentId = page.getParentId();
        return (parentId != null && parentId.length() > 0);
    }
}
