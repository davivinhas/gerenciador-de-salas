package view;

import controller.SpaceController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterSpaceScreen extends JFrame {
    private final User user;
    private SpaceController spaceController;

    // Campos comuns a todos os espaços
    private JTextField nameField, localizationField, capacityField, availableHoursField;
    private JComboBox<String> spaceTypeComboBox;

    // Painel para campos específicos
    private JPanel specificFieldsPanel;
    private CardLayout cardLayout;

    // Campos específicos para Sala de Aula
    private JTextField numBoardsField, numDataShowsField;

    // Campos específicos para Laboratório
    private JTextField numComputersField, labTypeField;

    // Campos específicos para Sala de Reunião
    private JTextField numProjectorsField, numVideoConferencesField;

    // Campos específicos para Quadra Esportiva
    private JTextField numGoalsField, numNetsField, numHoopsField;

    // Campos específicos para Auditório
    private JTextField numSoundSystemsField, numStagesField;

    // Botões
    private JButton registerButton, clearButton, cancelButton;

    public RegisterSpaceScreen(User user) {
        this.user = user;
        this.spaceController = new SpaceController();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Cadastrar Espaço - Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Campos comuns
        nameField = new JTextField(20);
        localizationField = new JTextField(20);
        capacityField = new JTextField(20);
        availableHoursField = new JTextField(20);

        spaceTypeComboBox = new JComboBox<>(new String[]{
                "Selecione um tipo...",
                "Sala de Aula",
                "Laboratório",
                "Sala de Reunião",
                "Quadra Esportiva",
                "Auditório"
        });

        // Campos específicos para cada tipo
        initializeSpecificFields();

        // Botões
        registerButton = new JButton("Cadastrar Espaço");
        clearButton = new JButton("Limpar Campos");
        cancelButton = new JButton("Cancelar");

        // Configurar cores dos botões
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(255, 165, 0));
        clearButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);

        // Configurar painel de campos específicos
        cardLayout = new CardLayout();
        specificFieldsPanel = new JPanel(cardLayout);
        setupSpecificFieldsPanels();
    }

    private void initializeSpecificFields() {
        // Sala de Aula
        numBoardsField = new JTextField(20);
        numDataShowsField = new JTextField(20);

        // Laboratório
        numComputersField = new JTextField(20);
        labTypeField = new JTextField(20);

        // Sala de Reunião
        numProjectorsField = new JTextField(20);
        numVideoConferencesField = new JTextField(20);

        // Quadra Esportiva
        numGoalsField = new JTextField(20);
        numNetsField = new JTextField(20);
        numHoopsField = new JTextField(20);

        // Auditório
        numSoundSystemsField = new JTextField(20);
        numStagesField = new JTextField(20);
    }

    private void setupSpecificFieldsPanels() {
        // Painel vazio (padrão)
        JPanel emptyPanel = new JPanel();
        emptyPanel.add(new JLabel("Selecione um tipo de espaço para ver os campos específicos"));
        specificFieldsPanel.add(emptyPanel, "empty");

        // Painel para Sala de Aula
        JPanel classroomPanel = createClassroomPanel();
        specificFieldsPanel.add(classroomPanel, "Sala de Aula");

        // Painel para Laboratório
        JPanel labPanel = createLaboratoryPanel();
        specificFieldsPanel.add(labPanel, "Laboratório");

        // Painel para Sala de Reunião
        JPanel meetingPanel = createMeetingRoomPanel();
        specificFieldsPanel.add(meetingPanel, "Sala de Reunião");

        // Painel para Quadra Esportiva
        JPanel sportsPanel = createSportsFieldPanel();
        specificFieldsPanel.add(sportsPanel, "Quadra Esportiva");

        // Painel para Auditório
        JPanel auditoriumPanel = createAuditoriumPanel();
        specificFieldsPanel.add(auditoriumPanel, "Auditório");
    }

    private JPanel createClassroomPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Campos Específicos - Sala de Aula"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Número de Quadros:"), gbc);
        gbc.gridx = 1;
        panel.add(numBoardsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Número de Data Shows:"), gbc);
        gbc.gridx = 1;
        panel.add(numDataShowsField, gbc);

        return panel;
    }

    private JPanel createLaboratoryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Campos Específicos - Laboratório"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Número de Computadores:"), gbc);
        gbc.gridx = 1;
        panel.add(numComputersField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tipo de Laboratório:"), gbc);
        gbc.gridx = 1;
        panel.add(labTypeField, gbc);

        return panel;
    }

    private JPanel createMeetingRoomPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Campos Específicos - Sala de Reunião"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Número de Projetores:"), gbc);
        gbc.gridx = 1;
        panel.add(numProjectorsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Número de Videoconferências:"), gbc);
        gbc.gridx = 1;
        panel.add(numVideoConferencesField, gbc);

        return panel;
    }

    private JPanel createSportsFieldPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Campos Específicos - Quadra Esportiva"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Número de Gols:"), gbc);
        gbc.gridx = 1;
        panel.add(numGoalsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Número de Redes:"), gbc);
        gbc.gridx = 1;
        panel.add(numNetsField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Número de Cestas:"), gbc);
        gbc.gridx = 1;
        panel.add(numHoopsField, gbc);

        return panel;
    }

    private JPanel createAuditoriumPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Campos Específicos - Auditório"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Número de Sistemas de Som:"), gbc);
        gbc.gridx = 1;
        panel.add(numSoundSystemsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Número de Palcos:"), gbc);
        gbc.gridx = 1;
        panel.add(numStagesField, gbc);

        return panel;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel superior - Título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Cadastrar Novo Espaço");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Painel central - Formulário
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Campos gerais
        JPanel generalFieldsPanel = createGeneralFieldsPanel();
        mainPanel.add(generalFieldsPanel, BorderLayout.NORTH);

        // Campos específicos
        specificFieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(specificFieldsPanel, BorderLayout.CENTER);

        // Scroll para o painel principal
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior - Botões
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createGeneralFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Informações Gerais"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome do Espaço:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);

        // Localização
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Localização:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(localizationField, gbc);

        // Capacidade
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Capacidade:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(capacityField, gbc);

        // Horários Disponíveis
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Horários Disponíveis:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(availableHoursField, gbc);

        // Tipo de Espaço
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Tipo de Espaço:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(spaceTypeComboBox, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(registerButton);
        panel.add(clearButton);
        panel.add(cancelButton);

        return panel;
    }

    private void setupEventListeners() {
        // ComboBox de tipo de espaço
        spaceTypeComboBox.addActionListener(e -> {
            String selectedType = (String) spaceTypeComboBox.getSelectedItem();
            if (selectedType != null && !selectedType.equals("Selecione um tipo...")) {
                cardLayout.show(specificFieldsPanel, selectedType);
            } else {
                cardLayout.show(specificFieldsPanel, "empty");
            }
            revalidate();
            repaint();
        });

        // Botão Cadastrar
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerSpace();
            }
        });

        // Botão Limpar
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        // Botão Cancelar
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void registerSpace() {
        if (!validateGeneralFields()) {
            return;
        }

        String name = nameField.getText().trim();
        String localization = localizationField.getText().trim();
        int capacity = Integer.parseInt(capacityField.getText().trim());
        String availableHours = availableHoursField.getText().trim();
        String spaceType = (String) spaceTypeComboBox.getSelectedItem();

        boolean success = false;

        try {
            switch (spaceType) {
                case "Sala de Aula":
                    if (!validateSpecificFields(spaceType)) return;
                    int numBoards = Integer.parseInt(numBoardsField.getText().trim());
                    int numDataShows = Integer.parseInt(numDataShowsField.getText().trim());
                    success = spaceController.createClassroom(name, localization, capacity, availableHours, numBoards, numDataShows);
                    break;

                case "Laboratório":
                    if (!validateSpecificFields(spaceType)) return;
                    int numComputers = Integer.parseInt(numComputersField.getText().trim());
                    String labType = labTypeField.getText().trim();
                    success = spaceController.createLaboratory(name, localization, capacity, availableHours, numComputers, labType);
                    break;

                case "Sala de Reunião":
                    if (!validateSpecificFields(spaceType)) return;
                    int numProjectors = Integer.parseInt(numProjectorsField.getText().trim());
                    int numVideoConferences = Integer.parseInt(numVideoConferencesField.getText().trim());
                    success = spaceController.createMeetingRoom(name, localization, capacity, availableHours, numProjectors, numVideoConferences);
                    break;

                case "Quadra Esportiva":
                    if (!validateSpecificFields(spaceType)) return;
                    int numGoals = Integer.parseInt(numGoalsField.getText().trim());
                    int numNets = Integer.parseInt(numNetsField.getText().trim());
                    int numHoops = Integer.parseInt(numHoopsField.getText().trim());
                    success = spaceController.createSportsField(name, localization, capacity, availableHours, numGoals, numNets, numHoops);
                    break;

                case "Auditório":
                    if (!validateSpecificFields(spaceType)) return;
                    int numSoundSystems = Integer.parseInt(numSoundSystemsField.getText().trim());
                    int numStages = Integer.parseInt(numStagesField.getText().trim());
                    success = spaceController.createAuditorium(name, localization, capacity, availableHours, numSoundSystems, numStages);
                    break;

                default:
                    showMessage("Selecione um tipo de espaço válido!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            if (success) {
                showMessage("Espaço cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                showMessage("Erro ao cadastrar espaço!", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            showMessage("Verifique se todos os campos numéricos foram preenchidos corretamente!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateGeneralFields() {
        if (nameField.getText().trim().isEmpty()) {
            showMessage("Nome do espaço é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (localizationField.getText().trim().isEmpty()) {
            showMessage("Localização é obrigatória!", "Erro", JOptionPane.ERROR_MESSAGE);
            localizationField.requestFocus();
            return false;
        }

        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity <= 0) {
                showMessage("Capacidade deve ser um número positivo!", "Erro", JOptionPane.ERROR_MESSAGE);
                capacityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Capacidade deve ser um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            capacityField.requestFocus();
            return false;
        }

        if (availableHoursField.getText().trim().isEmpty()) {
            showMessage("Horários disponíveis são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
            availableHoursField.requestFocus();
            return false;
        }

        String spaceType = (String) spaceTypeComboBox.getSelectedItem();
        if (spaceType == null || spaceType.equals("Selecione um tipo...")) {
            showMessage("Selecione um tipo de espaço!", "Erro", JOptionPane.ERROR_MESSAGE);
            spaceTypeComboBox.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateSpecificFields(String spaceType) {
        try {
            switch (spaceType) {
                case "Sala de Aula":
                    if (numBoardsField.getText().trim().isEmpty() || numDataShowsField.getText().trim().isEmpty()) {
                        showMessage("Todos os campos específicos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    Integer.parseInt(numBoardsField.getText().trim());
                    Integer.parseInt(numDataShowsField.getText().trim());
                    break;

                case "Laboratório":
                    if (numComputersField.getText().trim().isEmpty() || labTypeField.getText().trim().isEmpty()) {
                        showMessage("Todos os campos específicos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    Integer.parseInt(numComputersField.getText().trim());
                    break;

                case "Sala de Reunião":
                    if (numProjectorsField.getText().trim().isEmpty() || numVideoConferencesField.getText().trim().isEmpty()) {
                        showMessage("Todos os campos específicos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    Integer.parseInt(numProjectorsField.getText().trim());
                    Integer.parseInt(numVideoConferencesField.getText().trim());
                    break;

                case "Quadra Esportiva":
                    if (numGoalsField.getText().trim().isEmpty() || numNetsField.getText().trim().isEmpty() || numHoopsField.getText().trim().isEmpty()) {
                        showMessage("Todos os campos específicos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    Integer.parseInt(numGoalsField.getText().trim());
                    Integer.parseInt(numNetsField.getText().trim());
                    Integer.parseInt(numHoopsField.getText().trim());
                    break;

                case "Auditório":
                    if (numSoundSystemsField.getText().trim().isEmpty() || numStagesField.getText().trim().isEmpty()) {
                        showMessage("Todos os campos específicos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    Integer.parseInt(numSoundSystemsField.getText().trim());
                    Integer.parseInt(numStagesField.getText().trim());
                    break;
            }
            return true;
        } catch (NumberFormatException e) {
            showMessage("Verifique se todos os campos numéricos específicos foram preenchidos corretamente!", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void clearFields() {
        // Limpar campos gerais
        nameField.setText("");
        localizationField.setText("");
        capacityField.setText("");
        availableHoursField.setText("");
        spaceTypeComboBox.setSelectedIndex(0);

        // Limpar campos específicos
        numBoardsField.setText("");
        numDataShowsField.setText("");
        numComputersField.setText("");
        labTypeField.setText("");
        numProjectorsField.setText("");
        numVideoConferencesField.setText("");
        numGoalsField.setText("");
        numNetsField.setText("");
        numHoopsField.setText("");
        numSoundSystemsField.setText("");
        numStagesField.setText("");

        // Voltar para o painel vazio
        cardLayout.show(specificFieldsPanel, "empty");
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Método para ser chamado a partir do menu principal
    public static void showRegisterSpace(User user) {
        SwingUtilities.invokeLater(() -> new RegisterSpaceScreen(user));
    }

    public static void main(String[] args){
        showRegisterSpace(new User());
    }
}
