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

import junit.framework.TestCase;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripperByArea;

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

    /**
     * Verify the PDF export with table of contents.
     * 
     * @see "XWIKI-9370: PDF Export doesn't list the Table of Contents under certain circumstances"
     */
    public void testTableOfContents() throws Exception
    {
        Map<String, String> internalLinks =
            extractToCLinks(new URL("http://localhost:8080/xwiki/bin/export/Sandbox/WebHome"
                + "?format=pdf&pdftoc=1&attachments=1&pdfcover=0"), 0);
        // Make sure we have a Table of Contents.
        assertTrue(internalLinks.containsKey("Mixed list"));
        // Make sure the Table of Contents links point to their corresponding heading.
        for (Map.Entry<String, String> entry : internalLinks.entrySet()) {
            assertTrue(entry.getValue().contains(entry.getKey()));
        }
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

    private Map<String, String> extractURLs(URL url) throws Exception
    {
        Map<String, String> urls = new HashMap<String, String>();
        PDDocument document = null;
        try {
            document = PDDocument.load(url);
            for (Map.Entry<String, PDAction> entry : extractLinks(document).entrySet()) {
                if (entry.getValue() instanceof PDActionURI) {
                    PDActionURI uri = (PDActionURI) entry.getValue();
                    urls.put(entry.getKey(), uri.getURI());
                }
            }
        } finally {
            if (document != null) {
                document.close();
            }
        }
        return urls;
    }

    private Map<String, String> extractToCLinks(URL url, int tocPageIndex) throws Exception
    {
        Map<String, String> internalLinks = new HashMap<String, String>();
        PDDocument document = null;
        try {
            document = PDDocument.load(url);
            PDPage tocPage = (PDPage) document.getDocumentCatalog().getAllPages().get(tocPageIndex);
            for (Map.Entry<String, PDAction> entry : extractLinks(tocPage).entrySet()) {
                if (entry.getValue() instanceof PDActionGoTo) {
                    PDActionGoTo anchor = (PDActionGoTo) entry.getValue();
                    internalLinks.put(entry.getKey(), getDestinationText(anchor.getDestination()));
                }
            }
        } finally {
            if (document != null) {
                document.close();
            }
        }
        return internalLinks;
    }

    @SuppressWarnings("unchecked")
    private Map<String, PDAction> extractLinks(PDDocument document) throws Exception
    {
        Map<String, PDAction> links = new HashMap<String, PDAction>();
        for (PDPage page : (List<PDPage>) document.getDocumentCatalog().getAllPages()) {
            links.putAll(extractLinks(page));
        }
        return links;
    }

    /**
     * Code adapted from http://www.docjar.com/html/api/org/apache/pdfbox/examples/pdmodel/PrintURLs.java.html
     */
    private Map<String, PDAction> extractLinks(PDPage page) throws Exception
    {
        Map<String, PDAction> links = new HashMap<String, PDAction>();
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        List<PDAnnotation> annotations = page.getAnnotations();
        // First setup the text extraction regions.
        for (int j = 0; j < annotations.size(); j++) {
            PDAnnotation annotation = annotations.get(j);
            if (annotation instanceof PDAnnotationLink) {
                PDAnnotationLink link = (PDAnnotationLink) annotation;
                PDRectangle rect = link.getRectangle();
                // Need to reposition link rectangle to match text space.
                float x = rect.getLowerLeftX();
                float y = rect.getUpperRightY();
                float width = rect.getWidth();
                float height = rect.getHeight();
                int rotation = page.findRotation();
                if (rotation == 0) {
                    PDRectangle pageSize = page.findMediaBox();
                    y = pageSize.getHeight() - y;
                } else if (rotation == 90) {
                    // Do nothing.
                }

                Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);
                stripper.addRegion(String.valueOf(j), awtRect);
            }
        }

        stripper.extractRegions(page);

        for (int j = 0; j < annotations.size(); j++) {
            PDAnnotation annotation = annotations.get(j);
            if (annotation instanceof PDAnnotationLink) {
                PDAnnotationLink link = (PDAnnotationLink) annotation;
                String label = stripper.getTextForRegion(String.valueOf(j)).trim();
                links.put(label, link.getAction());
            }
        }

        return links;
    }

    private String getDestinationText(PDDestination destination) throws Exception
    {
        if (destination instanceof PDPageXYZDestination) {
            return getDestinationText((PDPageXYZDestination) destination);
        } else if (destination instanceof PDPageDestination) {
            return "Page " + ((PDPageDestination) destination).getPageNumber();
        }
        return destination.toString();
    }

    private String getDestinationText(PDPageXYZDestination destination) throws Exception
    {
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.addRegion("destination", getRectangleBelowDestination(destination));
        stripper.extractRegions(destination.getPage());
        return stripper.getTextForRegion("destination").trim();
    }

    private Rectangle2D getRectangleBelowDestination(PDPageXYZDestination destination)
    {
        PDPage page = destination.getPage();
        PDRectangle pageSize = page.findMediaBox();
        float x = destination.getLeft();
        float y = pageSize.getHeight() - destination.getTop();
        float width = pageSize.getWidth();
        float height = destination.getTop();
        return new Rectangle2D.Float(x, y, width, height);
    }
}
