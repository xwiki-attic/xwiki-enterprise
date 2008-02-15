package com.xpn.xwiki.it;

import junit.framework.TestCase;

import com.xpn.xwiki.it.framework.XWikiLDAPTestSetup;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPConnection;

/**
 * Tests {@link XWikiLDAPConnectionTest}.
 * 
 * @version $Id: $
 */
public class XWikiLDAPConnectionTest extends TestCase
{
    /**
     * {@inheritDoc}
     *
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {

    }

    /**
     * Test open and close of the LDAP connection.
     */
    public void testOpenClose()
    {
        int port = XWikiLDAPTestSetup.getLDAPPort();

        XWikiLDAPConnection connection = new XWikiLDAPConnection();

        assertTrue("LDAP connection failed", connection.open("localhost", port,
            XWikiLDAPTestSetup.HORATIOHORNBLOWER_DN, XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD,
            null, false));

        connection.close();
    }

    /**
     * {@inheritDoc}
     *
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
}
