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
package org.xwiki.test.xmlrpc;

/**
 * This class defines the constants that are used when performing the tests. In particular, the XWiki instance must have
 * all the pages TEST_PAGE_* existing, and the TEST_PAGE_WITH_ATTACHMENTS with some attachments loaded into.
 * 
 * @version $Id$
 */
public final class TestConstants
{
    public static final String USERNAME = "Admin";

    public static final String PASSWORD = "admin";

    public static final String ENDPOINT = "http://localhost:8080/xwiki/xmlrpc";

    public static final String SPACE_WITH_NO_ACCESS_RIGHTS = "Scheduler";

    public static final String PAGE_WITH_NO_ACCESS_RIGHTS = "XWiki.Administration";

    public static final String TEST_SPACE = "Test";

    public static final String TEST_PREFIX = "TEST";

    public static final String TEST_PAGE = "Test.Test";

    public static final String TEST_PAGE_WITH_TRANSLATIONS = "Test.Translations";

    public static final String TEST_PAGE_WITH_COMMENTS = "Test.Comments";

    public static final String TEST_PAGE_WITH_ATTACHMENTS = "Test.Attachments";

    public static final String TEST_PAGE_WITH_OBJECTS = "Test.Objects";
}
