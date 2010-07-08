package com.xpn.xwiki.it.framework;

public class URLPathTarget implements Target
{
    private String urlPath;

    public URLPathTarget(String urlPath)
    {
        this.urlPath = urlPath;
    }

    public String getUrlPath()
    {
        return urlPath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.it.framework.Target#getName()
     */
    public String getName()
    {
        return getUrlPath();
    }
}
