package org.xwiki.test.po.extension.server.editor;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.xwiki.test.po.platform.InlinePage;

public class ExtensionInlinePage extends InlinePage
{
    @FindBy(id = "ExtensionCode.ExtensionClass_0_id")
    private WebElement id;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_name")
    private WebElement name;

    @FindBy(id = "title")
    private WebElement title;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_type")
    private WebElement type;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_summary")
    private WebElement summary;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_authors")
    private WebElement authors;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_licenseName")
    private WebElement licenseName;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_source")
    private WebElement source;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_icon")
    private WebElement icon;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_description")
    private WebElement description;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_customInstallationOnly")
    private WebElement customInstallationOnly;

    @FindBy(id = "ExtensionCode.ExtensionClass_0_installation")
    private WebElement installation;

    public void setId(String id)
    {
        this.id.clear();
        this.id.sendKeys(id);
    }

    public void setName(String name)
    {
        this.name.clear();
        this.name.sendKeys(name);
    }

    public void setTitle(String title)
    {
        this.title.clear();
        this.title.sendKeys(title);
    }

    public void setType(String type)
    {
        Select select = new Select(this.type);
        select.selectByValue(type);
    }

    public void setSummary(String summary)
    {
        this.summary.clear();
        this.summary.sendKeys(summary);
    }

    public void setAuthors(String author)
    {
        this.authors.clear();
        this.authors.sendKeys(author);
    }

    public void setLicenseName(String licenseName)
    {
        Select select = new Select(this.licenseName);
        select.selectByValue(licenseName);
    }

    public void setSource(String source)
    {
        this.source.clear();
        this.source.sendKeys(source);
    }

    public void setIcon(String icon)
    {
        this.icon.clear();
        this.icon.sendKeys(icon);
    }

    public void setDescription(String description)
    {
        this.description.clear();
        this.description.sendKeys(description);
    }

    public void setCustomInstallationOnly(boolean customInstallationOnly)
    {
        Select select = new Select(this.customInstallationOnly);
        select.selectByValue(customInstallationOnly ? "1" : "0");
    }

    public void setInstallation(String installation)
    {
        this.installation.clear();
        this.installation.sendKeys(installation);
    }

}
