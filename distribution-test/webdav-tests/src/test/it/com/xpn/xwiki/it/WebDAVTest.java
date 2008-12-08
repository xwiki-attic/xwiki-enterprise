package com.xpn.xwiki.it;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
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
public class WebDAVTest extends TestCase
{

    /**
     * Root of the webdav server.
     */
    private static final String ROOT_URL = "http://localhost:8080/xwiki/webdav";

    /**
     * The {@link HttpClient} used to invoke various methods on the webdav server.
     */
    private static HttpClient client;

    /**
     * Initializes the http client.
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        client = new HttpClient();
        client.getState().setCredentials(
            new AuthScope(AuthScope.ANY_HOST,
                AuthScope.ANY_PORT,
                AuthScope.ANY_REALM,
                AuthScope.ANY_SCHEME), new UsernamePasswordCredentials("Admin", "admin"));
        client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
    }

    /**
     * Test PROPFIND request on root.
     */
    public void testPropFind()
    {
        PropFindMethod propFindMethod = new PropFindMethod(ROOT_URL);
        propFindMethod.setDoAuthentication(true);
        propFindMethod.setDepth(PropFindMethod.DEPTH_1);
        try {
            int status = client.executeMethod(propFindMethod);
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
        String spaceUrl = ROOT_URL + "/pages/TestSpace";
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        try {
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
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
        String spaceUrl = ROOT_URL + "/pages/TestSpace";
        String relativeDestinationPath = "/xwiki/webdav/pages/RenamedTestSpace";
        String movedSpaceUrl = ROOT_URL + "/pages/RenamedTestSpace";
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        MoveMethod moveMethod = new MoveMethod();
        moveMethod.setDoAuthentication(true);
        try {
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            deleteMethod.setPath(movedSpaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            moveMethod.setPath(spaceUrl);
            moveMethod.setDestination(relativeDestinationPath);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(moveMethod));
            deleteMethod.setPath(movedSpaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
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
        String spaceUrl = ROOT_URL + "/pages/TestSpace";
        String pageUrl = spaceUrl + "/TestPage";
        DeleteMethod deleteMethod = new DeleteMethod();
        deleteMethod.setDoAuthentication(true);
        MkcolMethod mkColMethod = new MkcolMethod();
        mkColMethod.setDoAuthentication(true);
        try {
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            mkColMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            deleteMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
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
        String spaceUrl = ROOT_URL + "/pages/TestSpace";
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
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            mkColMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            getMethod.setPath(wikiTextFileUrl);
            assertEquals(DavServletResponse.SC_OK, client.executeMethod(getMethod));
            assertTrue(getMethod.getResponseBodyAsStream().read() != -1);
            getMethod.setPath(wikiXMLFileUrl);
            assertEquals(DavServletResponse.SC_OK, client.executeMethod(getMethod));
            assertTrue(getMethod.getResponseBodyAsStream().read() != -1);
            deleteMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
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
        String spaceUrl = ROOT_URL + "/pages/TestSpace";
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
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            mkColMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            putMethod.setPath(wikiTextFileUrl);
            putMethod
                .setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(newContent
                    .getBytes())));
            // Already existing resource, in which case SC_NO_CONTENT will be the return status.
            assertEquals(DavServletResponse.SC_NO_CONTENT, client.executeMethod(putMethod));
            getMethod.setPath(wikiTextFileUrl);
            assertEquals(DavServletResponse.SC_OK, client.executeMethod(getMethod));
            assertEquals(newContent, getMethod.getResponseBodyAsString());
            putMethod.setPath(wikiXMLFileUrl);
            putMethod
                .setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(newContent
                    .getBytes())));
            // XML saving requires valid XML.
            assertEquals(DavServletResponse.SC_INTERNAL_SERVER_ERROR, client
                .executeMethod(putMethod));
            deleteMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
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
        String spaceUrl = ROOT_URL + "/pages/TestSpace";
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
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            mkColMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            mkColMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(mkColMethod));
            getMethod.setPath(attachmentUrl);
            assertEquals(DavServletResponse.SC_NOT_FOUND, client.executeMethod(getMethod));
            putMethod.setPath(attachmentUrl);
            putMethod
                .setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(attachmentContent
                    .getBytes())));
            assertEquals(DavServletResponse.SC_CREATED, client.executeMethod(putMethod));
            getMethod.setPath(attachmentUrl);
            assertEquals(DavServletResponse.SC_OK, client.executeMethod(getMethod));
            assertEquals(attachmentContent, getMethod.getResponseBodyAsString());
            deleteMethod.setPath(attachmentUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            deleteMethod.setPath(pageUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
            deleteMethod.setPath(spaceUrl);
            assertEquals(DavServletResponse.SC_NO_CONTENT, client
                .executeMethod(deleteMethod));
        } catch (HttpException ex) {
            fail(ex.getMessage());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }
}
