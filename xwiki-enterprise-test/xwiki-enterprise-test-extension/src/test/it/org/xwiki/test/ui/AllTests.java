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
package org.xwiki.test.ui;

import java.util.List;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.xwiki.test.integration.XWikiExecutor;
import org.xwiki.test.integration.XWikiExecutorSuite;
import org.xwiki.test.po.PageObjectSuite;

/**
 * Runs all functional tests found in the classpath.
 * 
 * @version $Id$
 */
@RunWith(PageObjectSuite.class)
public class AllTests
{
    @XWikiExecutorSuite.PreStart
    public void preInitialize(List<XWikiExecutor> executors) throws Exception
    {
        XWikiExecutor executor = executors.get(0);
     
        // Put self as extensions repository
        Properties properties = executor.loadXWikiProperties();
        properties.setProperty("extension.repositories", "self:xwiki:http://localhost:8080/xwiki/rest");
        executor.saveXWikiProperties(properties);
    }
}
