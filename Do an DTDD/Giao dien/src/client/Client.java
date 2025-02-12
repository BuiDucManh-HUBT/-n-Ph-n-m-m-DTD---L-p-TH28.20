package client;

import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;

public class Client {

    private BufferedWriter os;
    private BufferedReader is;
    private Socket socketOfClient;
    private ChatUI chatUI;
    private int clientId;
    private String clientName;

    public Client(String name) {
        this.clientName = name;
        chatUI = new ChatUI(name);
        chatUI.setVisible(true);
        setUpSocket();

        chatUI.getSendButton().addActionListener(evt -> sendMessage());
    }

    private void sendMessage() {
        String messageContent = chatUI.getMessageInput().getText();
        if (messageContent.isEmpty()) {
            return;
        }
        
        String selectedUser = chatUI.getSelectedUser();
        try {
            if (selectedUser.equals("Tất cả")) {
                write("send-to-global," + messageContent + "," + clientId);
            } else {
                write("send-to-person," + messageContent + "," + clientId + "," + selectedUser);
            }
            chatUI.appendMessage(clientName, messageContent, true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(chatUI, "Có lỗi xảy ra");
        }
        chatUI.getMessageInput().setText("");
    }

    private void setUpSocket() {
        try {
            socketOfClient = new Socket("localhost", 7777);
            os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));

            Thread listenerThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = is.readLine()) != null) {
                        String[] messageSplit = message.split(",");
                        if (messageSplit[0].equals("get-id")) {
                            clientId = Integer.parseInt(messageSplit[1]);
                        } else if (messageSplit[0].equals("global-message")) {
                            chatUI.appendMessage("Server", messageSplit[1], false);
                        } else if (messageSplit[0].equals("update-online-list")) {
                            chatUI.updateUserList(messageSplit[1]);
                            chatUI.showNotification("Người mới tham gia: " + messageSplit[1]);
                        }
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(chatUI, "Mất kết nối với máy chủ");
                }
            });
            listenerThread.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(chatUI, "Không thể kết nối đến server");
        }
    }

    private void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new Client("Máy chính"));
        SwingUtilities.invokeLater(() -> new Client("Máy phụ"));
    }
}
