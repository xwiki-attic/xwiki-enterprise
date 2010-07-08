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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.Page;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.xpn.xwiki.plugin.packaging.Package;

/**
 * Verifies that all pages in the default wiki have a parent
 * 
 * @version $Id$
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

        String path = System.getProperty("localRepository") + "/" + System.getProperty("pathToXWikiXar");

        String patternFilter = System.getProperty("documentsToTest");

        List<String> pageNames = readXarContents(path, patternFilter);
        for (String pageName : pageNames) {
            suite.addTest(new OrphanedPageTest(pageName));
        }

        return suite;
    }

    public static List<String> readXarContents(String fileName, String patternFilter) throws Exception
    {
        FileInputStream fileIS = new FileInputStream(fileName);
        ZipInputStream zipIS = new ZipInputStream(fileIS);

        ZipEntry entry;
        Document tocDoc = null;
        while ((entry = zipIS.getNextEntry()) != null) {
            if (entry.getName().compareTo(Package.DefaultPackageFileName) == 0) {
                SAXReader reader = new SAXReader();
                tocDoc = reader.read(zipIS);
                break;
            }
        }

        if (tocDoc == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<String>();

        Element filesElement = tocDoc.getRootElement().element("files");
        List<Element> fileElementList = filesElement.elements("file");
        for (Element el : fileElementList) {
            String docFullName = el.getStringValue();

            if (patternFilter == null || docFullName.matches(patternFilter)) {
                result.add(docFullName);
            }
        }

        return result;
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        this.rpc = new Confluence("http://127.0.0.1:8080/xwiki/xmlrpc");
        this.rpc.login("Admin", "admin");

        // TODO Until we find a way to incrementally display the result of tests this stays
        System.out.println(getName());
    }

    protected void tearDown() throws Exception
    {
        this.rpc.logout();

        super.tearDown();
    }

    public String getName()
    {
        return "Checking orphans for " + fullPageName;
    }

    public void testPageIsOrphaned() throws Exception
    {
        Page page = this.rpc.getPage(fullPageName);
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
