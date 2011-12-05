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
package org.xwiki.test.ui.repository;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.extension.repository.xwiki.Resources;
import org.xwiki.extension.repository.xwiki.model.jaxb.ExtensionDependency;
import org.xwiki.extension.repository.xwiki.model.jaxb.ExtensionVersion;
import org.xwiki.extension.repository.xwiki.model.jaxb.ExtensionsSearchResult;
import org.xwiki.test.po.AbstractAdminAuthenticatedTest;
import org.xwiki.test.po.extension.server.ExtensionPage;
import org.xwiki.test.po.extension.server.ExtensionsLiveTableElement;
import org.xwiki.test.po.extension.server.ExtensionsPage;
import org.xwiki.test.po.extension.server.RepositoryAdminPage;
import org.xwiki.test.po.extension.server.editor.ExtensionInlinePage;

/**
 * Repository Test.
 * 
 * @version $Id$
 */
public class RepositoryTest extends AbstractAdminAuthenticatedTest
{
    @Before
    @Override
    public void setUp()
    {
        super.setUp();

        // Import XR xar
        // TODO: use packager maven plugin instead of doing that during tests
        if (!getUtil().pageExists("Extension", "WebHome")) {
            try {
                getUtil().importXar(
                    new File("target/dependency/xwiki-platform-extension-repository-xwiki-server-ui.xar"));
            } catch (Exception e) {
                throw new RuntimeException("Failed to import XR xar", e);
            }
        }
    }

