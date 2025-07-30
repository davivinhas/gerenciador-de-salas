package view;

import controller.UserController;
import model.User;
import model.UserType;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserManagementScreen extends JFrame {
    private UserController userController;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField, passwordField;
    private JComboBox<UserType> typeComboBox;
    private JButton addButton, updateButton, deleteButton, refreshButton;
    private User selectedUser;

    public UserManagementScreen() {
        this.userController = new UserController();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadUsers();
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Gerenciamento de Usuários - Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        // Campos de entrada
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        typeComboBox = new JComboBox<>(UserType.values());

        // Botões
        addButton = new JButton("Adicionar");
        updateButton = new JButton("Atualizar");
        deleteButton = new JButton("Excluir");
        refreshButton = new JButton("Atualizar Lista");

        // Configurar cores dos botões
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        updateButton.setBackground(new Color(30, 144, 255));
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(128, 128, 128));
        refreshButton.setForeground(Color.WHITE);

        // Tabela
        String[] columnNames = {"ID", "Nome", "Email", "Tipo", "Data de Criação"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável diretamente
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configurar aparência da tabela
        userTable.setRowHeight(25);
        userTable.getTableHeader().setBackground(new Color(70, 130, 180));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.setFont(new Font("Arial", Font.PLAIN, 11));

        // Desabilitar botões inicialmente
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel superior - Título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Gerenciamento de Usuários");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Painel central - Tabela
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Usuários"));
        add(scrollPane, BorderLayout.CENTER);

        // Painel lateral - Formulário
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.EAST);

        // Painel inferior - Botões de ação
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));
        panel.setPreferredSize(new Dimension(250, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        // Email
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridy = 3;
        panel.add(emailField, gbc);

        // Senha
        gbc.gridy = 4;
        panel.add(new JLabel("Senha:"), gbc);
        gbc.gridy = 5;
        panel.add(passwordField, gbc);

        // Tipo
        gbc.gridy = 6;
        panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridy = 7;
        panel.add(typeComboBox, gbc);

        // Botão de limpar campos
        gbc.gridy = 8; gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton clearButton = new JButton("Limpar Campos");
        clearButton.addActionListener(e -> clearFields());
        panel.add(clearButton, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private void setupEventListeners() {
        // Seleção na tabela
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadUserToForm(selectedRow);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                } else {
                    clearFields();
                    updateButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        // Botão Adicionar
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        // Botão Atualizar
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUser();
            }
        });

        // Botão Excluir
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        // Botão Atualizar Lista
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUsers();
                clearFields();
            }
        });
    }

    private void loadUsers() {
        tableModel.setRowCount(0); // Limpar tabela
        List<User> users = userController.listAllUsers();

        if (users != null) {
            for (User user : users) {
                Object[] rowData = {
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getType().getDescription(),
                        user.getCreationDate() != null ? user.getCreationDate().toString() : "N/A"
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void loadUserToForm(int selectedRow) {
        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        selectedUser = userController.getUserById(userId);

        if (selectedUser != null) {
            nameField.setText(selectedUser.getName());
            emailField.setText(selectedUser.getEmail());
            passwordField.setText(""); // Não mostrar senha por segurança
            typeComboBox.setSelectedItem(selectedUser.getType());
        }
    }

    private void addUser() {
        if (!validateFields()) {
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        UserType type = (UserType) typeComboBox.getSelectedItem();

        // Verificar se email já existe
        if (userController.emailExists(email)) {
            showMessage("Email já está em uso!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = userController.createUser(name, email, password, type);

        if (success) {
            showMessage("Usuário adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
            clearFields();
        } else {
            showMessage("Erro ao adicionar usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        if (selectedUser == null || !validateFields()) {
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        UserType type = (UserType) typeComboBox.getSelectedItem();

        // Verificar se email já existe (exceto para o usuário atual)
        User existingUser = userController.getUserByEmail(email);
        if (existingUser != null && existingUser.getId() != selectedUser.getId()) {
            showMessage("Email já está em uso por outro usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedUser.setName(name);
        selectedUser.setEmail(email);
        selectedUser.setType(type);

        // Só atualizar senha se foi informada
        if (!password.isEmpty()) {
            selectedUser.setPassword(password);
        }

        boolean success = userController.updateUser(selectedUser);

        if (success) {
            showMessage("Usuário atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
            clearFields();
        } else {
            showMessage("Erro ao atualizar usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        if (selectedUser == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir o usuário '" + selectedUser.getName() + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userController.deleteUser(selectedUser);

            if (success) {
                showMessage("Usuário excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
                clearFields();
            } else {
                showMessage("Erro ao excluir usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            showMessage("Nome é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showMessage("Email é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        if (!emailField.getText().contains("@") || !emailField.getText().contains(".")) {
            showMessage("Email deve ter um formato válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        // Senha obrigatória apenas para novos usuários
        if (selectedUser == null && passwordField.getText().isEmpty()) {
            showMessage("Senha é obrigatória para novos usuários!", "Erro", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return false;
        }

        return true;
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        typeComboBox.setSelectedIndex(0);
        selectedUser = null;
        userTable.clearSelection();
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Método para ser chamado a partir do menu principal
    public static void showUserManagement() {
        SwingUtilities.invokeLater(() -> new UserManagementScreen());
    }

    public static void main(String args[]){
           showUserManagement();

    }
}