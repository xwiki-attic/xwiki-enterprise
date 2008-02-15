package com.xpn.xwiki.it.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.unit.AbstractServerTest;

import junit.framework.Test;

import com.xpn.xwiki.plugin.ldap.XWikiLDAPConnection;
import com.xpn.xwiki.test.XWikiTestSetup;

/**
 * JUnit TestSetup extension that starts/stops embedded LDAP server and modify xwiki.cfg file to use
 * LDAP as authentication system. This class is meant to wrap a JUnit TestSuite. For example:
 * 
 * <pre><code>
 * public static Test suite()
 * {
 *     // Create some TestSuite object here
 *     return new LDAPXWikiTestSetup(suite);
 * }
 * </code></pre>
 * 
 * @version $Id: $
 */
public class XWikiLDAPTestSetup extends XWikiTestSetup
{
    public static final String LDAP_USERUID_FIELD = "uid";

    public static final String PROPNAME_LDAPPORT = "ldap_port";

    public static final String EXECUTION_DIRECTORY =
        System.getProperty("xwikiExecutionDirectory");

    public static final String XWIKI_CFG_FILE =
        EXECUTION_DIRECTORY + "/webapps/xwiki/WEB-INF/xwiki.cfg";

    private LDAPRunner ldap = new LDAPRunner();

    private Properties initialXWikiConf;

    private Properties currentXWikiConf;

    public static int getLDAPPort()
    {
        return Integer.parseInt(System.getProperty(PROPNAME_LDAPPORT));
    }

    public XWikiLDAPTestSetup(Test test) throws IOException
    {
        super(test);

        FileInputStream fis = new FileInputStream(XWIKI_CFG_FILE);

        // Read properties file.
        this.initialXWikiConf = new Properties();
        this.initialXWikiConf.load(fis);

        // Read properties file.
        this.currentXWikiConf = new Properties();
        this.currentXWikiConf.load(fis);

        fis.close();

        this.currentXWikiConf.setProperty("xwiki.authentication.ldap", "1");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.authclass",
            "com.xpn.xwiki.user.impl.LDAP.LDAPAuthServiceImpl");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.server", "localhost");
        // currentXWikiConf.setProperty("xwiki.authentication.ldap.check_level", "1");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.base_DN", "o=sevenSeas");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.bind_DN",
            "cn={0},ou=people,o=sevenSeas");
        // currentXWikiConf.setProperty("xwiki.authentication.ldap.bind_pass", "{1}");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.UID_attr",
            LDAP_USERUID_FIELD);
        this.currentXWikiConf
            .setProperty("xwiki.authentication.ldap.fields_mapping",
                "name=uid,last_name=sn,first_name=givenname,fullname=description,email=mail,ldap_dn=dn");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.group_mapping_1",
            "XWiki.XWikiAdminGroup=cn=HMS Lydia,ou=crews,ou=groups,o=sevenSeas");
        // ldap_groupcache_expiration
        // ldap_user_group
        // ldap_validate_password
        // ldap_update_user
        // ldap_trylocal
        // ldap_mode_group_sync (create,always)
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.test.XWikiTestSetup#setUp()
     */
    protected void setUp() throws Exception
    {
        this.ldap.start();

        System.setProperty(PROPNAME_LDAPPORT, "" + ldap.getPort());
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.port", "" + ldap.getPort());

        FileOutputStream fos = new FileOutputStream(XWIKI_CFG_FILE);
        this.currentXWikiConf.store(fos, null);
        fos.close();

        super.setUp();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.test.XWikiTestSetup#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        FileOutputStream fos = new FileOutputStream(XWIKI_CFG_FILE);
        initialXWikiConf.store(fos, null);
        fos.close();

        this.ldap.stop();
    }
}

class LDAPRunner extends AbstractServerTest
{
    /**
     * Start the server.
     */
    public void start() throws Exception
    {
        // Add partition 'sevenSeas'
        MutablePartitionConfiguration pcfg = new MutablePartitionConfiguration();
        pcfg.setName("sevenSeas");
        pcfg.setSuffix("o=sevenseas");

        // Create some indices
        Set indexedAttrs = new HashSet();
        indexedAttrs.add("objectClass");
        indexedAttrs.add("o");
        pcfg.setIndexedAttributes(indexedAttrs);

        // Create a first entry associated to the partition
        Attributes attrs = new BasicAttributes(true);

        // First, the objectClass attribute
        Attribute attr = new BasicAttribute("objectClass");
        attr.add("top");
        attr.add("organization");
        attrs.put(attr);

        // The the 'Organization' attribute
        attr = new BasicAttribute("o");
        attr.add("sevenseas");
        attrs.put(attr);

        // Associate this entry to the partition
        pcfg.setContextEntry(attrs);

        // As we can create more than one partition, we must store
        // each created partition in a Set before initialization
        Set pcfgs = new HashSet();
        pcfgs.add(pcfg);

        configuration.setContextPartitionConfigurations(pcfgs);

        // Create a working directory
        File workingDirectory = new File("server-work");
        configuration.setWorkingDirectory(workingDirectory);

        // Now, let's call the upper class which is responsible for the
        // partitions creation
        setUp();

        // Load a demo ldif file
        importLdif(this.getClass().getResourceAsStream("init.ldif"));

        XWikiLDAPConnection connection = new XWikiLDAPConnection();

        connection.open("localhost", this.port, "cn=Horatio Hornblower,ou=people,o=sevenSeas",
            "pass", null, false);
    }

    /**
     * Shutdown the server.
     */
    public void stop() throws Exception
    {
        tearDown();
    }

    /**
     * @return the port to connect to LDAP server.
     */
    public int getPort()
    {
        return port;
    }
}
