package com.company;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: pongshy
 * @Date: 2020/9/14 14:58
 * @Description:
 **/
public class ServerHandler extends Thread {
    private static Map<String, Socket> socketMap = new ConcurrentHashMap<>();
    private static Map<String, String> userAndPassword = new ConcurrentHashMap<>();
    private Socket socket;
    private String name;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            sendMsg("连接成功", this.socket);
            sendMsg("请先进行登录或注册", this.socket);
            while (true) {
                String msg = br.readLine();
                String[] elements = null;
                try {
                    elements = msg.split(" ");
                } catch (NullPointerException ex) {
                    System.out.println(this.name + "下线了");
                    break;
                }
                if ("register".equals(elements[0]) && elements.length == 3) {
                    register(elements[1], elements[2]);
                    continue;
                }
                if ("login".equals(elements[0]) && elements.length == 3) {
                    login(elements[1], elements[2]);
                    continue;
                }

                System.out.println(this.name + ": "+ msg);
                groupChat(msg);

                if ("quit".equals(msg)) {
                    quit();
                    break;
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login(String username, String password) {
        if (userAndPassword.containsKey(username)) {
            if (userAndPassword.get(username).equals(password)) {
                this.name = username;
                socketMap.put(this.name, this.socket);
                System.out.println(this.name + "成功登录进入系统");
                sendMsg("欢迎" + this.name + "进入该系统", this.socket);
            } else {
                sendMsg("密码输入有误，请重新登录", this.socket);
            }
        } else {
            sendMsg("登录失败，请重新输入", this.socket);
        }
    }

    private void register(String name, String password) {
        this.name = name;
        userAndPassword.put(name, password);
        socketMap.put(this.name, this.socket);
        System.out.println(this.name + "注册进入系统");
        sendMsg("欢迎" + this.name + "进入该系统", this.socket);
    }

    private void groupChat(String msg) {
        for (Map.Entry<String, Socket> tmp : socketMap.entrySet()) {
            Socket socket = tmp.getValue();
            if (socket == this.socket) {
                continue;
            }
            sendMsg(this.name + ":" + msg, socket);
        }
    }

    private void sendMsg(String msg, Socket client) {
        try {
            OutputStream out = client.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

            bw.write(msg + "\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void quit() {
        System.out.println(this.name + "下线了");
    }
}
