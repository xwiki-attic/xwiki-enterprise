package com.xpn.xwiki.it.xmlrpc;

import java.util.List;

import com.xpn.xwiki.xmlrpc.client.XWikiClient;
import com.xpn.xwiki.xmlrpc.client.SwizzleXWikiClient;
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

    public void testReadAllPages() throws Exception
    {
        List spaces = rpc.getSpaces();
        for (int i = 0; i < spaces.size(); i++) {
        	SpaceSummary spaceSummary = (SpaceSummary)spaces.get(i);
            String key = spaceSummary.getKey();
            List pages = rpc.getPages(key);
            for (int j = 0; j < pages.size(); j++) {
                PageSummary pageSummary = (PageSummary)pages.get(j);
                String id = pageSummary.getId();
                rpc.getPage(id);
            }
        }
    }
}
