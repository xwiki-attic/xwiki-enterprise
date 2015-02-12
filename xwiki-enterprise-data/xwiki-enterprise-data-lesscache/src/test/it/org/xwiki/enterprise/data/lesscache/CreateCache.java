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
package org.xwiki.enterprise.data.lesscache;

import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import org.apache.commons.httpclient.HttpClient;

/**
 * Create a LESS cache just by browsing the wiki.
 *
 * @version $Id$
 * @since 7.0M2
 */
public class CreateCache
{
    @Test
    public void createLESSCache() throws Exception
    {
        HttpClient httpClient = new HttpClient();

        GetMethod getMethod = new GetMethod("http://localhost:8080/xwiki/");
        httpClient.executeMethod(getMethod);
    }
}
