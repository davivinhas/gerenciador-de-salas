package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
import model.User;

public class Login extends JFrame {
    private final UserController controller = new UserController();

    public Login() {
        super("Login - Sistema de Gestão de Espaços");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 800);
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setResizable(false);
        setLayout(new GridBagLayout()); // Usamos um layout flexível

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margem entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuário
        JLabel lblUsuario = new JLabel("Usuário:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblUsuario, gbc);

        JTextField txtUsuario = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(txtUsuario, gbc);

        // Senha
        JLabel lblSenha = new JLabel("Senha:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblSenha, gbc);

        JPasswordField txtSenha = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtSenha, gbc);

        // Botão Entrar
        JButton btnEntrar = new JButton("Entrar");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(btnEntrar, gbc);

        // Botão Criar Conta
        JButton btnCriarConta = new JButton("Criar Conta");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(btnCriarConta, gbc);

        // Ação do botão Entrar
        btnEntrar.addActionListener(e -> {
            String usuario = txtUsuario.getText();
            String senha = new String(txtSenha.getPassword());

            if (usuario.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            } else {
                User usuarioLogado = controller.login(usuario, senha);

                if (usuarioLogado != null) {
                    JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
                    this.dispose();
                    // Redirecionar para tela principal de acordo com tipo
                } else {
                    JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.");
                }
            }
        });

        // Ação do botão Criar Conta
        btnCriarConta.addActionListener(e -> {
            this.dispose();
            new Register().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
