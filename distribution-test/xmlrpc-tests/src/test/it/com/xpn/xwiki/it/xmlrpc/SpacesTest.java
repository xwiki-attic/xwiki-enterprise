package com.xpn.xwiki.it.xmlrpc;

import java.util.HashMap;
import java.util.Map;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;
import com.xpn.xwiki.xmlrpc.Page;
import com.xpn.xwiki.xmlrpc.PageSummary;
import com.xpn.xwiki.xmlrpc.SpaceSummary;

public class SpacesTest extends AbstractXmlRpcTestCase
{
    public void testAddRemoveSpace() throws Exception
    {
    	String spaceKey = "TestSpace";
        Map spaceProperties = new HashMap();
        spaceProperties.put("key", spaceKey);
        // Stupid: property needs to be set even if IGNORED (otherwise null pointer exception)
        spaceProperties.put("name", "Test Space");
        // Stupid: property needs to be set even if IGNORED (otherwise null pointer exception)
        spaceProperties.put("description", "A test space");
        getXWikiRpc().addSpace(getToken(), spaceProperties);
        
        Map spaceSummary = getXWikiRpc().getSpace(getToken(), spaceKey);

        // TODO The space name(!) is not saved and the key is used instead when querying
        // Is this behaviour acceptable ? 
        // assertEquals(spaceProperties.get("name"), spaceSummary.get("name"));
        
        assertEquals(spaceProperties.get("key"), spaceSummary.get("key"));
        
        // TODO The space description is not saved and the key is used instead when querying
        // Is this behaviour acceptable ? 
        // assertEquals(spaceProperties.get("description"), spaceSummary.get("description"));
        
        getXWikiRpc().removeSpace(getToken(), spaceKey);
        
        Object[] spaceObjs = getXWikiRpc().getSpaces(getToken());
        boolean found = false;
        for (int i = 0; i < spaceObjs.length && !found; i++) {
        	SpaceSummary summary = new SpaceSummary((Map)spaceObjs[i]);
            if (summary.getKey().equals(spaceKey)) {
                found = true;
            }            
        }
        assertFalse("Remove space failed (" + spaceKey + " still present)", found);
    }
}
