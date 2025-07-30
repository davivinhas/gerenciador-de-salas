package view;

import controller.SpaceController;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DeleteSpaceScreen extends JFrame {
    private final User user;
    private SpaceController spaceController;
    private JTable spaceTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JTextField searchField;
    private JComboBox<String> typeFilterComboBox;
    private JButton searchButton, clearFilterButton, refreshButton, deleteButton, viewDetailsButton;
    private Space selectedSpace;

    public DeleteSpaceScreen(User user) {
        this.user = user;
        this.spaceController = new SpaceController();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadSpaces();
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Remover Espaços - Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Campos de filtro
        searchField = new JTextField(20);
        typeFilterComboBox = new JComboBox<>(new String[]{
                "Todos os Tipos", "Sala de Aula", "Laboratório", "Sala de Reunião",
                "Quadra Esportiva", "Auditório"
        });

        // Botões
        searchButton = new JButton("Buscar");
        clearFilterButton = new JButton("Limpar Filtros");
        refreshButton = new JButton("Atualizar Lista");
        deleteButton = new JButton("Remover Espaço");
        viewDetailsButton = new JButton("Ver Detalhes");

        // Configurar cores dos botões
        searchButton.setBackground(new Color(34, 139, 34));
        searchButton.setForeground(Color.WHITE);
        clearFilterButton.setBackground(new Color(255, 165, 0));
        clearFilterButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(128, 128, 128));
        refreshButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        viewDetailsButton.setBackground(new Color(30, 144, 255));
        viewDetailsButton.setForeground(Color.WHITE);

        // Configurar tabela
        String[] columnNames = {"ID", "Nome", "Tipo", "Localização", "Capacidade", "Horários Disponíveis"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        };

        spaceTable = new JTable(tableModel);
        spaceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spaceTable.setRowHeight(25);
        spaceTable.getTableHeader().setBackground(new Color(70, 130, 180));
        spaceTable.getTableHeader().setForeground(Color.WHITE);
        spaceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        spaceTable.setFont(new Font("Arial", Font.PLAIN, 11));

        // Configurar ordenação da tabela
        tableSorter = new TableRowSorter<>(tableModel);
        spaceTable.setRowSorter(tableSorter);

        // Desabilitar botões inicialmente
        deleteButton.setEnabled(false);
        viewDetailsButton.setEnabled(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel superior - Título
        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Remover Espaços do Sistema");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Painel central - Contém filtros e tabela
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Painel de filtros
        centerPanel.add(createFilterPanel(), BorderLayout.NORTH);

        // Tabela com scroll
        JScrollPane scrollPane = new JScrollPane(spaceTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Espaços Cadastrados"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Painel inferior - Botões de ação
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros de Busca"));

        filterPanel.add(new JLabel("Buscar:"));
        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Tipo:"));
        filterPanel.add(typeFilterComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(searchButton);
        filterPanel.add(clearFilterButton);

        return filterPanel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(viewDetailsButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private void setupEventListeners() {
        // Seleção na tabela
        spaceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = spaceTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = spaceTable.convertRowIndexToModel(selectedRow);
                    int spaceId = (Integer) tableModel.getValueAt(modelRow, 0);
                    selectedSpace = spaceController.getSpaceById(spaceId);
                    deleteButton.setEnabled(true);
                    viewDetailsButton.setEnabled(true);
                } else {
                    selectedSpace = null;
                    deleteButton.setEnabled(false);
                    viewDetailsButton.setEnabled(false);
                }
            }
        });

        // Botão Buscar
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });

        // Botão Limpar Filtros
        clearFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFilters();
            }
        });

        // Botão Atualizar Lista
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSpaces();
                clearFilters();
            }
        });

        // Botão Remover Espaço
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSpace();
            }
        });

        // Botão Ver Detalhes
        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSpaceDetails();
            }
        });

        // Permitir busca ao pressionar Enter no campo de busca
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });
    }

    private void loadSpaces() {
        tableModel.setRowCount(0); // Limpar tabela
        List<Space> spaces = spaceController.listAllSpaces();

        if (spaces != null) {
            for (Space space : spaces) {
                Object[] rowData = {
                        space.getId(),
                        space.getName(),
                        space.getType(),
                        space.getLocalization(),
                        space.getCapacity(),
                        space.getAvailableHours()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedLabel = (String) typeFilterComboBox.getSelectedItem();
        String internalType = mapLabelToInternalType(selectedLabel);

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                boolean textMatch = true;
                if (!searchText.isEmpty()) {
                    String name = entry.getStringValue(1).toLowerCase();
                    String location = entry.getStringValue(3).toLowerCase();
                    textMatch = name.contains(searchText) || location.contains(searchText);
                }

                boolean typeMatch = true;
                if (internalType != null) {
                    String spaceType = entry.getStringValue(2);
                    typeMatch = internalType.equals(spaceType);
                }

                return textMatch && typeMatch;
            }
        };

        tableSorter.setRowFilter(rf);
    }

    private String mapLabelToInternalType(String label) {
        switch (label) {
            case "Sala de Aula": return "Classroom";
            case "Laboratório": return "Laboratory";
            case "Sala de Reunião": return "MeetingRoom";
            case "Auditório": return "Auditorium";
            case "Quadra Esportiva": return "SportsField";
            default: return null; // "Todos os Tipos"
        }
    }

    private void clearFilters() {
        searchField.setText("");
        typeFilterComboBox.setSelectedIndex(0);
        tableSorter.setRowFilter(null);
        spaceTable.clearSelection();
        selectedSpace = null;
        deleteButton.setEnabled(false);
        viewDetailsButton.setEnabled(false);
    }

    private void deleteSpace() {
        if (selectedSpace == null) {
            showMessage("Selecione um espaço para remover!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Criar mensagem de confirmação detalhada
        StringBuilder confirmMessage = new StringBuilder();
        confirmMessage.append("Tem certeza que deseja remover o seguinte espaço?\n\n");
        confirmMessage.append("Nome: ").append(selectedSpace.getName()).append("\n");
        confirmMessage.append("Tipo: ").append(selectedSpace.getType()).append("\n");
        confirmMessage.append("Localização: ").append(selectedSpace.getLocalization()).append("\n");
        confirmMessage.append("Capacidade: ").append(selectedSpace.getCapacity()).append(" pessoas\n\n");
        confirmMessage.append("⚠️ ATENÇÃO: Esta ação não pode ser desfeita!\n");
        confirmMessage.append("Todas as reservas associadas a este espaço também serão afetadas.");

        int confirm = JOptionPane.showConfirmDialog(
                this,
                confirmMessage.toString(),
                "Confirmar Remoção do Espaço",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Segunda confirmação para operações críticas
            int finalConfirm = JOptionPane.showConfirmDialog(
                    this,
                    "Esta é sua última chance de cancelar.\n" +
                            "Confirma definitivamente a remoção do espaço '" + selectedSpace.getName() + "'?",
                    "Confirmação Final",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );

            if (finalConfirm == JOptionPane.YES_OPTION) {
                boolean success = spaceController.deleteSpace(selectedSpace);

                if (success) {
                    showMessage(
                            "Espaço '" + selectedSpace.getName() + "' removido com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    loadSpaces(); // Recarregar lista
                    clearFilters(); // Limpar filtros e seleção
                } else {
                    showMessage(
                            "Erro ao remover o espaço!\n" +
                                    "Verifique se não há reservas ativas para este espaço.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    private void showSpaceDetails() {
        if (selectedSpace == null) {
            showMessage("Selecione um espaço para ver os detalhes!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("=== DETALHES DO ESPAÇO ===\n\n");
        details.append("ID: ").append(selectedSpace.getId()).append("\n");
        details.append("Nome: ").append(selectedSpace.getName()).append("\n");
        details.append("Tipo: ").append(selectedSpace.getType()).append("\n");
        details.append("Localização: ").append(selectedSpace.getLocalization()).append("\n");
        details.append("Capacidade: ").append(selectedSpace.getCapacity()).append(" pessoas\n");
        details.append("Horários Disponíveis: ").append(selectedSpace.getAvailableHours()).append("\n");
        details.append("Data de Criação: ").append(selectedSpace.getCreationDate()).append("\n\n");

        details.append("=== CARACTERÍSTICAS ESPECÍFICAS ===\n\n");

        // Adicionar características específicas baseado no tipo
        if (selectedSpace instanceof Classroom) {
            Classroom c = (Classroom) selectedSpace;
            details.append("Número de Quadros: ").append(c.getNumOfBoards()).append("\n");
            details.append("Número de Data Shows: ").append(c.getNumOfDataShows()).append("\n");
        } else if (selectedSpace instanceof Laboratory) {
            Laboratory l = (Laboratory) selectedSpace;
            details.append("Número de Computadores: ").append(l.getNumOfComputers()).append("\n");
            details.append("Tipo de Laboratório: ").append(l.getLabType()).append("\n");
        } else if (selectedSpace instanceof MeetingRooms) {
            MeetingRooms m = (MeetingRooms) selectedSpace;
            details.append("Número de Projetores: ").append(m.getNumOfProjectors()).append("\n");
            details.append("Número de Videoconferências: ").append(m.getNumOfVideoConferences()).append("\n");
        } else if (selectedSpace instanceof SportsField) {
            SportsField s = (SportsField) selectedSpace;
            details.append("Número de Gols: ").append(s.getNumOfGoals()).append("\n");
            details.append("Número de Redes: ").append(s.getNumOfNets()).append("\n");
            details.append("Número de Cestas: ").append(s.getNumOfHoops()).append("\n");
        } else if (selectedSpace instanceof Auditorium) {
            Auditorium a = (Auditorium) selectedSpace;
            details.append("Número de Sistemas de Som: ").append(a.getNumOfSoundSystems()).append("\n");
            details.append("Número de Palcos: ").append(a.getNumOfStages()).append("\n");
        }

        // Criar área de texto com scroll para os detalhes
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 350));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Detalhes do Espaço - " + selectedSpace.getName(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Método para ser chamado a partir do menu principal
    public static void showDeleteSpace(User user) {
        SwingUtilities.invokeLater(() -> new DeleteSpaceScreen(user));
    }

    // Método main para teste
    public static void main(String[] args) {
        // Criar usuário admin para teste
        User admin = new User(1, "Admin", "admin@gmail.com", "admin123", UserType.ADMIN);
        showDeleteSpace(admin);
    }
}