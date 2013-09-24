package org.xwiki.enterprise.migrator.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.extension.CoreExtension;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.distribution.internal.DistributionScriptService;
import org.xwiki.extension.repository.CoreExtensionRepository;
import org.xwiki.extension.repository.internal.core.MavenCoreExtension;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
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

    @Inject
    private DocumentReferenceResolver<String> documentReferenceResolver;

    /** Logging tool. */
    @Inject
    private static Logger logger;

    @Override
    public ExtensionId getUIExtensionId(String wiki)
    {
        XWikiContext xcontext = this.xcontextProvider.get();

        // If it is the main wiki, return the main UI.
        if (xcontext.isMainWiki(wiki))
        {
            return this.distributionManager.getMainUIExtensionId();
        }

        try {
            // Get the wiki document
            DocumentReference wikiDocumentRef = documentReferenceResolver.resolve(
                    xcontext.getMainXWiki()+":"+XWiki.getServerWikiPage(wiki));

            XWikiDocument wikiDocument = xcontext.getWiki().getDocument(wikiDocumentRef, xcontext);

            // Let see if the wiki document has an Workspace object
            DocumentReference workspaceClassRef = new DocumentReference(xcontext.getMainXWiki(),
                    "WorkspaceManager", "WorkspaceClass");

            // If there is an object, then it's a "workspace"
            if (wikiDocument.getXObject(workspaceClassRef) != null)
            {
                CoreExtension distributionExtension = this.coreExtensionRepository.getEnvironmentExtension();
                // Get the maven model
                Model mavenModel = (Model) distributionExtension.getProperty(MavenCoreExtension.PKEY_MAVEN_MODEL);
                // Get the UI Id
                String wikiUIId = mavenModel.getProperties().getProperty("xwiki.extension.distribution.workspaceui");

                return new ExtensionId(wikiUIId, distributionExtension.getId().getVersion());
            }

        } catch (XWikiException e) {
            logger.error("Failed to get wiki descriptor for wiki [{}]", wiki, e);
        }

        // Other case, it is a "normal" subwiki
        return this.distributionManager.getWikiUIExtensionId();
    }
}