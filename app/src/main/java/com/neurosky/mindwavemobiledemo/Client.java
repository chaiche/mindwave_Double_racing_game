package com.neurosky.mindwavemobiledemo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by chaiche on 16/8/24.
 */
public class Client {
    Socket socket = null;

    String tmp = "";

    BufferedReader br;
    BufferedWriter bw;

    GameTestActivity activity;


    public Client(GameTestActivity activity,String address){
        this.activity = activity;
        MyClientThread myClientThread = new MyClientThread(
                address);
        myClientThread.start();
        activity.setTxv4("Client");
    }
    public class MyClientThread extends Thread {

        String dstAddress;
        int dstPort;

        MyClientThread(String addr){
            dstAddress = addr;
            dstPort = 8080;
        }
        @Override
        public void run() {

            try {

                socket = new Socket(dstAddress, dstPort);

                socket.setTcpNoDelay(true);
                socket.setSendBufferSize(8192);
                socket.setOOBInline(true);

                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                ReadThread read = new ReadThread();
                read.start();
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                sendData("x:123z:1234");


            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }



    }

    private class ReadThread extends Thread{

        ReadThread() {

        }
        @Override
        public void run() {
            while(socket.isConnected()) {
                try {
                    tmp = br.readLine();
                    //tmp.substring(tmp.indexOf('x'),tmp.indexOf('z'));
                    // 如果不是空訊息則
                    if (tmp != null) {
                        activity.setTxv4(tmp +"\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public void sendData(final String tmp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket!=null) {
                    if(socket.isConnected()) {
                        try {
                            bw.write(tmp + "\n");
                            bw.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

}
