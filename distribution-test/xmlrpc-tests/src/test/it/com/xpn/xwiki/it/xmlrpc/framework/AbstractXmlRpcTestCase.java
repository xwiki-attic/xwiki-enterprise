package com.xpn.xwiki.it.xmlrpc.framework;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.SwizzleXWiki;
import org.custommonkey.xmlunit.XMLTestCase;

public abstract class AbstractXmlRpcTestCase extends XMLTestCase
{
    protected SwizzleXWiki rpc; // xml-rpc proxy
    
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
        
        rpc  = new SwizzleXWiki("http://127.0.0.1:8080/xwiki/xmlrpc");
            // = new Confluence("http://127.0.0.1:9090/rpc/xmlrpc");
        rpc.login("Admin", "admin");
           // rpc.login("admin", "admin");
    }

    public void tearDown() throws Exception
    {
        rpc.logout();
        
        super.tearDown();
    }
}
