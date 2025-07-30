package view;

import controller.ReservationController;
import controller.SpaceController;
import model.Reservation;
import model.ReservationStatus;
import model.Space;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReservationsSpaceScreen extends JFrame {

    private final ReservationController reservationController;
    private final SpaceController spaceController;
    private final int loggedUserId;

    // Campos de interface (componentes que precisam ser acessados)
    private JComboBox<String> spaceCombo;
    private JTextField dateField, startTimeField, endTimeField, purposeField;

    public ReservationsSpaceScreen(int loggedUserId) {
        this.reservationController = new ReservationController();
        this.spaceController = new SpaceController();
        this.loggedUserId = loggedUserId;
        setupUI(); // Monta a interface e exibe a janela
    }

    private void setupUI() {
        setTitle("Reserva de Espaços");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // Espaço
        JLabel spaceLabel = new JLabel("Espaço:");
        spaceLabel.setBounds(20, 20, 120, 25);
        add(spaceLabel);

        spaceCombo = new JComboBox<>();
        spaceCombo.setBounds(150, 20, 250, 25);
        add(spaceCombo);
        loadSpaces(spaceCombo);

        // Data
        JLabel dateLabel = new JLabel("Data (dd/MM/yyyy):");
        dateLabel.setBounds(20, 60, 120, 25);
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(150, 60, 250, 25);
        add(dateField);

        // Hora início
        JLabel startTimeLabel = new JLabel("Hora Início (HH:mm):");
        startTimeLabel.setBounds(20, 100, 120, 25);
        add(startTimeLabel);

        startTimeField = new JTextField();
        startTimeField.setBounds(150, 100, 250, 25);
        add(startTimeField);

        // Hora fim
        JLabel endTimeLabel = new JLabel("Hora Fim (HH:mm):");
        endTimeLabel.setBounds(20, 140, 120, 25);
        add(endTimeLabel);

        endTimeField = new JTextField();
        endTimeField.setBounds(150, 140, 250, 25);
        add(endTimeField);

        // Finalidade
        JLabel purposeLabel = new JLabel("Finalidade:");
        purposeLabel.setBounds(20, 180, 120, 25);
        add(purposeLabel);

        purposeField = new JTextField();
        purposeField.setBounds(150, 180, 250, 25);
        add(purposeField);

        // Botões
        JButton checkButton = new JButton("Verificar Disponibilidade");
        checkButton.setBounds(100, 230, 200, 30);
        add(checkButton);

        JButton reserveButton = new JButton("Fazer Reserva");
        reserveButton.setBounds(100, 270, 200, 30);
        add(reserveButton);

        // Ações
        checkButton.addActionListener(e -> verificarDisponibilidade());
        reserveButton.addActionListener(e -> fazerReserva());
        //listButton.addActionListener(e -> listarReservas());

        setVisible(true);
    }

    private void loadSpaces(JComboBox<String> combo) {
        try {
            List<Space> spaces = spaceController.listAllSpaces();
            combo.removeAllItems();

            if (spaces == null || spaces.isEmpty()) {
                combo.addItem("1 - Sala de Aula A1 (Prédio A)");
                combo.addItem("2 - Laboratório de Informática (Prédio B)");
                combo.addItem("3 - Auditório Principal (Prédio C)");
                combo.addItem("4 - Quadra Esportiva (Área Externa)");
                combo.addItem("5 - Sala de Reunião (Prédio A)");

                JOptionPane.showMessageDialog(null,
                        "Aviso: Problema de conexão com banco.\nUsando espaços de exemplo.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                for (Space space : spaces) {
                    String item = space.getId() + " - " + space.getName() + " (" + space.getLocalization() + ")";
                    combo.addItem(item);
                }
            }
        } catch (Exception e) {
            combo.removeAllItems();
            combo.addItem("1 - Sala de Aula A1 (Prédio A)");
            combo.addItem("2 - Laboratório de Informática (Prédio B)");
            combo.addItem("3 - Auditório Principal (Prédio C)");
            combo.addItem("4 - Quadra Esportiva (Área Externa)");
            combo.addItem("5 - Sala de Reunião (Prédio A)");

            JOptionPane.showMessageDialog(null,
                    "Erro ao carregar espaços: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verificarDisponibilidade() {
        try {
            if (!camposValidos()) return;

            int spaceId = extractSpaceId((String) spaceCombo.getSelectedItem());
            LocalDateTime start = parseDateTime(dateField.getText(), startTimeField.getText());
            LocalDateTime end = parseDateTime(dateField.getText(), endTimeField.getText());

            boolean available = checkAvailability(spaceId, start, end);

            if (available) {
                JOptionPane.showMessageDialog(this, "✓ Espaço disponível no horário solicitado!", "Disponibilidade", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "✗ Espaço não disponível no horário solicitado!", "Indisponível", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar disponibilidade: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fazerReserva() {
        try {
            if (!camposValidos()) return;

            if (purposeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A finalidade da reserva é obrigatória!", "Erro de validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int spaceId = extractSpaceId((String) spaceCombo.getSelectedItem());
            LocalDateTime start = parseDateTime(dateField.getText(), startTimeField.getText());
            LocalDateTime end = parseDateTime(dateField.getText(), endTimeField.getText());

            if (!checkAvailability(spaceId, start, end)) {
                JOptionPane.showMessageDialog(this, "Espaço não disponível no horário solicitado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = reservationController.createReservation(loggedUserId, spaceId, start, end, purposeField.getText().trim());

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Reserva realizada com sucesso!\n\n" +
                                "Espaço: " + spaceCombo.getSelectedItem() + "\n" +
                                "Data: " + dateField.getText() + "\n" +
                                "Horário: " + startTimeField.getText() + " às " + endTimeField.getText() + "\n" +
                                "Finalidade: " + purposeField.getText(),
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                clearFields(dateField, startTimeField, endTimeField, purposeField);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao realizar reserva!", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao fazer reserva: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === utilitários ===

    private boolean camposValidos() {
        if (spaceCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um espaço válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (dateField.getText().isEmpty() || startTimeField.getText().isEmpty() || endTimeField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha data e horários!", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private int extractSpaceId(String selected) {
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    private LocalDateTime parseDateTime(String date, String time) {
        return LocalDateTime.of(
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                LocalTime.parse(time)
        );
    }

    private boolean checkAvailability(int spaceId, LocalDateTime start, LocalDateTime end) {
        try {
            Space space = spaceController.getSpaceById(spaceId);
            if (space == null) return false;

            List<Reservation> todas = reservationController.listAllReservations();
            for (Reservation r : todas) {
                if (r.getSpaceId() == spaceId &&
                        (r.getStatus() == ReservationStatus.CONFIRMED || r.getStatus() == ReservationStatus.PENDING)) {
                    if (!(end.isBefore(r.getStartDateTime()) || start.isAfter(r.getEndDateTime()))) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<Reservation> findReservationsByUser(int userId) {
        return reservationController.listAllReservations().stream()
                .filter(r -> r.getUserId() == userId)
                .sorted((r1, r2) -> r2.getStartDateTime().compareTo(r1.getStartDateTime()))
                .toList();
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReservationsSpaceScreen(1));
    }
}