package com.silversea.aem.importers.services.impl;

import com.silversea.aem.importers.ImporterException;
import com.silversea.aem.importers.ImportersConstants;
import com.silversea.aem.importers.services.PortsImporter;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Component
public class PortsImporterImpl implements PortsImporter {

    @Reference
    private ResourceResolverFactory resolverFactory;
    private Logger logger = LoggerFactory.getLogger(PortsImporterImpl.class);
    private static String PATH_PORTS = "/etc/tags/ports";
    private static String PATH_PORTS_CONTENT = "/content/silversea-com/#LOCAL/other-resources/find-a-port";
    private static String PORTS_DATA = "portsCSVData";
    private static String SEPARATOR = "=";
    private static String[] LOCALIZATIONS = new String[]{"en", "fr", "de", "es", "pt-br"};
    private int successNumber = 0;
    private int errorNumber = 0;

    @Override
    public ImportResult importAllItems() {
        Map<String, Object> authenticationParams = new HashMap<>();
        authenticationParams.put(ResourceResolverFactory.SUBSERVICE, ImportersConstants.SUB_SERVICE_IMPORT_DATA);

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(authenticationParams)) {
            Session session = resolver.adaptTo(Session.class);
            Resource pageResource = resolver.getResource(PATH_PORTS);
            if (pageResource == null) {
                throw new ImporterException("Cannot find pageResource");
            }

            Node tagNode = pageResource.adaptTo(Node.class);

            if (tagNode == null) {
                throw new ImporterException("Cannot find node");
            }

            Property portsCsvProp = tagNode.getProperty(PORTS_DATA);
            String csvContent = portsCsvProp.getValue().getString();
            Iterable<Port> ports = parseCsv(csvContent);
            updateTitles(resolver, ports);
            if (session.hasPendingChanges()) {
                try {
                    session.save();
                    logger.info("Insert {} in ports and {} no insert", successNumber, errorNumber);
                } catch (RepositoryException e) {
                    session.refresh(false);
                }
            } else {
                logger.info("No updating Ports");
            }

        } catch (LoginException | ImporterException | RepositoryException e) {
            logger.error("Error during ports updating", e);

        }
        return new ImportResult(successNumber, errorNumber);
    }

    private Iterable<Port> parseCsv(String csvContent) {
        return Stream.of(csvContent.split("\n"))
                .filter(line -> {
                    if (!line.contains(SEPARATOR)) {
                        logger.warn("Line {} does not contain SEPARATOR {}", line, SEPARATOR);
                        errorNumber++;
                    }
                    return line.contains(SEPARATOR);
                })
                .map(line -> line.split(SEPARATOR))
                .filter(line -> {
                    if (line.length != 2) {
                        logger.warn("Line starting with {} does not have enough entries", line[0]);
                        errorNumber++;
                    }
                    return line.length == 2;
                })
                .map(line -> new Port(line[0].trim(), line[1].trim()))
                .collect(Collectors.toList());
    }

    private void updateTitles(ResourceResolver resolver, Iterable<Port> ports) throws RepositoryException {
        for (String local : LOCALIZATIONS) {
            String path = PATH_PORTS_CONTENT.replaceAll("#LOCAL", local);
            for (Port port : ports) {
                String portPath = getPath(path, port);
                Resource portResource = resolver.getResource(portPath);
                if (portResource != null) {
                    Node node = portResource.adaptTo(Node.class);
                    if (node != null) {
                        node.getProperty("jcr:title").setValue(port.getTitle());
                        successNumber++;
                    } else {
                        logger.warn("Error during updating of {} with value ", portPath, port.getTitle());
                        errorNumber++;
                    }
                } else {
                    logger.warn("Could not find {}", portPath);
                    errorNumber++;
                }

            }
        }
    }

    private String getPath(String prefix, Port port) {
        return prefix + "/" + port.getCode().charAt(0) + "/" + port.getCode() + "/jcr:content";
    }

    private class Port {
        private final String code;
        private final String title;

        private Port(String code, String title) {
            this.code = code;
            this.title = title;
        }

        public String getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }
    }
}
