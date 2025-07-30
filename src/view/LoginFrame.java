package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private UserController userController;

    public LoginFrame() {
        this.userController = new UserController();
        setupUI();
    }

    private void setupUI() {
        setTitle("Gerenciador de Espaços - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
        mainPanel.setBackground(Color.WHITE);

        // Título
        JLabel title = new JLabel("Gerenciamento de Espaços");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(50, 50, 50));
        mainPanel.add(title);

        mainPanel.add(Box.createVerticalStrut(40));

        // Campo Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(320, 40));
        emailField.setMaximumSize(new Dimension(320, 40));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        mainPanel.add(emailLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Campo Senha
        JLabel passLabel = new JLabel("Senha:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(320, 40));
        passwordField.setMaximumSize(new Dimension(320, 40));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        mainPanel.add(passLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(35));

        // Botões
        JButton loginBtn = createStyledButton("Entrar", new Color(52, 152, 219));
        JButton createBtn = createStyledButton("Criar Conta", new Color(46, 204, 113));

        mainPanel.add(loginBtn);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createBtn);

        add(mainPanel);

        // Eventos
        loginBtn.addActionListener(e -> login());
        createBtn.addActionListener(e -> openRegisterFrame());
        passwordField.addActionListener(e -> login());
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(320, 45));
        btn.setMaximumSize(new Dimension(320, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showError("Preencha todos os campos!");
            return;
        }

        User user = userController.login(email, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Login realizado com sucesso!\nBem-vindo, " + user.getName(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            // Aqui você pode abrir a tela principal do sistema
            // MainFrame mainFrame = new MainFrame(user);
            // mainFrame.setVisible(true);
            // this.dispose();

        } else {
            showError("Email ou senha incorretos!");
            passwordField.setText("");
        }
    }

    private void openRegisterFrame() {
        RegisterFrame registerFrame = new RegisterFrame(this);
        registerFrame.setVisible(true);
        this.setVisible(false);
    }

    public void showLoginFrame() {
        this.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {


        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}