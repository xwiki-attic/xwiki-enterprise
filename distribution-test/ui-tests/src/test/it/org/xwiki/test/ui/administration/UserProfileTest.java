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
package org.xwiki.test.ui.administration;

import junit.framework.Assert;

import org.junit.Test;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Test the user profile.
 * 
 * @version $Id$
 * @since 2.4
 */
public class UserProfileTest extends AbstractAdminAuthenticatedTest
{
    /**
     * Check that the content of the first comment isn't used as the "About" information in the user profile. See
     * XAADMINISTRATION-157.
     */
    @Test
    public void testCommentDoesntOverrideAboutInformation()
    {
        String commentContent = "this is from a comment";
        ViewPage profile = this.getUtil().gotoPage("XWiki", "Admin");
        int commentId = -1;
        try {
            commentId = profile.openCommentsDocExtraPane().postComment(commentContent);
            getDriver().navigate().refresh();
            Assert.assertFalse("Comment content was used as profile information",
                profile.getContent().contains(commentContent));
        } finally {
            if (commentId != -1) {
                profile.openCommentsDocExtraPane().deleteComment(commentId);
            }
        }
    }
}
