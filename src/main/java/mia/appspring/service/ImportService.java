package mia.appspring.service;

import mia.appspring.model.Chat;
import mia.appspring.model.Message;
import mia.appspring.model.User;
import mia.appspring.storage.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;


@Service
public class ImportService implements IImportService {

    private final IStorageService storage;
    private final ParseTxtFile parseTxtFile;
    private final ZipManagement zipManagement;
    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;

    private final String ZIP_EXTENSION = ".zip";

    private final String CHAT_EXTENSION = ".txt";
    private final String NAME_CHAT_ZIP = "_chat";
    private final String NAME_CHAT_FOLDER_ZIP = "WhatsApp Chat - ";
    private final String NAME_CHAT_FOLDER = "Chat WhatsApp con ";

    @Autowired
    public ImportService(IStorageService storageService,
                         ParseTxtFile parseTxtFile,
                         ZipManagement zipManagement,
                         UserService userService,
                         ChatService chatService,
                         MessageService messageService) {
        this.storage = storageService;
        this.parseTxtFile = parseTxtFile;
        this.zipManagement = zipManagement;
        this.userService = userService;
        this.chatService = chatService;
        this.messageService = messageService;
    }

    /**
     * store the zip file uploaded
     * @param files list of zip
     * @param idUser user to be associated
     * @return if all files are zip
     * //todo: il caricamento deve essere separato dalla elaborazione
     */
    @Override
    public boolean handleFilesZips(MultipartFile[] files, Long idUser){
        boolean allZip = true;
        for (MultipartFile file: files){
            if (!file.getOriginalFilename().contains(ZIP_EXTENSION)){
                allZip = false;
            }
        }
        if (allZip) {
            //if all file are zip
            ArrayList<String> listFile = new ArrayList<String>();
            String newImportFolder = newImportFolder();
            for (MultipartFile file : files) {
                listFile.add(file.getOriginalFilename());
                store(newImportFolder, file);
            }
            importListZip(newImportFolder, listFile, idUser);
            return true;
        }
        return false;
    }

    /**
     * store the file uploaded
     * @param files list of file
     * @param nameChat name of chat
     * @param idUser user to be associated
     * @return if there is a chat
     */
    @Override
    public boolean handleFilesFolder(MultipartFile[] files,String nameChat, Long idUser){
        boolean chatTxt = false;
        for (MultipartFile file: files){
            if (file.getOriginalFilename().contains(CHAT_EXTENSION) && isAChatFile(file.getOriginalFilename())){
                chatTxt = true;
            }
        }
        if (chatTxt) {
            //there is a chat file
            String newImportFolder = newImportFolder();
            for (MultipartFile file : files) {
                store(newImportFolder, file);
            }
            importADirectory(newImportFolder, nameChat, idUser);
            return true;
        }
        return false;
    }

    /**
     * create a folder in upload based on time
     * @return the name of folder
     */
    private String newImportFolder(){
        return storage.createDirDateTime(storage.getUploadStorage());
    }

    /**
     * Save a file in importfolder in upload dir before beginning importation of uploads
     * @param importFolder folder in to save
     * @param file file to store
     */
    private void store(String importFolder, MultipartFile file){
        storage.store(storage.getUploadStorage().resolve(importFolder), file);
    }

    /**
     * After saving zip file in upload dir
     * Process all zip file uploaded
     * @param importFolder name of folder import
     * @param listUpload all file uploaded
     * @param idUser identifier user to associate
     */
    @Async
    protected void importListZip(String importFolder, ArrayList<String> listUpload, Long idUser){
        /*
        ASYNC FUNZIONANTE, SI PUò IMPLEMENTARE IL SISTEMA DI NOTIFICHE
        try{
            Thread.sleep(10000);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
         */
        ArrayList<File> directories = new ArrayList<>();
        unZipAllDirectory(importFolder, listUpload, directories);
        scanAllDirectories(true, "", directories, idUser);
        File source = new File(storage.getUploadStorage().resolve(importFolder).toString());
        source.delete();
    }

    /**
     * After saving files in upload dir
     * Process all files uploaded
     * @param importFolder name of folder import
     * @param idUser identifier user to associate
     */
    @Async
    protected void importADirectory(String importFolder, String nameChat, Long idUser){
        /*
        ASYNC FUNZIONANTE, SI PUò IMPLEMENTARE IL SISTEMA DI NOTIFICHE
        */
        ArrayList<File> aDirectory = new ArrayList<>();
        aDirectory.add(new File(storage.getUploadStorage().resolve(importFolder).toString()));
        scanAllDirectories(false, nameChat, aDirectory, idUser);
        File source = new File(storage.getUploadStorage().resolve(importFolder).toString());
        source.delete();
    }

