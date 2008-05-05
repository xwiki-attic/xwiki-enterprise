package com.xpn.xwiki.it;

import junit.framework.TestCase;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.it.framework.XWikiConfig;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPConnection;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPException;
import com.xpn.xwiki.test.XWikiLDAPTestSetup;
import com.xpn.xwiki.web.XWikiEngineContext;

/**
 * Tests {@link XWikiLDAPConnection}.
 * 
 * @version $Id: $
 */
public class XWikiLDAPConnectionTest extends TestCase
{
    /**
     * The XWiki context.
     */
    private XWikiContext context;

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        this.context = new XWikiContext();

        new XWiki(new XWikiConfig(XWikiLDAPTestSetup.CURRENTXWIKICONF), this.context)
        {
            public void initXWiki(com.xpn.xwiki.XWikiConfig config, XWikiContext context,
                XWikiEngineContext enginecontext, boolean noupdate) throws XWikiException
            {
                context.setWiki(this);
                setConfig(config);
            }
        };
    }

    /**
     * Test open and close of the LDAP connection.
     * 
     * @throws XWikiLDAPException
     */
    public void testOpenClose() throws XWikiLDAPException
    {
        int port = XWikiLDAPTestSetup.getLDAPPort();

        XWikiLDAPConnection connection = new XWikiLDAPConnection();

        assertTrue("LDAP connection failed", connection.open("localhost", port,
            XWikiLDAPTestSetup.HORATIOHORNBLOWER_DN, XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD,
            null, false));

        connection.close();
    }

    /**
     * Test open and close of the LDAP connection using xwiki.cfg parameters.
     * 
     * @throws XWikiLDAPException
     */
    public void testOpen2Close() throws XWikiLDAPException
    {
        XWikiLDAPConnection connection = new XWikiLDAPConnection();

        assertTrue("LDAP connection failed", connection.open(
            XWikiLDAPTestSetup.HORATIOHORNBLOWER_UID, XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD,
            this.context));

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
