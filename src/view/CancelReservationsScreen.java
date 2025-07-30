package view;

import controller.ReservationController;
import controller.SpaceController;
import model.Reservation;
import model.Space;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CancelReservationsScreen extends JFrame {
    private final User user;
    private ReservationController reservationController;
    private SpaceController spaceController;
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JButton cancelReservationButton;

    //construtor pra filtrar as reservas do user
    public CancelReservationsScreen(User user) {
        this.user = user;
        this.reservationController = new ReservationController();
        this.spaceController = new SpaceController();
        initializeComponents();
        setupLayout();
        setupListeners();
        loadReservations();
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Cancelar Reservas");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //colunas da tabelinha
        String[] columnNames = {"ID Reserva", "Espaço", "Data Início", "Data Fim", "Descrição", "Status"};
        //cria modelo tabela
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        //cria a table
        reservationTable = new JTable(tableModel);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //botão de cancelar
        cancelReservationButton = new JButton("Cancelar Reserva");
        cancelReservationButton.setEnabled(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(cancelReservationButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cancelReservationButton.setEnabled(reservationTable.getSelectedRow() >= 0);
            } //cancelar a reserva só fica disponivel quando seleciona uma linha
        });

        cancelReservationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelSelectedReservation();
            }
        });
    }

    //reservas do user na tabela - CORRIGIDO
    private void loadReservations() {
        tableModel.setRowCount(0);   //limpa a tabela

        // Usa o método específico para buscar reservas do usuário
        List<Reservation> reservations = reservationController.getReservationsByUserId(user.getId());

        if (reservations != null) {
            for (Reservation reservation : reservations) {
                // Pega o nome do espaço
                String spaceName = reservation.getSpaceName(); // Já vem do JOIN
                if (spaceName == null || spaceName.equals("N/A")) {
                    Space space = spaceController.getSpaceById(reservation.getSpaceId());
                    spaceName = (space != null) ? space.getName() : "Desconhecido";
                }

                //cria a linha com as infos pra tabela
                Object[] rowData = {
                        reservation.getId(),
                        spaceName,
                        reservation.getStartDateTime(),
                        reservation.getEndDateTime(),
                        reservation.getDescription(),
                        reservation.getStatus().getDescription()
                };
                tableModel.addRow(rowData); //add na table
            }
        }
    }

    private void cancelSelectedReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = reservationTable.convertRowIndexToModel(selectedRow);
        int reservationId = (Integer) tableModel.getValueAt(modelRow, 0);

        Reservation reservation = reservationController.getReservationById(reservationId);
        if (reservation == null) {
            JOptionPane.showMessageDialog(this, "Reserva não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja cancelar a reserva selecionada?",
                "Confirmar Cancelamento",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reservationController.cancelReservation(reservation);
            if (success) {
                JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadReservations(); // Recarrega a tabela
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar a reserva.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void showCancelReservationsScreen(User user) {
        SwingUtilities.invokeLater(() -> new CancelReservationsScreen(user));
    }
}