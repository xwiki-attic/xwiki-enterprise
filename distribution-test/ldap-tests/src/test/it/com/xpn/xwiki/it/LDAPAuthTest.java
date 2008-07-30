package com.xpn.xwiki.it;

import junit.framework.Test;

import com.xpn.xwiki.it.framework.XWikiLDAPTestSetup;
import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the LDAP login and logout features.
 * 
 * @version $Id: $
 */
public class LDAPAuthTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the LDAP login and logout features");
        suite.addTestSuite(LDAPAuthTest.class, AlbatrossSkinExecutor.class);

        return suite;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        open("/xwiki/bin/view/Main/");

        // Verify that the user isn't logged in
        if (isAuthenticated()) {
            logout();
        }

        clickLogin();
    }

    /**
     * Validate that it tries to log as "common" XWiki login if user is not found in LDAP.
     */
    public void testLogAsXWikiUser()
    {
        setFieldValue("j_username", "Admin");
        setFieldValue("j_password", "admin");
        checkField("rememberme");
        submit();

        assertTrue("Admin user has not been authenticated", isAuthenticated());
    }

    /**
     * Validate that it success to authenticate with LDAP user. Also the user id contains space character.
     */
    public void testLogAsLDAPUser()
    {
        setFieldValue("j_username", XWikiLDAPTestSetup.HORATIOHORNBLOWER_UID);
        setFieldValue("j_password", XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD);
        checkField("rememberme");
        submit();

        assertTrue(XWikiLDAPTestSetup.HORATIOHORNBLOWER_UID + " user has not been authenticated", isAuthenticated());

        // ///////////////////
        // Validate exclusion group

        logout();

        setFieldValue("j_username", XWikiLDAPTestSetup.THOMASQUIST_UID);
        setFieldValue("j_password", XWikiLDAPTestSetup.THOMASQUIST_PWD);
        checkField("rememberme");
        submit();

        assertFalse(XWikiLDAPTestSetup.THOMASQUIST_UID + " user has been authenticated", isAuthenticated());

        // ///////////////////
        // Validate XE-136: log with LDAP user then search for provided user uid/pass

        logout();
        loginAsAdmin();

        open("/xwiki/bin/edit/XWiki/XWikiPreferences?editor=object");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_bind_DN", XWikiLDAPTestSetup.HORATIOHORNBLOWER_DN);
        setFieldValue("XWiki.XWikiPreferences_0_ldap_bind_pass", XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD);
        setFieldValue("XWiki.XWikiPreferences_0_ldap_UID_attr", XWikiLDAPTestSetup.LDAP_USERUID_FIELD_UID);
        setFieldValue("XWiki.XWikiPreferences_0_ldap_fields_mapping", "name="
            + XWikiLDAPTestSetup.LDAP_USERUID_FIELD_UID
            + ",last_name=sn,first_name=givenname,fullname=description,email=mail,ldap_dn=dn");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_group_mapping",
            "XWiki.XWikiAdminGroup=cn=HMS Lydia,ou=crews,ou=groups,o=sevenSeas");
        clickEditSaveAndView();

        logout();
        clickLogin();

        setFieldValue("j_username", XWikiLDAPTestSetup.WILLIAMBUSH_UID);
        setFieldValue("j_password", XWikiLDAPTestSetup.WILLIAMBUSH_PWD);
        checkField("rememberme");
        submit();

        assertTrue(XWikiLDAPTestSetup.WILLIAMBUSH_UID + " user has not been authenticated", isAuthenticated());

        // ///////////////////
        // Validate
        // - XWIKI-2205: case insensitive user uid
        // - XWIKI-2202: LDAP user update corrupt XWiki user page

        logout();
        clickLogin();

        setFieldValue("j_username", XWikiLDAPTestSetup.WILLIAMBUSH_UID_MIXED);
        setFieldValue("j_password", XWikiLDAPTestSetup.WILLIAMBUSH_PWD);
        checkField("rememberme");
        submit();

        assertTrue(XWikiLDAPTestSetup.WILLIAMBUSH_UID_MIXED + " user has not been authenticated", isAuthenticated());

        // ///////////////////
        // Validate XWIKI-2201: LDAP group mapping defined in XWikiPreferences is not working

        open("/xwiki/bin/view/XWiki/XWikiAdminGroup");

        String userFullName = "XWiki." + XWikiLDAPTestSetup.WILLIAMBUSH_UID;

        getSelenium().waitForCondition("selenium.page().bodyText().indexOf('" + userFullName + "') != -1;", "2000");

        assertTextPresent(userFullName);

        // ///////////////////
        // Validate XWIKI-2201: LDAP group mapping defined in XWikiPreferences is not working

        open("/xwiki/bin/view/XWiki/XWikiAdminGroup");
        assertTextPresent("XWiki." + XWikiLDAPTestSetup.WILLIAMBUSH_UID);

        // ///////////////////
        // Validate
        // - XWIKI-2264: LDAP authentication does not support "." in login names

        logout();
        clickLogin();

        setFieldValue("j_username", XWikiLDAPTestSetup.USERWITHPOINTS_UID);
        setFieldValue("j_password", XWikiLDAPTestSetup.USERWITHPOINTS_PWD);
        checkField("rememberme");
        submit();

        assertTrue(XWikiLDAPTestSetup.USERWITHPOINTS_UID + " user has not been authenticated", isAuthenticated());
    }
}
