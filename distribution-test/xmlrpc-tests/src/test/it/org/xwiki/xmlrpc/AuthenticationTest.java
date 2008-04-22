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
 *
 */
package org.xwiki.xmlrpc;

import java.net.MalformedURLException;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.codehaus.swizzle.confluence.ServerInfo;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.xwiki.xmlrpc.model.XWikiPage;
import junit.framework.Assert;
import junit.framework.TestCase;

public class AuthenticationTest extends TestCase
{
    public void setUp()
    {
    }

    public void tearDown()
    {
    }

    public void testLoginLogout() throws MalformedURLException, XmlRpcException
    {
        XWikiXmlRpcClient rpc = new XWikiXmlRpcClient(TestConstants.ENDPOINT);
        rpc.login(TestConstants.USERNAME, TestConstants.PASSWORD);
        rpc.logout();
    }

    public void testLoginWithInvalidUser() throws MalformedURLException, XmlRpcException
    {
        XWikiXmlRpcClient rpc =
            new XWikiXmlRpcClient(TestConstants.ENDPOINT, TestConstants.ENDPOINT_HANDLER);

        try {
            rpc.login("thisUserShouldNotExist", "foo");
            Assert.fail();
        } catch (XmlRpcException e) {
        }
    }

    public void testXWikiXmlRpcServiceWithoutLogin() throws MalformedURLException,
        XmlRpcException
    {
        XWikiXmlRpcClient rpc =
            new XWikiXmlRpcClient(TestConstants.ENDPOINT, TestConstants.ENDPOINT_HANDLER);

        List<SpaceSummary> spaces = rpc.getSpaces();

        TestUtils.banner("TEST: XWikiXmlRpcServiceWithoutLogin() (Anonymous access)");
        for (SpaceSummary spaceSummary : spaces) {
            System.out.format("%s\n", spaceSummary);
        }

        assertTrue(spaces.size() != 0);
    }

    public void testXWikiXmlRpcServiceWithoutLoginNoRights() throws MalformedURLException,
        XmlRpcException
    {
        XWikiXmlRpcClient rpc =
            new XWikiXmlRpcClient(TestConstants.ENDPOINT, TestConstants.ENDPOINT_HANDLER);

        try {
            XWikiPage page = rpc.getPage("XWiki.Admin");
            Assert.fail();
        } catch (XmlRpcException e) {
        }
    }

    public void testGetServerInfo() throws MalformedURLException, XmlRpcException
    {
        XWikiXmlRpcClient rpc =
            new XWikiXmlRpcClient(TestConstants.ENDPOINT, TestConstants.ENDPOINT_HANDLER);
        rpc.login(TestConstants.USERNAME, TestConstants.PASSWORD);

        ServerInfo serverInfo = rpc.getServerInfo();
        TestUtils.banner("TEST: getServerInfo()");
        System.out.format("%s\n", serverInfo);

        rpc.logout();
    }
}
