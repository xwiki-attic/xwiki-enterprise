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
package com.xpn.xwiki.it.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.Test;

import com.xpn.xwiki.test.XWikiTestSetup;

/**
 * JUnit TestSetup extension that modify xwiki.cfg file to use LDAP as authentication system. This class is meant to
 * wrap a JUnit TestSuite. For example:
 * 
 * <pre>
 * &lt;code&gt;
 * public static Test suite()
 * {
 *     // Create some TestSuite object here
 *     return new XWikiLDAPTestSetup(suite);
 * }
 * &lt;/code&gt;
 * </pre>
 * 
 * @version $Id$
 */
public class XWikiLDAPTestSetup extends XWikiTestSetup
{
    /**
     * The directory where is the instance of XWiki Enterprise used for theses tests.
     */
    public static final String EXECUTION_DIRECTORY = System.getProperty("xwikiExecutionDirectory");

    /**
     * The xwiki.cfg file used by the instance of XWiki Enterprise used for theses tests.
     */
    public static final String XWIKI_CFG_FILE = EXECUTION_DIRECTORY + "/webapps/xwiki/WEB-INF/xwiki.cfg";

    /**
     * The log4j.properties used by the instance of XWiki Enterprise used for theses tests.
     */
    public static final String XWIKI_LOG_FILE = EXECUTION_DIRECTORY + "/webapps/xwiki/WEB-INF/classes/log4j.properties";

    /**
     * The xwiki.cfg properties modified for the test.
     */
    public Properties CURRENTXWIKICONF;

    // ///

    /**
     * The default xwiki.cfg properties.
     */
    private Properties initialXWikiConf;

    /**
     * The log4j.properties properties.
     */
    private Properties logProperties;

    public XWikiLDAPTestSetup(Test test) throws IOException
    {
        super(test);

        // Prepare xwiki.cfg properties

        if (new File(XWIKI_CFG_FILE).exists()) {
            FileInputStream fis = new FileInputStream(XWIKI_CFG_FILE);
            this.initialXWikiConf = new Properties();
            this.initialXWikiConf.load(fis);
            fis.close();

            fis = new FileInputStream(XWIKI_CFG_FILE);
            CURRENTXWIKICONF = new Properties();
            CURRENTXWIKICONF.load(fis);
            fis.close();

            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap", "1");
            /*
             * CURRENTXWIKICONF.setProperty("xwiki.authentication.authclass",
             * "com.xpn.xwiki.user.impl.LDAP.XWikiLDAPAuthServiceImpl");
             */
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.server", LDAPTestSetup.LDAP_SERVER);
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.base_DN", LDAPTestSetup.LDAP_BASEDN);
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.bind_DN", LDAPTestSetup.LDAP_BINDDN_CN);
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.bind_pass", LDAPTestSetup.LDAP_BINDPASS_CN);
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.UID_attr", LDAPTestSetup.LDAP_USERUID_FIELD);
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.fields_mapping", "name="
                + LDAPTestSetup.LDAP_USERUID_FIELD
                + ",last_name=sn,first_name=givenname,fullname=description,email=mail,ldap_dn=dn");
            /*
             * CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.group_mapping", "XWiki.XWikiAdminGroup=cn=HMS
             * Lydia,ou=crews,ou=groups,o=sevenSeas");
             */
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.groupcache_expiration", "1");
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.user_group", LDAPTestSetup.HMSLYDIA_DN);
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.exclude_group", LDAPTestSetup.EXCLUSIONGROUP_DN);
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.validate_password", "0");
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.update_user", "1");
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.trylocal", "1");
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.mode_group_sync", "always");
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.ssl", "0");
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.ssl.keystore", "");
        }

        // Prepare log4j.properties properties
        this.logProperties = new Properties();
        this.logProperties.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        this.logProperties.setProperty("log4j.appender.stdout.Target", "System.out");
        this.logProperties.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        this.logProperties.setProperty("log4j.appender.stdout.layout.ConversionPattern",
            "%d [%X{url}] [%t] %-5p %-30.30c{2} %x - %m %n");
        this.logProperties.setProperty("log4j.rootLogger", "warn, stdout");
        this.logProperties.setProperty("log4j.logger.com.xpn.xwiki.plugin.ldap", "debug");
        this.logProperties.setProperty("log4j.logger.com.xpn.xwiki.user.impl.LDAP", "debug");
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.test.XWikiTestSetup#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        if (new File(XWIKI_CFG_FILE).exists()) {
            CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.port", "" + LDAPTestSetup.getLDAPPort());
            FileOutputStream fos = new FileOutputStream(XWIKI_CFG_FILE);
            CURRENTXWIKICONF.store(fos, null);
            fos.close();
        }

        FileOutputStream fos = new FileOutputStream(XWIKI_LOG_FILE);
        this.logProperties.store(fos, null);
        fos.close();

        super.setUp();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.test.XWikiTestSetup#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        if (new File(XWIKI_CFG_FILE).exists()) {
            FileOutputStream fos = new FileOutputStream(XWIKI_CFG_FILE);
            initialXWikiConf.store(fos, null);
            fos.close();
        }
    }
}
