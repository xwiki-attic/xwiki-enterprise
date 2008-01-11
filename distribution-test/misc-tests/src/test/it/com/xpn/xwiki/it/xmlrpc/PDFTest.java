package com.xpn.xwiki.it.xmlrpc;

import junit.framework.TestCase;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

public class PDFTest extends TestCase
{
    /**
     * Verify that the PDF export feature works on a single simple page by downloading the PDF and parsing it using
     * PDFBox.
     */
    public void testExportSingleSimplePageAsPDF() throws Exception
    {
        URL url = new URL("http://localhost:8080/xwiki/bin/export/Main/WebHome?format=pdf");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream is = connection.getInputStream();
        PDDocument pdd = PDDocument.load(is);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(pdd);
        pdd.close();
        is.close();

        assertTrue("Invalid content", text.contains("Welcome to your wiki"));
    }
}
