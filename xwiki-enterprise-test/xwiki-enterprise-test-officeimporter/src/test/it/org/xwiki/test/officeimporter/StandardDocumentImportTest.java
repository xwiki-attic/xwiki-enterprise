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
package org.xwiki.test.officeimporter;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;
import org.xwiki.test.officeimporter.framework.AbstractOfficeImporterTestCase;

/**
 * A test for importing standard office document formats.
 *
 * @version $Id$
 * @since 1.8M1
 */
public class StandardDocumentImportTest extends AbstractOfficeImporterTestCase
{   
    /**
     * @return the suit of tests containing all the test cases for office importer.
     */
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests importing of standard document types");
        suite.addTestSuite(StandardDocumentImportTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }
    
    /**
     * Test importing of all standard document types.
     */
    public void testAll() {
        importDocument("msoffice/97-2003/Test.doc");
        importDocument("msoffice/97-2003/Test.xls");
        importDocument("msoffice/97-2003/Test.ppt");
        importDocument("ooffice/3.0/Test.odt");
        importDocument("ooffice/3.0/Test.ods");
        importDocument("ooffice/3.0/Test.odp");
    }
}
