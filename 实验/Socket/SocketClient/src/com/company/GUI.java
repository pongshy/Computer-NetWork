package com.company;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * @Author: pongshy
 * @Date: 2020/9/14 16:10
 * @Description:
 **/
public class GUI extends JFrame {

    private JPanel jPanel;
    private JTextField textField_IP;
    private JTextField textField_port;
    private JTextField message;

    private InputStream inputStream;
    private OutputStream outputStream;

    private BufferedWriter bw;
    private BufferedReader br;

    JTextArea txtMsg;
    Socket socket;

    // 构造函数
    public GUI() {
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
                    socket = new Socket(ip, port);

                    new client().start();
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
                    bw.write("quit\n");
                    bw.flush();
                    bw.close();
                    br.close();
                    socket.close();
                    txtMsg.append(DateUtils.getNowTime() + " 连接已断开...." + "\n");
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
        message.addActionListener(new sendListener());
        message.setBounds(20,415,300,25);
        jPanel.add(message);
        message.setColumns(10);

        JButton button2 = new JButton("发送");
        button2.addActionListener(new sendListener());
        button2.setBounds(330, 415, 60, 25);
        jPanel.add(button2);


    }

    private class sendListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                bw.write(message.getText().trim() + "\n");
                bw.flush();

                txtMsg.append(DateUtils.getNowTime() + " 发送消息: " + message.getText().trim() + "\n");
                message.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
                txtMsg.append(DateUtils.getNowTime() + " 服务器连接断开\n");
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public class client extends Thread {
        @Override
        public void run() {
            try {
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();

                bw = new BufferedWriter(new OutputStreamWriter(outputStream));
                br = new BufferedReader(new InputStreamReader(inputStream));

                while (true) {
                    String fromServer = br.readLine();

                    // 服务端关闭
                    if ("quit".equals(fromServer)) {
                        txtMsg.append(DateUtils.getNowTime() + " 服务器关闭...\n");
                        socket.close();
                        break;
                    }
                    if ("ForceQuit".equals(fromServer)) {
                        txtMsg.append(DateUtils.getNowTime() + " 您被服务端强制下线了....\n");
                        socket.close();
                        break;
                    }

                    txtMsg.append(DateUtils.getNowTime() + " 服务器发来消息>" + fromServer + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }






//    public void create() {
//        JFrame frame = new JFrame();
//        frame.setTitle("简易群聊窗口");
//        frame.setBounds(250, 100, 600, 400);
//
//        // 实例化FlowLayout流式布局
//        frame.setLayout(null);
//
//        // 创建面板，以达到比较好的布局
//        JPanel jPanel1 = new JPanel();
//        JPanel jPanel2 = new JPanel();
//
//        // 设置组件的宽和高
//        Dimension dim = new Dimension(200, 35);
//
//        // 创建一个账号标签，并设置字体以及字体大小
//        Font font = new Font("宋体", Font.BOLD, 16);
//        JLabel labName = new JLabel("聊天内容: ");
//        labName.setFont(font);
//        jPanel1.add(labName);
//
//        // 创建一个文本框，并设置大小
//        JTextField textField = new JTextField();
//        textField.setPreferredSize(dim);
//
//        jPanel1.add(textField);
//        jPanel1.setBounds(50, 280, 300, 60);
//        frame.add(jPanel1);
//
//        // 设置一个按钮
//        Dimension dim1 = new Dimension(80, 30);
//        JButton button1 = new JButton("发送");
//        button1.setFont(font);
//        button1.setPreferredSize(dim1);
//        jPanel2.add(button1);
//        jPanel2.setBounds(400, 280, 80, 50);
//        frame.add(jPanel2);
//
//        // 设置textMessage
//        JTextArea textMessage = new JTextArea();
//        textMessage.setTabSize(4);
//        textMessage.setRows(11);
//        textMessage.setColumns(35);
//        textMessage.setBackground(Color.WHITE);
//        textMessage.setText("123");
//        JPanel jPanel3 = new JPanel();
//
//        jPanel3.add(textMessage);
//        jPanel3.setBounds(50, 80, 450, 240);
//        frame.add(jPanel3);
//
//        frame.setVisible(true);
//    }
}
