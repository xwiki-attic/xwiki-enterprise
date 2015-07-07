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
package org.xwiki.test.misc;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class HTMLExportTest extends TestCase
{
    /**
     * Verify that the HTML export feature works on a single simple page by downloading the generated Zip.
     */
    public void testHTMLExportCurrentPage() throws Exception
    {
        assertHTMLExportURL("http://localhost:8080/xwiki/bin/export/Main/WebHome?format=html");
    }

    /**
     * Verify that the HTML export feature works on a passed pattern instead of an explicit page name or the current
     * page.
     */
    public void testHTMLExportPattern() throws Exception
    {
        assertHTMLExportURL("http://localhost:8080/xwiki/bin/export/UnexistingSpace/UnexistingPage?format=html&pages=Main.WebH%25");
    }

    private void assertHTMLExportURL(String htmlExportURL) throws Exception
    {
        URL url = new URL(htmlExportURL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        InputStream is = connection.getInputStream();
        ZipInputStream zis = new ZipInputStream(is);

        boolean foundWebHome = false;
        boolean foundResourcesDirectory = false;
        boolean foundSkinsDirectory = false;
        boolean foundSkinCSS = false;
        boolean foundWebjars = false;

        // We must read the full stream as otherwise if we close it before we've fully read it
        // then the server side will get a broken pipe since it's still trying to send data on it.
        for (ZipEntry entry; (entry = zis.getNextEntry()) != null; zis.closeEntry()) {
            if (entry.getName().equals("xwiki.Main.WebHome.html")) {
                String content = IOUtils.toString(zis);

                // Verify that the content was rendered properly
                assertTrue("Should have contained 'Welcome to your wiki'", content.contains("Welcome to your wiki"));

                // Ensure that the translations have been rendered properly
                assertFalse("$services.localization.render should have been expanded",
                    content.contains("$services.localization.render"));

                foundWebHome = true;
            } else if (entry.getName().endsWith(".vm")) {
                fail("There shouldn't be any *.vm files in the generated zip!");
            } else if (entry.getName().endsWith(".less")) {
                fail("There shouldn't be any *.less files in the generated zip!");
            } else if (entry.getName().equals("xwiki.properties")) {
                fail("There shouldn't be any xwiki.properties file in the generated zip!");
            } else if (entry.getName().startsWith("resources/")) {
                foundResourcesDirectory = true;
                IOUtils.readLines(zis);
            } else if (entry.getName().startsWith("skins/")) {
                foundSkinsDirectory = true;
                // Verify that the skin is correctly going to be applied by verifying the flamingo/style.css file is
                // found
                // and is correctly referenced. This fixes http://jira.xwiki.org/browse/XWIKI-9145
                if (entry.getName().equals("skins/flamingo/style.css")) {
                    assertSkinIsActive(IOUtils.readLines(zis));
                    foundSkinCSS = true;
                } else {
                    IOUtils.readLines(zis);
                }
            } else if (entry.getName().startsWith("webjars")) {
                // We verify here that webjars URLs have been properly exported
                foundWebjars = true;
                IOUtils.readLines(zis);
            } else {
                IOUtils.readLines(zis);
            }
        }

        assertTrue("Failed to find the xwiki.Main.WebHome.html entry", foundWebHome);
        assertTrue("Failed to find the resources/ directory entry", foundResourcesDirectory);
        assertTrue("Failed to find the skins/ directory entry", foundSkinsDirectory);
        assertTrue("Failed to find the link to colibri.css in style.css", foundSkinCSS);
        assertTrue("Failed to find webjar resources in the HTML export", foundWebjars);

        zis.close();
    }

    private void assertSkinIsActive(List<String> content) throws Exception
    {
        assertTrue("style.css is not the one outputed by the flamingo skin", StringUtils.join(content.toArray())
            .contains("skin-flamingo"));
    }
}
