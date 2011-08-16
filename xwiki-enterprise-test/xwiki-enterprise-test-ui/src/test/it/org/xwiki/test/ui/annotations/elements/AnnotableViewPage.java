package org.xwiki.test.ui.annotations.elements;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.xwiki.test.ui.framework.elements.ViewPage;

public class AnnotableViewPage extends ViewPage
{

    private static final String XWIKI_ANNOTATION_ADD_SUCCESS = "Annotation has been successfully added";

    private static final String XWIKI_ANNOTATION_DELETE_SUCCESS = "Annotation deleted";

    private static final String XWIKI_SYNTAX_1_WARNING =
        "Annotations are not available for documents in XWiki/1.0 syntax.";

    private AnnotationsPane annotationsPane;

    private AnnotationsWindow annotationsWindow;

    private AnnotationsLabel annotationsLabel;

    private WebElement save;

    private StringBuilder script;

    public AnnotableViewPage()
    {

        /**
         * the constructor + injection of the js function that selects the text from the page (drag-over with mouse
         * simulation)
         */
        script = new StringBuilder();
        script.append("function findString (str) {\n");
        script.append("  var strFound;\n");
        script.append("  if (window.find) {\n");
        script.append("    if (parseInt(navigator.appVersion)<4) return;\n");
        script.append("    // CODE FOR BROWSERS THAT SUPPORT window.find\n");
        script.append("    strFound = self.find(str);\n");
        script.append("    if (strFound && self.getSelection && !self.getSelection().anchorNode) {\n");
        script.append("      strFound = self.find(str);\n");
        script.append("    }\n");
        script.append("    if (!strFound) {\n");
        script.append("      strFound = self.find(str,0,1);\n");
        script.append("      while (self.find(str,0,1)) continue;\n");
        script.append("    }\n");
        script.append("  } else if (navigator.appName.indexOf(\"Microsoft\")!=-1) {\n");
        script.append("    // EXPLORER-SPECIFIC CODE\n");
        script.append("    if (TRange != null) {\n");
        script.append("      TRange.collapse(false);\n");
        script.append("      strFound = TRange.findText(str);\n");
        script.append("      if (strFound) TRange.select();\n");
        script.append("    }\n");
        script.append("    if (TRange == null || strFound == 0) {\n");
        script.append("      TRange = self.document.body.createTextRange();\n");
        script.append("      strFound = TRange.findText(str);\n");
        script.append("      if (strFound) TRange.select();\n");
        script.append("    }\n");
        script.append("  } else if (navigator.appName == \"Opera\") {\n");
        script.append("    alert ('Opera browsers not supported, sorry...');\n");
        script.append("    return;\n");
        script.append("  }\n");
        script.append("  if (!strFound) \n");
        script.append("    return;\n");
        script.append("}\n");
        getDriver().executeScript(script.toString());

        annotationsPane = new AnnotationsPane();
        annotationsWindow = new AnnotationsWindow();
        annotationsLabel = new AnnotationsLabel();

    }

    public void addAnnotation(String annotatedText, String annotationText)
    {
        selectText(annotatedText);
        simulateCTRL_M();
        annotationsWindow.addAnnotation(annotationText);
        // waiting for the notification that the operation is done
        waitUntilElementIsVisible(By.className("xnotification-done"));
        // check is the saved successfully message is displayed
        save = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(XWIKI_ANNOTATION_ADD_SUCCESS, save.getText());
        save.click();
        waitUntilElementDisappears(By.className("xnotification-done"));
        Assert.assertEquals(annotationText, annotationsLabel.getAnnotationContentByText(annotatedText));
    }

    public void deleteAnnotationByID(String id)
    {
        annotationsLabel.deleteAnnotationById(id);
        waitUntilElementIsVisible(By.className("xnotification-done"));
        WebElement delete = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(XWIKI_ANNOTATION_DELETE_SUCCESS, delete.getText());
        delete.click();
    }

    public void deleteAnnotationByText(String annotatedText)
    {
        deleteAnnotationByID(this.annotationsLabel.getAnnotationIdByText(annotatedText));
    }

    // Shows the annotations pane from the top of the page
    public void showAnnotationsPane()
    {
        annotationsPane.showAnnotationsPane();
    }

    // Hides the annotations pane from the top of the page
    public void hideAnnotationsPane()
    {
        annotationsPane.hideAnnotationsPane();
    }

    // Checks the "Show Annotations" check box.
    public void clickShowAnnotations()
    {
        annotationsPane.clickShowAnnotations();
    }

    // Un-checks the "Show Annotations" check box.
    public void clickHideAnnotations()
    {
        annotationsPane.clickHideAnnotations();
    }

    // Checks if the checkBox within AnnotationsPane is visible
    public boolean checkIfClickbuttonExists()
    {
        return annotationsPane.checkIfClickbuttonExists();
    }

    public void simulateCTRL_M()
    {
        WebElement body = getDriver().findElement(By.id("body"));
        body.sendKeys(Keys.CONTROL, "m");
    }

    /**
     * @param annotationWord string that will be selected on the screen
     */
    public void selectText(String annotationWord)
    {
        getDriver().executeScript(script + "findString('" + annotationWord + "');");
    }

    public void checkIfAnnotationsAreDisabled()
    {
        Assert.assertEquals(0, getUtil().findElementsWithoutWaiting(getDriver(), By.id("annotationsdisplay")).size());
    }

    // Check if the bottom notifications warning appears that you are not allowed to annotate 1.0 syntax pages
    public void waitforAnnotationWarningNotification()
    {
        waitForNotificationWarningMessage(XWIKI_SYNTAX_1_WARNING);
    }
}
