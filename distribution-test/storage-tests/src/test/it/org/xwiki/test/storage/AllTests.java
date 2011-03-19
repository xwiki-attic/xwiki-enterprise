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
package org.xwiki.test.storage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.runner.RunWith;
import org.xwiki.test.storage.framework.PersistentTestContext;
import org.xwiki.test.storage.framework.AbstractTest;
import org.xwiki.test.storage.profiles.FilesystemAttachmentStorageProfile;

/**
 * Runs all functional tests found in the classpath.
 *
 * @version $Id$
 * @since 3.0M3
 */
@RunWith(ClasspathSuite.class)
public class AllTests
{
    /**
     * Because junit disallows any references which persist between tests,
     * there is a context which is static.
     */
    private static PersistentTestContext context;

    @BeforeClass
    public static void init() throws Exception
    {
        // TODO: How do we run every test in every profile?
        context = new PersistentTestContext(new FilesystemAttachmentStorageProfile());
        AbstractTest.setContext(context.getUnstoppable());
    }

    @AfterClass
    public static void shutdown() throws Exception
    {
        context.shutdown();
    }
}
