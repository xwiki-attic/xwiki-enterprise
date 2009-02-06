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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.apache.webdav.lib.methods.MkcolMethod;
import org.apache.webdav.lib.methods.MoveMethod;
import org.apache.webdav.lib.methods.PropFindMethod;

/**
 * The integration test suite for webdav.
 * 
 * @version $Id$
 */
public class DefaultWebDAVTest extends AbstractWebDAVTest
{
    /**
     * Test PROPFIND request on webdav root.
     */
    public void testPropFind()
    {
        PropFindMethod propFindMethod = new PropFindMethod(ROOT);
        propFindMethod.setDoAuthentication(true);
        propFindMethod.setDepth(PropFindMethod.DEPTH_1);
        try {
            int status = getHttpClient().executeMethod(propFindMethod);
            assertEquals(DavServletResponse.SC_MULTI_STATUS, status);
        } catch (HttpException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test create and delete space.
     */
    public void testCreateAndDeleteSpace()
    {
        String spaceUrl = SPACES + "/TestSpace";
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        try {
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
        } catch (HttpException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test rename space.
     */
    public void testRenameSpace()
    {
        String spaceUrl = SPACES + "/TestSpace";
        String relativeDestinationPath = "/xwiki/webdav/spaces/RenamedTestSpace";
        String movedSpaceUrl = SPACES + "/RenamedTestSpace";
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        MoveMethod moveMethod = new MoveMethod();
        moveMethod.setDoAuthentication(true);
        try {
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            deleteMethod.setPath(movedSpaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            moveMethod.setPath(spaceUrl);
            moveMethod.setDestination(relativeDestinationPath);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(moveMethod));
            deleteMethod.setPath(movedSpaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
        } catch (HttpException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test create and delete page.
     */
    public void testCreateAndDeletePage()
    {
        String spaceUrl = SPACES + "/TestSpace";
        String pageUrl = spaceUrl + "/TestPage";
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        try {
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            mkColMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            deleteMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
        } catch (HttpException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test get page content.
     */
    public void testGetPageWikiContent()
    {
        String spaceUrl = SPACES + "/TestSpace";
        String pageUrl = spaceUrl + "/TestPage";
        String wikiTextFileUrl = pageUrl + "/wiki.txt";
        String wikiXMLFileUrl = pageUrl + "/wiki.xml";
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        GetMethod getMethod = new GetMethod();
        getMethod.setDoAuthentication(true);
        try {
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            mkColMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            getMethod.setPath(wikiTextFileUrl);
            assertEquals(DavServletResponse.SC_OK, getHttpClient().executeMethod(getMethod));
            assertTrue(getMethod.getResponseBodyAsStream().read() != -1);
            getMethod.setPath(wikiXMLFileUrl);
            assertEquals(DavServletResponse.SC_OK, getHttpClient().executeMethod(getMethod));
            assertTrue(getMethod.getResponseBodyAsStream().read() != -1);
            deleteMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
        } catch (HttpException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test update page content.
     */
    public void testUpdatePageWikiContent()
    {
        String spaceUrl = SPACES + "/TestSpace";
        String pageUrl = spaceUrl + "/TestPage";
        String wikiTextFileUrl = pageUrl + "/wiki.txt";
        String wikiXMLFileUrl = pageUrl + "/wiki.xml";
        String newContent = "New Content";
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        PutMethod putMethod = new PutMethod();
        putMethod.setDoAuthentication(true);
        GetMethod getMethod = new GetMethod();
        getMethod.setDoAuthentication(true);
        try {
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            mkColMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            putMethod.setPath(wikiTextFileUrl);
            putMethod.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(newContent.getBytes())));
            // Already existing resource, in which case SC_NO_CONTENT will be the return status.
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(putMethod));
            getMethod.setPath(wikiTextFileUrl);
            assertEquals(DavServletResponse.SC_OK, getHttpClient().executeMethod(getMethod));
            assertEquals(newContent, getMethod.getResponseBodyAsString());
            putMethod.setPath(wikiXMLFileUrl);
            putMethod.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(newContent.getBytes())));
            // XML saving was disabled recently. See http://jira.xwiki.org/jira/browse/XWIKI-2910
            assertEquals(DavServletResponse.SC_METHOD_NOT_ALLOWED, getHttpClient().executeMethod(putMethod));
            deleteMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
        } catch (HttpException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test making attachment.
     */
    public void testMakingAttachment()
    {
        String spaceUrl = SPACES + "/TestSpace";
        String pageUrl = spaceUrl + "/TestPage";
        String attachmentUrl = pageUrl + "/attachment.txt";
        String attachmentContent = "Attachment Content";
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        PutMethod putMethod = new PutMethod();
        putMethod.setDoAuthentication(true);
        GetMethod getMethod = new GetMethod();
        getMethod.setDoAuthentication(true);
        try {
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            mkColMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(mkColMethod));
            getMethod.setPath(attachmentUrl);
            assertEquals(DavServletResponse.SC_NOT_FOUND, getHttpClient().executeMethod(getMethod));
            putMethod.setPath(attachmentUrl);
            putMethod.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(attachmentContent
                .getBytes())));
            assertEquals(DavServletResponse.SC_CREATED, getHttpClient().executeMethod(putMethod));
            getMethod.setPath(attachmentUrl);
            assertEquals(DavServletResponse.SC_OK, getHttpClient().executeMethod(getMethod));
            assertEquals(attachmentContent, getMethod.getResponseBodyAsString());
            deleteMethod.setPath(attachmentUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            deleteMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, getHttpClient().executeMethod(deleteMethod));
        } catch (HttpException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }
}
