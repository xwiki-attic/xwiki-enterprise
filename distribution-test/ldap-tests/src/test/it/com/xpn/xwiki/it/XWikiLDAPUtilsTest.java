package com.xpn.xwiki.it;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.cache.api.XWikiCacheService;
import com.xpn.xwiki.cache.impl.OSCacheService;
import com.xpn.xwiki.it.framework.XWikiLDAPTestSetup;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPConnection;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPUtils;
import com.xpn.xwiki.web.XWikiEngineContext;

/**
 * Tests {@link XWikiLDAPUtilsTest};
 * 
 * @version $Id: $
 */
public class XWikiLDAPUtilsTest extends TestCase
{
    private XWikiLDAPConnection connection = new XWikiLDAPConnection();

    private XWikiLDAPUtils ldapUtils = new XWikiLDAPUtils(connection);

    private XWikiContext context;

    private static Set HMSLYDIA_MEMBERS = new HashSet();

    static {
        HMSLYDIA_MEMBERS.add("cn=Horatio Hornblower,ou=people,o=sevenSeas");
        HMSLYDIA_MEMBERS.add("cn=William Bush,ou=people,o=sevenSeas");
        HMSLYDIA_MEMBERS.add("cn=Thomas Quist,ou=people,o=sevenSeas");
        HMSLYDIA_MEMBERS.add("cn=Moultrie Crystal,ou=people,o=sevenSeas");
    }

    /**
     * Initialize the server.
     */
    public void setUp() throws Exception
    {
        this.context = new XWikiContext();

        new XWiki(new XWikiConfig(), this.context)
        {
            private XWikiCacheService cacheService;
            
            public void initXWiki(XWikiConfig config, XWikiContext context,
                XWikiEngineContext engine_context, boolean noupdate) throws XWikiException
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
        
        this.connection.open("localhost", port,
            "cn=Horatio Hornblower,ou=people,o=sevenSeas", "pass", null, false);
    }

    public void testGetUidAttributeName()
    {
        assertSame("Wrong uid attribute name", XWikiLDAPTestSetup.LDAP_USERUID_FIELD,
            this.ldapUtils.getUidAttributeName());
    }

    public void testGetCache() throws XWikiException
    {
        assertTrue("Cache is recreated",
            this.ldapUtils.getCache("cache_name", this.context) == this.ldapUtils.getCache(
                "cache_name", this.context));
    }

    public void testGetGroupMembers()
    {
        List subGroups = new ArrayList();
        Map members = new HashMap();

        assertTrue("Fail to get members", this.ldapUtils.getGroupMembers(
            "cn=HMS Lydia,ou=crews,ou=groups,o=sevenSeas", members, subGroups, this.context));

        assertFalse("No member was found", members.isEmpty());

        assertTrue("Wrong members was found", HMSLYDIA_MEMBERS.equals(members.keySet()));
    }

    public void testIsUserInGroup()
    {
        
    }

    /**
     * Shutdown the server.
     */
    public void tearDown() throws Exception
    {
        this.connection.close();

        super.tearDown();
    }
}
