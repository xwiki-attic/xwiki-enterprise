package com.xpn.xwiki.it.xmlrpc;

import java.util.List;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.SpaceSummary;

import junit.framework.TestCase;

public class AnonymousAccessTest extends TestCase
{
	private Confluence rpc; // xml-rpc proxy

	public void setUp() throws Exception
    {
        super.setUp();
    
        rpc  = new Confluence("http://127.0.0.1:8080/xwiki/xmlrpc");
            // = new Confluence("http://127.0.0.1:9090/rpc/xmlrpc");
        // no login  = anonymous access 
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
