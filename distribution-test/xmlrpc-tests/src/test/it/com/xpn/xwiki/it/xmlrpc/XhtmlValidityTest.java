/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors.
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
package com.xpn.xwiki.it.xmlrpc;

import java.util.Map;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;

import junit.framework.AssertionFailedError;



/**
 * Verifies that all pages in the default wiki are valid XHTML documents using the Confluence XMLRPC
 * API.
 * 
 * @version $Id: $
 */
public class XhtmlValidityTest extends AbstractXmlRpcTestCase
{
    /*
     * TODO We should have a test for each document, so that individual documents fail individual tests,
     * instead of just one test.
     */
    public void testValidityOfAllDocumentsInDefaultWiki() throws Exception
    {
        Object[] spaceObjs = getXWikiRpc().getSpaces(getToken());
        int k = 0;
        for (int i = 0; i < spaceObjs.length; i++) {
            Map spaceSummary = (Map) spaceObjs[i];
            String key = (String) spaceSummary.get("key");
            assertNotNull(key);
            System.out.println("Checking space " + (i + 1) + " out of " + spaceObjs.length + ": "
                + key);
            Object[] pages = getXWikiRpc().getPages(getToken(), key);
            for (int j = 0; j < pages.length; j++) {
                Map pageSummary = (Map) pages[j];
                String id = (String) pageSummary.get("id");
                assertNotNull(id);
                Map page = getXWikiRpc().getPage(getToken(), id);
                String content = (String) page.get("content");
                assertNotNull(content);
                String title = (String) page.get("title");
                assertNotNull(title);
                String url = (String) page.get("url");
                assertNotNull(url);
                System.out.println(" Validating document " + (j + 1) + " out of " + pages.length
                    + ": " + id);
                String renderedContent = getXWikiRpc().renderContent(getToken(), key, id, content);
                if (renderedContent.indexOf("<rdf:RDF") != -1) {
                    // Ignored for the moment, until we can validate using
                    // XMLSchema
                } else {
                    try {
                        assertXMLValid(completeXhtml(title, renderedContent));
                    } catch (AssertionFailedError afe) {
                        System.err.println("Page: " + title);
                        System.err.println("URL:" + url);
                        System.err.println("Error " + (++k) + ": " + afe.getMessage());
                        System.err.println("");
                        throw afe;
                    }
                }
            }
        }
    }

    public static String completeXhtml(String title, String content)
    {
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n"
            + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n"
            + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n"
            + "<head>\n" + "<title>" + title + "</title>\n" + "</head>\n" + "<body>\n<div>\n"
            + content + "\n</div>\n</body>\n</html>";
    }
}
