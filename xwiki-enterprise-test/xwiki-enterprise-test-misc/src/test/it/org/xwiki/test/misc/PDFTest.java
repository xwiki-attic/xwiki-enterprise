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

import java.awt.geom.Rectangle2D;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripperByArea;

import junit.framework.TestCase;

public class PDFTest extends TestCase
{
    /**
     * Verify that the PDF export feature works on a single simple page by downloading the PDF and parsing it using
     * PDFBox.
     *
     * @see "XWIKI-7048: PDF export templates can display properties of other objects if the XWiki.PDFClass object is
     *      missing"
     */
    public void testExportSingleSimplePageAsPDF() throws Exception
    {
        // We're using Dashboard.WebHome page because it has objects of type XWiki.GadgetClass and they have a title
        // property which was mistaken with the title property of XWiki.PDFClass before XWIKI-7048 was fixed. The gadget
        // title contains Velocity code that isn't wrapped in a Velocity macro so it is printed as is if not rendered in
        // the right context.
        String text = getPDFContent(new URL("http://localhost:8080/xwiki/bin/export/Dashboard/WebHome?format=pdf"));
        assertTrue("Invalid content", text.contains("Welcome to your wiki"));
        assertFalse("Invalid content", text.contains("$services.localization.render("));
    }

    /**
     * Verify that we can export content having links to attachments.
     *
     * @see "XWIKI-8978: PDF Export does not handle XWiki links to attached files properly"
     */
    public void testExportContentWithAttachmentLink() throws Exception
    {
        Map<String, String> urls =
            extractURLs(new URL("http://localhost:8080/xwiki/bin/export/Sandbox/WebHome?format=pdf"));
        assertTrue(urls.containsKey("XWikiLogo.png"));
        assertEquals("http://localhost:8080/xwiki/bin/download/Sandbox/WebHome/XWikiLogo.png",
            urls.get("XWikiLogo.png"));
    }

    private String getPDFContent(URL url) throws Exception
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream is = connection.getInputStream();
        PDDocument pdd = PDDocument.load(is);
        PDFText2HTML stripper = new PDFText2HTML("UTF-8");
        String text = stripper.getText(pdd);
        pdd.close();
        is.close();
        return text;
    }

    /**
     * Code copied from http://www.docjar.com/html/api/org/apache/pdfbox/examples/pdmodel/PrintURLs.java.html
     */
    private Map<String, String> extractURLs(URL url) throws Exception
    {
        Map<String, String> urls = new HashMap<String, String>();
        PDDocument doc = null;
        try {

            doc = PDDocument.load(url);
            List allPages = doc.getDocumentCatalog().getAllPages();
            for (int i = 0; i < allPages.size(); i++) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                PDPage page = (PDPage) allPages.get(i);
                List annotations = page.getAnnotations();
                //first setup text extraction regions
                for (int j = 0; j < annotations.size(); j++) {
                    PDAnnotation annot = (PDAnnotation) annotations.get(j);
                    if (annot instanceof PDAnnotationLink) {
                        PDAnnotationLink link = (PDAnnotationLink) annot;
                        PDRectangle rect = link.getRectangle();
                        //need to reposition link rectangle to match text space
                        float x = rect.getLowerLeftX();
                        float y = rect.getUpperRightY();
                        float width = rect.getWidth();
                        float height = rect.getHeight();
                        int rotation = page.findRotation();
                        if (rotation == 0) {
                            PDRectangle pageSize = page.findMediaBox();
                            y = pageSize.getHeight() - y;
                        } else if (rotation == 90) {
                            //do nothing
                        }

                        Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);
                        stripper.addRegion("" + j, awtRect);
                    }
                }

                stripper.extractRegions(page);

                for (int j = 0; j < annotations.size(); j++) {
                    PDAnnotation annot = (PDAnnotation) annotations.get(j);
                    if (annot instanceof PDAnnotationLink) {
                        PDAnnotationLink link = (PDAnnotationLink) annot;
                        PDAction action = link.getAction();
                        String urlText = stripper.getTextForRegion("" + j);
                        if (action instanceof PDActionURI) {
                            PDActionURI uri = (PDActionURI) action;
                            urls.put(urlText.trim(), uri.getURI());
                        }
                    }
                }
            }
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
        return urls;
    }
}
