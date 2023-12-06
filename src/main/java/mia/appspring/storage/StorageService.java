package mia.appspring.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class StorageService implements IStorageService{

    /*
    https://www.baeldung.com/java-path-vs-file
    https://www.baeldung.com/java-file-directory-exists
     */

    private final Path fileStorage;
    private final Path uploadStorage;
    private final Path downloadStorage;
    private final Path DBStorage;

    @Autowired
    public StorageService(StorageProperties properties){
        Path baseDir = Paths.get(System.getProperty("user.dir"));
        fileStorage = baseDir.resolve(properties.getBaseLocation());
        uploadStorage = fileStorage.resolve(properties.getBaseLocationUpload());
        downloadStorage = fileStorage.resolve(properties.getBaseLocationDownload());
        DBStorage = fileStorage.resolve(properties.getBaseLocationDB());
        if(!Files.exists(fileStorage) && !Files.isReadable(fileStorage)) {
            try {
                Files.createDirectory(fileStorage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(!Files.exists(uploadStorage) && !Files.isReadable(uploadStorage)) {
            try {
                Files.createDirectory(uploadStorage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(!Files.exists(downloadStorage) && !Files.isReadable(downloadStorage)) {
            try {
                Files.createDirectory(downloadStorage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(!Files.exists(DBStorage) && !Files.isReadable(DBStorage)) {
            try {
                Files.createDirectory(DBStorage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void store(Path destination, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                // TODO: creare un errore
            }

            Path destinationFile = destination.resolve(file.getOriginalFilename());
            /*
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
             */
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            //throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public void move(Path sourcePath, Path destinationPath){
        try {
            Files.move(sourcePath, destinationPath);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource loadAsResource(Path parent, String filename){
        try{
            Path file = parent.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists()){
                return resource;
            }else{
                throw new RuntimeException("Could not read file: " + filename);
            }
        }catch (MalformedURLException e){
            throw new RuntimeException(e);
            //throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void createUserDir(Long idUser){
        Path newUserDir = DBStorage.resolve(idUser.toString());
        if(!Files.exists(newUserDir)){
            try{
                Files.createDirectory(newUserDir);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String createDirDateTime(Path parent){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
        String namedir = now.format(formatter);
        Path newdir = parent.resolve(namedir);
        if(!Files.exists(newdir)){
            try{
                Files.createDirectory(newdir);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        return namedir;
    }

    @Override
    public Path getFileStorage() {
        return fileStorage;
    }
    @Override
    public Path getUploadStorage() {
        return uploadStorage;
    }
    @Override
    public Path getDownloadStorage() {
        return downloadStorage;
    }
    @Override
    public Path getDBStorage() {
        return DBStorage;
    }
}
