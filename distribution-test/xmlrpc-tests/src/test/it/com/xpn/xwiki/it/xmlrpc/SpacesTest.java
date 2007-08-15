package com.xpn.xwiki.it.xmlrpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;
import com.xpn.xwiki.xmlrpc.SpaceSummary;

public class SpacesTest extends AbstractXmlRpcTestCase
{
    public void testAddRemoveSpace() throws Exception
    {
    	String spaceKey = "TestSpace" + (new Random()).nextInt();
        Map spaceProperties = new HashMap();
        spaceProperties.put("key", spaceKey);
        getXWikiRpc().addSpace(getToken(), spaceProperties);
        
        Map spaceSummary = getXWikiRpc().getSpace(getToken(), spaceKey);
        
        assertEquals(spaceProperties.get("key"), spaceSummary.get("key"));
        
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
