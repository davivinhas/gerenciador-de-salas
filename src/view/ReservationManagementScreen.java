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
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

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
    private JLabel statusLabel;

    // Cache para otimização
    private Map<Integer, User> userCache = new HashMap<>();
    private Map<Integer, Space> spaceCache = new HashMap<>();
    private List<Reservation> cachedReservations;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReservationManagementScreen(User admin) {
        this.reservationController = new ReservationController();
        this.spaceController = new SpaceController();
        this.userController = new UserController();

        // Mostrar splash de carregamento
        showLoadingMessage();

        // Carregar dados em background
        SwingUtilities.invokeLater(() -> {
            initializeComponents();
            setupLayout();
            setupEventListeners();
            loadInitialDataAsync();
        });
    }

    private void showLoadingMessage() {
        setTitle("Carregando...");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.add(new JLabel("Carregando dados...", SwingConstants.CENTER), BorderLayout.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        loadingPanel.add(progressBar, BorderLayout.SOUTH);

        add(loadingPanel);
        setVisible(true);
    }

    private void loadInitialDataAsync() {
        CompletableFuture.supplyAsync(this::loadCacheData)
                .thenAccept(success -> SwingUtilities.invokeLater(() -> {
                    if (success) {
                        finishInitialization();
                    } else {
                        showErrorAndClose();
                    }
                }));
    }

    private boolean loadCacheData() {
        try {
            // Carregar todos os dados uma única vez
            List<User> users = userController.listAllUsers();
            List<Space> spaces = spaceController.listAllSpaces();
            cachedReservations = reservationController.listAllReservations();

            // Popular caches
            userCache.clear();
            spaceCache.clear();

            if (users != null) {
                for (User user : users) {
                    userCache.put(user.getId(), user);
                }
            }

            if (spaces != null) {
                for (Space space : spaces) {
                    spaceCache.put(space.getId(), space);
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void finishInitialization() {
        getContentPane().removeAll();
        setSize(1000, 600);
        setTitle("Gerenciamento de Reservas - Admin");

        setupMainLayout();
        loadFiltersFromCache();
        loadReservationsFromCache();
        updateActionButtons(false);

        revalidate();
        repaint();

        statusLabel.setText("Pronto - " + tableModel.getRowCount() + " reservas carregadas");
    }

    private void showErrorAndClose() {
        JOptionPane.showMessageDialog(this, "Erro ao carregar dados!", "Erro", JOptionPane.ERROR_MESSAGE);
        dispose();
    }

    private void initializeComponents() {
        setResizable(true);

        // Filtros
        statusFilterCombo = new JComboBox<>(ReservationStatus.values());
        statusFilterCombo.insertItemAt(null, 0);
        statusFilterCombo.setSelectedIndex(0);

        spaceFilterCombo = new JComboBox<>();
        spaceFilterCombo.insertItemAt(null, 0);
        spaceFilterCombo.setSelectedIndex(0);

        userFilterCombo = new JComboBox<>();
        userFilterCombo.insertItemAt(null, 0);
        userFilterCombo.setSelectedIndex(0);

        // Botões
        filterButton = new JButton("Filtrar");
        clearFilterButton = new JButton("Limpar Filtros");
        cancelButton = new JButton("Cancelar Reserva");
        confirmButton = new JButton("Confirmar Reserva");
        concludeButton = new JButton("Concluir Reserva");
        deleteButton = new JButton("Excluir Reserva");
        refreshButton = new JButton("Atualizar Lista");

        // Label de status
        statusLabel = new JLabel("Carregando...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));

        // Configurar cores dos botões
        setupButtonColors();

        // Tabela otimizada
        setupTable();
    }

    private void setupButtonColors() {
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
    }

    private void setupTable() {
        String[] columnNames = {
                "ID", "Usuário", "Espaço", "Início", "Fim",
                "Status", "Descrição", "Data Criação"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationTable = new JTable(tableModel);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationTable.setRowHeight(25);
        reservationTable.getTableHeader().setBackground(new Color(70, 130, 180));
        reservationTable.getTableHeader().setForeground(Color.WHITE);
        reservationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        reservationTable.setFont(new Font("Arial", Font.PLAIN, 11));

        // Otimização da tabela
        reservationTable.setAutoCreateRowSorter(false); // Desabilita ordenação automática
        reservationTable.setFillsViewportHeight(true);
    }

    private void setupMainLayout() {
        setLayout(new BorderLayout());

        // Painel superior
        JPanel topPanel = new JPanel(new BorderLayout());

        // Título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Gerenciamento de Reservas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Filtros
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

        // Tabela
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Reservas"));
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(concludeButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupLayout() {
        setupMainLayout();
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
                    selectedReservation = findReservationInCache(reservationId);
                    updateActionButtons(true);
                } else {
                    selectedReservation = null;
                    updateActionButtons(false);
                }
            }
        });

        // Botão Filtrar
        filterButton.addActionListener(e -> {
            statusLabel.setText("Filtrando...");
            SwingUtilities.invokeLater(() -> {
                loadReservationsFromCache();
                statusLabel.setText("Filtrado - " + tableModel.getRowCount() + " reservas");
            });
        });

        // Botão Limpar Filtros
        clearFilterButton.addActionListener(e -> {
            statusFilterCombo.setSelectedIndex(0);
            spaceFilterCombo.setSelectedIndex(0);
            userFilterCombo.setSelectedIndex(0);
            statusLabel.setText("Carregando...");
            SwingUtilities.invokeLater(() -> {
                loadReservationsFromCache();
                statusLabel.setText("Carregado - " + tableModel.getRowCount() + " reservas");
            });
        });

        // Botões de ação
        cancelButton.addActionListener(e -> changeReservationStatus(ReservationStatus.CANCELED));
        confirmButton.addActionListener(e -> changeReservationStatus(ReservationStatus.CONFIRMED));
        concludeButton.addActionListener(e -> changeReservationStatus(ReservationStatus.CONCLUDED));
        deleteButton.addActionListener(e -> deleteReservation());

        refreshButton.addActionListener(e -> {
            statusLabel.setText("Atualizando...");
            CompletableFuture.supplyAsync(this::loadCacheData)
                    .thenAccept(success -> SwingUtilities.invokeLater(() -> {
                        if (success) {
                            loadFiltersFromCache();
                            loadReservationsFromCache();
                            updateActionButtons(false);
                            statusLabel.setText("Atualizado - " + tableModel.getRowCount() + " reservas");
                        } else {
                            statusLabel.setText("Erro ao atualizar");
                        }
                    }));
        });
    }

    private Reservation findReservationInCache(int reservationId) {
        if (cachedReservations != null) {
            return cachedReservations.stream()
                    .filter(r -> r.getId() == reservationId)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private void loadFiltersFromCache() {
        // Carregar espaços
        spaceFilterCombo.removeAllItems();
        spaceFilterCombo.addItem(null);
        for (Space space : spaceCache.values()) {
            spaceFilterCombo.addItem(space);
        }

        // Carregar usuários
        userFilterCombo.removeAllItems();
        userFilterCombo.addItem(null);
        for (User user : userCache.values()) {
            userFilterCombo.addItem(user);
        }
    }

    private void loadReservationsFromCache() {
        tableModel.setRowCount(0);

        if (cachedReservations == null) return;

        // Obter filtros
        ReservationStatus statusFilter = (ReservationStatus) statusFilterCombo.getSelectedItem();
        Space spaceFilter = (Space) spaceFilterCombo.getSelectedItem();
        User userFilter = (User) userFilterCombo.getSelectedItem();

        for (Reservation reservation : cachedReservations) {
            // Aplicar filtros
            if (statusFilter != null && reservation.getStatus() != statusFilter) continue;
            if (spaceFilter != null && reservation.getSpaceId() != spaceFilter.getId()) continue;
            if (userFilter != null && reservation.getUserId() != userFilter.getId()) continue;

            // Usar cache para obter detalhes
            User user = userCache.get(reservation.getUserId());
            Space space = spaceCache.get(reservation.getSpaceId());

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

    private void changeReservationStatus(ReservationStatus newStatus) {
        if (selectedReservation == null) return;

        String action = getActionName(newStatus);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja " + action + " esta reserva?",
                "Confirmar Ação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            statusLabel.setText("Processando...");

            CompletableFuture.supplyAsync(() ->
                            reservationController.changeReservationStatus(selectedReservation, newStatus))
                    .thenAccept(success -> SwingUtilities.invokeLater(() -> {
                        if (success) {
                            showMessage("Reserva " + action + " com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            // Atualizar cache
                            selectedReservation.setStatus(newStatus);
                            loadReservationsFromCache();
                            selectedReservation = null;
                            updateActionButtons(false);
                            statusLabel.setText("Ação concluída");
                        } else {
                            showMessage("Erro ao " + action + " reserva!", "Erro", JOptionPane.ERROR_MESSAGE);
                            statusLabel.setText("Erro na operação");
                        }
                    }));
        }
    }

    private String getActionName(ReservationStatus status) {
        switch (status) {
            case CANCELED: return "cancelar";
            case CONFIRMED: return "confirmar";
            case CONCLUDED: return "concluir";
            default: return "alterar";
        }
    }

    private void deleteReservation() {
        if (selectedReservation == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir permanentemente esta reserva?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            statusLabel.setText("Excluindo...");

            CompletableFuture.supplyAsync(() ->
                            reservationController.deleteReservation(selectedReservation))
                    .thenAccept(success -> SwingUtilities.invokeLater(() -> {
                        if (success) {
                            showMessage("Reserva excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            // Remover do cache
                            cachedReservations.remove(selectedReservation);
                            loadReservationsFromCache();
                            selectedReservation = null;
                            updateActionButtons(false);
                            statusLabel.setText("Reserva excluída");
                        } else {
                            showMessage("Erro ao excluir reserva!", "Erro", JOptionPane.ERROR_MESSAGE);
                            statusLabel.setText("Erro ao excluir");
                        }
                    }));
        }
    }

    private void updateActionButtons(boolean enabled) {
        cancelButton.setEnabled(enabled);
        confirmButton.setEnabled(enabled);
        concludeButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);

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
            User admin = new User(1, "Admin", "admin@example.com", "admin123", model.UserType.ADMIN);
            new ReservationManagementScreen(admin);
        });
    }
}