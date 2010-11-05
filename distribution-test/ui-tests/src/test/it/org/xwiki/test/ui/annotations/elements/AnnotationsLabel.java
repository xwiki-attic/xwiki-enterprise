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
import org.openqa.selenium.Keys;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Implements the Annotation window that appears when selecting an annotation
 * 
 * @version $Id&
 * @since 2.5M2
 */

public class AnnotationsLabel extends ViewPage
{

    @FindBy(xpath = "//a[@title='Delete this annotation']")
    private WebElement deleteAnnotation;

    @FindBy(xpath = "//span[@class='annotationAuthor']")
    private WebElement annotationAuthor;

    @FindBy(xpath = "annotationDate")
    private WebElement annotationDate;

    /**
     * Internal method used for finding the corresponding ID of the annotation that contains the specified text
     * 
     * @param searchText String containing the text that contains the desired annotation
     */
    private void selectAnnotationByText(String searchText)
    {

        waitUntilElementIsVisible(By.xpath("//span[contains(.,'" + searchText + "')]"));
        WebElement annotation = getDriver().findElement(By.xpath("//span[contains(.,'" + searchText + "')]"));
        String classId = annotation.getAttribute("class");
        // System.out.println("ClassID = " + classId.toString());
        classId = classId.split("\\s+")[1];
        // System.out.println("ClassID AFTER SPLIT = " + classId.toString());
        WebElement annotationPanel = getDriver().findElement(By.xpath("//span[@id='" + classId + "']"));
        annotationPanel.click();
    }

    /**
     * Internal method used for finding the corresponding ID
     * 
     * @param idText String that contains the annotation's ID
     */
    private void selectAnnotationById(String idText)
    {
        WebElement annotationPanel = getDriver().findElement(By.xpath("//span[@id='" + idText + "']"));
        annotationPanel.click();
    }

    /**
     * Deletes an annotation based on the annotated text
     * 
     * @param searchText String that contains the annotated text
     */
    public void deleteAnnotationByText(String searchText)
    {
        this.selectAnnotationByText(searchText);
        this.deleteAnnotation.click();
        waitUntilElementIsVisible(By.xpath("//input[@value='Yes']"));
        getDriver().findElement(By.xpath("//input[@value='Yes']")).click();

    }

    /**
     * Deletes an annotation based on it's ID
     * 
     * @param idText String that contains the annotation's ID
     */
    public void deleteAnnotationById(String idText)
    {
        this.selectAnnotationById(idText);
        this.deleteAnnotation.click();
        waitUntilElementIsVisible(By.xpath("//input[@value='Yes']"));
        getDriver().findElement(By.xpath("//input[@value='Yes']")).click();
    }

    /**
     * Deletes an annotation based on it's ID
     * 
     * @param idText returns string with the author of the annotation
     */
    public String getAnnotationAuthorById(String idText)
    {
        WebElement annotationPanel = getDriver().findElement(By.xpath("//span[@id='" + idText + "']"));
        annotationPanel.click();
        return this.annotationAuthor.getText();
    }

    /**
     * @param searchText annotated text
     * @return the Author of the Annotation
     */
    public String getAnnotationsAuthorByText(String searchText)
    {

        this.selectAnnotationByText(searchText);
        return this.annotationAuthor.getText();
    }

    /**
     * @param searchText annotated text
     * @return the unique ID of the annotation
     */
    public String getAnnotationIdByText(String searchText)
    {
        waitUntilElementIsVisible(By.xpath("//span[contains(.,'" + searchText + "')]"));
        WebElement annotation = getDriver().findElement(By.xpath("//span[contains(.,'" + searchText + "')]"));
        String classId = annotation.getAttribute("class");
        // System.out.println("ClassID = " + classId.toString());
        classId = classId.split("\\s+")[1];
        // System.out.println("ClassID AFTER SPLIT = " + classId.toString());
        return classId;
    }

    /**
     * @param searchText annotated text
     * @return annotation content
     */
    public String getAnnotationContentByText(String searchText)
    {

        String classId = this.getAnnotationIdByText(searchText);
        // System.out.println("ID=" + classId);
        getDriver().findElement(By.xpath("//span[@id='" + classId + "']")).click();
        waitUntilElementIsVisible(By.xpath("//div[@class='annotationText']/p"));
        String annotationContent = getDriver().findElement(By.xpath("//div[@class='annotationText']/p")).getText();
        RenderedWebElement body = (RenderedWebElement) getDriver().findElement(By.id("body"));
        body.sendKeys(Keys.ESCAPE);
        waitUntilElementDisappears(By.className("annotation-box-view"));
        return annotationContent;

    }
}
