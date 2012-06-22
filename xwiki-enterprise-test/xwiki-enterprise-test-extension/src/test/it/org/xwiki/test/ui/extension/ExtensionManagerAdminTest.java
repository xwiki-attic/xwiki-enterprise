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
package org.xwiki.test.ui.extension;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
import org.xwiki.test.po.extension.client.ExtensionsAdminPage;
import org.xwiki.test.po.extension.client.ResolveExtensionsAdminPage;

/**
 * Test the Extensions Manager admin UI.
 * 
 * @version $Id$
 */
public class ExtensionManagerAdminTest extends AbstractAdminAuthenticatedTest
{
    private ExtensionsAdminPage extensionsPage;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        this.extensionsPage = ExtensionsAdminPage.gotoPage();
    }

    /**
     * Test resolve of core extension.
     */
    @Test
    @Ignore
    public void testResolveCore() throws Exception
    {
        // FIXME: See XE-1062 (Fix failing Extension Manager tests). Don't forget to remove the @Ignore annotation.
        String version = getUtil().getMavenVersion();

        this.extensionsPage.setExtensionId("org.xwiki.commons:xwiki-commons-component-api");
        this.extensionsPage.setExtensionVersion(version);

        ResolveExtensionsAdminPage resolvePage = this.extensionsPage.clickResolveButton();

        Assert.assertEquals("org.xwiki.commons:xwiki-commons-component-api", resolvePage.getExtensionId());
        Assert.assertEquals(version, resolvePage.getExtensionVersion());

        Assert.assertEquals("org.xwiki.commons:xwiki-commons-component-api", resolvePage.getResolveField("Id"));
        Assert.assertEquals(version, resolvePage.getResolveField("Version"));
        Assert.assertEquals("jar", resolvePage.getResolveField("Type"));
        Assert.assertEquals("core", resolvePage.getResolveField("Repository"));
        Assert.assertEquals("org.xwiki.commons", resolvePage.getResolveField("maven.groupId"));
        Assert.assertEquals("xwiki-commons-component-api", resolvePage.getResolveField("maven.artifactId"));
    }

    /**
     * Test resolve of local extension.
     */
    @Test
    public void testResolveLocal()
    {
    }

    /**
     * Test resolve of installed extension.
     */
    @Test
    public void testResolveInstalled()
    {
    }

    /**
     * Test resolve of remote extension.
     */
    @Test
    public void testResolveRemote()
    {
    }

    /**
     * Test resolve of install of remote extension.
     */
    @Test
    public void testInstallRemote()
    {
    }

    /**
     * Test resolve of install of local extension.
     */
    @Test
    public void testInstallLocal()
    {
    }
}
