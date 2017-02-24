package com.neurosky.mindwavemobiledemo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by chaiche on 16/8/24.
 */
public class Serve {
    ServerSocket serverSocket;

    Socket socket = null;

    BufferedReader br;
    BufferedWriter bw;

    String tmp;
    GameTestActivity activity;

    public Serve(GameTestActivity activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                socket = serverSocket.accept();
                serverSocket.close();
                socket.setTcpNoDelay(true);
                socket.setSendBufferSize(8192);

                socket.setOOBInline(true);

                SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                        socket, count);
                socketServerReplyThread.run();


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        String tmp;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {


            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                // 寫入訊息到串流
                bw.write("x:123z:1345" + "\n");
                // 立即發送
                bw.flush();

            } catch (IOException e) {
                Log.e("error", e.getMessage() + " this");
            }


            ReadThread read = new ReadThread();
            read.start();



        }

    }

    private class ReadThread extends Thread {

        ReadThread() {

        }

        @Override
        public void run() {
            while (socket.isConnected()) {
                try {
                    tmp = br.readLine();
                    //tmp = tmp.substring(tmp.indexOf('x')+2,tmp.indexOf('z'));
                    // 如果不是空訊息則
                    if (tmp != null) {
                        activity.setTxv4(tmp);
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
                if(socket!=null) {
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
