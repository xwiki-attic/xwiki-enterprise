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

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.po.AbstractAdminAuthenticatedTest;
import org.xwiki.test.po.extension.server.ExtensionPage;
import org.xwiki.test.po.extension.server.ExtensionsPage;
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
        ExtensionsPage extensions = ExtensionsPage.gotoPage();

        ExtensionInlinePage extensionInline = extensions.contributeExtension("Macro JAR extension");

        Assert.assertEquals("Macro JAR extension", extensionInline.getName());

        extensionInline.setDescription("Macro JAR extension description");
        extensionInline.setInstallation("Macro JAR extension installation");
        extensionInline.setLicenseName("Do What The Fuck You Want To Public License 2");
        extensionInline.setSource("http://source");
        extensionInline.setSummary("Macro JAR extension summary");
        extensionInline.setType("jar");

        ExtensionPage extension = extensionInline.clickSaveAndView();

        getUtil().attachFile("Extension", "Macro JAR extension", new File("target/extensions/macro-jar-extension-1.0.jar"), true);
    }
}
