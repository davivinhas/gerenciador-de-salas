package view;

import controller.UserController;
import model.UserType;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeCombo;
    private JTextField adminKeyField;
    private JLabel adminKeyLabel;
    private UserController userController;
    private LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.userController = new UserController();
        setupUI();
    }

    private void setupUI() {
        setTitle("Criar Nova Conta");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        mainPanel.setBackground(Color.WHITE);

        // Título
        JLabel title = new JLabel("Criar Nova Conta");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(50, 50, 50));
        mainPanel.add(title);

        mainPanel.add(Box.createVerticalStrut(30));

        // Campo Nome
        mainPanel.add(createFieldPanel("Nome:", nameField = createTextField()));

        // Campo Email
        mainPanel.add(createFieldPanel("Email:", emailField = createTextField()));

        // Campo Senha
        mainPanel.add(createFieldPanel("Senha:", passwordField = createPasswordField()));

        // Tipo de usuário
        JLabel typeLabel = new JLabel("Tipo de usuário:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(typeLabel);
        mainPanel.add(Box.createVerticalStrut(8));

        userTypeCombo = new JComboBox<>(new String[]{"Usuário", "Administrador"});
        userTypeCombo.setPreferredSize(new Dimension(340, 40));
        userTypeCombo.setMaximumSize(new Dimension(340, 40));
        userTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        userTypeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userTypeCombo);
        mainPanel.add(Box.createVerticalStrut(20));

        // Campo chave admin (inicialmente oculto)
        adminKeyLabel = new JLabel("Chave de Administrador:");
        adminKeyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        adminKeyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminKeyField = createTextField();

        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.Y_AXIS));
        adminPanel.setBackground(Color.WHITE);
        adminPanel.add(adminKeyLabel);
        adminPanel.add(Box.createVerticalStrut(8));
        adminPanel.add(adminKeyField);
        adminPanel.add(Box.createVerticalStrut(18));

        // Inicialmente oculto
        adminPanel.setVisible(false);
        mainPanel.add(adminPanel);

        // Listener para mostrar/ocultar chave admin
        userTypeCombo.addActionListener(e -> {
            boolean isAdmin = userTypeCombo.getSelectedIndex() == 1;
            adminPanel.setVisible(isAdmin);
            revalidate();
            repaint();
        });

        mainPanel.add(Box.createVerticalStrut(25));

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton createBtn = createStyledButton("Criar Conta", new Color(46, 204, 113));
        JButton backBtn = createStyledButton("Voltar", new Color(108, 117, 125));

        buttonPanel.add(createBtn);
        buttonPanel.add(backBtn);
        mainPanel.add(buttonPanel);

        add(mainPanel);

        // Eventos
        createBtn.addActionListener(e -> createAccount());
        backBtn.addActionListener(e -> goBackToLogin());

        // Fechar janela volta para login
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBackToLogin();
            }
        });
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(field);
        panel.add(Box.createVerticalStrut(18));

        return panel;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(340, 40));
        field.setMaximumSize(new Dimension(340, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(340, 40));
        field.setMaximumSize(new Dimension(340, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void createAccount() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        boolean isAdmin = userTypeCombo.getSelectedIndex() == 1;
        String adminKey = adminKeyField.getText().trim();

        // Validações
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Por favor, preencha todos os campos obrigatórios!");
            return;
        }

        if (isAdmin && adminKey.isEmpty()) {
            showError("Por favor, digite a chave de administrador!");
            return;
        }

        if (userController.emailExists(email)) {
            showError("Este email já está cadastrado!");
            return;
        }

        // Criar conta
        boolean success;
        if (isAdmin) {
            success = userController.createAdmin(name, email, password, adminKey);
            if (!success) {
                showError("Chave de administrador inválida!");
                return;
            }
        } else {
            success = userController.createUser(name, email, password, UserType.USER);
        }

        if (success) {
            String userType = isAdmin ? "administrador" : "usuário";
            JOptionPane.showMessageDialog(this,
                    "Conta de " + userType + " criada com sucesso!\nVocê pode fazer login agora.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            goBackToLogin();
        } else {
            showError("Erro ao criar conta. Tente novamente.");
        }
    }

    private void goBackToLogin() {
        loginFrame.showLoginFrame();
        this.dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}