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

public class SpaceListingScreen extends JFrame {
    private SpaceController spaceController;
    private JTable spaceTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JTextField searchField;
    private JComboBox<String> typeFilterComboBox;
    private JButton searchButton, clearFilterButton, refreshButton, viewDetailsButton;
    private Space selectedSpace;

    public SpaceListingScreen() {
        this.spaceController = new SpaceController();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadSpaces();
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Listagem de Espaços");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        searchField = new JTextField(20);
        typeFilterComboBox = new JComboBox<>(new String[]{
                "Todos os Tipos", "Sala de Aula", "Laboratório", "Sala de Reunião",
                "Quadra Esportiva", "Auditório"
        });

        searchButton = new JButton("Buscar");
        clearFilterButton = new JButton("Limpar Filtros");
        refreshButton = new JButton("Atualizar Lista");
        viewDetailsButton = new JButton("Ver Detalhes");

        searchButton.setBackground(new Color(34, 139, 34));
        searchButton.setForeground(Color.WHITE);
        clearFilterButton.setBackground(new Color(255, 165, 0));
        clearFilterButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(128, 128, 128));
        refreshButton.setForeground(Color.WHITE);
        viewDetailsButton.setBackground(new Color(30, 144, 255));
        viewDetailsButton.setForeground(Color.WHITE);

        String[] columnNames = {"ID", "Nome", "Tipo", "Localização", "Capacidade", "Horários Disponíveis"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        spaceTable = new JTable(tableModel);
        spaceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spaceTable.setRowHeight(25);
        spaceTable.getTableHeader().setBackground(new Color(70, 130, 180));
        spaceTable.getTableHeader().setForeground(Color.WHITE);
        spaceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        spaceTable.setFont(new Font("Arial", Font.PLAIN, 11));

        tableSorter = new TableRowSorter<>(tableModel);
        spaceTable.setRowSorter(tableSorter);

        viewDetailsButton.setEnabled(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Listagem de Espaços");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        add(createFilterPanel(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(spaceTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Espaços"));
        add(scrollPane, BorderLayout.CENTER);
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
        panel.add(refreshButton);

        return panel;
    }

    private void setupEventListeners() {
        spaceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = spaceTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = spaceTable.convertRowIndexToModel(selectedRow);
                    int spaceId = (Integer) tableModel.getValueAt(modelRow, 0);
                    selectedSpace = spaceController.getSpaceById(spaceId);
                    viewDetailsButton.setEnabled(true);
                } else {
                    selectedSpace = null;
                    viewDetailsButton.setEnabled(false);
                }
            }
        });

        searchButton.addActionListener(e -> applyFilters());
        clearFilterButton.addActionListener(e -> clearFilters());
        refreshButton.addActionListener(e -> {
            loadSpaces();
            clearFilters();
        });
        viewDetailsButton.addActionListener(e -> showSpaceDetails());
        searchField.addActionListener(e -> applyFilters());
    }

    private void loadSpaces() {
        tableModel.setRowCount(0);
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

    // 🔧 Aqui está a correção
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

    // ✅ Mapeamento de rótulo → tipo interno
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
        viewDetailsButton.setEnabled(false);
    }

    private void showSpaceDetails() {
        if (selectedSpace == null) return;

        StringBuilder details = new StringBuilder();
        details.append("=== DETALHES DO ESPAÇO ===\n\n");
        details.append("ID: ").append(selectedSpace.getId()).append("\n");
        details.append("Nome: ").append(selectedSpace.getName()).append("\n");
        details.append("Tipo: ").append(selectedSpace.getType()).append("\n");
        details.append("Localização: ").append(selectedSpace.getLocalization()).append("\n");
        details.append("Capacidade: ").append(selectedSpace.getCapacity()).append(" pessoas\n");
        details.append("Horários Disponíveis: ").append(selectedSpace.getAvailableHours()).append("\n\n");

        details.append("=== CARACTERÍSTICAS ESPECÍFICAS ===\n\n");

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

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(
                this, scrollPane, "Detalhes do Espaço - " + selectedSpace.getName(), JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showSpaceListing() {
        SwingUtilities.invokeLater(SpaceListingScreen::new);
    }

    public static void main(String[] args) {
        showSpaceListing();
    }
}
