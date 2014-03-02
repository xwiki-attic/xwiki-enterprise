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

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jmock.Mock;
import org.jmock.core.Invocation;
import org.jmock.core.stub.CustomStub;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.test.ldap.framework.AbstractLDAPTestCase;
import org.xwiki.test.ldap.framework.LDAPTestSetup;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.ldap.XWikiLDAPUtils;
import com.xpn.xwiki.store.XWikiStoreInterface;
import com.xpn.xwiki.user.api.XWikiGroupService;
import com.xpn.xwiki.user.impl.LDAP.LDAPProfileXClass;
import com.xpn.xwiki.user.impl.LDAP.XWikiLDAPAuthServiceImpl;
import com.xpn.xwiki.web.Utils;

/**
 * Unit tests using embedded LDAP server (Apache DS). Theses test can be launched directly from JUnit plugin of EDI.
 * 
 * @version $Id$
 */
public class XWikiLDAPAuthServiceImplTest extends AbstractLDAPTestCase
{
    private static final String MAIN_WIKI_NAME = "xwiki";

    private static final String USER_XCLASS = "XWiki.XWikiUsers";

    private static final String GROUP_XCLASS = "XWiki.XWikiGroups";

    /**
     * Used to convert a proper Document Reference to a string but without the wiki name.
     */
    private EntityReferenceSerializer<String> localEntityReferenceSerializer;

    private XWikiLDAPAuthServiceImpl ldapAuth;

    private Properties properties = new Properties();

    private Map<String, Map<String, XWikiDocument>> databases = new HashMap<String, Map<String, XWikiDocument>>();

    private BaseClass userClass;

    private BaseClass groupClass;

    private Mock mockStore;

    private Mock mockGroupService;

    private Map<String, XWikiDocument> getDocuments(String database, boolean create) throws XWikiException
    {
        if (database == null) {
            database = getContext().getDatabase();
        }

        if (database == null || database.length() == 0) {
            database = MAIN_WIKI_NAME;
        }

        if (!this.databases.containsKey(database)) {
            if (create) {
                this.databases.put(database, new HashMap<String, XWikiDocument>());
            } else {
                throw new XWikiException(XWikiException.MODULE_XWIKI_STORE, XWikiException.ERROR_XWIKI_UNKNOWN,
                    "Database " + database + " does not exists.");
            }
        }

        return this.databases.get(database);
    }

    private XWikiDocument getDocument(String documentFullName) throws XWikiException
    {
        XWikiDocument document = new XWikiDocument();
        document.setFullName(documentFullName);

        return getDocument(document);
    }

    private XWikiDocument getDocument(DocumentReference documentReference) throws XWikiException
    {
        XWikiDocument document = new XWikiDocument(documentReference);

        return getDocument(document);
    }

    private XWikiDocument getDocument(XWikiDocument document) throws XWikiException
    {
        Map<String, XWikiDocument> docs = getDocuments(document.getDatabase(), false);

        if (docs.containsKey(document.getFullName())) {
            return docs.get(document.getFullName());
        } else {
            return document;
        }
    }

    private void saveDocument(XWikiDocument document) throws XWikiException
    {
        document.setNew(false);
        Map<String, XWikiDocument> database = getDocuments(document.getDatabase(), true);
        database.remove(document.getFullName());
        database.put(document.getFullName(), document);
    }

