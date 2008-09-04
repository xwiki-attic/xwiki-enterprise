package com.xpn.xwiki.it;

import junit.framework.Test;

import com.xpn.xwiki.it.framework.XWikiLDAPTestSetup;
import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the LDAP login and logout features.
 * 
 * @version $Id$
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
    @Override
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
        login("Admin", "admin", true);
    }

    /**
     * Validate that it success to authenticate with LDAP user. Also the user id contains space character.
     */
    public void testLogAsLDAPUser()
    {
        login(XWikiLDAPTestSetup.HORATIOHORNBLOWER_CN, XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD, true);

        // ///////////////////
        // Validate exclusion group

        logout();
        clickLogin();

        setFieldValue("j_username", XWikiLDAPTestSetup.THOMASQUIST_CN);
        setFieldValue("j_password", XWikiLDAPTestSetup.THOMASQUIST_PWD);
        checkField("rememberme");
        submit();

        assertFalse(XWikiLDAPTestSetup.THOMASQUIST_CN + " user has been authenticated", isAuthenticated());

        // ///////////////////
        // Validate XE-136: log with LDAP user then search for provided user uid/pass

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

        login(XWikiLDAPTestSetup.WILLIAMBUSH_UID, XWikiLDAPTestSetup.WILLIAMBUSH_PWD, true);

        // ///////////////////
        // Validate
        // - XWIKI-2205: case insensitive user uid
        // - XWIKI-2202: LDAP user update corrupt XWiki user page

        login(XWikiLDAPTestSetup.WILLIAMBUSH_UID_MIXED, XWikiLDAPTestSetup.WILLIAMBUSH_PWD, true);

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

        login(XWikiLDAPTestSetup.USERWITHPOINTS_UID, XWikiLDAPTestSetup.USERWITHPOINTS_PWD, true);
    }
}
