package com.xpn.xwiki.it.xmlrpc;

import java.util.HashMap;
import java.util.Map;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;
import com.xpn.xwiki.xmlrpc.Convert;
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
        String id = resultPage.getId(); // XWiki specific!
        
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
        pageProperties = new HashMap(); 
        pageProperties.put("id", id);
        pageProperties.put("space", spaceKey);
        pageProperties.put("title", title);
        pageProperties.put("content", newContent);
        pageProperties.put("version", Convert.int2str(resultPage.getVersion()));
        Page modifiedPage = new Page(getXWikiRpc().storePage(getToken(), pageProperties));
        
        // check that the page was modified
        assertEquals(id, modifiedPage.getId());
        assertEquals(title, modifiedPage.getTitle());
        assertEquals(spaceKey, modifiedPage.getSpace());
        assertEquals(newContent, modifiedPage.getContent());
        assertTrue(resultPage.getVersion() < modifiedPage.getVersion());
        
        // check again in a different way
        modifiedPage = new Page(getXWikiRpc().getPage(getToken(), id));
        assertEquals(id, modifiedPage.getId());
        assertEquals(title, modifiedPage.getTitle());
        assertEquals(spaceKey, modifiedPage.getSpace());
        assertEquals(newContent, modifiedPage.getContent());
        assertTrue(resultPage.getVersion() < modifiedPage.getVersion());
        
        // check page history
        Object[] historyObjs = getXWikiRpc().getPageHistory(getToken(), id);
        assertEquals(1, historyObjs.length);
        
    	PageHistorySummary phs0 = new PageHistorySummary((Map)historyObjs[0]);
    	assertEquals(resultPage.getVersion(), phs0.getVersion());
    	assertNotNull(phs0.getModified());
    	assertEquals("XWiki.Admin", phs0.getModifier()); // XWiki and setup specific
    	assertNotNull(phs0.getId());
    	Page page0 = new Page(getXWikiRpc().getPage(getToken(), phs0.getId()));
    	assertEquals(page.getContent(), page0.getContent());
        assertEquals(page.getVersion(), page0.getVersion());
    	
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
    
    public void testGetPageHistory() throws Exception
    {
        String title = "SomeOtherPage";
        String content1 = "Content v1";
        
        // add the page
        Map pageProperties = new HashMap();
        pageProperties.put("space", spaceKey);
        pageProperties.put("title", title);
        pageProperties.put("content", content1);
        Page page1 = new Page(getXWikiRpc().storePage(getToken(), pageProperties));
        
        // modify the page
        String content2 = "Content v2";
        pageProperties.put("content", content2);
        pageProperties.put("id", page1.getId());
        Page page2 = new Page(getXWikiRpc().storePage(getToken(), pageProperties));
        
        // modify the page again
        String content3 = "Content v3";
        pageProperties.put("content", content3);
        pageProperties.put("id", page2.getId());
        Page page3 = new Page(getXWikiRpc().storePage(getToken(), pageProperties));
        
        // get page history
        Object[] historyObjs = getXWikiRpc().getPageHistory(getToken(), page3.getId());
        assertEquals(2, historyObjs.length);
        PageHistorySummary phs1 = new PageHistorySummary((Map)historyObjs[0]);
        assertEquals(page1.getVersion(), phs1.getVersion());
        Page p1 = new Page(getXWikiRpc().getPage(getToken(),phs1.getId()));
        assertEquals(page1.getVersion(), p1.getVersion());
        assertEquals(page1.getContent(), p1.getContent());
        assertEquals(page1.getCreated(), p1.getCreated());
        assertEquals(page1.getCreator(), p1.getCreator());
        assertEquals(page1.getModified(), p1.getModified());
        assertEquals(page1.getModifier(), p1.getModifier());
        assertEquals(page1.getParentId(), p1.getParentId());
        assertEquals(page1.getSpace(), p1.getSpace());
        assertEquals(page1.getTitle(), p1.getTitle());
        assertFalse(page1.getUrl().equals(p1.getUrl()));

        PageHistorySummary phs2 = new PageHistorySummary((Map)historyObjs[1]);
        assertEquals(page2.getVersion(), phs2.getVersion());
        Page p2 = new Page(getXWikiRpc().getPage(getToken(),phs2.getId()));
        assertEquals(page2.getVersion(), p2.getVersion());
        assertEquals(page2.getContent(), p2.getContent());
        assertEquals(page2.getCreated(), p2.getCreated());
        assertEquals(page2.getCreator(), p2.getCreator());
        assertEquals(page2.getModified(), p2.getModified());
        assertEquals(page2.getModifier(), p2.getModifier());
        assertEquals(page2.getParentId(), p2.getParentId());
        assertEquals(page2.getSpace(), p2.getSpace());
        assertEquals(page2.getTitle(), p2.getTitle());
        assertFalse(page2.getUrl().equals(p2.getUrl()));
        
        // get history of page from history
        historyObjs = getXWikiRpc().getPageHistory(getToken(), p2.getId());
        assertEquals(1, historyObjs.length);
        phs1 = new PageHistorySummary((Map)historyObjs[0]);
        assertEquals(page1.getVersion(), phs1.getVersion());
        p1 = new Page(getXWikiRpc().getPage(getToken(),phs1.getId()));
        assertEquals(page1.getVersion(), p1.getVersion());
        assertEquals(page1.getContent(), p1.getContent());
        assertEquals(page1.getCreated(), p1.getCreated());
        assertEquals(page1.getCreator(), p1.getCreator());
        assertEquals(page1.getModified(), p1.getModified());
        assertEquals(page1.getModifier(), p1.getModifier());
        assertEquals(page1.getParentId(), p1.getParentId());
        assertEquals(page1.getSpace(), p1.getSpace());
        assertEquals(page1.getTitle(), p1.getTitle());

    }
    
//    // not a real test
//    public void testExceptions() throws Exception
//    {
//        try {
//            getXWikiRpc().getPage("", "InexistantSpace.InexistantPage");
//            fail();
//        } catch (UndeclaredThrowableException e) {
//            assertTrue(e.getCause() instanceof XmlRpcException);
//            System.out.println(e.getCause().getMessage());
//        }
//    }
}
