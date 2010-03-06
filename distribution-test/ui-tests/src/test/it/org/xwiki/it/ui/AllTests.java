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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.runner.RunWith;
import org.xwiki.it.ui.framework.TestUtils;
import org.xwiki.test.XWikiExecutor;

/**
 * Runs all functional tests found in the classpath.
 *
 * @version $Id$
 * @since 2.3M1
 */
@RunWith(ClasspathSuite.class)
public class AllTests
{
    private static XWikiExecutor executor;

    @BeforeClass
    public static void init() throws Exception
    {
        // We set a system property in order to specify we're running in a test suite so that
        // AbstractTest.shutdown() doesn't close the WebDriver when a Test finishes (so that we reuse the same
        // driver instance for all tests in this suite).
        System.setProperty("xwiki.alltests", "true");

        // Start XE
        executor = new XWikiExecutor(0);
        executor.start();
    }

    @AfterClass
    public static void shutdown() throws Exception
    {
        TestUtils.closeDriver();

        // Stop XE
        executor.stop();
    }
}
