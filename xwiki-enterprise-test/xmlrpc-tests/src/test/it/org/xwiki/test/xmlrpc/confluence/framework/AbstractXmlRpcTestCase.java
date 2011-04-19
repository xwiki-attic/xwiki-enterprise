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
package org.xwiki.test.xmlrpc.confluence.framework;

import org.codehaus.swizzle.confluence.Confluence;
import org.custommonkey.xmlunit.XMLTestCase;

public abstract class AbstractXmlRpcTestCase extends XMLTestCase
{
    protected Confluence rpc; // xml-rpc proxy
    
    public AbstractXmlRpcTestCase()
    {
        super();
    }

    public AbstractXmlRpcTestCase(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        
        rpc  = new Confluence("http://127.0.0.1:8080/xwiki/xmlrpc");
             //= new SwizzleXWikiClient("http://127.0.0.1:9090/rpc/xmlrpc");
        rpc.login("Admin", "admin");
           // rpc.login("admin", "admin");
    }

    public void tearDown() throws Exception
    {
        rpc.logout();
        
        super.tearDown();
    }
}
