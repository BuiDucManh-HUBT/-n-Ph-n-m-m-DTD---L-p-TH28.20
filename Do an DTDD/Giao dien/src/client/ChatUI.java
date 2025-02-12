package client;

import javax.swing.*;
import java.awt.*;

public class ChatUI extends JFrame {
    private JTextField messageInput;
    private JButton sendButton;
    private JComboBox<String> userList;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;

    public ChatUI(String clientName) {
        setTitle("Messenger - " + clientName);
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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

    public void showNotification(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}