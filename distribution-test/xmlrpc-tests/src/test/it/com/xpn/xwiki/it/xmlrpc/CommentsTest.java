package com.xpn.xwiki.it.xmlrpc;

import java.util.HashMap;
import java.util.Map;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;
import com.xpn.xwiki.xmlrpc.Comment;
import com.xpn.xwiki.xmlrpc.Page;

public class CommentsTest extends AbstractXmlRpcTestCase
{
	private String spaceKey;
	
	private String pageTitle;
	
	private String pageId;

	public void setUp() throws Exception {
		super.setUp();
		
        spaceKey = "SomeContainerSpace";
		Map spaceProperties = new HashMap();
        spaceProperties.put("key", spaceKey);
        getXWikiRpc().addSpace(getToken(), spaceProperties);
        
        pageTitle = "SomeContainerPage";
        Map pageProperties = new HashMap();
        pageProperties.put("space", spaceKey);
        pageProperties.put("title", pageTitle);
        pageProperties.put("content", "");
        // no id in pageProperties means storePage will add
        Page resultPage = new Page(getXWikiRpc().storePage(getToken(), pageProperties));
        pageId = resultPage.getId();
        
        // add and remove one comment (makes test more realistic)
        Map map = new HashMap();
        map.put("pageId", pageId);
        map.put("content", "Dummy Comment");
        Comment comment = new Comment(getXWikiRpc().addComment(getToken(), map));
        getXWikiRpc().removeComment(getToken(), comment.getId());
	}
	
	public void tearDown() throws Exception {
		getXWikiRpc().removePage(getToken(), pageId);
		getXWikiRpc().removeSpace(getToken(), spaceKey);
		
		super.tearDown();
	}

    public void testAddGetComments() throws Exception
    {
    	// first check that the page has no comments
    	assertEquals(0, getXWikiRpc().getComments(getToken(), pageId).length);
    	
    	// then add some comments
    	Map map = new HashMap();
    	map.put("pageId", pageId);
    	map.put("content", "Comment1");
    	Comment c1 = new Comment(getXWikiRpc().addComment(getToken(), map));
    	assertNotNull(c1.getId());
    	assertEquals(pageId, c1.getPageId());
    	assertEquals("Comment1", c1.getContent());
    	assertNotNull(c1.getUrl());
    	map.put("content", "Comment2");
    	Comment c2 = new Comment(getXWikiRpc().addComment(getToken(), map)); 
    	assertNotNull(c2.getId());
    	assertEquals(pageId, c2.getPageId());
    	assertEquals("Comment2", c2.getContent());
    	assertNotNull(c2.getUrl());
    	
    	// check that the page has the comments
    	assertEquals(2, getXWikiRpc().getComments(getToken(), pageId).length);
    	Map map1 = getXWikiRpc().getComment(getToken(), c1.getId());
    	assertEquals(c1.toMap(), map1);
    	Map map2 = getXWikiRpc().getComment(getToken(), c2.getId());
    	assertEquals(c2.toMap(), map2);
    	
        // delete 1st comment
        assertTrue(getXWikiRpc().removeComment(getToken(), c1.getId()));
    	// check that 1st comment is still there
    	assertEquals(1, getXWikiRpc().getComments(getToken(), pageId).length);
    	assertNotNull(c2.getId());
    	assertEquals(pageId, c2.getPageId());
    	assertEquals("Comment2", c2.getContent());
    	assertNotNull(c2.getUrl());
        // delete 2nd comment
        assertTrue(getXWikiRpc().removeComment(getToken(), c2.getId()));
    	assertEquals(0, getXWikiRpc().getComments(getToken(), pageId).length);
    }
}
