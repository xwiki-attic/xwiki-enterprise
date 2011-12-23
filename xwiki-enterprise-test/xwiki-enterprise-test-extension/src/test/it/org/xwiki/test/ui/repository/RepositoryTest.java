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
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.xwiki.extension.repository.xwiki.Resources;
import org.xwiki.extension.repository.xwiki.model.jaxb.ExtensionAuthor;
import org.xwiki.extension.repository.xwiki.model.jaxb.ExtensionDependency;
import org.xwiki.extension.repository.xwiki.model.jaxb.ExtensionVersion;
import org.xwiki.extension.repository.xwiki.model.jaxb.ExtensionsSearchResult;
import org.xwiki.extension.repository.xwiki.model.jaxb.License;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
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
    private static final String IDPREFIX = "prefix-";

    private ExtensionVersion baseExtension;

    private License baseLicense;

    private ExtensionAuthor baseAuthor;

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

        // base extension informations

        this.baseExtension = new ExtensionVersion();

        this.baseExtension.setId(IDPREFIX + "macro-jar-extension");
        this.baseExtension.setType("jar");
        this.baseExtension.setName("Macro JAR extension");
        this.baseExtension.setDescription("extension description");
        this.baseExtension.setSummary("extension summary, **not bold**");

        this.baseLicense = new License();
        this.baseLicense.setName("Do What The Fuck You Want To Public License 2");
        this.baseExtension.getLicenses().add(this.baseLicense);

        this.baseAuthor = new ExtensionAuthor();
        this.baseAuthor.setName("Administrator");
        this.baseAuthor.setUrl(getUtil().getURL("XWiki", "Admin"));
        this.baseExtension.getAuthors().add(this.baseAuthor);

        this.baseExtension.setVersion("10.0");
    }

    @Test
    public void testAddExtension() throws Exception
    {
        // Set id prefix

        RepositoryAdminPage repositoryAdminPage = RepositoryAdminPage.gotoPage();

        repositoryAdminPage.setDefaultIdPrefix(IDPREFIX);
        repositoryAdminPage.clickUpdateButton();

        // Create extension

        ExtensionsPage extensionsPage = ExtensionsPage.gotoPage();

        ExtensionInlinePage extensionInline = extensionsPage.contributeExtension(this.baseExtension.getName());

        Assert.assertEquals(this.baseExtension.getName(), extensionInline.getName());

        extensionInline.setDescription(this.baseExtension.getDescription());
        extensionInline.setInstallation("extension installation");
        extensionInline.setLicenseName(this.baseLicense.getName());
        extensionInline.setSource("http://source");
        extensionInline.setSummary(this.baseExtension.getSummary());
        extensionInline.setType(this.baseExtension.getType());

        ExtensionPage extensionPage = extensionInline.clickSaveAndView();

        // Test summary
        getUtil().findElementsWithoutWaiting(getDriver(), By.xpath("//tt[text()=\""+this.baseExtension.getSummary()+"\"]"));

        Assert.assertFalse(extensionPage.isValidExtension());

        // Add version
        // TODO: add XR UI to manipulate versions

        getUtil().addObject("Extension", this.baseExtension.getName(), "ExtensionCode.ExtensionVersionClass",
            "version", "1.0");
        getUtil()
            .addObject(
                "Extension",
                this.baseExtension.getName(),
                "ExtensionCode.ExtensionVersionClass",
                "version",
                this.baseExtension.getVersion(),
                "download",
                getUtil().getAttachmentURL("Extension", this.baseExtension.getName(),
                    "prefix-macro-jar-extension-1.0.jar"));
        getUtil().addObject("Extension", this.baseExtension.getName(), "ExtensionCode.ExtensionVersionClass",
            "version", "2.0", "download", "attach:prefix-macro-jar-extension-1.0.jar");

        // Add dependencies
        // TODO: add XR UI to manipulate versions

        getUtil().addObject("Extension", this.baseExtension.getName(), "ExtensionCode.ExtensionDependencyClass",
            "version", "1.0", "id", "dependencyid1", "extensionVersion", this.baseExtension.getVersion());
        getUtil().addObject("Extension", this.baseExtension.getName(), "ExtensionCode.ExtensionDependencyClass",
            "version", "2.0", "id", "dependencyid2", "extensionVersion", this.baseExtension.getVersion());

        // Add attachment

        File extensionFile = new File("target/extensions/prefix-macro-jar-extension-1.0.jar");
        getUtil().attachFile("Extension", this.baseExtension.getName(), "prefix-macro-jar-extension-1.0.jar",
            extensionFile, true);

        // Check livetable

        extensionsPage = ExtensionsPage.gotoPage();

        ExtensionsLiveTableElement livetable = extensionsPage.getLiveTable();

        livetable.filterName(this.baseExtension.getName());

        extensionPage = livetable.clickExtensionName(this.baseExtension.getName());

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
            getUtil().getRESTResource(Resources.EXTENSION_VERSION, null, this.baseExtension.getId(), "1.0");

        Assert.assertEquals(this.baseExtension.getId(), extension.getId());
        Assert.assertEquals(this.baseExtension.getType(), extension.getType());
        Assert.assertEquals(this.baseExtension.getSummary(), extension.getSummary());
        Assert.assertEquals(this.baseLicense.getName(), extension.getLicenses().get(0).getName());
        Assert.assertEquals(this.baseExtension.getDescription(), extension.getDescription());
        Assert.assertEquals(this.baseAuthor.getName(), extension.getAuthors().get(0).getName());
        Assert.assertEquals(this.baseAuthor.getUrl(), extension.getAuthors().get(0).getUrl());
        Assert.assertEquals("1.0", extension.getVersion());

        Assert.assertEquals(getUtil().getURL("Extension", this.baseExtension.getName()), extension.getWebsite());

        // File

        Assert.assertEquals(FileUtils.readFileToByteArray(extensionFile).length,
            getUtil().getRESTBuffer(Resources.EXTENSION_VERSION_FILE, null, this.baseExtension.getId(), "1.0").length);

        // //////////////////////////////////////////
        // 2.0
        // //////////////////////////////////////////

        // Resolve

        extension = getUtil().getRESTResource(Resources.EXTENSION_VERSION, null, this.baseExtension.getId(), "2.0");

        Assert.assertEquals(this.baseExtension.getId(), extension.getId());
        Assert.assertEquals(this.baseExtension.getType(), extension.getType());
        Assert.assertEquals(this.baseExtension.getSummary(), extension.getSummary());
        Assert.assertEquals(this.baseLicense.getName(), extension.getLicenses().get(0).getName());
        Assert.assertEquals(this.baseExtension.getDescription(), extension.getDescription());
        Assert.assertEquals(this.baseAuthor.getName(), extension.getAuthors().get(0).getName());
        Assert.assertEquals(this.baseAuthor.getUrl(), extension.getAuthors().get(0).getUrl());
        Assert.assertEquals("2.0", extension.getVersion());

        Assert.assertEquals(getUtil().getURL("Extension", this.baseExtension.getName()), extension.getWebsite());

        // File

        Assert.assertEquals(FileUtils.readFileToByteArray(extensionFile).length,
            getUtil().getRESTBuffer(Resources.EXTENSION_VERSION_FILE, null, this.baseExtension.getId(), "2.0").length);

        // //////////////////////////////////////////
        // 10.0
        // //////////////////////////////////////////

        // Resolve

        extension =
            getUtil().getRESTResource(Resources.EXTENSION_VERSION, null, this.baseExtension.getId(),
                this.baseExtension.getVersion());

        Assert.assertEquals(this.baseExtension.getId(), extension.getId());
        Assert.assertEquals(this.baseExtension.getType(), extension.getType());
        Assert.assertEquals(this.baseExtension.getSummary(), extension.getSummary());
        Assert.assertEquals(this.baseLicense.getName(), extension.getLicenses().get(0).getName());
        Assert.assertEquals(this.baseExtension.getDescription(), extension.getDescription());
        Assert.assertEquals(this.baseAuthor.getName(), extension.getAuthors().get(0).getName());
        Assert.assertEquals(this.baseAuthor.getUrl(), extension.getAuthors().get(0).getUrl());
        Assert.assertEquals(this.baseExtension.getVersion(), extension.getVersion());

        Assert.assertEquals(getUtil().getURL("Extension", this.baseExtension.getName()), extension.getWebsite());
        
        ExtensionDependency dependency1 = extension.getDependencies().get(0);
        Assert.assertEquals("dependencyid1", dependency1.getId());
        Assert.assertEquals("1.0", dependency1.getVersion());
        ExtensionDependency dependency2 = extension.getDependencies().get(1);
        Assert.assertEquals("dependencyid2", dependency2.getId());
        Assert.assertEquals("2.0", dependency2.getVersion());

        // File

        Assert.assertEquals(
            FileUtils.readFileToByteArray(extensionFile).length,
            getUtil().getRESTBuffer(Resources.EXTENSION_VERSION_FILE, null, this.baseExtension.getId(),
                this.baseExtension.getVersion()).length);

        // //////////////////////////////////////////
        // Search
        // //////////////////////////////////////////

        // Empty search

        Map<String, Object[]> queryParams = new HashMap<String, Object[]>();
        ExtensionsSearchResult result = getUtil().getRESTResource(Resources.SEARCH, queryParams);

        Assert.assertEquals(1, result.getTotalHits());
        Assert.assertEquals(0, result.getOffset());
        extension = result.getExtensions().get(0);

        Assert.assertEquals(this.baseExtension.getId(), extension.getId());
        Assert.assertEquals(this.baseExtension.getType(), extension.getType());
        Assert.assertEquals(this.baseExtension.getSummary(), extension.getSummary());
        Assert.assertEquals(this.baseLicense.getName(), extension.getLicenses().get(0).getName());
        Assert.assertEquals(this.baseExtension.getDescription(), extension.getDescription());
        Assert.assertEquals(this.baseAuthor.getName(), extension.getAuthors().get(0).getName());
        Assert.assertEquals(this.baseAuthor.getUrl(), extension.getAuthors().get(0).getUrl());
        Assert.assertEquals(this.baseExtension.getVersion(), extension.getVersion());

        // TODO: add support for dependencies in XR search

        // Search pattern

        queryParams.clear();
        queryParams.put("q", new Object[] {"macro"});

        result = getUtil().getRESTResource(Resources.SEARCH, queryParams);

        Assert.assertEquals(1, result.getTotalHits());
        Assert.assertEquals(0, result.getOffset());
        extension = result.getExtensions().get(0);

        Assert.assertEquals(this.baseExtension.getId(), extension.getId());
        Assert.assertEquals(this.baseExtension.getType(), extension.getType());
        Assert.assertEquals(this.baseExtension.getSummary(), extension.getSummary());
        Assert.assertEquals(this.baseLicense.getName(), extension.getLicenses().get(0).getName());
        Assert.assertEquals(this.baseExtension.getDescription(), extension.getDescription());
        Assert.assertEquals(this.baseAuthor.getName(), extension.getAuthors().get(0).getName());
        Assert.assertEquals(this.baseAuthor.getUrl(), extension.getAuthors().get(0).getUrl());
        Assert.assertEquals(this.baseExtension.getVersion(), extension.getVersion());

        // Wrong search pattern

        queryParams.clear();
        queryParams.put("q", new Object[] {"notexisting"});

        result = getUtil().getRESTResource(Resources.SEARCH, queryParams);

        Assert.assertEquals(0, result.getTotalHits());
        Assert.assertEquals(0, result.getOffset());
        Assert.assertEquals(0, result.getExtensions().size());

        // Search limit offset

        queryParams.clear();
        queryParams.put("start", new Object[] {1});

        result = getUtil().getRESTResource(Resources.SEARCH, queryParams);

        Assert.assertEquals(1, result.getTotalHits());
        Assert.assertEquals(1, result.getOffset());
        Assert.assertEquals(0, result.getExtensions().size());

        // Search limit nb

        queryParams.clear();
        queryParams.put("number", new Object[] {0});

        result = getUtil().getRESTResource(Resources.SEARCH, queryParams);

        Assert.assertEquals(1, result.getTotalHits());
        Assert.assertEquals(0, result.getOffset());
        Assert.assertEquals(0, result.getExtensions().size());
    }
}
