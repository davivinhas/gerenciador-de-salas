package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
import model.UserType;

public class Register extends JFrame {
    public Register() {
        super("Criar Conta - Sistema de Gestão de Espaços");
        final UserController controller = new UserController();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 800);
        setLocationRelativeTo(null);  // centraliza a janela na tela
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Configurações gerais do layout
        gbc.insets = new Insets(10, 10, 10, 10); // margem entre os elementos
        gbc.anchor = GridBagConstraints.WEST;

        // Label e campo de usuário
        JLabel lblUsuario = new JLabel("Usuário:");
        JTextField txtUsuario = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblUsuario, gbc);
        gbc.gridx = 1;
        panel.add(txtUsuario, gbc);

        // Label e campo de senha
        JLabel lblSenha = new JLabel("Senha:");
        JPasswordField txtSenha = new JPasswordField(15);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblSenha, gbc);
        gbc.gridx = 1;
        panel.add(txtSenha, gbc);

        // Botão Registrar
        JButton btnRegistrar = new JButton("Registrar");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnRegistrar, gbc);

        // Botão Voltar
        JButton btnVoltar = new JButton("Voltar");
        gbc.gridy = 3;
        panel.add(btnVoltar, gbc);

        // Lógica do botão Registrar
        btnRegistrar.addActionListener(e -> {
            String usuario = txtUsuario.getText();
            String senha = new String(txtSenha.getPassword());

            if (usuario.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            } else {
                UserType tipo = senha.equals("admin123") ? UserType.ADMIN : UserType.USER;
                boolean success = controller.createUser(usuario, usuario + "@email.com", senha, tipo);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Conta criada como " + tipo + "!");
                    this.dispose();
                    new Login().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao criar conta. Email já existente?");
                }
            }
        });

        // Lógica do botão Voltar
        btnVoltar.addActionListener(e -> {
            this.dispose();
            new Login().setVisible(true);
        });

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Register().setVisible(true);
        });
    }
}