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

import java.util.List;

import org.junit.Assert;

import org.apache.xmlrpc.XmlRpcException;
import org.xwiki.xmlrpc.model.XWikiPage;

public class RenderingTest extends AbstractXWikiXmlRpcTest
{
    public void testGetInputSyntaxes() throws XmlRpcException
    {
        List<String> syntaxes = rpc.getInputSyntaxes();

        TestUtils.banner("TEST: getInputSyntaxes()");
        System.out.format("%d conversion input syntaxes found:\n", syntaxes.size());
        for (String syntaxId : syntaxes) {
            System.out.format("%s\n", syntaxId);
        }
        Assert.assertTrue(syntaxes.size() != 0);
    }

    public void testGetOutputSyntaxes() throws XmlRpcException
    {
        List<String> syntaxes = rpc.getOutputSyntaxes();

        TestUtils.banner("TEST: getOutputSyntaxes()");
        System.out.format("%d conversion output syntaxes found:\n", syntaxes.size());
        for (String syntaxId : syntaxes) {
            System.out.format("%s\n", syntaxId);
        }
        Assert.assertTrue(syntaxes.size() != 0);
    }

    public void testConvert() throws XmlRpcException
    {
        String inputXWiki =
                "**BoldText**//ItalicText//" + String.format("%n") + "==h2Text==" + String.format("%n") + "normalText";
        String inputXhtml =
                "<div><p><strong>boldText</strong><br/><em>ItalicText</em></p>" + String.format("%n")
                    + "<h2 id=\"h2Text\"><span>h2Text</span></h2><p>normalText</p></div>";

        TestUtils.banner("TEST: convert()");
        System.out.format("\nTesting xwiki/2.0 -> xhtml/1.0 conversion\n");
        System.out.format("Input text:\n%s", inputXWiki);

        String outputXhtml = rpc.convert(inputXhtml, "xwiki/2.0", "xhtml/1.0");

        System.out.format("\nOutput text:\n%s", outputXhtml);

        Assert.assertNotNull(outputXhtml);
        Assert.assertFalse(outputXhtml.equals(""));
        Assert.assertTrue(outputXhtml.contains("strong"));
        Assert.assertTrue(outputXhtml.contains("normalText"));
        Assert.assertTrue(outputXhtml.contains("ItalicText"));

        System.out.format("\nTesting xhtml/1.0 -> xwiki/2.0 conversion\n");
        System.out.format("Input text:\n%s", inputXhtml);

        String outputXWiki = rpc.convert(inputXhtml, "xhtml/1.0", "xwiki/2.0");

        System.out.format("\nOutput text:\n%s\n", outputXWiki);

        Assert.assertNotNull(outputXWiki);
        Assert.assertFalse(outputXhtml.equals(""));
        Assert.assertTrue(outputXhtml.contains("normalText"));
        Assert.assertTrue(outputXhtml.contains("ItalicText"));
    }

    public void testGetRenderedContent() throws XmlRpcException
    {
        String pageContent = "**Text in Bold**{{velocity}}VelocityCode{{/velocity}}";

        XWikiPage page = new XWikiPage();
        page.setId(TestConstants.TEST_PAGE);
        page.setContent(pageContent);
        page.setSyntaxId("xwiki/2.0");

        TestUtils.banner("TEST: getRenderedContent()");
        System.out.format("\nCalling getRenderedContent for page %s\n", page.getId());
        System.out.format("\nWiki content is:\n%s\n", pageContent);

        rpc.storePage(page);

        try {
            String renderedContent = rpc.getRenderedContent(page.getId(), "annotatedxhtml/1.0");

            System.out.format("\nObtained rendered content:\n%s\n\n", renderedContent);

            Assert.assertTrue(renderedContent.contains("Text in Bold"));
            Assert.assertTrue(renderedContent.contains("startmacro"));
        } finally {
            rpc.removePage(page.getId());
        }
    }

    public void testRenderPageContent() throws XmlRpcException
    {
        String pageContent = "**Text in Bold**{{velocity}}VelocityCode{{/velocity}}";

        XWikiPage page = new XWikiPage();
        page.setId(TestConstants.TEST_PAGE);
        page.setContent(pageContent);
        page.setSyntaxId("xwiki/2.0");

        TestUtils.banner("TEST: renderPageContent");
        System.out.format("\nCalling renderPageContent with context as page %s\n", TestConstants.TEST_PAGE);
        System.out.format("\nWiki content is:\n%s\n", pageContent);

        rpc.storePage(page);

        try {
            String renderedContent =
                    rpc.renderPageContent(page.getId(), page.getContent(), page.getSyntaxId(), "annotatedxhtml/1.0");

            System.out.format("\nObtained rendered content:\n%s\n\n", renderedContent);

            Assert.assertTrue(renderedContent.contains("Text in Bold"));
            Assert.assertTrue(renderedContent.contains("startmacro"));
        } finally {
            rpc.removePage(page.getId());
        }
    }
}
