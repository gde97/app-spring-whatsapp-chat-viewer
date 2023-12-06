package mia.appspring.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Service
public interface IImportService {


    boolean handleFilesZips(MultipartFile[] files, Long idUser);

    boolean handleFilesFolder(MultipartFile[] files,String nameChat, Long idUser);
}
