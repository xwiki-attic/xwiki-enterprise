/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.test.ldap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jmock.Mock;
import org.jmock.core.Invocation;
import org.jmock.core.stub.CustomStub;
import org.xwiki.cache.Cache;
import org.xwiki.cache.CacheException;
import org.xwiki.cache.config.CacheConfiguration;
import org.xwiki.test.ldap.framework.AbstractLDAPTestCase;
import org.xwiki.test.ldap.framework.LDAPTestSetup;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPConnection;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPSearchAttribute;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPUtils;

/**
 * Tests {@link XWikiLDAPUtils}.
 * 
 * @version $Id$
 */
public class XWikiLDAPUtilsTest extends AbstractLDAPTestCase
{
    /**
     * The LDAP connection tool.
     */
    private XWikiLDAPConnection connection = new XWikiLDAPConnection();

    /**
     * The LDAP tool.
     */
    private XWikiLDAPUtils ldapUtils = new XWikiLDAPUtils(connection);

    private Properties properties = new Properties();

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        Mock mockXWiki = mock(XWiki.class, new Class[] {}, new Object[] {});

        mockXWiki.stubs().method("getXWikiPreference").will(returnValue(null));
        mockXWiki.stubs().method("getXWikiPreferenceAsInt").will(throwException(new NumberFormatException("null")));
        mockXWiki.stubs().method("Param").will(new CustomStub("Implements XWiki.Param")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                return properties.getProperty((String) invocation.parameterValues.get(0));
            }
        });
        mockXWiki.stubs().method("ParamAsLong").will(new CustomStub("Implements XWiki.ParamAsLong")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                return Long.parseLong(properties.getProperty((String) invocation.parameterValues.get(0)));
            }
        });

        getContext().setWiki((XWiki) mockXWiki.proxy());

        this.properties.setProperty("xwiki.authentication.ldap", "1");
        this.properties.setProperty("xwiki.authentication.ldap.server", LDAPTestSetup.LDAP_SERVER);
        this.properties.setProperty("xwiki.authentication.ldap.port", "" + LDAPTestSetup.getLDAPPort());
        this.properties.setProperty("xwiki.authentication.ldap.base_DN", LDAPTestSetup.LDAP_BASEDN);
        this.properties.setProperty("xwiki.authentication.ldap.bind_DN", LDAPTestSetup.LDAP_BINDDN_CN);
        this.properties.setProperty("xwiki.authentication.ldap.bind_pass", LDAPTestSetup.LDAP_BINDPASS_CN);
        this.properties.setProperty("xwiki.authentication.ldap.UID_attr", LDAPTestSetup.LDAP_USERUID_FIELD);
        this.properties.setProperty("xwiki.authentication.ldap.groupcache_expiration", "1");

        this.ldapUtils.setUidAttributeName(LDAPTestSetup.LDAP_USERUID_FIELD);
        this.ldapUtils.setBaseDN(LDAPTestSetup.LDAP_BASEDN);

        int port = LDAPTestSetup.getLDAPPort();

        this.connection.open("localhost", port, LDAPTestSetup.HORATIOHORNBLOWER_DN,
            LDAPTestSetup.HORATIOHORNBLOWER_PWD, null, false, getContext());

        this.ldapUtils.resetGroupCache();
    }

    @Override
    public void tearDown() throws Exception
    {
        this.connection.close();

        // Make sure to reset group cache so that one test data is not reused in another test
        XWikiLDAPUtils.resetGroupCache();

        super.tearDown();
    }

    /**
     * Verify if the user uid attribute name has been correctly set.
     */
    public void testGetUidAttributeName()
    {
        assertSame("Wrong uid attribute name", LDAPTestSetup.LDAP_USERUID_FIELD, this.ldapUtils.getUidAttributeName());
    }

    /**
     * Check that the cache is not created each time it's retrieved and correctly handle refresh time.
     */
    public void testCache() throws XWikiException, InterruptedException, CacheException
    {
        CacheConfiguration cacheConfigurationGroups = new CacheConfiguration();
        cacheConfigurationGroups.setConfigurationId("ldap.groups");

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
        String userDN = this.ldapUtils.searchUserDNByUid(LDAPTestSetup.HORATIOHORNBLOWER_CN);

        assertEquals(LDAPTestSetup.HORATIOHORNBLOWER_DN, userDN);
    }

    /**
     * Test {@link XWikiLDAPUtils#getGroupMembers(String, XWikiContext)}.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testGetGroupMembers() throws XWikiException
    {
        // HMS Lydia

        Map<String, String> hmslydiamembers = this.ldapUtils.getGroupMembers(LDAPTestSetup.HMSLYDIA_DN, getContext());

        assertFalse("No member was found", hmslydiamembers.isEmpty());

        assertEquals(LDAPTestSetup.HMSLYDIA_MEMBERS, hmslydiamembers.keySet());

        this.ldapUtils.resetGroupCache();

        hmslydiamembers = this.ldapUtils.getGroupMembers("cn=HMS Lydia", getContext());

        assertFalse("No member was found", hmslydiamembers.isEmpty());

        this.ldapUtils.resetGroupCache();

        hmslydiamembers = this.ldapUtils.getGroupMembers("(cn=HMS Lydia)", getContext());

        assertFalse("No member was found", hmslydiamembers.isEmpty());

        assertEquals(LDAPTestSetup.HMSLYDIA_MEMBERS, hmslydiamembers.keySet());

        // Top group

        Map<String, String> topGroupMembers = this.ldapUtils.getGroupMembers(LDAPTestSetup.TOPGROUP_DN, getContext());

        assertFalse("No member was found", topGroupMembers.isEmpty());

        assertEquals(LDAPTestSetup.TOPGROUP_MEMBERS, topGroupMembers.keySet());

        this.ldapUtils.resetGroupCache();

        topGroupMembers = this.ldapUtils.getGroupMembers("Top group", getContext());

        assertFalse("No member was found", topGroupMembers.isEmpty());

        assertEquals(LDAPTestSetup.TOPGROUP_MEMBERS, topGroupMembers.keySet());

        this.ldapUtils.resetGroupCache();

        topGroupMembers = this.ldapUtils.getGroupMembers("(cn=Top group)", getContext());

        assertFalse("No member was found", topGroupMembers.isEmpty());

        assertEquals(LDAPTestSetup.TOPGROUP_MEMBERS, topGroupMembers.keySet());

        // Top group with disabled subgroups

        this.ldapUtils.setResolveSubgroups(false);
        this.ldapUtils.resetGroupCache();

        Map<String, String> topGroupMembersNoResolve = this.ldapUtils.getGroupMembers(LDAPTestSetup.TOPGROUP_DN, getContext());

        assertFalse("No member was found", topGroupMembersNoResolve.isEmpty());

        assertEquals(LDAPTestSetup.TOPGROUP_MEMBERS_NORESOLVE, topGroupMembersNoResolve.keySet());

        topGroupMembersNoResolve = this.ldapUtils.getGroupMembers("Top group", getContext());

        assertFalse("No member was found", topGroupMembersNoResolve.isEmpty());

        assertEquals(LDAPTestSetup.TOPGROUP_MEMBERS_NORESOLVE, topGroupMembersNoResolve.keySet());

        topGroupMembersNoResolve = this.ldapUtils.getGroupMembers("(cn=Top group)", getContext());

        assertFalse("No member was found", topGroupMembersNoResolve.isEmpty());

        assertEquals(LDAPTestSetup.TOPGROUP_MEMBERS_NORESOLVE, topGroupMembersNoResolve.keySet());

        // Wrong group

        this.ldapUtils.resetGroupCache();

        Map<String, String> wrongGroupMembers =
            this.ldapUtils.getGroupMembers("cn=wronggroupdn,ou=people,o=sevenSeas", getContext());

        assertNull("Should return null if group does not exists [" + wrongGroupMembers + "]", wrongGroupMembers);
    }

    /**
     * Test {@link XWikiLDAPUtils#isUidInGroup(String, String, XWikiContext)} by passing CN value.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testIsUserInGroupByCN() throws XWikiException
    {
        String userDN =
            this.ldapUtils.isUidInGroup(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HMSLYDIA_DN, getContext());

        assertNotNull("User " + LDAPTestSetup.HORATIOHORNBLOWER_CN + " not found", userDN);
        assertEquals(LDAPTestSetup.HORATIOHORNBLOWER_DN.toLowerCase(), userDN);
    }

    /**
     * Test {@link XWikiLDAPUtils#isUidInGroup(String, String, XWikiContext)} by passing UID value.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testIsUserInGroupByUID() throws XWikiException
    {
        this.ldapUtils.setUidAttributeName(LDAPTestSetup.LDAP_USERUID_FIELD_UID);

        String userDN =
            this.ldapUtils.isUidInGroup(LDAPTestSetup.WILLIAMBUSH_UID, LDAPTestSetup.HMSLYDIA_DN, getContext());

        assertNotNull("User " + LDAPTestSetup.WILLIAMBUSH_UID + " not found", userDN);
        assertEquals(LDAPTestSetup.WILLIAMBUSH_DN.toLowerCase(), userDN);
    }

    /**
     * Test {@link XWikiLDAPUtils#isUidInGroup(String, String, XWikiContext)} by passing UID value.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testIsUserInGroupWithWrongId() throws XWikiException
    {
        String wrongUserDN = this.ldapUtils.isUidInGroup("wronguseruid", LDAPTestSetup.HMSLYDIA_DN, getContext());

        assertNull("Should return null if user is not in the group", wrongUserDN);
    }

    /**
     * Test {@link XWikiLDAPUtils#isMemberOfGroup(String, String, XWikiContext)}.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testIsMemberOfGroup() throws XWikiException
    {
        assertTrue(this.ldapUtils.isMemberOfGroup(LDAPTestSetup.HORATIOHORNBLOWER_DN, LDAPTestSetup.HMSLYDIA_DN,
            getContext()));

        assertFalse(this.ldapUtils.isMemberOfGroup(LDAPTestSetup.HORATIOHORNBLOWER_DN, LDAPTestSetup.EXCLUSIONGROUP_DN,
            getContext()));
    }

    /**
     * Test {@link XWikiLDAPUtils#isMemberOfGroups(String, java.util.Collection, XWikiContext)}.
     * 
     * @throws XWikiException error when getting group members from cache.
     */
    public void testIsMemberOfGroups() throws XWikiException
    {
        assertTrue(this.ldapUtils.isMemberOfGroups(LDAPTestSetup.HORATIOHORNBLOWER_DN,
            Arrays.asList(LDAPTestSetup.HMSLYDIA_DN, LDAPTestSetup.EXCLUSIONGROUP_DN), getContext()));

        assertTrue(this.ldapUtils.isMemberOfGroups(LDAPTestSetup.HORATIOHORNBLOWER_DN,
            Arrays.asList(LDAPTestSetup.EXCLUSIONGROUP_DN, LDAPTestSetup.HMSLYDIA_DN), getContext()));
    }
}
