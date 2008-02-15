package com.xpn.xwiki.it;

import junit.framework.TestCase;

import com.xpn.xwiki.it.framework.XWikiLDAPTestSetup;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPConnection;

/**
 * Tests {@link XWikiLDAPConnectionTest};
 * 
 * @version $Id: $
 */
public class XWikiLDAPConnectionTest extends TestCase
{
    /**
     * Initialize the server.
     */
    public void setUp() throws Exception
    {
        
    }

    public void testOpenClose()
    {
        int port = XWikiLDAPTestSetup.getLDAPPort();
        
        XWikiLDAPConnection connection = new XWikiLDAPConnection();
       
        assertTrue("LDAP connection failed", connection.open("localhost", port,
            "cn=Horatio Hornblower,ou=people,o=sevenSeas", "pass", null, false));

        connection.close();
    }

    /**
     * Shutdown the server.
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
}
