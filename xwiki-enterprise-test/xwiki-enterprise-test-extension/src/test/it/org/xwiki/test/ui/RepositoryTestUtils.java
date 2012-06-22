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
 */package org.xwiki.test.ui;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xwiki.extension.Extension;
import org.xwiki.extension.ExtensionDependency;
import org.xwiki.extension.repository.xwiki.internal.XWikiRepositoryModel;

/**
 * @version $Id$
 * @since 4.2M1
 */
public class RepositoryTestUtils
{
    private final TestUtils testUtils;

    public RepositoryTestUtils(TestUtils testUtils)
    {
        this.testUtils = testUtils;
    }

    private String getPageName(Extension extension)
    {
        return extension.getName() != null ? extension.getName() : extension.getId().getId();
    }

    public void addExtension(Extension extension) throws Exception
    {
        // Add the Extension object
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(XWikiRepositoryModel.PROP_EXTENSION_ID, extension.getId().getId());
        queryParameters.put(XWikiRepositoryModel.PROP_EXTENSION_TYPE, extension.getType());

        queryParameters.put(XWikiRepositoryModel.PROP_EXTENSION_NAME, extension.getName());
        queryParameters.put(XWikiRepositoryModel.PROP_EXTENSION_SUMMARY, extension.getSummary());
        if (!extension.getLicenses().isEmpty()) {
            queryParameters.put(XWikiRepositoryModel.PROP_EXTENSION_LICENSENAME, extension.getLicenses().iterator()
                .next().getName());
        }
        queryParameters.put(XWikiRepositoryModel.PROP_EXTENSION_FEATURES, extension.getFeatures());
        if (!extension.getAuthors().isEmpty()) {
            queryParameters.put(XWikiRepositoryModel.PROP_EXTENSION_AUTHORS, extension.getAuthors().iterator().next()
                .getName());
        }

        this.testUtils.addObject("Extension", getPageName(extension), XWikiRepositoryModel.EXTENSION_CLASSNAME,
            queryParameters);

        // Add the ExtensionVersion object
        addVersionObject(extension);

        // Add the ExtensionDependency objects
        for (ExtensionDependency dependency : extension.getDependencies()) {
            addDependency(extension, dependency);
        }

        // Attach the file
        InputStream is = extension.getFile().openStream();
        try {
            this.testUtils.attachFile("Extension", getPageName(extension), extension.getId().getId() + "-"
                + extension.getId().getVersion() + "." + extension.getType(), is, true);
        } finally {
            is.close();
        }
    }

    public void addVersionObject(Extension extension)
    {
        this.testUtils.addObject("Extension", getPageName(extension), XWikiRepositoryModel.EXTENSIONVERSION_CLASSNAME,
            XWikiRepositoryModel.PROP_VERSION_VERSION, extension.getId().getVersion());
    }

    public void addDependency(Extension extension, ExtensionDependency dependency)
    {
        this.testUtils.addObject("Extension", getPageName(extension),
            XWikiRepositoryModel.EXTENSIONDEPENDENCY_CLASSNAME, XWikiRepositoryModel.PROP_DEPENDENCY_CONSTRAINT,
            dependency.getVersionConstraint(), XWikiRepositoryModel.PROP_DEPENDENCY_ID, extension.getId().getId(),
            XWikiRepositoryModel.PROP_DEPENDENCY_EXTENSIONVERSION, extension.getId().getVersion());

    }
}
