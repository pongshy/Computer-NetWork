package com.company;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

/**
 * @Author: pongshy
 * @Date: 2020/9/17 19:21
 * @Description: 使用UDP协议，进行数据的传输
 **/
public class UDP extends JFrame {

    private JPanel jPanel;
    private JTextField textField_IP;
    private JTextField textField_port;
    private JTextField message;

    JTextArea txtMsg;
    DatagramSocket socket;
    private String name;

    // 构造函数
    public UDP() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 500);
        jPanel = new JPanel();
        jPanel.setToolTipText("client");
        jPanel.setBorder(new EmptyBorder(5,5,5,5));

        setContentPane(jPanel);
        jPanel.setLayout(null);

        JPanel jPanel1 = new JPanel();
        jPanel1.setBounds(12,5,415,100);
        jPanel.add(jPanel1);
        jPanel1.setLayout(null);

        JLabel label = new JLabel("服务器IP: ");
        label.setBounds(12,12,65,15);
        jPanel1.add(label);

        textField_IP = new JTextField();
        textField_IP.setText("127.0.0.1");
        textField_IP.setBounds(82,10,114,19);
        jPanel1.add(textField_IP);
        textField_IP.setColumns(10);

        JLabel label1 = new JLabel("端口: ");
        label1.setBounds(214, 12, 49, 15);
        jPanel1.add(label1);

        textField_port = new JTextField();
        textField_port.setText("8081");
        textField_port.setBounds(265,10,114,19);
        textField_port.setColumns(10);
        jPanel1.add(textField_port);

        JButton button = new JButton("连接");
        button.setBounds(80,50,60,20);
        // 连接
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = textField_IP.getText();
                int port = Integer.parseInt(textField_port.getText());

                try {
                    // 这边的端口，是客户端监听的端口，可用来接受消息
                    txtMsg.append(DateUtils.getNowTime() + "连接中.....\n");
                    socket = new DatagramSocket(8080);
                    txtMsg.append(DateUtils.getNowTime() + "连接成功...\n");

                    new UDP.client().start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    txtMsg.append(DateUtils.getNowTime() + " 服务器尚未启动或IP、Port输入有误\n");
                }
            }
        });
        jPanel1.add(button);

        JButton button1 = new JButton("断开");
        button1.setBounds(270,50,80,20);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 向服务器发送消息说quit
                    byte[] sendMsg = (name + ":quit").getBytes();
                    String ip = textField_IP.getText().trim();
                    int port = Integer.parseInt(textField_port.getText());

                    DatagramPacket datagramPacket = new DatagramPacket(sendMsg, sendMsg.length, InetAddress.getByName(ip), port);
                    socket.send(datagramPacket);

                    txtMsg.append(DateUtils.getNowTime() + " 连接已断开...." + "\n");
//                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        jPanel1.add(button1);

        JPanel jPanel2 = new JPanel();
        jPanel2.setBounds(12, 100, 415, 280);
        jPanel.add(jPanel2);

        txtMsg = new JTextArea();
        txtMsg.setBackground(Color.WHITE);
        txtMsg.setColumns(35);
        txtMsg.setRows(20);
        txtMsg.setTabSize(4);

        jPanel2.add(txtMsg);

        message = new JTextField();
        message.addActionListener(new UDP.sendListener());
        message.setBounds(20,415,300,25);
        jPanel.add(message);
        message.setColumns(10);

        JButton button2 = new JButton("发送");
        button2.addActionListener(new UDP.sendListener());
        button2.setBounds(330, 415, 60, 25);
        jPanel.add(button2);


    }

    private class sendListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String tmpStr = message.getText().trim();
                byte[] sendMsg = tmpStr.getBytes();
                int port = Integer.parseInt(textField_port.getText().trim());
                String ip = textField_IP.getText().trim();
                // 这里的端口是，需要接受报文的服务端端口
                DatagramPacket ds = new DatagramPacket(sendMsg, sendMsg.length, InetAddress.getByName(ip), port);

                socket.send(ds);

                txtMsg.append(DateUtils.getNowTime() + " 发送消息: " + message.getText().trim() + "\n");
                message.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
                txtMsg.append(DateUtils.getNowTime() + " 服务器连接断开\n");
            }
        }
    }

    // 从服务端获取发来的信息
    public class client extends Thread {
        @Override
        public void run() {
            try {
                byte[] message = new byte[1024];


                while (true) {
                    DatagramPacket packet = new DatagramPacket(message, message.length);
                    socket.receive(packet);
                    String fromServer = new String(packet.getData(), 0, packet.getData().length);
                    String[] elements = fromServer.split(":");
                    if (fromServer.contains(":") && elements[0] != null) {
                        name = elements[0];
                        System.out.println(name);
                        continue;
                    }
                    // 服务端关闭
                    if ("quit".equals(fromServer)) {
                        txtMsg.append(DateUtils.getNowTime() + " 服务器关闭...\n");
                        break;
                    }
                    if ("ForceQuit".equals(fromServer)) {
                        txtMsg.append(DateUtils.getNowTime() + " 您被服务端强制下线了....\n");
                        break;
                    }

                    txtMsg.append(DateUtils.getNowTime() + " 服务器发来消息>" + fromServer + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

//    public void client() {
//        System.out.println("发送端启动....");
//
//        try {
//            DatagramSocket datagramSocket = new DatagramSocket(8080);
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//            String msg = "";
//
//            while ((msg = br.readLine()) != null) {
//                byte[] sendMsg = msg.getBytes();
//                DatagramPacket dp = new DatagramPacket(sendMsg, sendMsg.length, InetAddress.getByName("127.0.0.1"), 8081);
//
//                datagramSocket.send(dp);
//                if ("quit".equals(msg)) {
//                    System.out.println("退出成功!");
//                    break;
//                }
//
//                byte[] getMsg = new byte[1024];
//                DatagramPacket ds = new DatagramPacket(getMsg, getMsg.length);
//                datagramSocket.receive(ds);
//                String message = new String(ds.getData(), 0, ds.getData().length);
//                System.out.println("服务器>" + message);
//
//                System.out.println("发送成功");
//
//            }
//            datagramSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
