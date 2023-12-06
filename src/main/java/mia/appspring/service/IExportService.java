package mia.appspring.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public interface IExportService {

    /*
    https://stackoverflow.com/questions/22007341/spring-jpa-selecting-specific-columns
     */
    Resource getAttachment(String pathFile);

    Resource getChatZip(String pathDir);
}
