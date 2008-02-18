package com.xpn.xwiki.it;

import java.util.Map;

import junit.framework.TestCase;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.cache.api.XWikiCache;
import com.xpn.xwiki.cache.api.XWikiCacheNeedsRefreshException;
import com.xpn.xwiki.cache.api.XWikiCacheService;
import com.xpn.xwiki.cache.impl.OSCacheService;
import com.xpn.xwiki.it.framework.XWikiLDAPTestSetup;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPConnection;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPUtils;
import com.xpn.xwiki.web.XWikiEngineContext;

/**
 * Tests {@link XWikiLDAPUtilsTest}.
 * 
 * @version $Id: $
 */
public class XWikiLDAPUtilsTest extends TestCase
{
    /**
     * The name of the group cache.
     */
    public static final String GROUPCACHE_NAME = "groups";

    /**
     * The LDAP connection tool.
     */
    private XWikiLDAPConnection connection = new XWikiLDAPConnection();

    /**
     * The LDAP tool.
     */
    private XWikiLDAPUtils ldapUtils = new XWikiLDAPUtils(connection);

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

        new XWiki(new XWikiConfig(), this.context)
        {
            private XWikiCacheService cacheService;

            public void initXWiki(XWikiConfig config, XWikiContext context,
                XWikiEngineContext enginecontext, boolean noupdate) throws XWikiException
            {
                context.setWiki(this);
                setConfig(config);
            }

            public XWikiCacheService getCacheService()
            {
                if (this.cacheService == null) {
                    cacheService = new OSCacheService();
                    cacheService.init(this);
                }

                return cacheService;
            }
        };

        this.ldapUtils.setUidAttributeName(XWikiLDAPTestSetup.LDAP_USERUID_FIELD);

        int port = XWikiLDAPTestSetup.getLDAPPort();

        this.connection.open("localhost", port, XWikiLDAPTestSetup.HORATIOHORNBLOWER_DN,
            XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD, null, false);
    }

    /**
     * Verify if the user uid attribute name has been correctly set.
     */
    public void testGetUidAttributeName()
    {
        assertSame("Wrong uid attribute name", XWikiLDAPTestSetup.LDAP_USERUID_FIELD,
            this.ldapUtils.getUidAttributeName());
    }

    /**
     * check that the cache is not created each time it's getted and correctly handle refresh time.
     * 
     * @throws XWikiException error when getting the cache.
     * @throws XWikiCacheNeedsRefreshException
     * @throws InterruptedException
     */
    public void testCache() throws XWikiException, XWikiCacheNeedsRefreshException,
        InterruptedException
    {
        XWikiCache tmpCache = this.ldapUtils.getCache(GROUPCACHE_NAME, this.context);
        XWikiCache cache = this.ldapUtils.getCache(GROUPCACHE_NAME, this.context);

        assertTrue("Cache is recreated", tmpCache == cache);

        cache.putInCache("key", "value");

        String value = (String) cache.getFromCache("key");

        assertEquals("Value getted from cache is wrong", "value", value);

        Thread.sleep(1000);

        value = null;
        try {
            value = (String) cache.getFromCache("key", 1);
        } catch (XWikiCacheNeedsRefreshException e) {

        }

        assertNull("Object in cache is not cleaned", value);
    }

    /**
     * Test {@link XWikiLDAPUtils#getGroupMembers(String, XWikiContext)}.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testGetGroupMembers() throws XWikiException
    {
        Map members =
            this.ldapUtils.getGroupMembers(XWikiLDAPTestSetup.HMSLYDIA_DN, this.context);

        assertFalse("No member was found", members.isEmpty());

        assertTrue("Wrong members was found", XWikiLDAPTestSetup.HMSLYDIA_MEMBERS.equals(members
            .keySet()));
    }

    /**
     * Test {@link XWikiLDAPUtils#isUserInGroup(String, String, XWikiContext)}.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testIsUserInGroup() throws XWikiException
    {
        String userDN =
            this.ldapUtils.isUserInGroup(XWikiLDAPTestSetup.HORATIOHORNBLOWER_UID,
                XWikiLDAPTestSetup.HMSLYDIA_DN, this.context);

        assertNotNull("Users not found", userDN);
        assertEquals(XWikiLDAPTestSetup.HORATIOHORNBLOWER_DN, userDN);
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        this.connection.close();

        super.tearDown();
    }
}
