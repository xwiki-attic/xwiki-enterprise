package org.xwiki.test.ui.panels;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.panels.elements.NewPagePanel;

/**
 * Test page creation using the NewPage Panel.
 * 
 * @version $Id$
 * @since 2.5RC1
 */
public class NewPagePanelTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The object used to access the name of the current test.
     */
    @Rule
    public final TestName testName = new TestName();

    /**
     * Tests if a new page can be created using the create page panel.
     */
    @Test
    public void testCreatePageFromPanel()
    {
        NewPagePanel newPagePanel = new NewPagePanel();
        newPagePanel.gotoPage();

        String spaceName = this.getClass().getSimpleName();
        String pageName = testName.getMethodName();

        WYSIWYGEditPage editPage = newPagePanel.createPage(spaceName, pageName);

        Assert.assertEquals(pageName, editPage.getDocumentTitle());
        Assert.assertEquals(pageName, editPage.getMetaDataValue("page"));
        Assert.assertEquals(spaceName, editPage.getMetaDataValue("space"));
    }
}
