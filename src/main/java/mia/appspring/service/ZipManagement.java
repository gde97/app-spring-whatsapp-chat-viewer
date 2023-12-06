package mia.appspring.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ZipManagement {

    /**
     * https://www.html.it/articoli/java-gestire-file-zip/
     * https://www.digitalocean.com/community/tutorials/java-unzip-file-example
     * https://www.digitalocean.com/community/tutorials/java-zip-file-folder-example
     */


    public final String FILE_EXTENSION = ".zip";
    private ArrayList<String> filesListInDir = new ArrayList<String>();

    /**
     * Method to unzip a file
     * generate a folder in the same directory
     * @param pathFileZip path to file
     */
    public void unZip(File pathFileZip, String destName){
        File destFolder = new File(pathFileZip.getParent(), destName);
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(pathFileZip);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destFolder + File.separator + fileName);
                //System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for subdirectories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method populates all the files in a directory to a List
     * @param dir source folder
     * @throws IOException error creating list
     */
    private void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isFile()){
                filesListInDir.add(file.getAbsolutePath());
            }else{
                populateFilesList(file);
            }
        }
    }

    /**
     * Method to generate a zip from a given directory
     * The final name is the same of original folder plus extension
     * @param dir directory to zip, will use last folder for the result
     * @param zipDirDownload path to all zip generated to send
     */
    public void zipDirectory(File dir, String zipDirDownload) {
        String zipDirName = zipDirDownload + "\\" + dir.getName() + FILE_EXTENSION;
        try {
            filesListInDir = new ArrayList<String>();
            populateFilesList(dir);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(String filePath : filesListInDir){
                //System.out.println("Zipping "+filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
