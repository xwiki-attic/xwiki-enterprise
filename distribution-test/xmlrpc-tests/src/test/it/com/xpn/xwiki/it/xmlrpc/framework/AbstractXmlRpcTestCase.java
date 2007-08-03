package com.xpn.xwiki.it.xmlrpc.framework;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;
import org.custommonkey.xmlunit.XMLTestCase;

import com.xpn.xwiki.xmlrpc.ConfluenceRpcInterface;

public abstract class AbstractXmlRpcTestCase extends XMLTestCase
{

    private XmlRpcClient rpcClient;
    private ConfluenceRpcInterface xwikiRpc;
    private String token;

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
    
        rpcClient = new XmlRpcClient();
        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setServerURL(new URL("http://127.0.0.1:8080/xwiki/xmlrpc"));
        rpcClient.setConfig(clientConfig);
    
        ClientFactory factory = new ClientFactory(rpcClient);
        xwikiRpc = (ConfluenceRpcInterface) factory.newInstance(ConfluenceRpcInterface.class);
        token = (String) xwikiRpc.login("Admin", "admin");
    }

    public void tearDown() throws Exception
    {
        xwikiRpc.logout(token);
    
        super.tearDown();
    }

    public ConfluenceRpcInterface getXWikiRpc()
    {
        return xwikiRpc;
    }

    public void setXWikiRpc(ConfluenceRpcInterface xwikiRpc)
    {
        this.xwikiRpc = xwikiRpc;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

}
