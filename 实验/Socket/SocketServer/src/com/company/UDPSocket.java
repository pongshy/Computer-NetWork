package com.company;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: pongshy
 * @Date: 2020/9/17 19:32
 * @Description:
 **/
public class UDPSocket extends JFrame {

    private JPanel jPanel;
    private JTextField textField;
    private JTextField stopField;   // 所要强制下线的用户
    private JTextArea txtMsg;
//    private JTextField message;


    DatagramSocket datagramSocket;
    private static Map<String, DatagramPacket> dataPacketMap = new ConcurrentHashMap<>();
    private static Map<String, String> userAndPassword = new ConcurrentHashMap<>();
    private static ArrayList<DatagramPacket> dataPacketArrayList = new ArrayList<>();
    private Socket socket;
    private String name;

    private JTextArea userMsg;
    private JTextField deleteMsg;

    public UDPSocket() {
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
            if (tmpName != null && dataPacketMap.containsKey(tmpName)) {
                DatagramPacket tmpPacket = dataPacketMap.get(tmpName);

                try {
                    byte[] tmpStr = "ForceQuit".getBytes();
                    int port = tmpPacket.getPort();
                    DatagramPacket tmp = new DatagramPacket(
                            tmpStr,
                            tmpStr.length,
                            InetAddress.getByName(tmpPacket.getAddress().getHostAddress()),
                            port
                    );
                    datagramSocket.send(tmp);

                    // 从正在运行的socket列表中移除
                    dataPacketMap.remove(tmpName);
                    dataPacketArrayList.remove(tmpName);

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
                datagramSocket = new DatagramSocket(port);
//                txtMsg.append("服务器已启动...\n" + "启动时间: " + new Date() + "\n");
                txtMsg.append(DateUtils.getNowTime() + " 服务器已启动...\n");
                txtMsg.append(DateUtils.getNowTime() + " 等待用户连接....\n");
                System.out.println("服务器启动");

                new Thread(() -> {
                    try {
                        updateUserMsg();
                        new UDPSocket.Server_Thread().start();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (datagramSocket.isClosed()) {
                            System.out.println("已关闭serverSocket");
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
                    //TODO: 向所有在线用户发送服务器关闭通知
                    for (DatagramPacket tmpPacket : dataPacketArrayList) {
                        byte[] tmp = "quit".getBytes();
                        int port = tmpPacket.getPort();
                        DatagramPacket packetTmp = new DatagramPacket(
                                tmp,
                                tmp.length,
                                InetAddress.getByName(tmpPacket.getAddress().getHostAddress()),
                                port
                                );
                        datagramSocket.send(packetTmp);
                    }
                    dataPacketMap.clear();
                    dataPacketArrayList.clear();

                    System.out.println("socket关闭");
                    datagramSocket.close();
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

            if (dataPacketMap.containsKey(username)) {
                userMsg.append("username: " + username
                        + " password: " + entry.getValue()
                        + " 在线 "
                        + " host: " + dataPacketMap.get(username).getAddress().getHostAddress()
                        + "\n");
            } else {
                userMsg.append("username: " + username + " password: " + entry.getValue() + " 离线\n");
            }
        }
    }


    private class Server_Thread extends Thread {
        private DatagramPacket packet;
        private String username;
        private BufferedReader br;
        private BufferedWriter bw;

        public Server_Thread() {

        }

        @Override
        public void run() {
            try {
//                txtMsg.append("连接成功" + this.client.getRemoteSocketAddress() + "\n");
//                sendMsg( "连接成功", this.client);
//                sendMsg("请先登录或是注册", this.client);

                while (true) {
                    updateUserMsg();

                    byte[] getMsg = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(getMsg, getMsg.length);

                    datagramSocket.receive(packet);
                    this.packet = packet;

                    if (!dataPacketArrayList.contains(packet)) {
                        dataPacketArrayList.add(packet);
                    }

                    String msg = new String(packet.getData(), 0, packet.getData().length);
                    String[] elements = null;
                    try {
                        elements = msg.split(" ");
                    } catch (NullPointerException ex) {
                        if (this.username != null) {
                            txtMsg.append(DateUtils.getNowTime() + " " + this.username + "下线了" + "\n");
                        } else {
                            txtMsg.append(DateUtils.getNowTime() + " " + this.packet.getAddress().getHostAddress() + "下线了\n");
                        }
                        break;
                    }
                    if ("register".equals(elements[0]) && elements.length == 3) {
                        register(elements[1], elements[2]);
                        continue;
                    }
                    if ("login".equals(elements[0]) && elements.length == 3) {
                        login(elements[1], elements[2], this.packet);
                        continue;
                    }
                    String[] msgElements = msg.split(":");
                    if (msgElements.length < 2 || (msgElements.length == 2 && !dataPacketMap.containsKey(msgElements[0]))|| (elements.length == 3 && !dataPacketMap.containsKey(elements[1]))) {
                        sendMsg("请您先登录或是注册", this.packet);
                        continue;
                    }
                    // 用户退出连接
                    if (msg.contains("quit")) {
                        String[] tmpElements = msg.split(":");
                        dataPacketMap.remove(tmpElements[0]);
                        txtMsg.append(DateUtils.getNowTime() + " " + elements[0] + this.packet.getAddress().getHostAddress() + "退出连接\n");
                        updateUserMsg();
                        continue;
                    }
                    txtMsg.append(DateUtils.getNowTime() + " " + msg + "\n");
//                    System.out.println(this.username + ": "+ msg);
                    groupChat(msg);

                }

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        public void login(String username, String password, DatagramPacket packet) {
            if (userAndPassword.containsKey(username)) {
                password = CommonUtils.regular(password);
                if (userAndPassword.get(username).equals(password)) {
                    this.username = username;
                    dataPacketMap.put(this.username, packet);
                    txtMsg.append(DateUtils.getNowTime() + " " + this.username + "成功登录进入系统\n");
//                    System.out.println(this.username + "成功登录进入系统");
                    sendMsg("欢迎" + this.username + "进入该系统", packet);
                } else {
                    System.out.println(userAndPassword.get(username));
                    sendMsg("密码输入有误，请重新登录", packet);
                }
            } else {
                sendMsg("登录失败，请重新输入", packet);
            }
        }

        private void register(String name, String password) throws InterruptedException {
            this.username = name;
            password = CommonUtils.regular(password);
            userAndPassword.put(name, password);
            dataPacketMap.put(this.username, this.packet);
            txtMsg.append(DateUtils.getNowTime() + " " + this.username + "注册进入系统\n");
//            System.out.println(this.username + "注册进入系统");
            sendMsg("欢迎" + this.username + "进入该系统", this.packet);
            sendMsg(this.username + ":", this.packet);
        }

        private void groupChat(String msg) {
            for (Map.Entry<String, DatagramPacket> tmp : dataPacketMap.entrySet()) {
                DatagramPacket tmpValue = tmp.getValue();
//                if (tmpValue.getAddress().getHostAddress() == this.packet.getAddress().getHostAddress()) {
//                    System.out.println(1);
//                    continue;
//                }
                sendMsg(msg, tmpValue);
            }
        }

        private void sendMsg(String msg, DatagramPacket datagramPacket) {
            try {
                byte[] tmp = msg.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(
                        tmp,
                        tmp.length,
                        InetAddress.getByName(datagramPacket.getAddress().getHostAddress()), datagramPacket.getPort()
                );
                datagramSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    public void start() {
//        try {
//            DatagramSocket socket = new DatagramSocket(8081);
//
//            while (true) {
//                byte[] msg = new byte[1024];
//
//                DatagramPacket dp = new DatagramPacket(msg, msg.length);
//                socket.receive(dp);
//
//                String ip = dp.getAddress().getHostAddress();
//                int port = dp.getPort();
//
//                String text = new String(dp.getData(), 0, dp.getLength());
//                byte[] sendMsg = text.getBytes();
//                DatagramPacket ds = new DatagramPacket(sendMsg, sendMsg.length, InetAddress.getByName(ip), port);
//                socket.send(ds);
//
//                System.out.println("-----" + ip + "-------" + port + "-----" + text);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

}
