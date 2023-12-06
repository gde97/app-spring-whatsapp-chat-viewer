package mia.appspring.controller.api;

import mia.appspring.service.IImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping("/api/upload")
public class ImportController {

    /*
    https://www.bezkoder.com/spring-boot-upload-multiple-files/
     */

    private final IImportService importService;

    @Autowired
    public ImportController(IImportService importService){
        this.importService = importService;
    }

    /**
     * allow upload chats in format zip
     * @param files all file uploaded
     * @param idUser user to which will be associated the chats, it is always a valid user
     * @return result upload
     */
    // /zips?user=1
    // body params: file
    @PostMapping(value = "/zips", params = {"user"})
    public ResponseEntity<String> handleFilesZips(@RequestParam("user") Long idUser,
                                                  @RequestParam("file") MultipartFile[] files){
        //check if all file uploaded are zip
        if (importService.handleFilesZips(files, idUser)) {
            //if all file are zip
            return ResponseEntity.ok().build();
        }
        //at least one file is not zip
        return ResponseEntity.badRequest().build();
    }

    /**
     * allow upload a chat form a folder with is attachments
     * @param files all file uploaded
     * @param idUser user to which will be associated the chats, it is always a valid user
     * @return result upload
     */
    // /folder?user=1
    @PostMapping(value = "/folder", params = {"user"})
    public ResponseEntity handleFilesFolder(@RequestParam("user") Long idUser,
                                            @RequestParam("namechat") String nameChat,
                                            @RequestParam("file") MultipartFile[] files){
        //check if there is a chat file
        if (importService.handleFilesFolder(files,nameChat, idUser)){
            //there is a chat file
            return ResponseEntity.ok().build();
        }
        //no chat file
        return ResponseEntity.badRequest().build();
    }

}
