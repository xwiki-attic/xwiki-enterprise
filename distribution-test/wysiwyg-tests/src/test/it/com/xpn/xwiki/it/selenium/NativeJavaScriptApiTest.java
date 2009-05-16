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
package com.xpn.xwiki.it.selenium;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractWysiwygTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

public class NativeJavaScriptApiTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests wysiwyg native JS API");
        suite.addTestSuite(NativeJavaScriptApiTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }       
    
    public void testTextAreaElementsGetters()
    {        
        // Wait for the API to be injected.
        waitForCondition("typeof window.Wysiwyg.getPlainTextArea == 'function'");
        
        // Test plain text editor.
        assertTrue("xPlainTextEditor".equals(getEval("window.Wysiwyg.getPlainTextArea('content').className")));
        assertTrue("TEXTAREA".equals(getEval("window.Wysiwyg.getPlainTextArea('content').nodeName")));
        
        // Test rich text editor.
        assertTrue("xRichTextEditor".equals(getEval("window.Wysiwyg.getRichTextArea('content').className")));
        assertTrue("IFRAME".equals(getEval("window.Wysiwyg.getRichTextArea('content').nodeName")));
    }
}
