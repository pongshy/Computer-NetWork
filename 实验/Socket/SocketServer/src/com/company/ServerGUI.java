package com.company;

import com.sun.xml.internal.bind.v2.TODO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @Author: pongshy
 * @Date: 2020/9/14 21:47
 * @Description:
 **/
public class ServerGUI extends JFrame {
    private JPanel jPanel;
    private JTextField textField;
    private JTextField stopField;   // 所要强制下线的用户
    private JTextArea txtMsg;
//    private JTextField message;

    private OutputStream outputStream;
    private InputStream inputStream;

    ServerSocket serverSocket;
    private static Map<String, Socket> socketMap = new ConcurrentHashMap<>();
    private static Map<String, String> userAndPassword = new ConcurrentHashMap<>();
    private static ArrayList<Socket> socketArrayList = new ArrayList<>();
    private Socket socket;
    private String name;

    private JTextArea userMsg;
    private JTextField deleteMsg;

    public ServerGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 820, 600);
        jPanel = new JPanel();
        jPanel.setBorder(new EmptyBorder(5,5,5,5));
        setContentPane(jPanel);
        jPanel.setLayout(null);

        JPanel jPanel1 = new JPanel();
        jPanel1.setBounds(0,0,740,57);
        jPanel.add(jPanel1);
        jPanel1.setLayout(null);

        JLabel jLabel = new JLabel("端口: ");
        jLabel.setBounds(12,12,39,15);
        jPanel1.add(jLabel);

        textField = new JTextField();
        textField.setText("8081");
        textField.setBounds(55,10,50,19);
        jPanel1.add(textField);
        textField.setColumns(10);

        JLabel jLabel1 = new JLabel("服务器状态: ");
        jLabel1.setBounds(12, 40, 85, 19);
        jPanel1.add(jLabel1);

        JLabel jLabel2 = new JLabel("停止");
        jLabel2.setBounds(102, 40, 50, 19);
        jPanel1.add(jLabel2);

        JLabel jLabel3 = new JLabel("强制下线用户:");
        jLabel3.setBounds(400, 35, 85, 19);
        jPanel1.add(jLabel3);

        stopField = new JTextField();
        stopField.setBounds(500, 35, 120, 19);
        jPanel1.add(stopField);

        // 确定强制停止该用户
        JButton stopButton = new JButton("确定");
        stopButton.setBounds(650, 35, 80, 19);
        jPanel1.add(stopButton);
        stopButton.addActionListener(e -> {
            String tmpName = stopField.getText();
            if (tmpName != null && socketMap.containsKey(tmpName)) {
                Socket stopSocket = socketMap.get(tmpName);

                try {
                    BufferedWriter tmpBw = new BufferedWriter(
                            new OutputStreamWriter(stopSocket.getOutputStream())
                    );

                    tmpBw.write("ForceQuit");
                    tmpBw.flush();
                    tmpBw.close();

                    // 从正在运行的socket列表中移除
                    socketMap.remove(tmpName);
                    stopSocket.close();

                    //更新用户列表
                    updateUserMsg();
                    txtMsg.append(DateUtils.getNowTime() + " " + tmpName + "被强制下线\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
            stopField.setText("");
        });

        // 启动按钮
        JButton button = new JButton("启动");
        button.setBounds(200,7,70,25);
        jPanel1.add(button);
        button.addActionListener(e -> {
            int port = Integer.parseInt(textField.getText());
            try {
                serverSocket = new ServerSocket(port);
//                txtMsg.append("服务器已启动...\n" + "启动时间: " + new Date() + "\n");
                txtMsg.append(DateUtils.getNowTime() + " 服务器已启动...\n");
                txtMsg.append(DateUtils.getNowTime() + " 等待用户连接....\n");
                System.out.println("服务器启动");

                new Thread(() -> {
                    try {
                        updateUserMsg();
                        while (true) {
                            if (serverSocket.isClosed()) {
                                break;
                            }
                            socket = serverSocket.accept();
                            txtMsg.append(DateUtils.getNowTime() + " " + socket.getRemoteSocketAddress() + "连接成功...\n");
                            new Server_Thread(socket).start();
                            socketArrayList.add(socket);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (serverSocket.isClosed()) {
                            System.out.println("已关闭serverSocket");
                            // TODO: 关闭所有线程
//                            ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
//                            int nowThreads = currentGroup.activeCount();
//
//                            Thread[] listThreads = new Thread[nowThreads];
//                            currentGroup.enumerate(listThreads);
//                            System.out.println("现有线程数: " + nowThreads);
//                            for (int i = 0; i < nowThreads; ++i) {
//                                String nm = listThreads[i].getName();
//                                System.out.println("线程号: " + i + " = " + nm);
//                                listThreads[i].interrupt();
//                            }

                        }
                    }
                }).start();

                jLabel2.setText("运行中");

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        JButton button1 = new JButton("停止");
        button1.setBounds(300,7,70,25);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
//                    socket.close();
                    //TODO: 关闭所有socket
                    //TODO: 向所有在线用户发送服务器关闭通知
                    for (Socket tmpSocket : socketArrayList) {
                        if (!tmpSocket.isClosed()) {
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(tmpSocket.getOutputStream()));
                            bufferedWriter.write("quit\n");
                            bufferedWriter.flush();
                            tmpSocket.close();
                        }
                    }
//                    for (Map.Entry<String, Socket> tmp : socketMap.entrySet()) {
//                        Socket closeScoket = tmp.getValue();
//
//                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(closeScoket.getOutputStream()));
//                        bufferedWriter.write("quit\n");
//                        bufferedWriter.flush();
//                        closeScoket.close();
//                    }
                    socketMap.clear();
                    socketArrayList.clear();
//                    outputStream.close();
//                    inputStream.close();
                    System.out.println("socket关闭");
                    serverSocket.close();
                    txtMsg.append(DateUtils.getNowTime() + " 连接已断开...." + "\n");
                    txtMsg.append(DateUtils.getNowTime() + " 服务器关闭...\n");
                    jLabel2.setText("停止");
                    updateUserMsg();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    txtMsg.append(DateUtils.getNowTime() + " 服务器已关闭...." + "\n");
                }
            }
        });
        jPanel1.add(button1);

        JPanel jPanel2 = new JPanel();
        jPanel2.setBounds(10,69,390,450);
        jPanel.add(jPanel2);


        txtMsg = new JTextArea();
        txtMsg.setTabSize(4);
        txtMsg.setRows(50);
        txtMsg.setColumns(35);
        txtMsg.setBackground(Color.WHITE);
        txtMsg.setText("123");

        jPanel2.add(txtMsg);

        // 用户登录信息
        JPanel jPanel3 = new JPanel();
        jPanel3.setBounds(400, 69, 390, 450);
        jPanel.add(jPanel3);

        userMsg = new JTextArea();
        userMsg.setTabSize(4);
        userMsg.setRows(50);
        userMsg.setColumns(35);
        userMsg.setBackground(Color.WHITE);
        userMsg.setText("123");
        jPanel3.add(userMsg);


    }

    // 更新用户信息表
    private void updateUserMsg() {
        userMsg.setText("");
        for (Map.Entry<String, String> entry : userAndPassword.entrySet()) {
            String username = entry.getKey();

            if (socketMap.containsKey(username)) {
                userMsg.append("username: " + username
                        + " password: " + entry.getValue()
                        + " 在线 "
                        + " host: " + socketMap.get(username).getRemoteSocketAddress()
                        + "\n");
            } else {
                userMsg.append("username: " + username + " password: " + entry.getValue() + " 离线\n");
            }
        }
    }


    private class Server_Thread extends Thread {
        private Socket client;
        private String username;
        private BufferedReader br;
        private BufferedWriter bw;

        public Server_Thread(Socket socket) {
            this.client = socket;
        }

        @Override
        public void run() {
            try {
                inputStream = this.client.getInputStream();
                outputStream =  this.client.getOutputStream();

                bw = new BufferedWriter(new OutputStreamWriter(outputStream));
                br = new BufferedReader(new InputStreamReader(inputStream));
//                txtMsg.append("连接成功" + this.client.getRemoteSocketAddress() + "\n");
                sendMsg( "连接成功", this.client);
                sendMsg("请先登录或是注册", this.client);

                while (true) {
                    updateUserMsg();

                    String msg = br.readLine();
                    String[] elements = null;
                    try {
                        elements = msg.split(" ");
                    } catch (NullPointerException ex) {
                        if (this.username != null) {
                            txtMsg.append(DateUtils.getNowTime() + " " + this.username + "下线了" + "\n");
                        } else {
                            txtMsg.append(DateUtils.getNowTime() + " " + this.client.getRemoteSocketAddress() + "下线了\n");
                        }
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
                    if (this.username == null || "".equals(username)) {
                        sendMsg("请您先登录或是注册", this.client);
                        continue;
                    }
                    // 用户退出连接
                    if ("quit".equals(msg)) {
                        socketMap.remove(this.username);
                        txtMsg.append(DateUtils.getNowTime()+ " 用户: " + this.username + " " + this.client.getRemoteSocketAddress() + "退出连接\n");
                        updateUserMsg();
                        this.client.close();
                        break;
                    }
                    txtMsg.append(DateUtils.getNowTime() + " " + this.username + ": " + msg + "\n");
//                    System.out.println(this.username + ": "+ msg);
                    groupChat(msg);

                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void login(String username, String password) {
            if (userAndPassword.containsKey(username)) {
                if (userAndPassword.get(username).equals(password)) {
                    this.username = username;
                    socketMap.put(this.username, this.client);
                    txtMsg.append(DateUtils.getNowTime() + " " + this.username + "成功登录进入系统\n");
//                    System.out.println(this.username + "成功登录进入系统");
                    sendMsg("欢迎" + this.username + "进入该系统", this.client);
                } else {
                    sendMsg("密码输入有误，请重新登录", this.client);
                }
            } else {
                sendMsg("登录失败，请重新输入", this.client);
            }
        }

        private void register(String name, String password) {
            this.username = name;
            userAndPassword.put(name, password);
            socketMap.put(this.username, this.client);
            txtMsg.append(DateUtils.getNowTime() + " " + this.username + "注册进入系统\n");
//            System.out.println(this.username + "注册进入系统");
            sendMsg("欢迎" + this.username + "进入该系统", this.client);
        }

        private void groupChat(String msg) {
            for (Map.Entry<String, Socket> tmp : socketMap.entrySet()) {
                Socket socket = tmp.getValue();
                if (socket == this.client) {
                    continue;
                }
                sendMsg(this.username + ":" + msg, socket);
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
    }


}
