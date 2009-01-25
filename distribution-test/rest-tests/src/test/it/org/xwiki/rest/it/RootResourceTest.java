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
package org.xwiki.rest.it;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.rest.model.Link;
import org.xwiki.rest.model.Relations;
import org.xwiki.rest.model.XWikiRoot;
import org.xwiki.rest.resources.RootResource;

public class RootResourceTest extends AbstractHttpTest
{
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod = executeGet(getFullUri(getUriPatternForResource(RootResource.class)));
        assertTrue(getMethod.getStatusCode() == HttpStatus.SC_OK);
        TestUtils.printHttpMethodInfo(getMethod);

        XWikiRoot xwikiRoot = (XWikiRoot) xstream.fromXML(getMethod.getResponseBodyAsString());

        Link link = xwikiRoot.getFirstLinkByRelation(Relations.WIKIS);
        assertNotNull(link);

        link = xwikiRoot.getFirstLinkByRelation(Relations.WADL);
        assertNotNull(link);

        checkLinks(xwikiRoot);
    }
}
