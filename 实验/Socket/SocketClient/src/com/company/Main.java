package com.company;

import java.net.Socket;

public class Main {

    public static void main(String[] args) {
	// write your code here
//        GUI gui = new GUI();
//        gui.create();
//        try {
//            Socket socket = new Socket("127.0.0.1", 8081);
//            System.out.println("启动读取线程");
//            new SocketRead(socket).start();
//            System.out.println("启动写入线程");
//            new SocketWrite(socket).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        /*
         * @Description: TCP协议
         * @Author: pongshy
         * @createTime: 2020/9/17 19:31
         */
//        new Thread(() -> {
//            GUI gui = new GUI();
//            gui.setVisible(true);
//        }).start();


        /*
         * @Description: UDP协议客户端
         * @Author: pongshy
         * @createTime: 2020/9/17 22:22
         */
        new Thread(() -> {
            UDP udp = new UDP();
            udp.setVisible(true);
        }).start();
    }
}
