package view;

import model.User;

import javax.swing.*;

public class ViewReservationsScreen extends JFrame {
    public ViewReservationsScreen(User user) {
        super("Tela de Reserva (Simulada)");
        setSize(400, 300);
        add(new JLabel("Esta seria a tela de reserva para: " + user.getName()));
    }
}