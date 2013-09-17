package org.xwiki.enterprise.migrator.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.xwiki.component.annotation.Component;
import org.xwiki.extension.CoreExtension;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.distribution.internal.DistributionScriptService;
import org.xwiki.extension.repository.CoreExtensionRepository;
import org.xwiki.extension.repository.internal.core.MavenCoreExtension;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;

@Component
@Named("distribution")
@Singleton
public class Migrator extends DistributionScriptService
{
    /**
     * The repository with core modules provided by the platform.
     */
    @Inject
    private CoreExtensionRepository coreExtensionRepository;

    /**
     * @return the recommended user interface for {@link #getDistributionExtension()}
     */
    @Override
    public ExtensionId getUIExtensionId()
    {
        XWikiContext xcontext = this.xcontextProvider.get();

        // If it is the main wiki, return the main UI.
        if (xcontext.isMainWiki()) {
            return this.distributionManager.getMainUIExtensionId();
        }

        // Get the wiki document
        XWikiDocument wikiDocument = xcontext.getWikiServer();
        // Let see if the wiki document has an Workspace object
        DocumentReference workspaceClassRef = new DocumentReference(xcontext.getMainXWiki(),
                "WorkspaceManager", "WorkspaceClass");

        // If there is an object, then it's a "workspace"
        if (wikiDocument.getXObject(workspaceClassRef) != null) {
            return this.distributionManager.getWikiUIExtensionId();
        }

        // Other case, it is a "normal" subwiki
        CoreExtension distributionExtension = this.coreExtensionRepository.getEnvironmentExtension();
        // Get the maven model
        Model mavenModel = (Model) distributionExtension.getProperty(MavenCoreExtension.PKEY_MAVEN_MODEL);
        // Get the UI Id
        String wikiUIId = mavenModel.getProperties().getProperty("xwiki.extension.distribution.oldwikiui");

        return new ExtensionId(wikiUIId, distributionExtension.getId().getVersion());
    }
}