    /**
     * all file uploaded as zip will be unzipped, delete the unzip
     * create a link to the unzipped folder
     * @param listUpload list of all file in uploadfolder ad a zip
     * @param directories list of all directory unzipped
     */
    private void unZipAllDirectory(String importFolder,ArrayList<String> listUpload, ArrayList<File> directories){
        for (String element: listUpload) {
            /*
            WhatsApp Chat - name.zip
             */
            //TODO: da generalizzare se non c'è questa stringa
            String[] newName = element.split(zipManagement.FILE_EXTENSION);

            File source = new File(storage.getUploadStorage().resolve(importFolder).toString(), element);
            zipManagement.unZip(source, newName[0]);
            directories.add(new File(storage.getUploadStorage().resolve(importFolder).toString(), newName[0]));
            //delete zip file
            source.delete();
        }
    }

    /**
     * scan all directory passed
     * for each directory create a chat, add all messages, verify attachments
     * @param folderIsNameChat to choose the final name of the chat between folder or chat.txt
     * @param directories list of all directory to be scanned
     * @param idUser user associated
     */
    private void scanAllDirectories(Boolean folderIsNameChat, String givenNameChat, ArrayList<File> directories, Long idUser){
        /*
        all zip to folder, now scan every folder
        */

        for (File directory : directories){

            ArrayList<Message> newListMessages = new ArrayList<>();
            ArrayList<String> attachments = new ArrayList<>();
            ArrayList<String> senders = new ArrayList<>();
            String nameChat;
            List<String> namesOwner = new ArrayList<String>();
            nameChat = scanAllFileInDirectory(directory, newListMessages, attachments, senders);

            existParsedAttachments(newListMessages, attachments);

            if(folderIsNameChat){
                nameChat = directory.getName();
                nameChat = nameChat.replace(NAME_CHAT_FOLDER_ZIP, "");
            }else{
                /*
                nameChat = nameChat.replace(NAME_CHAT_FOLDER, "");
                nameChat = nameChat.replace(CHAT_EXTENSION, "");
                 */
                nameChat = givenNameChat;
            }
            if (senders.size() < 4){
                senders.remove(nameChat);
                senders.remove("Whatsapp");
                namesOwner = senders;
            }else{
                namesOwner.add("") ;
            }

            User workingUser = userService.getUser(idUser);
            Optional<Chat> chatExist = chatService.getChatByUserName(workingUser, nameChat);
            if (!chatExist.isEmpty()) {
                break;//TODO: mettere un controllo se esiste già
            }
            Chat newChat = chatService.newChat(workingUser, nameChat);
            newChat.setNameowner(namesOwner);
            if (!newListMessages.isEmpty()) {
                newChat = chatService.saveChat(newChat);
                userService.addChat(workingUser.getId(), newChat);
                messageService.saveAllMessage(newListMessages);
                chatService.addAllMessage(newChat.getIdchat(), newListMessages);

                //TODO vedere cosa fare con i file presenti
                saveToUserFolder(directory.toPath(), idUser, newChat.getIdchat());
            }
        }
    }

    /**
     * scan all file in a directory
     * search for the file of messages to be parsed
     * @param directory in which there are all files
     * @param attachments list will be populated of attachment
     * @return the list of messages parsed from chat file
     */
    private String scanAllFileInDirectory(File directory, ArrayList<Message> newListMessages, ArrayList<String> attachments, ArrayList<String> senders){
        /*
        in a directory there is only 1 file of chat
        */
        String chatName = "";
        for (File file : directory.listFiles()){
            try {
                if ( file.isFile() && file.getName().endsWith(".txt") && isAChatFile(file.getName()) ) {
                    Scanner scanner = new Scanner(file, "UTF-8").useDelimiter("\\n");
                    parseTxtFile.parseAllFile(newListMessages, senders, scanner);
                    scanner.close();
                    chatName = file.getName();
                }else{
                    attachments.add(file.getName());
                }
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        return chatName;
    }

    /**
     * verify that the .txt is a chat
     * @param name name of file
     * @return true if is a chat
     */
    private boolean isAChatFile(String name){
        if(name.contains(NAME_CHAT_ZIP)){
            //example: "_chat.txt"
            return true;
        } else if (name.contains(NAME_CHAT_FOLDER)) {
            //example: "Chat WhatsApp con nome.txt
            return true;
        }
        return false;
    }

    /**
     * verify that every attachment of the list exist in the list of attachment
     * list is passed by reference, not by value, not needed return it
     * @param list all messages to search
     * @param attachments list of all attachment of the folder
     */
    private void existParsedAttachments(ArrayList<Message> list, ArrayList<String> attachments){
        /*
        there is only an attachment for message, even if send in bulk
        */
        for (Message msg: list){
            if(msg.getAttachment() && !attachments.contains(msg.getNameattachment())){
                msg.setAttachment(false);
            }
        }
    }

    /**
     * move file from a source folder to a chat folder of a user
     * @param source folder to move
     * @param idUser identify user folder
     * @param idChat identify user chat
     */
    private void saveToUserFolder(Path source, Long idUser, Long idChat){
        /*
        user DB folder must exist
        */
        storage.createUserDir(idUser);
        File destination = new File(storage.getDBStorage() + "\\" + idUser + "\\" + idChat);
        storage.move(source, destination.toPath());
        /*
        todo: opzione 1 - sarebbe da fare una sotto cartella ad ogni importazione
                        si deve aggiungere una colonna alla tabella
            opzione 2 - altrimenti fare un merge della cartella
         */
    }
}
