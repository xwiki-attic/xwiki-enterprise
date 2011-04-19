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
