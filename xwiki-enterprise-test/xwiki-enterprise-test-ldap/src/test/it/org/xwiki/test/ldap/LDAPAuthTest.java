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

import org.xwiki.test.ldap.framework.LDAPTestSetup;
import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.FlamingoSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

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
        suite.addTestSuite(LDAPAuthTest.class, FlamingoSkinExecutor.class);

        return suite;
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        open("Main", "WebHome");

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
        // ///////////////////
        // Validate normal login
        login(LDAPTestSetup.HORATIOHORNBLOWER_CN, LDAPTestSetup.HORATIOHORNBLOWER_PWD, true);

        // ///////////////////
        // Validate exclusion group
        logout();
        clickLogin();
        setFieldValue("j_username", LDAPTestSetup.THOMASQUIST_CN);
        setFieldValue("j_password", LDAPTestSetup.THOMASQUIST_PWD);
        checkField("rememberme");
        submit();
        assertFalse(LDAPTestSetup.THOMASQUIST_CN + " user has been authenticated", isAuthenticated());

        // ///////////////////
        // Validate XE-136: log with LDAP user then search for provided user uid/pass
        loginAsAdmin();
        open("XWiki", "XWikiPreferences", "edit", "editor=object");
        expandObject("XWiki.XWikiPreferences", 0);
        setFieldValue("XWiki.XWikiPreferences_0_ldap_bind_DN", LDAPTestSetup.HORATIOHORNBLOWER_DN);
        setFieldValue("XWiki.XWikiPreferences_0_ldap_bind_pass", LDAPTestSetup.HORATIOHORNBLOWER_PWD);
        setFieldValue("XWiki.XWikiPreferences_0_ldap_UID_attr", LDAPTestSetup.LDAP_USERUID_FIELD_UID);
        setFieldValue("XWiki.XWikiPreferences_0_ldap_fields_mapping", "name=" + LDAPTestSetup.LDAP_USERUID_FIELD_UID
            + ",last_name=sn,first_name=givenname,fullname=description,email=mail");
        setFieldValue("XWiki.XWikiPreferences_0_ldap_group_mapping",
            "XWiki.XWikiAdminGroup=cn=HMS Lydia,ou=crews,ou=groups,o=sevenSeas");
        clickEditSaveAndView();
        login(LDAPTestSetup.WILLIAMBUSH_UID, LDAPTestSetup.WILLIAMBUSH_PWD, true);

        // ///////////////////
        // Validate
        // - XWIKI-2205: case insensitive user uid
        // - XWIKI-2202: LDAP user update corrupt XWiki user page
        login(LDAPTestSetup.WILLIAMBUSH_UID_MIXED, LDAPTestSetup.WILLIAMBUSH_PWD, true);

        // ///////////////////
        // Validate XWIKI-2201: LDAP group mapping defined in XWikiPreferences is not working
        open("XWiki", "XWikiAdminGroup");
        waitForBodyContains("XWiki." + LDAPTestSetup.WILLIAMBUSH_UID);

        // ///////////////////
        // Validate
        // - XWIKI-2264: LDAP authentication does not support "." in login names
        login(LDAPTestSetup.USERWITHPOINTS_UID, LDAPTestSetup.USERWITHPOINTS_PWD, true);
    }
}
