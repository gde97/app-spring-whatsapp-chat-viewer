package mia.appspring.service;

import mia.appspring.storage.IStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class ExportService implements IExportService{

    private final IStorageService storageService;
    private final ZipManagement zipManagement;

    @Autowired
    public ExportService(IStorageService storageService,
                         ZipManagement zipManagement){
        this.storageService = storageService;
        this.zipManagement = zipManagement;
    }

    @Override
    public Resource getAttachment(String pathFile){
        return storageService.loadAsResource(storageService.getDBStorage(), pathFile);
    }

    @Override
    public Resource getChatZip(String pathDir){
        File pathChat = new File(storageService.getDBStorage().toString(), pathDir);
        //file in temp-download/ not in sub dir
        zipManagement.zipDirectory(pathChat, storageService.getDownloadStorage().toString());
        //get the file in temp-download/
        return storageService.loadAsResource(storageService.getDownloadStorage(),
                pathChat.getName() + zipManagement.FILE_EXTENSION);
    }


}