    @Test
    public void testAddExtension() throws Exception
    {
        // Set id prefix

        RepositoryAdminPage repositoryAdminPage = RepositoryAdminPage.gotoPage();

        repositoryAdminPage.setDefaultIdPrefix("prefix-");
        repositoryAdminPage.clickUpdateButton();

        // Create extension

        String extensionName = "Macro JAR extension";

        ExtensionsPage extensionsPage = ExtensionsPage.gotoPage();

        ExtensionInlinePage extensionInline = extensionsPage.contributeExtension(extensionName);

        Assert.assertEquals(extensionName, extensionInline.getName());

        extensionInline.setDescription("extension description");
        extensionInline.setInstallation("extension installation");
        extensionInline.setLicenseName("Do What The Fuck You Want To Public License 2");
        extensionInline.setSource("http://source");
        extensionInline.setSummary("extension summary");
        extensionInline.setType("jar");

        ExtensionPage extensionPage = extensionInline.clickSaveAndView();

        Assert.assertFalse(extensionPage.isValidExtension());

        // Add version
        // TODO: add XR UI to manipulate versions

        getUtil().addObject("Extension", extensionName, "ExtensionCode.ExtensionVersionClass", "version", "1.0");
        getUtil().addObject("Extension", extensionName, "ExtensionCode.ExtensionVersionClass", "version", "10.0",
            "download", getUtil().getAttachmentURL("Extension", extensionName, "prefix-macro-jar-extension-1.0.jar"));
        getUtil().addObject("Extension", extensionName, "ExtensionCode.ExtensionVersionClass", "version", "2.0",
            "download", "attach:prefix-macro-jar-extension-1.0.jar");

        // Add dependencies
        // TODO: add XR UI to manipulate versions

        getUtil().addObject("Extension", extensionName, "ExtensionCode.ExtensionDependencyClass", "version", "1.0",
            "id", "dependencyid1", "extensionVersion", "10.0");
        getUtil().addObject("Extension", extensionName, "ExtensionCode.ExtensionDependencyClass", "version", "2.0",
            "id", "dependencyid2", "extensionVersion", "10.0");

        // Add attachment

        File extensionFile = new File("target/extensions/prefix-macro-jar-extension-1.0.jar");
        getUtil().attachFile("Extension", extensionName, "prefix-macro-jar-extension-1.0.jar", extensionFile, true);

        // Check livetable

        extensionsPage = ExtensionsPage.gotoPage();

        ExtensionsLiveTableElement livetable = extensionsPage.getLiveTable();

        livetable.filterName(extensionName);

        extensionPage = livetable.clickExtensionName(extensionName);

        // Validate extension state

        Assert.assertTrue(extensionPage.isValidExtension());

        // //////////////////////////////////////////
        // Validate REST
        // //////////////////////////////////////////

        // //////////////////////////////////////////
        // 1.0
        // //////////////////////////////////////////

        // Resolve

        ExtensionVersion extension =
            getUtil().getRESTResource(Resources.EXTENSION_VERSION, "prefix-macro-jar-extension", "1.0");

        Assert.assertEquals("prefix-macro-jar-extension", extension.getId());
        Assert.assertEquals("1.0", extension.getVersion());
        Assert.assertEquals("jar", extension.getType());
        Assert.assertEquals("extension summary", extension.getSummary());
        Assert.assertEquals("Do What The Fuck You Want To Public License 2", extension.getLicenses().get(0).getName());
        Assert.assertEquals("extension description", extension.getDescription());

        // File

        Assert.assertEquals(FileUtils.readFileToByteArray(extensionFile).length,
            getUtil().getRESTBuffer(Resources.EXTENSION_VERSION_FILE, "prefix-macro-jar-extension", "1.0").length);

        // //////////////////////////////////////////
        // 2.0
        // //////////////////////////////////////////

        // Resolve

        extension = getUtil().getRESTResource(Resources.EXTENSION_VERSION, "prefix-macro-jar-extension", "2.0");

        Assert.assertEquals("prefix-macro-jar-extension", extension.getId());
        Assert.assertEquals("2.0", extension.getVersion());
        Assert.assertEquals("jar", extension.getType());
        Assert.assertEquals("extension summary", extension.getSummary());
        Assert.assertEquals("Do What The Fuck You Want To Public License 2", extension.getLicenses().get(0).getName());
        Assert.assertEquals("extension description", extension.getDescription());

        // File

        Assert.assertEquals(FileUtils.readFileToByteArray(extensionFile).length,
            getUtil().getRESTBuffer(Resources.EXTENSION_VERSION_FILE, "prefix-macro-jar-extension", "2.0").length);

        // //////////////////////////////////////////
        // 10.0
        // //////////////////////////////////////////

        // Resolve

        extension = getUtil().getRESTResource(Resources.EXTENSION_VERSION, "prefix-macro-jar-extension", "10.0");

        Assert.assertEquals("prefix-macro-jar-extension", extension.getId());
        Assert.assertEquals("10.0", extension.getVersion());
        Assert.assertEquals("jar", extension.getType());
        Assert.assertEquals("extension summary", extension.getSummary());
        Assert.assertEquals("Do What The Fuck You Want To Public License 2", extension.getLicenses().get(0).getName());
        Assert.assertEquals("extension description", extension.getDescription());

        ExtensionDependency dependency1 = extension.getDependencies().get(0);
        Assert.assertEquals("dependencyid1", dependency1.getId());
        Assert.assertEquals("1.0", dependency1.getVersion());
        ExtensionDependency dependency2 = extension.getDependencies().get(1);
        Assert.assertEquals("dependencyid2", dependency2.getId());
        Assert.assertEquals("2.0", dependency2.getVersion());

        // File

        Assert.assertEquals(FileUtils.readFileToByteArray(extensionFile).length,
            getUtil().getRESTBuffer(Resources.EXTENSION_VERSION_FILE, "prefix-macro-jar-extension", "10.0").length);

        // //////////////////////////////////////////
        // Search
        // //////////////////////////////////////////

        ExtensionsSearchResult result = getUtil().getRESTResource(Resources.SEARCH);

        Assert.assertEquals(1, result.getTotalHits());
        Assert.assertEquals(0, result.getOffset());
        extension = result.getExtensions().get(0);

        Assert.assertEquals("prefix-macro-jar-extension", extension.getId());
        Assert.assertEquals("10.0", extension.getVersion());
        Assert.assertEquals("jar", extension.getType());
        Assert.assertEquals("extension summary", extension.getSummary());
        Assert.assertEquals("Do What The Fuck You Want To Public License 2", extension.getLicenses().get(0).getName());
        Assert.assertEquals("extension description", extension.getDescription());

        // TODO: add support for dependencies in XR search
    }
}
