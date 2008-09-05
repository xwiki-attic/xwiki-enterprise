package com.xpn.xwiki.it;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xwiki.cache.Cache;
import org.xwiki.cache.CacheException;
import org.xwiki.cache.CacheFactory;
import org.xwiki.cache.config.CacheConfiguration;
import org.xwiki.component.manager.ComponentLookupException;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.it.framework.XWikiConfig;
import com.xpn.xwiki.it.framework.XWikiLDAPTestSetup;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPConnection;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPSearchAttribute;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPUtils;
import com.xpn.xwiki.test.AbstractBridgedXWikiComponentTestCase;
import com.xpn.xwiki.web.XWikiEngineContext;

/**
 * Tests {@link XWikiLDAPUtilsTest}.
 * 
 * @version $Id$
 */
public class XWikiLDAPUtilsTest extends AbstractBridgedXWikiComponentTestCase
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
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        new XWiki(new XWikiConfig(XWikiLDAPTestSetup.CURRENTXWIKICONF), getContext())
        {
            @Override
            public void initXWiki(com.xpn.xwiki.XWikiConfig config, XWikiContext context,
                XWikiEngineContext enginecontext, boolean noupdate) throws XWikiException
            {
                context.setWiki(this);
                setConfig(config);
            }

            @Override
            public CacheFactory getCacheFactory()
            {
                CacheFactory cacheFactory = null;

                try {
                    cacheFactory = (CacheFactory) getComponentManager().lookup(CacheFactory.ROLE);
                } catch (ComponentLookupException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return cacheFactory;
            }
        };

        this.ldapUtils.setUidAttributeName(XWikiLDAPTestSetup.LDAP_USERUID_FIELD);
        this.ldapUtils.setBaseDN(XWikiLDAPTestSetup.LDAP_BASEDN);

        int port = XWikiLDAPTestSetup.getLDAPPort();

        this.connection.open("localhost", port, XWikiLDAPTestSetup.HORATIOHORNBLOWER_DN,
            XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD, null, false, getContext());
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception
    {
        this.connection.close();

        super.tearDown();
    }

    /**
     * Verify if the user uid attribute name has been correctly set.
     */
    public void testGetUidAttributeName()
    {
        assertSame("Wrong uid attribute name", XWikiLDAPTestSetup.LDAP_USERUID_FIELD, this.ldapUtils
            .getUidAttributeName());
    }

    /**
     * check that the cache is not created each time it's retrieved and correctly handle refresh time.
     * 
     * @throws XWikiException error when getting the cache.
     * @throws XWikiCacheNeedsRefreshException
     * @throws InterruptedException
     * @throws CacheException
     */
    public void testCache() throws XWikiException, InterruptedException, CacheException
    {
        CacheConfiguration cacheConfigurationGroups = new CacheConfiguration();

        Cache<Map<String, String>> tmpCache = this.ldapUtils.getCache(cacheConfigurationGroups, getContext());
        Cache<Map<String, String>> cache = this.ldapUtils.getCache(cacheConfigurationGroups, getContext());

        assertSame("Cache is recreated", tmpCache, cache);
    }

    public void testSearchUserAttributesByUid()
    {
        List<XWikiLDAPSearchAttribute> attributes =
            this.ldapUtils.searchUserAttributesByUid("Moultrie Crystal", new String[] {"dn", "cn"});

        Map<String, String> mexpected = new HashMap<String, String>();
        mexpected.put("dn", "cn=Moultrie Crystal,ou=people,o=sevenSeas");
        mexpected.put("cn", "Moultrie Crystal");

        Map<String, String> mresult = new HashMap<String, String>();
        for (XWikiLDAPSearchAttribute att : attributes) {
            mresult.put(att.name, att.value);
        }

        assertEquals(mexpected, mresult);
    }

    public void testSearchUserDNByUid()
    {
        String userDN = this.ldapUtils.searchUserDNByUid(XWikiLDAPTestSetup.HORATIOHORNBLOWER_CN);

        assertEquals(XWikiLDAPTestSetup.HORATIOHORNBLOWER_DN, userDN);
    }

    /**
     * Test {@link XWikiLDAPUtils#getGroupMembers(String, XWikiContext)}.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testGetGroupMembers() throws XWikiException
    {
        Map<String, String> members = this.ldapUtils.getGroupMembers(XWikiLDAPTestSetup.HMSLYDIA_DN, getContext());

        assertFalse("No member was found", members.isEmpty());

        assertEquals(XWikiLDAPTestSetup.HMSLYDIA_MEMBERS, members.keySet());

        Map<String, String> wrongGroupMembers =
            this.ldapUtils.getGroupMembers("cn=wronggroupdn,ou=people,o=sevenSeas", getContext());

        assertNull("Should return null if group does not exists [" + wrongGroupMembers + "]", wrongGroupMembers);
    }

    /**
     * Test {@link XWikiLDAPUtils#isUserInGroup(String, String, XWikiContext)}.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testIsUserInGroup() throws XWikiException
    {
        String userDN =
            this.ldapUtils.isUserInGroup(XWikiLDAPTestSetup.HORATIOHORNBLOWER_CN, XWikiLDAPTestSetup.HMSLYDIA_DN,
                getContext());

        assertNotNull("User " + XWikiLDAPTestSetup.HORATIOHORNBLOWER_CN + " not found", userDN);
        assertEquals(XWikiLDAPTestSetup.HORATIOHORNBLOWER_DN, userDN);

        this.ldapUtils.setUidAttributeName(XWikiLDAPTestSetup.LDAP_USERUID_FIELD_UID);

        userDN =
            this.ldapUtils.isUserInGroup(XWikiLDAPTestSetup.WILLIAMBUSH_UID, XWikiLDAPTestSetup.HMSLYDIA_DN,
                getContext());

        assertNotNull("User " + XWikiLDAPTestSetup.WILLIAMBUSH_UID + " not found", userDN);
        assertEquals(XWikiLDAPTestSetup.WILLIAMBUSH_DN, userDN);

        String wrongUserDN =
            this.ldapUtils.isUserInGroup("wronguseruid", XWikiLDAPTestSetup.HMSLYDIA_DN, getContext());

        assertNull("Should return null if user is not in the group", wrongUserDN);
    }
}
