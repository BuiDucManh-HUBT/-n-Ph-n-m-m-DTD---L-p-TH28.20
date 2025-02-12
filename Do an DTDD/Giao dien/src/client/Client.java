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

    public Client() {
        chatUI = new ChatUI();
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
            chatUI.appendMessage("Bạn", messageContent, true);
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
                            boolean isSelf = messageSplit[1].startsWith("Client " + clientId);
                            chatUI.appendMessage(messageSplit[1].split(" ", 2)[0], messageSplit[1].split(" ", 2)[1], isSelf);
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
        SwingUtilities.invokeLater(Client::new);
    }
}

class ChatUI extends JFrame {
    private JLabel notificationLabel;
    private JTextField messageInput;
    private JButton sendButton;
    private JComboBox<String> userList;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;

    public ChatUI() {
        setTitle("Messenger");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        notificationLabel = new JLabel("", SwingConstants.LEFT);
        notificationLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        notificationLabel.setForeground(Color.GRAY);
        add(notificationLabel, BorderLayout.NORTH);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        userList = new JComboBox<>();
        userList.addItem("Tất cả");

        messageInput = new JTextField();
        sendButton = new JButton("Gửi");
        sendButton.setBackground(new Color(0, 122, 255));
        sendButton.setForeground(Color.WHITE);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        inputPanel.add(userList, BorderLayout.WEST);
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(chatScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public void showNotification(String message) {
        notificationLabel.setText(message);
    }

    public JTextField getMessageInput() {
        return messageInput;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public String getSelectedUser() {
        return (String) userList.getSelectedItem();
    }

    public void appendMessage(String sender, String message, boolean isRight) {
        JPanel messageBubble = new JPanel();
        messageBubble.setLayout(new BoxLayout(messageBubble, BoxLayout.Y_AXIS));
        messageBubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messageBubble.setBackground(isRight ? new Color(173, 216, 230) : new Color(220, 220, 220));
        messageBubble.setOpaque(true);
        
        JLabel senderLabel = new JLabel(sender);
        senderLabel.setFont(new Font("Arial", Font.BOLD, 12));
        messageBubble.add(senderLabel);

        JTextArea messageText = new JTextArea(message);
        messageText.setWrapStyleWord(true);
        messageText.setLineWrap(true);
        messageText.setEditable(false);
        messageText.setBackground(messageBubble.getBackground());
        messageBubble.add(messageText);

        JPanel container = new JPanel();
        container.setLayout(new FlowLayout(isRight ? FlowLayout.RIGHT : FlowLayout.LEFT));
        container.add(messageBubble);

        chatPanel.add(container);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    public void updateUserList(String users) {
        userList.removeAllItems();
        userList.addItem("Tất cả");
        for (String user : users.split("-")) {
            if (!user.isEmpty()) {
                userList.addItem(user);
            }
        }
    }
}
