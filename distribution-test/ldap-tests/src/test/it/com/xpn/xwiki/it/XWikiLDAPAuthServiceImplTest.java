package com.xpn.xwiki.it;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jmock.Mock;
import org.jmock.core.Invocation;
import org.jmock.core.stub.CustomStub;
import org.xwiki.cache.Cache;
import org.xwiki.cache.CacheException;
import org.xwiki.cache.CacheFactory;
import org.xwiki.cache.config.CacheConfiguration;
import org.xwiki.cache.internal.DefaultCache;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.it.framework.AbstractLDAPTestCase;
import com.xpn.xwiki.it.framework.LDAPTestSetup;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.user.impl.LDAP.XWikiLDAPAuthServiceImpl;

public class XWikiLDAPAuthServiceImplTest extends AbstractLDAPTestCase
{
    private static final String MAIN_WIKI_NAME = "xwiki";

    private XWikiLDAPAuthServiceImpl ldapAuth = new XWikiLDAPAuthServiceImpl();

    private CacheFactory cacheFactory = new CacheFactory()
    {
        public <T> Cache<T> newCache(CacheConfiguration config) throws CacheException
        {
            return new DefaultCache<T>();
        }
    };

    private Properties properties = new Properties();

    private boolean isVirtualMode = false;

    private Map<String, Map<String, XWikiDocument>> databases = new HashMap<String, Map<String, XWikiDocument>>();

    private BaseClass userClass = new BaseClass();

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
        getDocuments(document.getDatabase(), true).put(document.getFullName(), document);
    }

    private boolean documentExists(String documentFullName) throws XWikiException
    {
        return !getDocument(documentFullName).isNew();
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        getContext().setDatabase(MAIN_WIKI_NAME);
        getContext().setMainXWiki(MAIN_WIKI_NAME);

        this.databases.put(MAIN_WIKI_NAME, new HashMap<String, XWikiDocument>());

        Mock mockXWiki = mock(XWiki.class, new Class[] {}, new Object[] {});

        mockXWiki.stubs().method("getCacheFactory").will(returnValue(this.cacheFactory));
        mockXWiki.stubs().method("getXWikiPreference").will(returnValue(null));
        mockXWiki.stubs().method("getXWikiPreferenceAsInt").will(throwException(new NumberFormatException("null")));
        mockXWiki.stubs().method("isVirtualMode").will(returnValue(this.isVirtualMode));
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
                return getDocument((String) invocation.parameterValues.get(0));
            }
        });
        mockXWiki.stubs().method("exists").will(new CustomStub("Implements XWiki.exists")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                return documentExists((String) invocation.parameterValues.get(0));
            }
        });
        mockXWiki.stubs().method("search").will(returnValue(Collections.EMPTY_LIST));

        this.userClass.setName("XWiki.XWikiUsers");
        this.userClass.addTextField("ldap_dn", "LDAP DN", 80);
        mockXWiki.stubs().method("getUserClass").will(returnValue(this.userClass));

        mockXWiki.stubs().method("createUser").will(new CustomStub("Implements XWiki.createUser")
        {
            public Object invoke(Invocation invocation) throws Throwable
            {
                XWikiDocument document = new XWikiDocument();
                document.setFullName("XWiki." + invocation.parameterValues.get(0));

                BaseObject newobject = new BaseObject();
                newobject.setClassName(userClass.getName());
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
    }

    public void testAuthenticate() throws XWikiException
    {
        Principal principal =
            this.ldapAuth.authenticate(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD,
                getContext());

        // Check that authentication return a valid Principal
        assertNotNull(principal);

        // Check that the returned Principal has the good name
        assertEquals("xwiki:XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN, principal.getName());

        // check hat user has been created
        assertTrue(documentExists("XWiki." + LDAPTestSetup.HORATIOHORNBLOWER_CN));
    }
}
