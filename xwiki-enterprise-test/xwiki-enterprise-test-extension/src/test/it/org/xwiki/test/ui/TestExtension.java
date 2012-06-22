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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.xwiki.extension.AbstractExtension;
import org.xwiki.extension.ExtensionId;

/**
 * @version $Id$
 * @since 4.2M1
 */
public class TestExtension extends AbstractExtension
{
    public final static String FOLDERNAME_EXETENSIONS = "target/extensions/";
    
    public TestExtension(ExtensionId id, String type)
    {
        super(null, id, type);

        setFile(new TestExtensionFile(new File(FOLDERNAME_EXETENSIONS + encode(id.getId()) + '-'
            + encode(id.getVersion().getValue()) + '.' + type)));
    }

    @Override
    public TestExtensionFile getFile()
    {
        return (TestExtensionFile) super.getFile();
    }

    /**
     * @param name the file or directory name to encode
     * @return the encoding name
     */
    private String encode(String name)
    {
        String encoded;
        try {
            encoded = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never happen

            encoded = name;
        }

        return encoded;
    }
}
