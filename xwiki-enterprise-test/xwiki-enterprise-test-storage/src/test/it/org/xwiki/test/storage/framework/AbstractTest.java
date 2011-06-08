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
package org.xwiki.test.storage.framework;

import org.xwiki.test.integration.XWikiExecutor;

/**
 * To be extended by all Test Classes. Provides access to information such as the port number.
 *
 * @version $Id$
 * @since 3.0RC1
 */
public class AbstractTest
{
    private static XWikiExecutor executor;

    /** Used so that AllTests can set the executor. */
    public static void setExecutor(final XWikiExecutor executor)
    {
        AbstractTest.executor = executor;
    }

    protected short getPort()
    {
        return (short) executor.getPort();
    }

    protected String getAddressPrefix()
    {
        return "http://127.0.0.1:" + this.getPort() + "/xwiki/bin/";
    }
}
