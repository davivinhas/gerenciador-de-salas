package view;

import controller.ReservationController;
import controller.SpaceController;
import controller.UserController;
import model.Reservation;
import model.ReservationStatus;
import model.Space;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationManagementScreen extends JFrame {
    private ReservationController reservationController;
    private SpaceController spaceController;
    private UserController userController;
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JComboBox<ReservationStatus> statusFilterCombo;
    private JComboBox<Space> spaceFilterCombo;
    private JComboBox<User> userFilterCombo;
    private JButton filterButton, clearFilterButton;
    private JButton cancelButton, confirmButton, concludeButton, deleteButton, refreshButton;
    private Reservation selectedReservation;

    public ReservationManagementScreen(User admin) {
        this.reservationController = new ReservationController();
        this.spaceController = new SpaceController();
        this.userController = new UserController();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadReservations();
        loadFilters();
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Gerenciamento de Reservas - Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        // Filtros
        statusFilterCombo = new JComboBox<>(ReservationStatus.values());
        statusFilterCombo.insertItemAt(null, 0); // Adiciona opção "Todos"
        statusFilterCombo.setSelectedIndex(0);

        spaceFilterCombo = new JComboBox<>();
        spaceFilterCombo.insertItemAt(null, 0); // Adiciona opção "Todos"
        spaceFilterCombo.setSelectedIndex(0);

        userFilterCombo = new JComboBox<>();
        userFilterCombo.insertItemAt(null, 0); // Adiciona opção "Todos"
        userFilterCombo.setSelectedIndex(0);

        // Botões de filtro
        filterButton = new JButton("Filtrar");
        clearFilterButton = new JButton("Limpar Filtros");

        // Botões de ação
        cancelButton = new JButton("Cancelar Reserva");
        confirmButton = new JButton("Confirmar Reserva");
        concludeButton = new JButton("Concluir Reserva");
        deleteButton = new JButton("Excluir Reserva");
        refreshButton = new JButton("Atualizar Lista");

        // Configurar cores dos botões
        filterButton.setBackground(new Color(70, 130, 180));
        filterButton.setForeground(Color.WHITE);
        clearFilterButton.setBackground(new Color(128, 128, 128));
        clearFilterButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        confirmButton.setBackground(new Color(34, 139, 34));
        confirmButton.setForeground(Color.WHITE);
        concludeButton.setBackground(new Color(255, 140, 0));
        concludeButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(178, 34, 34));
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(128, 128, 128));
        refreshButton.setForeground(Color.WHITE);

        // Tabela
        String[] columnNames = {
                "ID",
                "Usuário",
                "Espaço",
                "Início",
                "Fim",
                "Status",
                "Descrição",
                "Data Criação"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(tableModel);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configurar aparência da tabela
        reservationTable.setRowHeight(25);
        reservationTable.getTableHeader().setBackground(new Color(70, 130, 180));
        reservationTable.getTableHeader().setForeground(Color.WHITE);
        reservationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        reservationTable.setFont(new Font("Arial", Font.PLAIN, 11));

        // Desabilitar botões inicialmente
        updateActionButtons(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel superior - Título e Filtros
        JPanel topPanel = new JPanel(new BorderLayout());

        // Painel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Gerenciamento de Reservas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Painel de filtros
        JPanel filterPanel = new JPanel(new GridLayout(1, 4, 10, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        filterPanel.add(createFilterComboPanel("Status:", statusFilterCombo));
        filterPanel.add(createFilterComboPanel("Espaço:", spaceFilterCombo));
        filterPanel.add(createFilterComboPanel("Usuário:", userFilterCombo));

        JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        filterButtonPanel.add(filterButton);
        filterButtonPanel.add(clearFilterButton);
        filterPanel.add(filterButtonPanel);

        topPanel.add(filterPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Painel central - Tabela
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Reservas"));
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior - Botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(concludeButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFilterComboPanel(String label, JComboBox<?> comboBox) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventListeners() {
        // Seleção na tabela
        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = reservationTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int reservationId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    selectedReservation = reservationController.getReservationById(reservationId);
                    updateActionButtons(true);
                } else {
                    selectedReservation = null;
                    updateActionButtons(false);
                }
            }
        });

        // Botão Filtrar
        filterButton.addActionListener(e -> loadReservations());

        // Botão Limpar Filtros
        clearFilterButton.addActionListener(e -> {
            statusFilterCombo.setSelectedIndex(0);
            spaceFilterCombo.setSelectedIndex(0);
            userFilterCombo.setSelectedIndex(0);
            loadReservations();
        });

        // Botão Cancelar Reserva
        cancelButton.addActionListener(e -> changeReservationStatus(ReservationStatus.CANCELED));

        // Botão Confirmar Reserva
        confirmButton.addActionListener(e -> changeReservationStatus(ReservationStatus.CONFIRMED));

        // Botão Concluir Reserva
        concludeButton.addActionListener(e -> changeReservationStatus(ReservationStatus.CONCLUDED));

        // Botão Excluir Reserva
        deleteButton.addActionListener(e -> deleteReservation());

        // Botão Atualizar Lista
        refreshButton.addActionListener(e -> {
            loadReservations();
            updateActionButtons(false);
        });
    }

    private void loadFilters() {
        // Carregar espaços
        spaceFilterCombo.removeAllItems();
        spaceFilterCombo.addItem(null); // Opção "Todos"
        List<Space> spaces = spaceController.listAllSpaces();
        for (Space space : spaces) {
            spaceFilterCombo.addItem(space);
        }

        // Carregar usuários
        userFilterCombo.removeAllItems();
        userFilterCombo.addItem(null); // Opção "Todos"
        List<User> users = userController.listAllUsers();
        for (User user : users) {
            userFilterCombo.addItem(user);
        }
    }

    private void loadReservations() {
        tableModel.setRowCount(0); // Limpar tabela

        // Obter filtros selecionados
        ReservationStatus statusFilter = (ReservationStatus) statusFilterCombo.getSelectedItem();
        Space spaceFilter = (Space) spaceFilterCombo.getSelectedItem();
        User userFilter = (User) userFilterCombo.getSelectedItem();

        List<Reservation> reservations = reservationController.listAllReservations();

        if (reservations != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Reservation reservation : reservations) {
                // Aplicar filtros
                if (statusFilter != null && reservation.getStatus() != statusFilter) {
                    continue;
                }
                if (spaceFilter != null && reservation.getSpaceId() != spaceFilter.getId()) {
                    continue;
                }
                if (userFilter != null && reservation.getUserId() != userFilter.getId()) {
                    continue;
                }

                // Obter detalhes do usuário e espaço
                User user = userController.getUserById(reservation.getUserId());
                Space space = spaceController.getSpaceById(reservation.getSpaceId());

                String userName = user != null ? user.getName() : "Usuário não encontrado";
                String spaceName = space != null ? space.getName() : "Espaço não encontrado";

                Object[] rowData = {
                        reservation.getId(),
                        userName,
                        spaceName,
                        reservation.getStartDateTime().format(dateFormatter),
                        reservation.getEndDateTime().format(dateFormatter),
                        reservation.getStatus().getDescription(),
                        reservation.getDescription(),
                        reservation.getCreationDate().format(dateFormatter)
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void changeReservationStatus(ReservationStatus newStatus) {
        if (selectedReservation == null) {
            return;
        }

        String action = "";
        switch (newStatus) {
            case CANCELED: action = "cancelar"; break;
            case CONFIRMED: action = "confirmar"; break;
            case CONCLUDED: action = "concluir"; break;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja " + action + " esta reserva?",
                "Confirmar Ação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reservationController.changeReservationStatus(selectedReservation, newStatus);

            if (success) {
                showMessage("Reserva " + action + " com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadReservations();
                selectedReservation = null;
                updateActionButtons(false);
            } else {
                showMessage("Erro ao " + action + " reserva!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteReservation() {
        if (selectedReservation == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir permanentemente esta reserva?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reservationController.deleteReservation(selectedReservation);

            if (success) {
                showMessage("Reserva excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadReservations();
                selectedReservation = null;
                updateActionButtons(false);
            } else {
                showMessage("Erro ao excluir reserva!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateActionButtons(boolean enabled) {
        cancelButton.setEnabled(enabled);
        confirmButton.setEnabled(enabled);
        concludeButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);

        // Desabilitar ações que não fazem sentido para o status atual
        if (selectedReservation != null) {
            ReservationStatus status = selectedReservation.getStatus();
            cancelButton.setEnabled(status != ReservationStatus.CANCELED && status != ReservationStatus.CONCLUDED);
            confirmButton.setEnabled(status == ReservationStatus.PENDING);
            concludeButton.setEnabled(status == ReservationStatus.CONFIRMED);
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Criar um usuário admin fictício para teste
            User admin = new User(1, "Admin", "admin@example.com", "admin123", model.UserType.ADMIN);
            new ReservationManagementScreen(admin);
        });
    }
}