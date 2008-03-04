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
        setFieldValue("j_username", "Admin");
        setFieldValue("j_password", "admin");
        checkField("rememberme");
        submit();

        assertTrue("Admin user has not been authenticated", isAuthenticated());
    }

    /**
     * Validate that it success to authenticate with LDAP user. Also the user id contains space
     * character.
     */
    public void testLogAsLDAPUser()
    {
        setFieldValue("j_username", XWikiLDAPTestSetup.HORATIOHORNBLOWER_UID);
        setFieldValue("j_password", XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD);
        checkField("rememberme");
        submit();

        assertTrue(XWikiLDAPTestSetup.HORATIOHORNBLOWER_UID + " user has not been authenticated",
            isAuthenticated());

        logout();

        // ///////////////////
        // Validate XE-136: log with LDAP user then search for provided user uid/pass

        loginAsAdmin();

        open("/xwiki/bin/edit/XWiki/XWikiPreferences?editor=object");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_bind_DN",
            XWikiLDAPTestSetup.HORATIOHORNBLOWER_DN);
        setFieldValue("XWiki.XWikiPreferences_0_ldap_bind_pass",
            XWikiLDAPTestSetup.HORATIOHORNBLOWER_PWD);
        //setFieldValue("XWiki.XWikiPreferences_0_ldap_validate_password", "1");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_UID_attr",
            XWikiLDAPTestSetup.LDAP_USERUID_FIELD_UID);
        setFieldValue("XWiki.XWikiPreferences_0_ldap_fields_mapping", "name="
            + XWikiLDAPTestSetup.LDAP_USERUID_FIELD_UID
            + ",last_name=sn,first_name=givenname,fullname=description,email=mail,ldap_dn=dn");
        clickEditSaveAndView();

        logout();
        clickLogin();

        setFieldValue("j_username", XWikiLDAPTestSetup.WILLIAMBUSH_UID);
        setFieldValue("j_password", XWikiLDAPTestSetup.WILLIAMBUSH_PWD);
        checkField("rememberme");
        submit();

        assertTrue(XWikiLDAPTestSetup.WILLIAMBUSH_UID + " user has not been authenticated",
            isAuthenticated());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        // Verify that the user isn't logged in
        /*if (isAuthenticated()) {
            logout();
        }

        loginAsAdmin();

        open("/xwiki/bin/edit/XWiki/XWikiPreferences?editor=object");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_validate_password", "");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_bind_DN", "");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_bind_pass", "");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_UID_attr", "");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_fields_mapping", "");
        clickEditSaveAndView();*/

        super.tearDown();
    }
}
