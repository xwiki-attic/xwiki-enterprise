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
package org.xwiki.it.ui;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.it.ui.elements.WikiEditPage;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;

/**
 * Test script macros.
 * 
 * @version $Id$
 * @since 2.4M2
 */
public class ScriptingTest extends AbstractAdminAuthenticatedTest
{
    private static boolean initialized;

    @Before
    public void setUp()
    {
        if (!initialized) {
            super.setUp();
            initialized = true;
        }
    }

    @Test
    public void testNestedEvaluation()
    {
        final String content = "{{html wiki=\"true\"}}\n{{velocity}}\n#set($x = 'script' + ' test')\n$x\n"
            + "{{/velocity}}\n{{/html}}\n";

        getDriver().get(getUtil().getURL("ScriptingTest", "testNestedEvaluation", "preview",
            new HashMap<String, String>(){{put("content", content);}}));

        // check that the nested script is evaluated
        WebElement pageContent = getDriver().findElement(By.xpath("//div[@id='xwikicontent']/p"));
        Assert.assertEquals("Invalid content", "script test", pageContent.getText());
    }

    @Test
    public void testNestedInline()
    {
        final String content = "text {{velocity}}\n\n{{html}}<strong>$doc.fullName</strong>{{/html}}\n\n"
            + "{{/velocity}} text";

        getDriver().get(getUtil().getURL("ScriptingTest", "testNestedInline", "preview",
            new HashMap<String, String>(){{put("content", content);}}));

        // check that the inner text is inline despite having empty lines around html
        WebElement pageContent = getDriver().findElement(By.id("xwikicontent"));
        Assert.assertEquals("Invalid content", "text ScriptingTest.testNestedInline text", pageContent.getText());
        long numParagraphs = getDriver().findElements(By.xpath("//div[@id='xwikicontent']//p")).size();
        Assert.assertEquals("Content is not inline", 1, numParagraphs);
    }

    @Test
    public void testNestedNotInline()
    {
        final String content = "text\n\n{{velocity}}{{html}}<strong>$doc.fullName</strong>{{/html}}{{/velocity}}"
            + "\n\ntext";

        getDriver().get(getUtil().getURL("ScriptingTest", "testNestedNotInline", "preview",
            new HashMap<String, String>(){{put("content", content);}}));

        // check that inner text is not inline
        WebElement pageContent = getDriver().findElement(By.id("xwikicontent"));
        Assert.assertEquals("Invalid content", "text\nScriptingTest.testNestedNotInline\ntext", pageContent.getText());
        long numParagraphs = getDriver().findElements(By.xpath("//div[@id='xwikicontent']//p")).size();
        Assert.assertEquals("Invalid number of paragraphs", 3, numParagraphs);
    }

    @Test
    public void testNoNestedScripts()
    {

        final String content = "{{velocity}}\n{{html wiki=\"true\"}}<strong>"
            + "{{velocity}}\\$doc.fullName{{/velocity}}</strong>{{/html}}\n{{/velocity}}";

        getDriver().get(getUtil().getURL("ScriptingTest", "testNoNestedScripts", "preview",
            new HashMap<String, String>(){{put("content", content);}}));

        // check that the content does not contain Test.ScriptTest anywhere
        WebElement pageContent = getDriver().findElement(By.id("xwikicontent"));
        Assert.assertFalse("The nested script was evaluated", 
            pageContent.getText().contains("ScriptingTest.testNoNestedScripts"));

        // check that the nested script produced an error message
        WebElement error = getDriver().findElement(By.xpath("//div[@id='xwikicontent']/p/strong"
            + "/span[@class='xwikirenderingerror']"));
        Assert.assertNotNull("No error message on nested scripts", error);
    }
}
