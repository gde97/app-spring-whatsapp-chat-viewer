package mia.appspring.storage;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

@Service
public interface IStorageService {

    void store(Path destination, MultipartFile file);

    void move(Path sourcePath, Path destinationPath);

    Resource loadAsResource(Path parent, String filename);

    void createUserDir(Long idUser);

    String createDirDateTime(Path parent);

    Path getFileStorage();

    Path getUploadStorage();

    Path getDownloadStorage();

    Path getDBStorage();
}
