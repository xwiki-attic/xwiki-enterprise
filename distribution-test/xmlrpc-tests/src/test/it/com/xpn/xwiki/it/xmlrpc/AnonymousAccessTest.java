package com.xpn.xwiki.it.xmlrpc;

import java.util.List;

import com.xpn.xwiki.xmlrpc.client.XWikiClient;
import com.xpn.xwiki.xmlrpc.client.SwizzleXWikiClient;
import com.xpn.xwiki.xmlrpc.client.XWikiClientException;
import com.xpn.xwiki.xmlrpc.model.PageSummary;
import com.xpn.xwiki.xmlrpc.model.SpaceSummary;

import junit.framework.TestCase;

public class AnonymousAccessTest extends TestCase
{
	private XWikiClient rpc; // xml-rpc proxy

	public void setUp() throws Exception
    {
        super.setUp();
    
        // no login  = anonymous access 
        rpc = new SwizzleXWikiClient("http://127.0.0.1:8080/xwiki/xmlrpc");
    }

    public void testReadSomePagesWhenNotLoggedIn() throws Exception
    {
        List spaces = rpc.getSpaces();
        for (int i = 0; i < spaces.size(); i++) {
        	SpaceSummary spaceSummary = (SpaceSummary)spaces.get(i);
            String key = spaceSummary.getKey();

            // Only read pages from the Main space in this test since we're sure Guest users
            // are allowed to read them.
            if (key.equals("Main")) {
                List pages = rpc.getPages(key);
                for (int j = 0; j < pages.size(); j++) {
                    PageSummary pageSummary = (PageSummary)pages.get(j);
                    String id = pageSummary.getId();
                    rpc.getPage(id);
                }
            }
        }
    }

    public void testReadUnauthorizedPage() throws Exception
    {
        try {
            rpc.getPage("Scheduler.WebHome");
            fail("Should have thrown an exception here");
        } catch (XWikiClientException expected) {
            assertTrue(expected.getMessage().contains(
                "Access to document Scheduler.WebHome has been denied to user XWiki.XWikiGuest"));
        }
    }
}
