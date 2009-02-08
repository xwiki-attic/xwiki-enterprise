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
package com.xpn.xwiki.it.webdav;

import org.apache.jackrabbit.webdav.DavServletResponse;

/**
 * Test case for webdav root view.
 * 
 * @version $Id$
 * @since 1.8RC2
 */
public class RootViewTest extends AbstractWebDAVTest
{
    /**
     * Test PROPFIND request on webdav root.
     */
    public void testPropFind()
    {
        propFind(ROOT, 1, DavServletResponse.SC_MULTI_STATUS);
    }

    /**
     * Test creating a collection resource (directory) under webdav root.
     */
    public void testCreateCollection()
    {
        mkCol(ROOT + "/collection", DavServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    /**
     * Test creating an ordinary resource (file) under webdav root.
     */
    public void testCreateFile()
    {
        put(ROOT + "/test.txt", "Content", DavServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    /**
     * Test creating, moving and deleting a temporary collection resource under webdav root.
     */
    public void testTempCollectionOperations()
    {
        String tempCollectionUrl = ROOT + "/.temp";
        // Create.
        mkCol(tempCollectionUrl, DavServletResponse.SC_CREATED);
        // Invalid move.
        move(tempCollectionUrl, "/xwiki/webdav/temp", DavServletResponse.SC_METHOD_NOT_ALLOWED);
        // Valid move: renaming temporary collections is not allowed at the moment.
        move(tempCollectionUrl, "/xwiki/webdav/.tmp", DavServletResponse.SC_FORBIDDEN);
        // Delete.
        delete(tempCollectionUrl, DavServletResponse.SC_NO_CONTENT);
    }

    /**
     * Test creating, moving and deleting a temporary (file) resource under webdav root.
     */
    public void testTempFileOperations()
    {
        String tempFileUrl = ROOT + "/temp.txt~";
        String destinationUrl = "/xwiki/webdav/.temp.txt";
        // Create.
        put(tempFileUrl, "Content", DavServletResponse.SC_CREATED);
        // Invalid move.
        move(tempFileUrl, "/xwiki/webdav/temp.txt", DavServletResponse.SC_METHOD_NOT_ALLOWED);
        // Valid move.
        move(tempFileUrl, destinationUrl, DavServletResponse.SC_CREATED);
        // Delete.
        delete(ROOT + "/.temp.txt", DavServletResponse.SC_NO_CONTENT);
    }

    /**
     * Test renaming each of base views.
     */
    public void testMoveBaseViews()
    {
        String invalidDestination = "/xwiki/webdav/target";
        String validDestination = "/xwiki/webdav/.temp";
        for (String baseView : BASE_VIEWS) {
            move(baseView, invalidDestination, DavServletResponse.SC_METHOD_NOT_ALLOWED);
            move(baseView, validDestination, DavServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
    
    /**
     * Test deleting each of base views.
     */
    public void testDeleteBaseViews()
    {
        for (String baseView : BASE_VIEWS) {
            delete(baseView, DavServletResponse.SC_FORBIDDEN);
        }
    }
}
