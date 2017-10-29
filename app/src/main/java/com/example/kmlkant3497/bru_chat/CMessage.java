package com.example.kmlkant3497.bru_chat;

import android.util.Log;

import java.util.Map;

/**
 * Created by gulshanraj116 on 29/10/17.
 */


class CMessage {
     static String sender;
     static String receiver;
     static String msg;
    private final static String msgTAG = "CMessage";


     static String getMsg(String sender, String receiver, String msg) {
        String message = (sender + "_" + receiver + "_" + msg);
        Log.i("Cmessage", message);
        return message;
    }

    static String getMsg() {
        String message = (sender + "_" + receiver + "_" + msg);
        Log.i("Cmessage", message);
        return message;
    }

    static boolean isMessageFromServer() {
         if(msg.charAt(0)=='0') {
             return true;
         }
         return false;
    }

    static String getMyMappingNumber() {
         String msgs[] = msg.split("_", 3);
        Log.i("Cmessage", "My mapping no is "+msgs[1]);
         return msgs[1];
    }

    static String getClientIndex() {
         //msg format 0_mymapNO_NAME_mapNo_otherName
         String msgs[] = msg.split("_", 5);
        Log.i("Cmessage", "client mapping no is "+msgs[3]);
         return msgs[3];
    }

    static String getClientName() {
        String msgs[] = msg.split("_", 5);
        Log.i("Cmessage", "client mapping name is "+msgs[4]);
        return msgs[4];
    }

    static void updateList(Map<Integer, String> clientNames) {
         //msg format 0_mp_LIST_NO_NAME
         String msgs[] = msg.split("_");
         if(!msgs[2].equals("LIST")) {
             Log.i("Cmessage", "Not LIST, Wrong msg: "+msgs[2]);
             return;
         }
         Log.i("Cmessage", "List from Server");
         for(int i=3; i<msgs.length; i+=2) {
             Log.i("Cmessage", "List: "+msgs[i]+" "+msgs[i+1]);
             clientNames.put(Integer.parseInt(msgs[i]), msgs[i+1]);
         }
         return;
    }
}