package com.xpn.xwiki.it.xmlrpc;

import java.util.HashMap;
import java.util.Map;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;
import com.xpn.xwiki.xmlrpc.Page;
import com.xpn.xwiki.xmlrpc.PageSummary;

public class SpacesTest extends AbstractXmlRpcTestCase
{
    public void testAddNewSpace() throws Exception
    {
        Map spaceProperties = new HashMap();
        // Stupid: property needs to be set even if IGNORED (otherwise null pointer exception)
        spaceProperties.put("name", "Test Space");
        spaceProperties.put("key", "TestSpace");
        // Stupid: property needs to be set even if IGNORED (otherwise null pointer exception)
        spaceProperties.put("description", "A test space");
        getXWikiRpc().addSpace(getToken(), spaceProperties);
        
        Map spaceSummary = getXWikiRpc().getSpace(getToken(), "TestSpace");

        // TODO The space name(!) is not saved and the key is used instead when querying
        // Is this behaviour acceptable ? 
        // assertEquals(spaceProperties.get("name"), spaceSummary.get("name"));
        
        assertEquals(spaceProperties.get("key"), spaceSummary.get("key"));
        
        // TODO The space description is not saved and the key is used instead when querying
        // Is this behaviour acceptable ? 
        // assertEquals(spaceProperties.get("description"), spaceSummary.get("description"));        
    }

    public void testAddSpaceAndPage2() throws Exception
    {
        String spaceKey = "ContainerSpace";
        Map spaceProperties = new HashMap();
        spaceProperties.put("key", spaceKey);
        // Stupid: property needs to be set even if IGNORED (otherwise null pointer exception)
        spaceProperties.put("name", "Stupid");
        // Stupid: property needs to be set even if IGNORED (otherwise null pointer exception)
        spaceProperties.put("description", "Stupid");        
        getXWikiRpc().addSpace(getToken(), spaceProperties);
        
        String title = "SomeNewPage";
        Map pageProperties = new HashMap();
        pageProperties.put("space", spaceKey);
        // Stupid: This title is also ignored, id used instead 
        pageProperties.put("title", title);
        pageProperties.put("content", "Some Content");
        Page resultPage = new Page(getXWikiRpc().storePage(getToken(), pageProperties));
        
        // TODO: This is plain wrong (returns Main.WebHome)
        // assertEquals(title, resultPage.getTitle());
        
        Object[] pageObjs = getXWikiRpc().getPages(getToken(), spaceKey);
        
        boolean found = false;
        for (int i = 0; i < pageObjs.length && !found; i++) {
            PageSummary summary = new PageSummary((Map)pageObjs[i]);
            if (summary.getTitle().equals(title)) {
                found = true;
                assertEquals(spaceKey, summary.getSpace());
            }
            
            System.out.println(summary.getTitle());
        }
        assertTrue("Adding page failed. There should be a page entitled \""+ title + "\" in this space", found); 
    }
}
