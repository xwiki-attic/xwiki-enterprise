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
package org.xwiki.test.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for extension manager functional tests.
 * 
 * @version $Id$
 * @since 4.2M1
 */
public class ExtensionTestUtils
{
    /**
     * The key used to store an instance of this class in the context.
     */
    public final static String PROPERTY_KEY = "extensionUtils";

    /**
     * The name of the space that contains the service page.
     */
    private static final String SERVICE_SPACE_NAME = "ExtensionTest";

    /**
     * The name of the service page.
     */
    private static final String SERVICE_PAGE_NAME = "Service";

    /**
     * The generic test utility methods.
     */
    private final TestUtils utils;

    /**
     * Creates a new instance.
     * 
     * @param utils the generic test utility methods
     */
    public ExtensionTestUtils(TestUtils utils)
    {
        this.utils = utils;

        // Create the service page.
        StringBuilder code = new StringBuilder("{{velocity output=\"false\"}}\n");
        code.append("#if ($request.action == 'uninstall')\n");
        code.append("  $services.extension.uninstall($request.extensionId, $NULL).join()\n");
        code.append("#elseif ($request.action == 'install')\n");
        code.append("  $services.extension.install($request.extensionId, $request.extensionVersion, $NULL).join()\n");
        code.append("#end\n");
        code.append("{{/velocity}}");
        utils.gotoPage(SERVICE_SPACE_NAME, SERVICE_PAGE_NAME, "save",
            Collections.singletonMap("content", code.toString()));
    }

    /**
     * Uninstalls the specified extension.
     * 
     * @param extensionId the id of the extension to uninstall
     */
    public void uninstall(String extensionId)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("action", "uninstall");
        parameters.put("extensionId", extensionId);
        utils.gotoPage(SERVICE_SPACE_NAME, SERVICE_PAGE_NAME, "get", parameters);
    }

    /**
     * Installs the specified extension.
     * 
     * @param extensionId the id of the extension to install
     * @param extensionVersion the version to install
     */
    public void install(String extensionId, String extensionVersion)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("action", "install");
        parameters.put("extensionId", extensionId);
        parameters.put("extensionVersion", extensionVersion);
        utils.gotoPage(SERVICE_SPACE_NAME, SERVICE_PAGE_NAME, "get", parameters);
    }
}
