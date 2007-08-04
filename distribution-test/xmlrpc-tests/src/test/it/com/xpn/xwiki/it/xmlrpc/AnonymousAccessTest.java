package com.xpn.xwiki.it.xmlrpc;

import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;
import com.xpn.xwiki.xmlrpc.ConfluenceRpcInterface;
import com.xpn.xwiki.xmlrpc.PageSummary;
import com.xpn.xwiki.xmlrpc.SpaceSummary;

public class AnonymousAccessTest extends TestCase
{
	private ConfluenceRpcInterface xwikiRpc;

	public void setUp() throws Exception
    {
        super.setUp();
    
        XmlRpcClient rpcClient = new XmlRpcClient();
        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setServerURL(new URL("http://127.0.0.1:8080/xwiki/xmlrpc"));
        rpcClient.setConfig(clientConfig);
    
        ClientFactory factory = new ClientFactory(rpcClient);
        xwikiRpc = (ConfluenceRpcInterface) factory.newInstance(ConfluenceRpcInterface.class);

        // no login to get a token, use "" as a token
    }

    public void testReadAllPages() throws Exception
    {
        Object[] spaceObjs = getXWikiRpc().getSpaces("");
        for (int i = 0; i < spaceObjs.length; i++) {
        	SpaceSummary spaceSummary = new SpaceSummary((Map)spaceObjs[i]);
            String key = spaceSummary.getKey();
            Object[] pages = getXWikiRpc().getPages("", key);
            for (int j = 0; j < pages.length; j++) {
                PageSummary pageSummary = new PageSummary((Map)pages[j]);
                String id = pageSummary.getId();
                getXWikiRpc().getPage("", id);                
            }
        }
    }

	private ConfluenceRpcInterface getXWikiRpc()
	{
		return xwikiRpc;
	}
}
