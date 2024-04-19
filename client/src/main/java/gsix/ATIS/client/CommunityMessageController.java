package gsix.ATIS.client;

import java.util.List;

public class CommunityMessageController {//By Ayal

    public static void send(String senderID, String receiverID, String message){
        //store the message in the CommunityMessage table(using mySQL)
        //we need to use the server to access the data base



    }
    static List<String> getMessageBox(String receiverID){
        //get all the messages that the receiverID has received.(using mySQL)
        //need to use the server to access the data base
        List<String> messages = null;

        return messages;
    }
}
