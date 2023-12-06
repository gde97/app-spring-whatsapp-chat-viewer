package mia.appspring.controller.api;

import mia.appspring.service.IExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping("/api/download")
public class ExportController {

    /*
    https://stackoverflow.com/questions/51519160/spring-download-file-from-rest-controller
    https://stackoverflow.com/questions/16601428/how-to-set-content-disposition-and-filename-when-using-filesystemresource-to
     */

    private final IExportService exportService;

    @Autowired
    public ExportController(IExportService exportService){
        this.exportService = exportService;
    }

    /**
     * to retrieve an attachment
     * @param idUser user associated to chat
     * @param idChat chat associated to attachment
     * @param nameFile name of attachment in memory
     * @return the file requested as resource
     */
    // ?user=1&chat=1&file=nome.estenzione
    @GetMapping(params = {"user", "chat", "file"})
    public ResponseEntity<Resource> getAttachment(@RequestParam("user") String idUser,
                                        @RequestParam("chat") String idChat,
                                        @RequestParam("file") String nameFile){
        String pathAndFile = idUser +"\\" + idChat + "\\" + nameFile;
        Resource file = exportService.getAttachment(pathAndFile);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    /**
     * to get all files saved on DB folder of a chat
     * @param idUser user associated to chat
     * @param idChat number chat to download
     * @return the entire folder as a zip
     */
    // /zip?user=1&chat=1
    @GetMapping(value = "/zip", params = {"user", "chat"})
    public ResponseEntity<Resource> getZip(@RequestParam("user") String idUser,
                                                  @RequestParam("chat") String idChat){
        String pathDir = idUser +"\\" + idChat;
        Resource file = exportService.getChatZip(pathDir);

        if (file == null)
            return ResponseEntity.notFound().build();

        /*
        MODO BRUTTO PER ELIMINARE, DA PENSARE COME FARE
        new Thread(){
            @Override
            public void run(){
                try {
                    file
                    Thread.sleep(2000);
                    Files.delete(file.getFile().toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

         */

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}
