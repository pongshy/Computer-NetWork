package com.company;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @Author: pongshy
 * @Date: 2020/9/14 15:24
 * @Description:
 **/
public class SocketWrite extends Thread {
    private Socket socket;

    public SocketWrite(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            int i = 1;
            System.out.println("写入数据:" + i++);
            OutputStream out = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in);

            // 带有缓冲区的字符串流
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            while (true) {
                System.out.print("请输入>");
                String msg = scanner.nextLine();

                if ("quit".equals(msg)) {
                    System.out.println("下线了");
                    break;
                }
                bw.write(msg + "\n");
                bw.flush();
                sleep(100);
            }
            this.socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
