package com.xpn.xwiki.it.xmlrpc.framework;

import org.custommonkey.xmlunit.XMLTestCase;

import com.xpn.xwiki.xmlrpc.client.SwizzleXWikiClient;
import com.xpn.xwiki.xmlrpc.client.XWikiClient;

public abstract class AbstractXmlRpcTestCase extends XMLTestCase
{
    protected XWikiClient rpc; // xml-rpc proxy
    
    public AbstractXmlRpcTestCase()
    {
        super();
    }

    public AbstractXmlRpcTestCase(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        
        rpc  = new SwizzleXWikiClient("http://127.0.0.1:8080/xwiki/xmlrpc");
             //= new SwizzleXWikiClient("http://127.0.0.1:9090/rpc/xmlrpc");
        rpc.login("Admin", "admin");
           // rpc.login("admin", "admin");
    }

    public void tearDown() throws Exception
    {
        rpc.logout();
        
        super.tearDown();
    }
}
