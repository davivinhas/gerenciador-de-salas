package view;

import dao.ReservationDao;
import model.Reservation;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewReservationsScreen extends JFrame {

    public ViewReservationsScreen(User user) {
        setTitle("My Reservations");
        setSize(800, 400); // aumentei um pouco para melhor visualização
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("My Reservations", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Tabela de Reservas
        String[] columnNames = {"Space", "Date", "Hour"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        // Redimensionamento automático das colunas
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        try {
            List<Reservation> reservations = new ReservationDao().listByUserId(user.getId());

            for (Reservation r : reservations) {
                String[] rowData = {
                        r.getSpace().getName(),
                        r.getDateAsString(),
                        r.getHourAsString()
                };
                tableModel.addRow(rowData);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }
}