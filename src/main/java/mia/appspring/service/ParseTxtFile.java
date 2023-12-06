package mia.appspring.service;


import mia.appspring.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ParseTxtFile {

    private final String DEFAULT_USER = "Whatsapp";

    /*
    ATTACHMENT 1
    must have "<" and ": " for verify attachments
    example: "<allegato: nome.jpg>"
     */
    public final String ATTACHMENT_START1 = "<allegato: ";
    public final String ATTACHMENT_END1 = ">";
    /*
    ATTACHMENT 2
    example: "nome.jpg (file allegato)\n"
	https://regex101.com/
     */
    public final String ATTACHMENT_END2 = " (file allegato)";
    public final String ATTACHMENT_END2_FOR_SPLIT = " [(]file allegato[)]";
    public int typeAttachment = 0;

    private ArrayList<Message> listMessages;
    private ArrayList<String> listSenders;

    private final MessageService messageService;

    @Autowired
    public ParseTxtFile(MessageService messageService){
        this.messageService = messageService;
    }

    private boolean testWithSquareBracketAndBackslashDate(String s){
        //example:[18/11/20, 13:56:06]
        return (s.length() > 19)
                && (s.charAt(0) == '[')
                && (s.charAt(3) == '/')
                && (s.charAt(19) == ']');
    }

    private boolean testWithSquareBracketAndDotDate(String s){
        //example:[18.11.20, 13:56:06]
        return (s.length() > 19)
                && (s.charAt(0) == '[')
                && (s.charAt(3) == '.')
                && (s.charAt(19) == ']');
    }

    private boolean testWithNoBracketAndBackslashDate(String s){
        // example:29/05/22, 14:42 -
        return (s.length() > 16) && (s.charAt(8) == ',') && (s.charAt(16) == '-');
    }

    private void testAttachment(String text){
        if (text.contains(ATTACHMENT_START1)){
            typeAttachment=1;
        } else if (text.contains(ATTACHMENT_END2)) {
            typeAttachment=2;
        }
    }

    private String parseAttachment(String text){
        String[] arrOfStr;
        if (typeAttachment == 0){
            testAttachment(text);
        }
        switch (typeAttachment){
            case 1:
                if (text.contains(ATTACHMENT_START1)) {
                    //example: "<allegato: nome.jpg>"
                    arrOfStr = text.split(ATTACHMENT_START1, 2);
                    arrOfStr = arrOfStr[1].split(ATTACHMENT_END1, 2);
                    return arrOfStr[0];
                }
                return "";
            case 2:
                if(text.contains(ATTACHMENT_END2)){
                    //example: "nome.jpg (file allegato)"
                    arrOfStr = text.split(ATTACHMENT_END2_FOR_SPLIT, 2);
                    return arrOfStr[0];
                }
                return "";
            default:
                return "";
        }

    }

    private void parseMessage(String textToParse,
                                     Boolean test,
                                     String charsTimeToUser,
                                     String patternDateTime,
                                     Boolean initialBracket){
        //29/05/22, 14:41 - utente: testo
        if (test){
            Message nuovo = messageService.newMessage();
            String[] arrOfStr = textToParse.split(charsTimeToUser);
            if (initialBracket){
                arrOfStr[0] = arrOfStr[0].substring(1);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternDateTime);
            LocalDateTime msgDateTime = LocalDateTime.parse(arrOfStr[0], formatter);
            nuovo.setDatetime(msgDateTime.toString());
            //System.out.println(Arrays.toString(arrOfStr));
            arrOfStr = arrOfStr[1].split(": ",2);
            if (arrOfStr.length == 1){
                nuovo.setSender(DEFAULT_USER);
                nuovo.setText(arrOfStr[0]);
            }else {
                nuovo.setSender(arrOfStr[0]);
                nuovo.setText(arrOfStr[1]);
            }
            if( !(listSenders.contains(nuovo.getSender())) ){
                listSenders.add(nuovo.getSender());
            }
            String nomeAtt = parseAttachment(nuovo.getText());
            nuovo.setNameattachment(nomeAtt);
            if (!nomeAtt.isEmpty()){
                nuovo.setAttachment(true);
            }else {
                nuovo.setAttachment(false);
            }
            //System.out.println(msgDateTime);
            //System.out.println(Arrays.toString(arrOfStr));
            listMessages.add(nuovo);
        }else {
            int index = listMessages.size()-1;
            Message tmp = listMessages.remove(index);
            tmp.setText(tmp.getText() + "\n" + textToParse);
            String nomeAtt = parseAttachment(tmp.getText());
            if (nomeAtt.isEmpty()){
                tmp.setNameattachment(nomeAtt);
                tmp.setAttachment(true);
            }
            listMessages.add(index,tmp);
        }
    }

    private int test(String firstLine){
        if (testWithSquareBracketAndBackslashDate(firstLine)){
            return 1;
        } else if (testWithSquareBracketAndDotDate(firstLine)) {
            return 2;
        } else if (testWithNoBracketAndBackslashDate(firstLine)) {
            return 3;
        }else {
            //TODO creare un errore per file non parsing
            return 0;
        }
    }

    private String purgeString(String dirty){
        if (dirty.contains("\u200E") || (dirty.contains("\r"))){
            // escape utf-8 char
            dirty = dirty.replaceAll("\u200E", "");
            // file txt with windows escape char CR-LF (\r\n)
            dirty = dirty.replaceAll("\r", "");
        }
        return dirty;
    }

    public void parseAllFile(ArrayList<Message> newListMessages, ArrayList<String> sender, Scanner file){
        int tipeOfText = 0;
        boolean test = true;
        listMessages = new ArrayList<Message>();
        listSenders = new ArrayList<String>();

        while(file.hasNext()){
            String nextLine = purgeString(file.next());
            //System.out.println(nextLine);

            if (test){
                tipeOfText = test(nextLine);
                test = false;
                if (tipeOfText == 0){
                    break;
                }
            }
            switch (tipeOfText){
                case 1:
                    parseMessage(nextLine,
                            testWithSquareBracketAndBackslashDate(nextLine),
                            "] ",
                            "dd/MM/yy, HH:mm:ss",
                            true);
                    break;
                case 2:
                    parseMessage(nextLine,
                            testWithSquareBracketAndDotDate(nextLine),
                            "] ",
                            "dd.MM.yy, HH:mm:ss",
                            true);
                    break;
                case 3:
                    parseMessage(nextLine,
                            testWithNoBracketAndBackslashDate(nextLine),
                            " - ",
                            "dd/MM/yy, HH:mm",
                            false);
                    break;
            }
        }
        newListMessages.addAll(listMessages);
        sender.addAll(listSenders);
    }

}
