package com.xpn.xwiki.it.xmlrpc;

import java.util.HashMap;
import java.util.Map;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;
import com.xpn.xwiki.xmlrpc.Page;
import com.xpn.xwiki.xmlrpc.PageSummary;
import com.xpn.xwiki.xmlrpc.SpaceSummary;

public class PagesTest extends AbstractXmlRpcTestCase
{
	private String spaceKey;

	public void setUp() throws Exception {
		super.setUp();
		
        spaceKey = "ContainerSpace";
		Map spaceProperties = new HashMap();
        spaceProperties.put("key", spaceKey);
        // Stupid: property needs to be set even if IGNORED (otherwise null pointer exception)
        spaceProperties.put("name", "Stupid");
        // Stupid: property needs to be set even if IGNORED (otherwise null pointer exception)
        spaceProperties.put("description", "Stupid");        
        getXWikiRpc().addSpace(getToken(), spaceProperties);
	}

    public void testAddRemovePage() throws Exception
    {
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
        String id = null;
        for (int i = 0; i < pageObjs.length && !found; i++) {
            PageSummary summary = new PageSummary((Map)pageObjs[i]);
            if (summary.getTitle().equals(title)) {
                found = true;
                id = summary.getId();
                assertEquals(spaceKey, summary.getSpace());
            }
        }
        assertTrue("Adding page failed. There should be a page entitled \""+ title + "\" in this space", found);
        
        getXWikiRpc().removePage(getToken(), id);
        
        pageObjs = getXWikiRpc().getPages(getToken(), spaceKey);
        
        found = false;
        for (int i = 0; i < pageObjs.length && !found; i++) {
            PageSummary summary = new PageSummary((Map)pageObjs[i]);
            assertFalse("Remove page failed. Page still present.", summary.getId().equals(id));
        }
    }
}
