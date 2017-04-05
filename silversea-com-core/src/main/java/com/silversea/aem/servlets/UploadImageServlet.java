package com.silversea.aem.servlets;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;

//@SlingServlet(paths = ServletConstant.PATH_UPLOAD_IMAGE_SERVLET, methods = "POST", metatype = true)
//@Property(name = "sling.auth.requirements", value = ServletConstant.PATH_UPLOAD_IMAGE_SERVLET)
public class UploadImageServlet extends SlingAllMethodsServlet {

//    /**
//     * 
//     */
//    private static final long serialVersionUID = 5367812045831684562L;
//
//    static final private Logger LOGGER = LoggerFactory.getLogger(UploadImageServlet.class);
//
//    private Session session;
//
//    private String clientLibPath = "";
//
//    @Reference
//    private ResourceResolverFactory resolverFactory;
//
//    @Override
//    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            final boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
//            PrintWriter out = null;
//            int index = 1;
//
//            out = response.getWriter();
//            if (isMultiPart) {
//                final Map<String, RequestParameter[]> params = request.getRequestParameterMap();
//                for (final Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
//                    final String key = pairs.getKey();
//                    RequestParameter[] paraArr = pairs.getValue();
//
//                    RequestParameter param0 = paraArr[0];
//                    // Determine if the posted value is a file or JCR Path
//                    boolean formField = param0.isFormField();
//                    if (formField) {
//                        String libLoc = param0.getString();
//                        clientLibPath = libLoc; // Set the class member - its
//                                                // the first posted value from
//                                                // the client
//                        LOGGER.debug("Field value is {} ", libLoc);
//                    } else {
//                        RequestParameter param1 = paraArr[1];
//                        final InputStream stream = param0.getInputStream();
//                        String mimeType = param0.getContentType();
//                        LOGGER.debug("The content type is : " + mimeType);
//
//                        // Save to Adobe CQ Dam
//                        writeToClientLib(stream,param0.getFileName(),clientLibPath,mimeType  ); 
//                    }
//                    index++;
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error("Upload Image Servlet Error : {} ", e);
//        }
//    }
//
//    private String writeToClientLib(InputStream is, String fileName, String path, String mimetype) {
//        try {
//            // Invoke the session
//            ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
//            session = resourceResolver.adaptTo(Session.class);
//            Node node = session.getNode(path); // Get the client lib node
//
//            ValueFactory valueFactory = session.getValueFactory();
//            Binary contentValue = valueFactory.createBinary(is);
//
//            Node fileNode = node.addNode(fileName, "nt:file");
//            fileNode.addMixin("mix:referenceable");
//            Node resNode = fileNode.addNode("jcr:content", "nt:resource");
//
//            resNode.setProperty("jcr:mimeType", mimetype);
//            resNode.setProperty("jcr:data", contentValue);
//            Calendar lastModified = Calendar.getInstance();
//            lastModified.setTimeInMillis(lastModified.getTimeInMillis());
//            resNode.setProperty("jcr:lastModified", lastModified);
//            session.save();
//            session.logout();
//
//            // Return the path to the document that was stored in CRX.
//            return fileNode.getPath();
//
//        } catch (Exception e) {
//            LOGGER.error("Error on write file : {} ", e);
//        }
//        return null;
//    }

}
