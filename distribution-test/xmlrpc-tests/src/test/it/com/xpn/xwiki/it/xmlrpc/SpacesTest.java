package com.xpn.xwiki.it.xmlrpc;

import java.util.List;
import java.util.Random;

import org.codehaus.swizzle.confluence.Space;
import org.codehaus.swizzle.confluence.SpaceSummary;

import com.xpn.xwiki.it.xmlrpc.framework.AbstractXmlRpcTestCase;

public class SpacesTest extends AbstractXmlRpcTestCase
{
    public void testAddRemoveSpace() throws Exception
    {
    	String spaceKey = "TestSpace" + (new Random()).nextInt();
        Space space = new Space();
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
