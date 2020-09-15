package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @Author: pongshy
 * @Date: 2020/9/14 15:23
 * @Description:
 **/
public class SocketRead extends Thread {
    private Socket socket;

    public SocketRead(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            int i = 1;
            System.out.println("读取数据:" + i++);
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            while (true) {
                try {
                    String msg = br.readLine();
                    System.out.println("来自服务端>" + msg);
                } catch (Exception e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
