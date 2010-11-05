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
package org.xwiki.test.ui.annotations.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Represents the Annotation window that appears when selecting a text and simulating CTRL+M
 * 
 * @version $Id$
 * @since 2.5M2
 */
public class AnnotationsWindow extends ViewPage
{

    @FindBy(xpath = "//div[@class='buttons']//input[@type='submit']")
    private WebElement submitButton;

    @FindBy(xpath = "//div[@class='buttons']//input[@type='reset']")
    private WebElement cancelButton;

    @FindBy(xpath = "//textarea[@id='annotation']")
    private WebElement inputText;

    private StringBuilder script;

    /**
     * the super() constructor + injection of the js function that selects the text from the page (drag-over with mouse
     * simulation)
     */
    public AnnotationsWindow()
    {
        super();
        script = new StringBuilder();
        script.append("function findString (str) {\n");
        script.append("var strFound;\n");
        script.append("if (window.find) {\n");
        script.append("if (parseInt(navigator.appVersion)<4) return;\n");
        script.append("// CODE FOR BROWSERS THAT SUPPORT window.find\n");
        script.append("strFound=self.find(str);\n");
        script.append("if (strFound && self.getSelection && !self.getSelection().anchorNode) {\n");
        script.append("strFound=self.find(str);\n");
        script.append("}\n");
        script.append("if (!strFound) {\n");
        script.append("strFound=self.find(str,0,1);\n");
        script.append("while (self.find(str,0,1)) continue;\n");
        script.append("}\n");
        script.append("}\n");
        script.append("else if (navigator.appName.indexOf(\"Microsoft\")!=-1) {\n");
        script.append("// EXPLORER-SPECIFIC CODE\n");
        script.append("if (TRange!=null)\n");
        script.append("{\n");
        script.append("TRange.collapse(false);\n");
        script.append("strFound=TRange.findText(str);\n");
        script.append("if (strFound) TRange.select();\n");
        script.append("}\n");
        script.append("if (TRange==null || strFound==0) {\n");
        script.append("TRange=self.document.body.createTextRange();\n");
        script.append("strFound=TRange.findText(str);\n");
        script.append(" if (strFound) TRange.select();\n");
        script.append("}\n");
        script.append("}\n");
        script.append("else if (navigator.appName==\"Opera\") {\n");
        script.append("alert (\"Opera browsers not supported, sorry...\");\n");
        script.append("return;\n");
        script.append("}\n");
        script.append("if (!strFound) \n");
        script.append("return;\n");
        script.append("}\n");
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript(script.toString());

    }

    /**
     * @param annotationWord word or succession of words that will be selected on the screen
     */
    public void selectAnnotationWord(String annotationWord)
    {

        script.append("findString('" + annotationWord + "');");
        // System.out.println(script.toString());
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript(script.toString());
    }

    /**
     * The text that the annotation will contain
     * 
     * @param annotationText String that contains the annotation
     */
    public void enterAnnotationText(String annotationText)
    {
        // waitUntilElementDisappears(By.className("annotation-box annotation-box-create"));
        waitUntilElementIsVisible(By.className("annotation-box-create"));
        this.inputText.sendKeys(annotationText);
    }

    /**
     * Saves the annotation and waits until the notification window disappears
     * 
     * @param none
     */
    public void saveAnnotation()
    {
        this.submitButton.click();
    }

    /**
     * Cancels adding the annotation
     * 
     * @param none
     */
    public void cancelAnnotation()
    {
        this.cancelButton.click();
    }

    /**
     * Adds an annotation
     * 
     * @param selectedText The text that is to be annotated
     * @param annotationText The text that is added to annotation's content
     */
    public void addAnnotation(String selectedText, String annotationText)
    {
        this.selectAnnotationWord(selectedText);
        this.simulateCTRL_M();
        this.enterAnnotationText(annotationText);
        this.saveAnnotation();
    }

    public void simulateCTRL_M()
    {
        RenderedWebElement body = (RenderedWebElement) getDriver().findElement(By.id("body"));
        body.sendKeys(Keys.CONTROL, "m");
    }

}
