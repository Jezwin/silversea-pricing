package com.silversea.aem.importers.services.impl;

import com.day.cq.replication.Replicator;
import com.silversea.aem.importers.services.ReplicateImporter;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;

import static com.silversea.aem.importers.polling.ApiUpdater.replicateModifications;


@Service
@Component
public class ReplicateImporterImpl implements ReplicateImporter {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private Replicator replicator;

    @Override
    public void replicate() {
        replicateModifications(resourceResolverFactory, replicator,
                "/jcr:root/content/dam/silversea-com//element(*,dam:AssetContent)[toDeactivate or toActivate]");
        replicateModifications(resourceResolverFactory, replicator,
                "/jcr:root/content/silversea-com/en//element(*,cq:PageContent)[toDeactivate or toActivate]");
        replicateModifications(resourceResolverFactory, replicator,
                "/jcr:root/content/silversea-com/de//element(*,cq:PageContent)[toDeactivate or toActivate]");
        replicateModifications(resourceResolverFactory, replicator,
                "/jcr:root/content/silversea-com/es//element(*,cq:PageContent)[toDeactivate or toActivate]");
        replicateModifications(resourceResolverFactory, replicator,
                "/jcr:root/content/silversea-com/pt-br//element(*,cq:PageContent)[toDeactivate or toActivate]");
        replicateModifications(resourceResolverFactory, replicator,
                "/jcr:root/content/silversea-com/fr//element(*,cq:PageContent)[toDeactivate or toActivate]");

    }
}
