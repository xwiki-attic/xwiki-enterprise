package com.xpn.xwiki.it.xmlrpc.framework;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;
import org.custommonkey.xmlunit.XMLTestCase;

import com.xpn.xwiki.xmlrpc.ConfluenceRpcInterface;

public abstract class AbstractXmlRpcTestCase extends XMLTestCase
{

    private ConfluenceRpcInterface xwikiRpc; // dynamic proxy
    
    private String token; // authentication token

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
    
        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setServerURL(new URL("http://127.0.0.1:8080/xwiki/xmlrpc"));
        XmlRpcClient rpcClient = new XmlRpcClient();
        rpcClient.setConfig(clientConfig);

        // Note:
        // We use a dynamic proxy (http://ws.apache.org/xmlrpc/advanced.html),
        // which means the server needs to implement ConfluenceRpcInterface.
        // In other words this only works with XWiki servers. If you need to
        // connect to a generic server then you should use calls like this:
        // token = (String)rpcClient.execute("confluence1.login", new Object[] {});
        ClientFactory factory = new ClientFactory(rpcClient);
        xwikiRpc = (ConfluenceRpcInterface) factory.newInstance(ConfluenceRpcInterface.class);
        token = (String) xwikiRpc.login("Admin", "admin");        
    }

    public void tearDown() throws Exception
    {
        xwikiRpc.logout(token);
    
        super.tearDown();
    }

    /**
     * @return A dynamic proxy implementing the ConfluenceRpcInterface
     * and making XML-RPC invocations to the server
     */
    public ConfluenceRpcInterface getXWikiRpc()
    {
        return xwikiRpc;
    }

    /**
     * @return The authentication token (for the admin user)
     */
    public String getToken()
    {
        return token;
    }
}
