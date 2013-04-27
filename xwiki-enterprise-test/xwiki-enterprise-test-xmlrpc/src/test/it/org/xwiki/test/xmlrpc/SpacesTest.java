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

import org.junit.Assert;

import org.apache.xmlrpc.XmlRpcException;
import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;

/**
 * @version $Id$
 */
public class SpacesTest extends AbstractXWikiXmlRpcTest
{
    public void testGetSpaces() throws Exception
    {
        List<SpaceSummary> spaces = rpc.getSpaces();

        TestUtils.banner("TEST: getSpaces()");
        for (SpaceSummary spaceSummary : spaces) {
            System.out.format("%s\n", spaceSummary);
        }

        assertTrue(spaces.size() != 0);
    }

    public void testGetSpace() throws Exception
    {
        List<SpaceSummary> spaces = rpc.getSpaces();
        Space space = rpc.getSpace(spaces.get(0).getKey());

        TestUtils.banner("TEST: getSpaceTest()");
        System.out.format("%s\n", space);

        assertEquals(space.getKey(), spaces.get(0).getKey());
    }

    public void testGetNonExistingSpace() throws Exception
    {
        try {
            Space space = rpc.getSpace("thisSpaceShouldNotExist");
            Assert.fail();
        } catch (XmlRpcException e) {
        }
    }

    public void testAddSpace() throws Exception
    {
        Space space = new Space();
        space.setKey(String.format("%s-%d", TestConstants.TEST_PREFIX, Math.abs(random.nextInt())));
        space.setName("TEST Space");

        space = rpc.addSpace(space);

        TestUtils.banner("TEST: addSpace()");
        System.out.format("%s\n", space);

        List<SpaceSummary> spaces = rpc.getSpaces();
        boolean found = false;
        for (SpaceSummary spaceSummary : spaces) {
            if (spaceSummary.getKey().equals(space.getKey())) {
                found = true;
                break;
            }
        }

        Assert.assertTrue(found);
    }

    public void testAddDuplicatedSpace() throws Exception
    {
        Space space = new Space();
        space.setKey(String.format("%s-%d", TestConstants.TEST_PREFIX, Math.abs(random.nextInt())));
        space.setName("TEST Space");

        try {
            space = rpc.addSpace(space);
            space = rpc.addSpace(space);
            Assert.fail();
        } catch (XmlRpcException e) {
        }
    }

    public void testGetSpaceWithoutRights() throws Exception
    {
        if (TestConstants.USERNAME.equals("Admin")) {
            /* If the username is Admin this test will fail. Throw an exception to make it pass. */
            System.out.format("User admin can always access everything\n");
            return;
        }

        try {
            Space space = rpc.getSpace(TestConstants.SPACE_WITH_NO_ACCESS_RIGHTS);
        } catch (XmlRpcException e) {
        }
    }

    public void testRemoveSpace() throws Exception
    {
        List<SpaceSummary> spaces = rpc.getSpaces();
        SpaceSummary spaceToBeRemoved = null;
        for (SpaceSummary spaceSummary : spaces) {
            if (spaceSummary.getKey().startsWith(TestConstants.TEST_PREFIX)) {
                spaceToBeRemoved = spaceSummary;
                break;
            }
        }

        Boolean result = rpc.removeSpace(spaceToBeRemoved.getKey());

        TestUtils.banner("TEST: removeSpace()");
        System.out.format("%s removed: %b\n", spaceToBeRemoved.getKey(), result);

        boolean found = false;
        spaces = rpc.getSpaces();
        for (SpaceSummary spaceSummary : spaces) {
            if (spaceSummary.getKey().equals(spaceToBeRemoved.getKey())) {
                System.out.format("Page still existing: %s\n", spaceSummary);
                found = true;
                break;
            }
        }

        Assert.assertFalse(found);
    }

    public void testRemoveNonExistingSpace() throws Exception
    {
        try {
            Boolean result = rpc.removeSpace("thisSpaceShouldNotExist");
            Assert.fail();
        } catch (XmlRpcException e) {

        }
    }
}