    private boolean documentExists(String documentFullName) throws XWikiException
    {
        return !getDocument(documentFullName).isNew();
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        this.localEntityReferenceSerializer = Utils.getComponent(EntityReferenceSerializer.TYPE_STRING, "local");

        this.userClass = new BaseClass();
        this.groupClass = new BaseClass();

        getContext().setDatabase(MAIN_WIKI_NAME);
        getContext().setMainXWiki(MAIN_WIKI_NAME);

        this.databases.put(MAIN_WIKI_NAME, new HashMap<String, XWikiDocument>());

        this.mockStore = mock(XWikiStoreInterface.class, new Class[] {}, new Object[] {});
        this.mockStore.stubs().method("searchDocuments").will(returnValue(Collections.EMPTY_LIST));

        this.mockGroupService = mock(XWikiGroupService.class, new Class[] {}, new Object[] {});
        this.mockGroupService.stubs().method("getAllGroupsNamesForMember").will(returnValue(Collections.EMPTY_LIST));
        this.mockGroupService.stubs().method("getAllMatchedGroups").will(returnValue(Collections.EMPTY_LIST));

        Mock mockXWiki = mock(XWiki.class, new Class[] {}, new Object[] {});

        mockXWiki.stubs().method("getStore").will(returnValue(mockStore.proxy()));
        mockXWiki.stubs().method("getGroupService").will(returnValue(mockGroupService.proxy()));
        mockXWiki.stubs().method("getXWikiPreference").will(returnValue(null));
        mockXWiki.stubs().method("getXWikiPreferenceAsInt").will(throwException(new NumberFormatException("null")));
        mockXWiki.stubs().method("getDefaultDocumentSyntax").will(returnValue(Syntax.XWIKI_1_0.toIdString()));
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
        mockXWiki.stubs().method("getDocument").will(new CustomStub("Implements XWiki.getDocument")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                Object document = invocation.parameterValues.get(0);

                if (document instanceof String) {
                    return getDocument((String) document);
                } else if (document instanceof EntityReference) {
                    DocumentReferenceResolver<EntityReference> resolver =
                        Utils.getComponent(DocumentReferenceResolver.TYPE_REFERENCE, "current");
                    return getDocument(resolver.resolve((EntityReference) document));
                } else {
                    return getDocument((DocumentReference) document);
                }
            }
        });
        mockXWiki.stubs().method("saveDocument").will(new CustomStub("Implements XWiki.saveDocument")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                saveDocument((XWikiDocument) invocation.parameterValues.get(0));

                return null;
            }
        });
        mockXWiki.stubs().method("exists").will(new CustomStub("Implements XWiki.exists")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                return documentExists((String) invocation.parameterValues.get(0));
            }
        });
        mockXWiki.stubs().method("getXClass").will(new CustomStub("Implements XWiki.getClass")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                return getDocument(
                    localEntityReferenceSerializer.serialize((EntityReference) invocation.parameterValues.get(0)))
                    .getXClass();
            }
        });
        mockXWiki.stubs().method("search").will(returnValue(Collections.EMPTY_LIST));

        this.userClass.setName(USER_XCLASS);
        this.userClass.addTextField("first_name", "First Name", 30);
        this.userClass.addTextField("last_name", "Last Name", 30);
        this.userClass.addTextField("email", "e-Mail", 30);
        this.userClass.addPasswordField("password", "Password", 10);
        this.userClass.addTextField("customproperty", "Custom property", 10);

        mockXWiki.stubs().method("getUserClass").will(returnValue(this.userClass));

        this.groupClass.setName(GROUP_XCLASS);
        this.groupClass.addTextField("member", "Member", 30);

        mockXWiki.stubs().method("getGroupClass").will(returnValue(this.groupClass));

        mockXWiki.stubs().method("createUser").will(new CustomStub("Implements XWiki.createUser")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                XWikiDocument document = new XWikiDocument();
                document.setFullName("XWiki." + invocation.parameterValues.get(0));

                BaseObject newobject = new BaseObject();
                newobject.setClassName(userClass.getName());

                userClass.fromMap((Map) invocation.parameterValues.get(1), newobject);

                document.addObject(userClass.getName(), newobject);

                saveDocument(document);

                return 1;
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
        this.properties.setProperty("xwiki.authentication.ldap.try_local", "0");
        this.properties.setProperty("xwiki.authentication.ldap.update_user", "1");
        this.properties.setProperty("xwiki.authentication.ldap.fields_mapping",
            "last_name=sn,first_name=givenName,fullname=cn,email=mail");

        this.ldapAuth = new XWikiLDAPAuthServiceImpl();
    }

    @Override
    public void tearDown() throws Exception
    {
        // Make sure to reset group cache so that one test data is not reused in another test
        XWikiLDAPUtils.resetGroupCache();

        super.tearDown();
    }

    private void assertAuthenticate(String login, String password, String storedDn) throws XWikiException
    {
        assertAuthenticate(login, password, "XWiki." + login, storedDn);
    }

    private void assertAuthenticate(String login, String password, String xwikiUserName, String storedDn)
        throws XWikiException
    {
        assertAuthenticate(login, password, xwikiUserName, storedDn, login);
    }

    private void assertAuthenticate(String login, String password, String xwikiUserName, String storedDn,
        String storedUid) throws XWikiException
    {
        Principal principal = this.ldapAuth.authenticate(login, password, getContext());

        // Check that authentication return a valid Principal
        assertNotNull("Authentication failed", principal);

        // Check that the returned Principal has the good name
        assertEquals("Wrong returned principal", xwikiUserName, principal.getName());

        XWikiDocument userProfile = getDocument(xwikiUserName);

        // check hat user has been created
        assertTrue("The user profile has not been created", !userProfile.isNew());

        BaseObject userProfileObj = userProfile.getObject(USER_XCLASS);

        assertNotNull("The user profile document does not contains user object", userProfileObj);

        BaseObject ldapProfileObj = userProfile.getObject(LDAPProfileXClass.LDAP_XCLASS);

        assertNotNull("The user profile document does not contains ldap object", ldapProfileObj);

        assertEquals(storedDn, ldapProfileObj.getStringValue(LDAPProfileXClass.LDAP_XFIELD_DN));
        assertEquals(storedUid, ldapProfileObj.getStringValue(LDAPProfileXClass.LDAP_XFIELD_UID));
    }

    /**
     * Validate "simple" LDAP authentication.
     */
    public void testAuthenticate() throws XWikiException
    {
        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);
    }

    /**
     * Validate "simple" LDAP authentication fail with wrong user.
     */
    public void testAuthenticateWithWrongUser() throws XWikiException
    {
        Principal principal = this.ldapAuth.authenticate("WrongUser", "WrongPass", getContext());

        // Check that authentication return a null Principal
        assertNull(principal);

        XWikiDocument userProfile = getDocument("XWiki.WrongUser");

        // check hat user has not been created
        assertTrue("The user profile has been created", userProfile.isNew());
    }

    /**
     * Validate the same user profile is used when authentication is called twice for same user.
     */
    public void testAuthenticateTwice() throws XWikiException
    {
        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        this.mockStore.stubs().method("searchDocuments")
            .will(returnValue(Collections.singletonList(getDocument("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN))));

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);
    }

    /**
     * Validate the same user profile is used when authentication is called twice for same user even the uid used have
     * different case.
     */
    public void testAuthenticateTwiceAndDifferentCase() throws XWikiException
    {
        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        this.mockStore.stubs().method("searchDocuments")
            .will(returnValue(Collections.singletonList(getDocument("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN))));

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN.toUpperCase(), LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            "XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_DN,
            LDAPTestSetup.HORATIOHORNBLOWER_CN);
    }

    /**
     * Validate "simple" LDAP authentication when uid contains point(s).
     */
    public void testAuthenticateWhenUidContainsPoints() throws XWikiException
    {
        assertAuthenticate(LDAPTestSetup.USERWITHPOINTS_CN, LDAPTestSetup.USERWITHPOINTS_PWD, "XWiki."
            + LDAPTestSetup.USERWITHPOINTS_CN.replaceAll("\\.", ""), LDAPTestSetup.USERWITHPOINTS_DN);
    }

    /**
     * Validate a different profile is used for different uid containing points but having same cleaned uid.
     */
    public void testAuthenticateTwiceWhenDifferentUsersAndUidContainsPoints() throws XWikiException
    {
        assertAuthenticate(LDAPTestSetup.USERWITHPOINTS_CN, LDAPTestSetup.USERWITHPOINTS_PWD, "XWiki."
            + LDAPTestSetup.USERWITHPOINTS_CN.replaceAll("\\.", ""), LDAPTestSetup.USERWITHPOINTS_DN);

        assertAuthenticate(LDAPTestSetup.OTHERUSERWITHPOINTS_CN, LDAPTestSetup.OTHERUSERWITHPOINTS_PWD, "XWiki."
            + LDAPTestSetup.OTHERUSERWITHPOINTS_CN.replaceAll("\\.", "") + "_1", LDAPTestSetup.OTHERUSERWITHPOINTS_DN);
    }

    /**
     * Validate "simple" LDAP authentication when the user already exists but does not contains LDAP profile object.
     */
    public void testAuthenticateWhenNonLDAPUserAlreadyExists() throws XWikiException
    {
        XWikiDocument userDoc = getDocument("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN);
        userDoc.newObject(this.userClass.getName(), getContext());
        saveDocument(userDoc);

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);
    }

    /**
     * Validate "simple" LDAP authentication when the user profile default page already exists but does not contains
     * user object. In that case it is using another document to create the user.
     */
    public void testAuthenticateWhenNonLDAPNonUserAlreadyExists() throws XWikiException
    {
        XWikiDocument userDoc = getDocument("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN);
        saveDocument(userDoc);

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD, "XWiki."
            + LDAPTestSetup.HORATIOHORNBLOWER_CN + "_1", LDAPTestSetup.HORATIOHORNBLOWER_DN);
    }

    public void testAuthenticateWithGroupMembership() throws XWikiException
    {
        saveDocument(getDocument("XWiki.Group1"));

        this.properties.setProperty("xwiki.authentication.ldap.group_mapping", "XWiki.Group1="
            + LDAPTestSetup.HMSLYDIA_DN);

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        List<BaseObject> groupList = getDocument("XWiki.Group1").getObjects(this.groupClass.getName());

        assertTrue("No user has been added to the group", groupList != null && groupList.size() > 0);

        BaseObject groupObject = groupList.get(0);

        assertEquals("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN, groupObject.getStringValue("member"));
    }

    public void testAuthenticateWithGroupMembershipWhenOneXWikiGroupMapTwoLDAPGroups() throws XWikiException
    {
        saveDocument(getDocument("XWiki.Group1"));

        this.properties.setProperty("xwiki.authentication.ldap.group_mapping", "XWiki.Group1="
            + LDAPTestSetup.HMSLYDIA_DN + "|" + "XWiki.Group1=" + LDAPTestSetup.EXCLUSIONGROUP_DN);

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        List<BaseObject> groupList = getDocument("XWiki.Group1").getObjects(this.groupClass.getName());

        assertTrue("No user has been added to the group", groupList != null && groupList.size() > 0);

        BaseObject groupObject = groupList.get(0);

        assertEquals("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN, groupObject.getStringValue("member"));
    }

    public void testAuthenticateTwiceWithGroupMembership() throws XWikiException
    {
        saveDocument(getDocument("XWiki.Group1"));

        this.properties.setProperty("xwiki.authentication.ldap.group_mapping", "XWiki.Group1="
            + LDAPTestSetup.HMSLYDIA_DN);

        this.mockGroupService.stubs().method("getAllMatchedGroups")
            .will(returnValue(Collections.singletonList("XWiki.Group1")));

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        this.mockGroupService.stubs().method("getAllGroupsNamesForMember")
            .will(returnValue(Collections.singletonList("XWiki.Group1")));

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        List<BaseObject> groupList = getDocument("XWiki.Group1").getObjects(this.groupClass.getName());

        assertTrue("No user has been added to the group", groupList != null);

        assertTrue("The user has been added twice in the group", groupList.size() == 1);

        BaseObject groupObject = groupList.get(0);

        assertEquals("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN, groupObject.getStringValue("member"));

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);
    }

    /**
     * Validate user field synchronization in "simple" LDAP authentication.
     */
    public void testAuthenticateUserSync() throws XWikiException
    {
        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        XWikiDocument userProfile = getDocument("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN);

        BaseObject userProfileObj = userProfile.getObject(USER_XCLASS);

        assertEquals(LDAPTestSetup.HORATIOHORNBLOWER_SN, userProfileObj.getStringValue("last_name"));
        assertEquals(LDAPTestSetup.HORATIOHORNBLOWER_GIVENNAME, userProfileObj.getStringValue("first_name"));
        assertEquals(LDAPTestSetup.HORATIOHORNBLOWER_MAIL, userProfileObj.getStringValue("email"));

        // Check non mapped properties are not touched

        userProfileObj.setStringValue("customproperty", "customvalue");

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        userProfile = getDocument("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN);

        userProfileObj = userProfile.getObject(USER_XCLASS);

        assertEquals("customvalue", userProfileObj.getStringValue("customproperty"));
    }

    public void testAuthenticateUserSyncWithoutMapping() throws XWikiException
    {
        this.properties.setProperty("xwiki.authentication.ldap.fields_mapping", "");

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);
    }

    public void testAuthenticateUserSyncWithEmptyMapping() throws XWikiException
    {
        this.properties.remove("xwiki.authentication.ldap.fields_mapping");

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);
    }

    public void testAuthenticateUserSyncWithWrongMapping() throws XWikiException
    {
        this.properties.setProperty("xwiki.authentication.ldap.fields_mapping", "wrongfield=wrongfield");

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);
    }

    public void testAuthenticateWhenLDAPDNChanged() throws XWikiException
    {
        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        XWikiDocument userProfile = getDocument("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN);
        BaseObject ldapProfileObj = userProfile.getObject(LDAPProfileXClass.LDAP_XCLASS);
        ldapProfileObj.setStringValue(LDAPProfileXClass.LDAP_XFIELD_DN, "oldDN");

        this.mockStore.stubs().method("searchDocuments")
            .will(returnValue(Collections.singletonList(getDocument("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN))));

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);
    }

    public void testAuthenticateWithOUMembership() throws XWikiException
    {
        saveDocument(getDocument("XWiki.Group1"));

        this.properties
            .setProperty("xwiki.authentication.ldap.group_mapping", "XWiki.Group1=" + LDAPTestSetup.USERS_OU);

        assertAuthenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
            LDAPTestSetup.HORATIOHORNBLOWER_DN);

        List<BaseObject> groupList = getDocument("XWiki.Group1").getObjects(this.groupClass.getName());

        assertTrue("No user has been added to the group", groupList != null && groupList.size() > 0);

        BaseObject groupObject = groupList.get(0);

        assertEquals("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN, groupObject.getStringValue("member"));
    }
}
