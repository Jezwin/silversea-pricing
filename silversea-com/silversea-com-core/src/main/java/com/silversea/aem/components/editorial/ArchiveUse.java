package com.silversea.aem.components.editorial;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.foundation.Navigation;

public class ArchiveUse extends WCMUsePojo {
    public Navigation archives;

    @Override
    public void activate() throws Exception {
        String archivePath = getProperties().get("archivePath", String.class);

        if (StringUtils.isNotBlank(archivePath)) {
            archives = new Navigation(getCurrentPage(), PathUtils.getDepth(archivePath) - 1, new PageFilter(), 2);
        }
    }

    /**
     * @return the archives
     */
    public Navigation getArchives() {
        return archives;
    }
}