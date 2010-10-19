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
package org.xwiki.test.xmlrpc;

import java.util.List;

import org.xwiki.xmlrpc.model.XWikiClass;
import org.xwiki.xmlrpc.model.XWikiClassSummary;

/**
 * @version $Id$
 */
public class XWikiClassesTest extends AbstractXWikiXmlRpcTest
{
    public void testGetXWikiClasses() throws Exception
    {
        List<XWikiClassSummary> xwikiClasses = rpc.getClasses();

        TestUtils.banner("TEST: getXWikiClasses()");
        for (XWikiClassSummary xwikiClassSummary : xwikiClasses) {
            System.out.format("%s\n", xwikiClassSummary);
        }

        assertFalse(xwikiClasses.isEmpty());
    }

    public void testGetXWikiClass() throws Exception
    {
        List<XWikiClassSummary> xwikiClasses = rpc.getClasses();
        XWikiClass xwikiClass = rpc.getClass(xwikiClasses.get(0).getId());

        TestUtils.banner("TEST: getXWikiClass()");
        System.out.format("%s\n", xwikiClass);

        assertEquals(xwikiClasses.get(0).getId(), xwikiClass.getId());
    }

    public void testGetNonExistentXWikiClass() throws Exception
    {
        try {
            XWikiClass xwikiClass = rpc.getClass("thisClassShouldNotExist");
            fail();
        } catch (Exception e) {
        }
    }
}
