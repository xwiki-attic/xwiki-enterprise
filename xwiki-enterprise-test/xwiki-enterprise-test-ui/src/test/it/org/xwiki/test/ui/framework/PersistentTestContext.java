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
package org.xwiki.test.ui.framework;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.xwiki.test.integration.XWikiExecutor;

/**
 * This is a container for holding all of the information which should persist throughout all of the tests.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class PersistentTestContext
{
    /** This starts and stops the wiki engine. */
    private final XWikiExecutor executor;

    private final WebDriver driver;

    /** Utility methods which should be available to tests and to pages. */
    private final TestUtils util = new TestUtils();

    public PersistentTestContext() throws Exception
    {
        this.executor = new XWikiExecutor(0);
        executor.start();

        // Ensure that we display page source information if a UI element fails to be found, for easier debugging.
        this.driver = new FirefoxDriver()
        {
            @Override
            public WebElement findElement(By by)
            {
                try {
                    return super.findElement(by);
                } catch (NoSuchElementException e) {
                    throw new NoSuchElementException("Failed to locate element from page source [" + getPageSource()
                        + "]", e);
                }
            }
        };
    }

    public PersistentTestContext(PersistentTestContext toClone)
    {
        this.executor = toClone.executor;
        this.driver = toClone.driver;
    }

    public WebDriver getDriver()
    {
        return this.driver;
    }

    /**
     * @return Utility class with functions not specific to any test or element.
     */
    public TestUtils getUtil()
    {
        return this.util;
    }

    public void shutdown() throws Exception
    {
        driver.close();
        executor.stop();
    }

    /**
     * Get a clone of this context which cannot be stopped by calling shutdown. this is needed so that individual tests
     * don't shutdown when AllTests ware being run.
     */
    public PersistentTestContext getUnstoppable()
    {
        return new PersistentTestContext(this)
        {
            public void shutdown()
            {
                // Do nothing, that's why it's unstoppable.
            }
        };
    }
}
