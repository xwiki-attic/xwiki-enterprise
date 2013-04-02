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
package org.xwiki.test.cluster.framework;

import org.junit.Test;
import org.xwiki.test.integration.XWikiExecutor;
import org.xwiki.test.rest.framework.AbstractHttpTest;

/**
 * Base class for REST based clustering integration test.
 * 
 * @version $Id$
 */
// TODO: Fix this, it's wrong to reuse AbstractHttpTest from the REST Tests module
public abstract class AbstractClusterHttpTest extends AbstractHttpTest
{
    @Test
    @Override
    public void testRepresentation() throws Exception
    {
    }

    protected void switchXWiki(int index)
    {
        setPort(Integer.valueOf(XWikiExecutor.DEFAULT_PORT) + index);
    }
}
