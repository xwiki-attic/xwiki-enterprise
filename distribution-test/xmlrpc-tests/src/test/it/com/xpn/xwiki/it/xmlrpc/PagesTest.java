package com.xpn.xwiki.it.xmlrpc;

import java.util.HashMap;
import java.util.Map;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;
import com.xpn.xwiki.xmlrpc.Page;
import com.xpn.xwiki.xmlrpc.PageHistorySummary;
import com.xpn.xwiki.xmlrpc.PageSummary;
import com.xpn.xwiki.xmlrpc.SearchResult;

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
	
	public void tearDown() throws Exception {
		getXWikiRpc().removeSpace(getToken(), spaceKey);
		
		super.tearDown();
	}

    public void testAddModifyRemovePage() throws Exception
    {
        String title = "SomeNewPage";
        String content = "Some Content";
        
        // add the page
        Map pageProperties = new HashMap();
        pageProperties.put("space", spaceKey);
        pageProperties.put("title", title);
        pageProperties.put("content", content);
        // no id in pageProperties means storePage will add
        Page resultPage = new Page(getXWikiRpc().storePage(getToken(), pageProperties));
        String id = resultPage.getId();
        
        assertEquals(title, resultPage.getTitle());
        assertEquals(spaceKey, resultPage.getSpace());
        assertEquals(content, resultPage.getContent());
        assertEquals(spaceKey+"."+title, id);
        
        // check that the page was added using getPages
        Object[] pageObjs = getXWikiRpc().getPages(getToken(), spaceKey);
        boolean found = false;
        for (int i = 0; i < pageObjs.length && !found; i++) {
            PageSummary summary = new PageSummary((Map)pageObjs[i]);
            if (summary.getTitle().equals(title)) {
                found = true;
                assertEquals(spaceKey, summary.getSpace());
            }
        }
        assertTrue("Adding page failed. There should be a page entitled \""+ title + "\" in this space", found);
        
        // also check that the page was added using getPage
        Page page = new Page(getXWikiRpc().getPage(getToken(), id));        
        assertEquals(id, page.getId());
        assertEquals(title, page.getTitle());
        assertEquals(spaceKey, page.getSpace());
        assertEquals(content, page.getContent());
        
        // modify the page
        String newContent = "Some Other Content";
        int newVersion = resultPage.getVersion() + 1;
        pageProperties = new HashMap();
        pageProperties.put("id", id);
        pageProperties.put("space", spaceKey);
        pageProperties.put("title", title);
        pageProperties.put("content", newContent);
        pageProperties.put("version", newVersion);
        Page modifiedPage = new Page(getXWikiRpc().storePage(getToken(), pageProperties));
        
        // check that the page was modified
        assertEquals(id, modifiedPage.getId());
        assertEquals(title, modifiedPage.getTitle());
        assertEquals(spaceKey, modifiedPage.getSpace());
        assertEquals(newContent, modifiedPage.getContent());
        assertEquals(newVersion, modifiedPage.getVersion());
        
        // check again in a different way
        modifiedPage = new Page(getXWikiRpc().getPage(getToken(), id));
        assertEquals(id, modifiedPage.getId());
        assertEquals(title, modifiedPage.getTitle());
        assertEquals(spaceKey, modifiedPage.getSpace());
        assertEquals(newContent, modifiedPage.getContent());
        assertEquals(newVersion, modifiedPage.getVersion());
        
        // check page history
        Object[] historyObjs = getXWikiRpc().getPageHistory(getToken(), id);
        assertEquals(2, historyObjs.length);
    	PageHistorySummary phs0 = new PageHistorySummary((Map)historyObjs[0]);
    	assertEquals(id, phs0.getId());
    	assertEquals(newVersion-1, phs0.getVersion());
    	assertNotNull(phs0.getModified());
    	assertEquals("XWiki.Admin", phs0.getModifier());
    	PageHistorySummary phs1 = new PageHistorySummary((Map)historyObjs[1]);
    	assertEquals(id, phs1.getId());
    	assertEquals(newVersion, phs1.getVersion());
    	assertNotNull(phs1.getModified());
    	assertEquals("XWiki.Admin", phs1.getModifier());
    	
    	// search for the page
    	Object[] searchResults = getXWikiRpc().search(getToken(), title, 1);
        assertEquals(1, searchResults.length);
        SearchResult searchResult = new SearchResult((Map)searchResults[0]);
        assertEquals(id, searchResult.getId());
        assertNotNull(searchResult.getExcerpt());
        assertEquals(title, searchResult.getTitle());
        assertEquals("page", searchResult.getType());
        assertNotNull(searchResult.getUrl());
        
        // remove the page
        getXWikiRpc().removePage(getToken(), id);
        
        // check that the page was really removed
        pageObjs = getXWikiRpc().getPages(getToken(), spaceKey);
        found = false;
        for (int i = 0; i < pageObjs.length && !found; i++) {
            PageSummary summary = new PageSummary((Map)pageObjs[i]);
            assertFalse("Remove page failed. Page still present.", summary.getId().equals(id));
        }
    }
}
