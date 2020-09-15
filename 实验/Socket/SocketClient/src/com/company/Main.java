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


        new Thread(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        }).start();
    }
}
