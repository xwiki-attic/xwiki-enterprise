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

import com.xpn.xwiki.it.framework.LDAPTestSetup;
import com.xpn.xwiki.test.AbstractBridgedXWikiComponentTestCase;

/**
 * Start LDAP embedded server if it's not already started.
 * 
 * @version $Id$
 */
public abstract class AbstractLDAPTestCase extends AbstractBridgedXWikiComponentTestCase
{
    /**
     * Tool to start and stop embedded LDAP server.
     */
    private LDAPRunner ldap;

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        if (System.getProperty(LDAPTestSetup.SYSPROPNAME_LDAPPORT) == null) {
            this.ldap = new LDAPRunner();
            this.ldap.start();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception
    {
        if (this.ldap != null) {
            this.ldap.stop();
        }

        super.tearDown();
    }
}
