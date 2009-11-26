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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xwiki.test.XWikiTestSetup;
import org.xwiki.xmlrpc.PagesTestWithGuest;

/**
 * A class listing all the XMLRPC Functional tests to execute. We need such a class (rather than
 * letting the JUnit Runner discover the different TestCases classes by itself) because we want to
 * start/stop XWiki before and after the tests start (but only once).
 *
 * @version $Id$
 */
public class AllTests extends TestCase
{
    private static final String PATTERN = ".*" + System.getProperty("pattern", "");

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();

        // TODO: I don't like listing tests here as it means we can add a new TestCase class and
        // forget to add it here and the tests won't be run but we'll not know about it and we'll
        // think the tests are all running fine. I haven't found a simple solution to this yet
        // (there are complex solutions like searching for all tests by parsing the source tree).
        // I think there are TestSuite that do this out there but I haven't looked for them yet.
        addTest(suite, OrphanedPageTest.suite(), OrphanedPageTest.class);

        addTestCase(suite, AnonymousAccessTest.class);
        addTestCase(suite, AttachmentsTest.class);
        addTestCase(suite, CommentsTest.class);
        addTestCase(suite, PagesTest.class);
        addTestCase(suite, PagesTestWithGuest.class);
        addTestCase(suite, SpacesTest.class);

        addTestCase(suite, org.xwiki.xmlrpc.AttachmentsTest.class);
        addTestCase(suite, org.xwiki.xmlrpc.AuthenticationTest.class);
        addTestCase(suite, org.xwiki.xmlrpc.CommentsTest.class);
        addTestCase(suite, org.xwiki.xmlrpc.PagesTest.class);
        addTestCase(suite, org.xwiki.xmlrpc.SpacesTest.class);
        addTestCase(suite, org.xwiki.xmlrpc.XWikiClassesTest.class);
        addTestCase(suite, org.xwiki.xmlrpc.XWikiObjectsTest.class);
        addTestCase(suite, org.xwiki.xmlrpc.SearchTest.class);
        addTestCase(suite, org.xwiki.xmlrpc.RenderingTest.class);

        return new XWikiTestSetup(suite);
    }

    private static void addTestCase(TestSuite suite, Class testClass) throws Exception
    {
        if (testClass.getName().matches(PATTERN)) {
            suite.addTest(new TestSuite(testClass));
        }
    }

    private static void addTest(TestSuite suite, Test test, Class testClass) throws Exception
    {
        if (testClass.getName().matches(PATTERN)) {
            suite.addTest(test);
        }
    }
}
