package org.nuxeo.sample;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentSecurityException;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.filemanager.service.extension.AbstractFolderImporter;
import org.nuxeo.ecm.platform.filemanager.utils.FileManagerUtils;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.runtime.api.Framework;

public class CustomFolderImporter extends AbstractFolderImporter {

    private static final Logger log = LogManager.getLogger(CustomFolderImporter.class);

    @Override
    public DocumentModel create(CoreSession documentManager, String fullname, String path, boolean overwrite, TypeManager typeManager) {
        log.debug("Hi from custom folder mporter");

        String title = FileManagerUtils.fetchFileName(fullname);
        if (overwrite) {
            DocumentModel docModel = FileManagerUtils.getExistingDocByTitle(documentManager, path, title);
            if (docModel != null) {
                return docModel;
            }
        }
        PathRef containerRef = new PathRef(path);
        if (!documentManager.hasPermission(containerRef, SecurityConstants.READ_PROPERTIES)
                || !documentManager.hasPermission(containerRef, SecurityConstants.ADD_CHILDREN)) {
            throw new DocumentSecurityException("Not enough rights to create folder");
                }
        PathSegmentService pss = Framework.getService(PathSegmentService.class);
        // We can add some logic depending on the parent to compute the needed subtype
        String type = "CustomFolder";
        DocumentModel docModel = documentManager.createDocumentModel(type);
        docModel.setProperty("dublincore", "title", title);
        docModel.setPathInfo(path, pss.generatePathSegment(docModel));
        docModel = documentManager.createDocument(docModel);
        documentManager.save();
        return docModel;

    }

}
