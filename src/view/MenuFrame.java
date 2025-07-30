package view;
import model.User;
import model.UserType;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuFrame extends JFrame {
    private final User user;

    public MenuFrame(User user) {
        super("Menu Principal");
        this.user = user;
        configureWindow();
    }

    private void configureWindow() {
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        getContentPane().setBackground(new Color(248, 250, 252));
        setLocationRelativeTo(null);
    }

    public void showMenu() {
        add(Box.createRigidArea(new Dimension(0, 40)));

        JLabel title = new JLabel("Sistema de Reservas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(51, 65, 85));
        add(title);

        JLabel userInfo = new JLabel("Usuário: " + user.getName());
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfo.setForeground(new Color(100, 116, 139));
        add(userInfo);

        add(Box.createRigidArea(new Dimension(0, 30)));

        //botões de users normais
        addButton("Reservar Espaço", e -> openScreen(new ReservationsSpaceScreen(user.getId())));
        addButton("Ver Reservas", e -> openScreen(new ViewReservationsScreen(user)));
        addButton("Cancelar Reserva", e -> openScreen(new CancelReservationsScreen(user)));

        //botões para os admin
        if (user.getType() == UserType.ADMIN) {
            add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel adminLabel = new JLabel("Painel Administrativo");
            adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            adminLabel.setForeground(new Color(100, 116, 139));
            add(adminLabel);

            add(Box.createRigidArea(new Dimension(0, 15)));

            addButton("Cadastrar Espaço", e -> openScreen(new RegisterSpaceScreen(user)));
            addButton("Remover Espaço", e -> openScreen(new DeleteSpaceScreen(user)));
            addButton("Gerenciar Usuários", e -> openScreen(new UserManagementScreen()));
            addButton("Gerenciar Reservas", e -> openScreen(new ReservationManagementScreen(user)));
        }

        setVisible(true);
    }

    private void addButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setPreferredSize(new Dimension(380, 45));
        button.setMaximumSize(new Dimension(380, 45));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(51, 65, 85));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);

        //efeito pra quando o mouse passa em cima do botão
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(248, 250, 252));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(156, 163, 175), 1),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)
                ));
            }
        });

        button.addActionListener(action);
        add(button);
        add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void openScreen(JFrame screen) {
        screen.setLocationRelativeTo(null);
        screen.setVisible(true);
    }

    public static void main(String args[]) {
        User admin = new User(1, "Admin", "admin@gmail.com", "admin123", UserType.ADMIN); //testar manualmente a mudança de telas
        MenuFrame frame = new MenuFrame(admin);
        frame.showMenu();
    }
}