package com.xpn.xwiki.it.xmlrpc;

import java.util.List;
import java.util.Random;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;
import com.xpn.xwiki.xmlrpc.model.Space;
import com.xpn.xwiki.xmlrpc.model.SpaceSummary;
import com.xpn.xwiki.xmlrpc.model.swizzle.SpaceImpl;

public class SpacesTest extends AbstractXmlRpcTestCase
{
    public void testAddRemoveSpace() throws Exception
    {
    	String spaceKey = "TestSpace" + (new Random()).nextInt(10000);
        Space space = new SpaceImpl();
        space.setKey(spaceKey);
        space.setName("Some Name");
        rpc.addSpace(space);
        
        Space spaceSummary = rpc.getSpace(spaceKey);
        
        assertEquals(spaceKey, spaceSummary.getKey());
        
        rpc.removeSpace(spaceKey);
        
        List spaces = rpc.getSpaces();
        boolean found = false;
        for (int i = 0; i < spaces.size() && !found; i++) {
        	SpaceSummary summary = (SpaceSummary)spaces.get(i);
            if (summary.getKey().equals(spaceKey)) {
                found = true;
            }            
        }
        assertFalse("Remove space failed (" + spaceKey + " still present)", found);
    }
}
