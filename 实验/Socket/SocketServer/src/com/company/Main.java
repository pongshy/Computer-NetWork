package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
	// write your code here
//        try {
//            ServerSocket serverSocket = new ServerSocket(8081);
//            System.out.println("服务器启动中.....");
//            System.out.println("服务器启动，运行在" + serverSocket.getLocalSocketAddress());
//            while (true) {
////                int i = 1;
////                System.out.println("测试: " + i++);
//                Socket socket = serverSocket.accept();
//                System.out.println("客户端连接，来自:" + socket.getRemoteSocketAddress());
//                new ServerHandler(socket).start();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        /*
         * @Description: TCP协议服务端
         * @Author: pongshy
         * @createTime: 2020/9/17 20:02
         */
//        new Thread(()->{
//            ServerGUI serverGUI = new ServerGUI();
//            serverGUI.setVisible(true);
//        }).start();

        /*
         * @Description: UDP协议服务端
         * @Author: pongshy
         * @createTime: 2020/9/17 19:38
         */
        new Thread(() -> {
            UDPSocket udpSocket = new UDPSocket();
            udpSocket.setVisible(true);
        }).start();

    }
}
