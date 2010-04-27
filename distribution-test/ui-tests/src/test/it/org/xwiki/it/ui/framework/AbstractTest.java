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
package org.xwiki.it.ui.framework;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.xwiki.test.XWikiExecutor;

/**
 * To be extended by all Test Classes. Allows to start/stop the Web Driver and get access to it. 
 *
 * @version $Id$
 * @since 2.3M1
 */
public class AbstractTest
{
    private static XWikiExecutor executor;

    @BeforeClass
    public static void init() throws Exception
    {
        if (!Boolean.parseBoolean(System.getProperty("xwiki.alltests"))) {
            // Start XE
            executor = new XWikiExecutor(0);
            executor.start();
        }

        if (TestUtils.getDriver() == null) {
            TestUtils.initDriver();
        }
    }

    @AfterClass
    public static void shutdown() throws Exception
    {
        // Only close if we're not part of the AllTests test suite
        if (!Boolean.parseBoolean(System.getProperty("xwiki.alltests"))) {
            TestUtils.closeDriver();
            // Stop XE
            executor.stop();
        }
    }

    public WebDriver getDriver()
    {
        return TestUtils.getDriver();
    }
}
