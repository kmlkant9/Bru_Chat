package com.example.kmlkant3497.bru_chat;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by gulshanraj116 on 29/10/17.
 */

public class SocketHandler {
    public static Socket socket;
    public static BufferedReader bufferedReader;
    public static PrintWriter printWriter;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }

    public static synchronized BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public static synchronized void setBufferedReader(BufferedReader bufferedReader){
        SocketHandler.bufferedReader = bufferedReader;
    }

    public static synchronized PrintWriter getPrintWriter() {
        return printWriter;
    }

    public static synchronized void setPrintWriter(PrintWriter printWriter){
        SocketHandler.printWriter = printWriter;
    }


}
