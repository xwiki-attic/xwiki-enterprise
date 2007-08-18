/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors.
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
package com.xpn.xwiki.it.xmlrpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.Page;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.css.css.DocumentParser;
import org.w3c.css.css.StyleReport;
import org.w3c.css.css.StyleReportFactory;
import org.w3c.css.css.StyleSheet;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.HTTPURL;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.xpn.xwiki.plugin.packaging.Package;

/**
 * Verifies that all pages in the default wiki are valid XHTML documents using the Confluence XMLRPC
 * API.
 * 
 * @version $Id: $
 */
public class XhtmlValidityTest extends TestCase implements ErrorHandler
{
    private String fullPageName;

    private static Validator xv;

    // private static Validator rv;

    private Confluence rpc;

    private int errors = 0;

    public XhtmlValidityTest(String fullPageName)
    {
        super("testValidityOfDocument");

        this.fullPageName = fullPageName;
    }

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();

        String path =
            System.getProperty("localRepository") + "/" + System.getProperty("pathToXWikiXar");

        List pageNames = readXarContents(path);
        Iterator it = pageNames.iterator();
        while (it.hasNext()) {
            suite.addTest(new XhtmlValidityTest((String) it.next()));
        }

        // Prepare validators only once, as this is a costly process
        System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema",
            "org.apache.xerces.jaxp.validation.XMLSchemaFactory");
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema xhtmlSchema = sf.newSchema(new File("xhtml1-strict.xsd"));
        // Schema rssSchema = sf.newSchema(new File("rdf.xsd"));
        xv = xhtmlSchema.newValidator();
        // rv = rssSchema.newValidator();
        // rv.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
        // "http://purl.org/dc/elements/1.1/ dc.xsd http://www.w3.org/1999/xhtml xhtml1-strict.xsd
        // http://purl.org/rss/1.0/ rss.rdf");

        return suite;
    }

    public String getName()
    {
        return "Validating " + fullPageName;
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        rpc = new Confluence("http://127.0.0.1:8080/xwiki/xmlrpc");
        rpc.login("Admin", "admin");
    }

    protected void tearDown() throws Exception
    {
        rpc.logout();

        super.tearDown();
    }

    public void testValidityOfDocument() throws Exception
    {
        // TODO Until we find a way to incrementally display the result of tests this stays
        System.out.println(getName());

        Page page = rpc.getPage(fullPageName);
        String renderedContent = rpc.renderContent(page);
        assertNotNull(renderedContent);

        if (renderedContent.indexOf("<rdf:RDF") != -1) {
            // Ignored for the moment, until we can validate using XMLSchema
            // rv.setErrorHanndler(this);
        } else {
            xv.setErrorHandler(this);

            InputSource src =
                new InputSource(new StringReader(completeXhtml(page.getTitle(), renderedContent)));
            Source s = new SAXSource(src);
            try {
                xv.validate(s);
            } catch (SAXParseException ex) {
                System.err.println(ex.getMessage());
                errors++;
            }
            errors += assertCssValid(page.getUrl());
        }
        assertTrue(errors == 0);
    }

    private static List readXarContents(String fileName) throws Exception
    {
        FileInputStream fileIS = new FileInputStream(fileName);
        ZipInputStream zipIS = new ZipInputStream(fileIS);

        ZipEntry entry;
        Document tocDoc = null;
        while ((entry = zipIS.getNextEntry()) != null) {
            if (entry.getName().compareTo(Package.DefaultPackageFileName) == 0) {
                SAXReader reader = new SAXReader();
                tocDoc = reader.read(zipIS);
                break;
            }
        }

        if (tocDoc == null) {
            return new ArrayList();
        }

        List result = new ArrayList();

        Element filesElement = tocDoc.getRootElement().element("files");
        List fileElementList = filesElement.elements("file");
        Iterator it = fileElementList.iterator();
        while (it.hasNext()) {
            Element el = (Element) it.next();
            result.add(el.getStringValue());
        }

        return result;
    }

    private static String completeXhtml(String title, String content)
    {
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n"
            + "<head>\n" + "<title>" + title + "</title>\n" + "</head>\n" + "<body>\n<div>\n"
            + content.replaceAll("&nbsp;", "&#160;") + "\n</div>\n</body>\n</html>";
    }

    private static int assertCssValid(String url) throws Exception
    {
        ApplContext ac = new ApplContext("en");
        ac.setProfile("css21");
        ac.setCssVersion("css21");
        ac.setMedium("all");
        String output = "text";
        int warningLevel = 2;

        String encoding = ac.getMsg().getString("output-encoding-name");
        PrintWriter out;
        if (encoding != null) {
            out = new PrintWriter(new OutputStreamWriter(System.out, encoding));
        } else {
            out = new PrintWriter(new OutputStreamWriter(System.out));
        }
        String uri = HTTPURL.getURL(url).toString();
        DocumentParser URLparser = new DocumentParser(ac, uri);

        StyleSheet styleSheet = URLparser.getStyleSheet();
        if (styleSheet == null) {
            throw new IOException(ac.getMsg().getServletString("process") + " " + uri);
        }

        styleSheet.findConflicts(ac); // this is bogus ?

        StyleReport style =
            StyleReportFactory.getStyleReport(ac, uri, styleSheet, output, warningLevel);

        int errors = styleSheet.getErrors().getErrorCount();
        if (errors > 0) {
            style.print(out);
        }
        return errors;
    }

    public void error(SAXParseException exception) throws SAXException
    {
        this.errors++;
        System.out.println(exception.getMessage());
    }

    public void fatalError(SAXParseException exception) throws SAXException
    {
        this.errors++;
        System.out.println(exception.getMessage());
        exception.printStackTrace();
    }

    public void warning(SAXParseException exception) throws SAXException
    {
        System.out.println(exception.getMessage());
    }
}
