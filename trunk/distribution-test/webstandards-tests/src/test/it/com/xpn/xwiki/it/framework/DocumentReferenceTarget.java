package com.xpn.xwiki.it.framework;

import org.xwiki.model.reference.DocumentReference;

public class DocumentReferenceTarget implements Target
{
    private DocumentReference documentReference;

    public DocumentReferenceTarget(DocumentReference documentReference)
    {
        this.documentReference = documentReference;
    }

    public DocumentReference getDocumentReference()
    {
        return documentReference;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.it.framework.Target#getName()
     */
    public String getName()
    {
        return "space=" + this.documentReference.getLastSpaceReference().getName() + ", page="
            + this.documentReference.getName();
    }
}
