package server;

import server.enums.Difficulty;
import server.enums.Gamemode;
import server.schemas.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.Arrays;

public class Main extends JFrame {
    public static String URL;
    public static String SECRET;
    Server server;
    private static Main instance;

    // --- Models ---
    private DefaultListModel<Player> onlinePlayersModel;
    private DefaultListModel<UserBan> bannedPlayersModel;
    private DefaultListModel<IPBan> ipBansModel;
    private DefaultListModel<Operator> operatorsModel;
    private DefaultListModel<Player> whitelistModel;
    private DefaultTableModel gameRulesModel;

    // --- JLists ---
    private JList<Player> onlinePlayersList;
    private JList<UserBan> bannedPlayersList;
    private JList<IPBan> ipBansList;
    private JList<Operator> operatorsList;
    private JList<Player> whitelistList;

    // --- JTable ---
    private JTable gameRulesTable;

    // --- Buttons ---
    private JButton refreshButton;
    private JButton banButton;
    private JButton unbanButton;
    private JButton kickButton;
    private JButton ipBanButton;
    private JButton opButton;
    private JButton deopButton;
    private JButton addWhitelistButton;
    private JButton addOPButton;
    private JButton addBanButton;
    private JButton addIPBanButton;
    private JButton removeWhitelistButton;
    private JButton removeIpBanButton;
    private JButton updateGameruleButton;
    private JButton sendSystemMessageButton;
    private JButton saveButton; // <-- ADDED
    private JButton stopButton; // <-- ADDED
    private JButton setMaxPlayersButton, setPauseEmptyButton, setIdleTimeoutButton, setMotdButton, setSpawnProtectButton;
    private JButton setViewDistButton, setSimDistButton, setHeartbeatButton, setOpLevelButton, setEntityRangeButton;
    private JButton setDifficultyButton, setGamemodeButton;


    // --- TextFields ---
    private JTextField addWhitelistField;
    private JTextField addOPField;
    private JTextField addBanField;
    private JTextField addIPBanField;
    private JTextField sendSystemMessageField;
    private JTextField maxPlayersField, pauseEmptyField, idleTimeoutField, motdField, spawnProtectField;
    private JTextField viewDistanceField, simDistanceField, heartbeatField, opLevelField, entityRangeField;

    // --- Labels (for current values) ---
    private JLabel maxPlayersLabel, pauseEmptyLabel, idleTimeoutLabel, motdLabel, spawnProtectLabel;
    private JLabel viewDistanceLabel, simDistanceLabel, heartbeatLabel, opLevelLabel, entityRangeLabel;

    // --- CheckBoxes ---
    private JCheckBox autosaveCheck, enforceAllowlistCheck, useAllowlistCheck, allowFlightCheck;
    private JCheckBox forceGamemodeCheck, acceptTransfersCheck, hidePlayersCheck, statusRepliesCheck;

    // --- ComboBoxes ---
    private JComboBox<Difficulty> difficultyCombo;
    private JComboBox<Gamemode> gamemodeCombo;


    public static Main getInstance() {
        return instance;
    }

    public Main() {
        server = new Server();
        instance = this;

        // --- Frame Setup ---
        setTitle("Server Admin Manager");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top Panel (for Refresh) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshButton = new JButton("Refresh All Lists");
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        // --- Main Tabbed Pane ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Players", createPlayerPanel());
        tabbedPane.addTab("Lists (Whitelist, Ops, IP Bans)", createListsPanel());
        tabbedPane.addTab("Gamerules", createGameRulesPanel());
        tabbedPane.addTab("Settings", createSettingsPanel());
        tabbedPane.addTab("Server", createServerActionsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // --- Add Event Listeners ---
        addListeners();

        // --- Initial Data Load ---
        refreshLists();
    }

    // =================================================================================
    // --- PANEL CREATION ---
    // =================================================================================

    /**
     * Creates the main "Players" tab with online players and banned players.
     */
    private JPanel createPlayerPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Left Panel (Online Players) ---
        JPanel onlinePanel = new JPanel(new BorderLayout());
        onlinePanel.add(new JLabel("Online Players", SwingConstants.CENTER), BorderLayout.NORTH);

        onlinePlayersModel = new DefaultListModel<>();
        onlinePlayersList = new JList<>(onlinePlayersModel);
        onlinePlayersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        onlinePanel.add(new JScrollPane(onlinePlayersList), BorderLayout.CENTER);

        // Action buttons for online players
        JPanel onlineActionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        kickButton = new JButton("Kick Player");
        banButton = new JButton("Ban Player");
        ipBanButton = new JButton("IP Ban Player");
        opButton = new JButton("OP Player");
        onlineActionPanel.add(kickButton);
        onlineActionPanel.add(banButton);
        onlineActionPanel.add(ipBanButton);
        onlineActionPanel.add(opButton);
        onlinePanel.add(onlineActionPanel, BorderLayout.SOUTH);

        // --- Right Panel (Banned Players) ---
        JPanel bannedPanel = new JPanel(new BorderLayout());
        bannedPanel.add(new JLabel("Banned Players (User)", SwingConstants.CENTER), BorderLayout.NORTH);

        bannedPlayersModel = new DefaultListModel<>();
        bannedPlayersList = new JList<>(bannedPlayersModel);
        bannedPlayersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bannedPanel.add(new JScrollPane(bannedPlayersList), BorderLayout.CENTER);
        JPanel banActionPanel = new JPanel(new BorderLayout());
        addBanField = new JTextField();
        addBanButton = new JButton("Add by Name");
        unbanButton = new JButton("Unban Selected Player");
        banActionPanel.add(addBanField,BorderLayout.NORTH);
        banActionPanel.add(addBanButton,BorderLayout.CENTER);
        banActionPanel.add(unbanButton, BorderLayout.SOUTH);
        bannedPanel.add(banActionPanel,BorderLayout.SOUTH);

        mainPanel.add(onlinePanel);
        mainPanel.add(bannedPanel);
        return mainPanel;
    }

    /**
     * Creates the "Lists" tab for Whitelist, Operators, and IP Bans.
     */
    private JPanel createListsPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Whitelist Panel ---
        JPanel whitelistPanel = new JPanel(new BorderLayout());
        whitelistPanel.add(new JLabel("Whitelist", SwingConstants.CENTER), BorderLayout.NORTH);
        whitelistModel = new DefaultListModel<>();
        whitelistList = new JList<>(whitelistModel);
        whitelistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        whitelistPanel.add(new JScrollPane(whitelistList), BorderLayout.CENTER);

        JPanel whitelistActionPanel = new JPanel(new BorderLayout());
        addWhitelistField = new JTextField();
        addWhitelistButton = new JButton("Add by Name");
        removeWhitelistButton = new JButton("Remove Selected");
        whitelistActionPanel.add(addWhitelistField, BorderLayout.NORTH);
        whitelistActionPanel.add(addWhitelistButton, BorderLayout.CENTER);
        whitelistActionPanel.add(removeWhitelistButton, BorderLayout.SOUTH);
        whitelistPanel.add(whitelistActionPanel, BorderLayout.SOUTH);

        // --- Operators Panel ---
        JPanel operatorsPanel = new JPanel(new BorderLayout());
        operatorsPanel.add(new JLabel("Operators", SwingConstants.CENTER), BorderLayout.NORTH);
        operatorsModel = new DefaultListModel<>();
        operatorsList = new JList<>(operatorsModel);
        operatorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        operatorsPanel.add(new JScrollPane(operatorsList), BorderLayout.CENTER);
        JPanel opActionPanel = new JPanel(new BorderLayout());
        addOPField = new JTextField();
        addOPButton = new JButton("Add by Name");
        deopButton = new JButton("De-OP Selected");
        opActionPanel.add(addOPField,BorderLayout.NORTH);
        opActionPanel.add(addOPButton,BorderLayout.CENTER);
        opActionPanel.add(deopButton, BorderLayout.SOUTH);
        operatorsPanel.add(opActionPanel,BorderLayout.SOUTH);

        // --- IP Bans Panel ---
        JPanel ipBansPanel = new JPanel(new BorderLayout());
        ipBansPanel.add(new JLabel("Banned IPs", SwingConstants.CENTER), BorderLayout.NORTH);
        ipBansModel = new DefaultListModel<>();
        ipBansList = new JList<>(ipBansModel);
        ipBansList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ipBansPanel.add(new JScrollPane(ipBansList), BorderLayout.CENTER);
        JPanel ipActionPanel = new JPanel(new BorderLayout());
        addIPBanField = new JTextField();
        addIPBanButton = new JButton("Add by IP");
        removeIpBanButton = new JButton("Remove Selected IP Ban");
        ipActionPanel.add(addIPBanField,BorderLayout.NORTH);
        ipActionPanel.add(addIPBanButton,BorderLayout.CENTER);
        ipActionPanel.add(removeIpBanButton,BorderLayout.SOUTH);
        ipBansPanel.add(ipActionPanel, BorderLayout.SOUTH);

        mainPanel.add(whitelistPanel);
        mainPanel.add(operatorsPanel);
        mainPanel.add(ipBansPanel);
        return mainPanel;
    }

    /**
     * Creates the "Gamerules" tab.
     */
    private JPanel createGameRulesPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(new JLabel("Gamerules", SwingConstants.CENTER), BorderLayout.NORTH);

        gameRulesModel = new DefaultTableModel(new String[]{"Key", "Value", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Not editable directly
            }
        };
        gameRulesTable = new JTable(gameRulesModel);
        mainPanel.add(new JScrollPane(gameRulesTable), BorderLayout.CENTER);

        updateGameruleButton = new JButton("Update Selected Gamerule");
        mainPanel.add(updateGameruleButton, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Creates the "Settings" tab with all 20 settings.
     */
    private JPanel createSettingsPanel() {
        // Main panel with GridBagLayout, wrapped in a JScrollPane
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        int gridY = 0;

        // --- Headers ---
        c.gridx = 0;
        c.gridy = gridY;
        c.weightx = 0.4;
        gridPanel.add(new JLabel("Setting"), c);
        c.gridx = 1;
        c.weightx = 0.2;
        gridPanel.add(new JLabel("Current Value"), c);
        c.gridx = 2;
        c.weightx = 0.2;
        gridPanel.add(new JLabel("New Value"), c);
        c.gridx = 3;
        c.weightx = 0.2;
        gridPanel.add(new JLabel("Action"), c);
        gridY++;

        // --- Separator ---
        c.gridy = gridY++;
        c.gridx = 0;
        c.gridwidth = 4;
        gridPanel.add(new JSeparator(), c);
        c.gridwidth = 1;

        // --- Boolean Checkboxes ---
        autosaveCheck = new JCheckBox("Autosave");
        enforceAllowlistCheck = new JCheckBox("Enforce Allowlist");
        useAllowlistCheck = new JCheckBox("Use Allowlist");
        allowFlightCheck = new JCheckBox("Allow Flight (Survival)");
        forceGamemodeCheck = new JCheckBox("Force Gamemode");
        acceptTransfersCheck = new JCheckBox("Accept Transfers");
        hidePlayersCheck = new JCheckBox("Hide Online Players");
        statusRepliesCheck = new JCheckBox("Respond to Status");

        gridY = addCheckboxSetting(gridPanel, autosaveCheck, gridY);
        gridY = addCheckboxSetting(gridPanel, enforceAllowlistCheck, gridY);
        gridY = addCheckboxSetting(gridPanel, useAllowlistCheck, gridY);
        gridY = addCheckboxSetting(gridPanel, allowFlightCheck, gridY);
        gridY = addCheckboxSetting(gridPanel, forceGamemodeCheck, gridY);
        gridY = addCheckboxSetting(gridPanel, acceptTransfersCheck, gridY);
        gridY = addCheckboxSetting(gridPanel, hidePlayersCheck, gridY);
        gridY = addCheckboxSetting(gridPanel, statusRepliesCheck, gridY);

        // --- Separator ---
        c.gridy = gridY++;
        c.gridx = 0;
        c.gridwidth = 4;
        gridPanel.add(new JSeparator(), c);
        c.gridwidth = 1;

        // --- Enum ComboBoxes ---
        difficultyCombo = new JComboBox<>(Difficulty.values());
        setDifficultyButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Difficulty", difficultyCombo, setDifficultyButton, gridY);

        gamemodeCombo = new JComboBox<>(Gamemode.values());
        setGamemodeButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Default Gamemode", gamemodeCombo, setGamemodeButton, gridY);

        // --- Separator ---
        c.gridy = gridY++;
        c.gridx = 0;
        c.gridwidth = 4;
        gridPanel.add(new JSeparator(), c);
        c.gridwidth = 1;

        // --- Integer/String Fields ---
        maxPlayersLabel = new JLabel("loading...");
        maxPlayersField = new JTextField(5);
        setMaxPlayersButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Max Players", maxPlayersLabel, maxPlayersField, setMaxPlayersButton, gridY);

        pauseEmptyLabel = new JLabel("loading...");
        pauseEmptyField = new JTextField(5);
        setPauseEmptyButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Pause When Empty (sec)", pauseEmptyLabel, pauseEmptyField, setPauseEmptyButton, gridY);

        idleTimeoutLabel = new JLabel("loading...");
        idleTimeoutField = new JTextField(5);
        setIdleTimeoutButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Player Idle Timeout (sec)", idleTimeoutLabel, idleTimeoutField, setIdleTimeoutButton, gridY);

        spawnProtectLabel = new JLabel("loading...");
        spawnProtectField = new JTextField(5);
        setSpawnProtectButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Spawn Protection", spawnProtectLabel, spawnProtectField, setSpawnProtectButton, gridY);

        opLevelLabel = new JLabel("loading...");
        opLevelField = new JTextField(5);
        setOpLevelButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Operator Permission Level", opLevelLabel, opLevelField, setOpLevelButton, gridY);

        viewDistanceLabel = new JLabel("loading...");
        viewDistanceField = new JTextField(5);
        setViewDistButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "View Distance", viewDistanceLabel, viewDistanceField, setViewDistButton, gridY);

        simDistanceLabel = new JLabel("loading...");
        simDistanceField = new JTextField(5);
        setSimDistButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Simulation Distance", simDistanceLabel, simDistanceField, setSimDistButton, gridY);

        entityRangeLabel = new JLabel("loading...");
        entityRangeField = new JTextField(5);
        setEntityRangeButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Entity Broadcast Range %", entityRangeLabel, entityRangeField, setEntityRangeButton, gridY);

        heartbeatLabel = new JLabel("loading...");
        heartbeatField = new JTextField(5);
        setHeartbeatButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "Status Heartbeat (sec)", heartbeatLabel, heartbeatField, setHeartbeatButton, gridY);

        motdLabel = new JLabel("loading...");
        motdField = new JTextField(15);
        setMotdButton = new JButton("Set");
        gridY = addSettingRow(gridPanel, "MOTD", motdLabel, motdField, setMotdButton, gridY);

        // Add a spacer panel to push everything up
        c.gridy = gridY;
        c.gridx = 0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        gridPanel.add(new JPanel(), c);

        return mainPanel;
    }

    /** Helper to add a JCheckbox setting row */
    private int addCheckboxSetting(JPanel panel, JCheckBox checkBox, int gridY) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = gridY;

        c.gridx = 0;
        c.gridwidth = 4;
        panel.add(checkBox, c);
        c.gridwidth = 1;
        return gridY + 1;
    }

    /** Helper to add a JComboBox setting row */
    private int addSettingRow(JPanel panel, String label, JComboBox<?> combo, JButton button, int gridY) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = gridY;

        c.gridx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.gridwidth = 2;
        panel.add(combo, c);
        c.gridwidth = 1;
        c.gridx = 3;
        panel.add(button, c);
        return gridY + 1;
    }

    /** Helper to add a JTextField (int/String) setting row */
    private int addSettingRow(JPanel panel, String label, JLabel currentLabel, JTextField field, JButton button, int gridY) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = gridY;

        c.gridx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        panel.add(currentLabel, c);
        c.gridx = 2;
        panel.add(field, c);
        c.gridx = 3;
        panel.add(button, c);
        return gridY + 1;
    }


    /**
     * Creates the "Server Actions" tab.
     */
    private JPanel createServerActionsPanel() {
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("System Message (sends to all):"));
        sendSystemMessageField = new JTextField(40);
        mainPanel.add(sendSystemMessageField);
        sendSystemMessageButton = new JButton("Send");
        mainPanel.add(sendSystemMessageButton);

        // --- ADDED SAVE/STOP BUTTONS ---
        saveButton = new JButton("Save Server (flush)");
        mainPanel.add(saveButton);

        stopButton = new JButton("Stop Server");
        stopButton.setForeground(Color.RED);
        mainPanel.add(stopButton);
        // --- END OF ADDED ---

        return mainPanel;
    }


    // =================================================================================
    // --- EVENT LISTENERS ---
    // =================================================================================

    private void addListeners() {
        // --- Global ---
        refreshButton.addActionListener(e -> refreshLists());

        // --- Player Tab ---
        banButton.addActionListener(e -> banSelectedPlayer());
        unbanButton.addActionListener(e -> unbanSelectedPlayer());
        kickButton.addActionListener(e -> kickSelectedPlayer());
        ipBanButton.addActionListener(e -> ipBanSelectedPlayer());
        opButton.addActionListener(e -> opSelectedPlayer());
        addBanButton.addActionListener(e -> addBanPlayer());

        // --- Lists Tab ---
        addWhitelistButton.addActionListener(e -> addWhitelistPlayer());
        addOPButton.addActionListener(e -> addOPPlayer());
        addIPBanButton.addActionListener(e -> addIPBanPlayer());
        removeWhitelistButton.addActionListener(e -> removeSelectedWhitelistPlayer());
        deopButton.addActionListener(e -> deopSelectedPlayer());
        removeIpBanButton.addActionListener(e -> removeSelectedIpBan());

        // --- Gamerules Tab ---
        updateGameruleButton.addActionListener(e -> updateSelectedGameRule());

        // --- Server Tab ---
        sendSystemMessageButton.addActionListener(e -> sendSystemMessage());
        saveButton.addActionListener(e -> saveServer()); // <-- ADDED
        stopButton.addActionListener(e -> stopServerWithConfirm()); // <-- ADDED

        // --- Settings Tab ---
        addSettingsListeners();
    }

    private void addSettingsListeners() {
        // Checkboxes
        autosaveCheck.addActionListener(e -> setAutosave());
        enforceAllowlistCheck.addActionListener(e -> setEnforceAllowlist());
        useAllowlistCheck.addActionListener(e -> setUseAllowlist());
        allowFlightCheck.addActionListener(e -> setAllowFlight());
        forceGamemodeCheck.addActionListener(e -> setForceGamemode());
        acceptTransfersCheck.addActionListener(e -> setAcceptTransfers());
        hidePlayersCheck.addActionListener(e -> setHideOnlinePlayers());
        statusRepliesCheck.addActionListener(e -> setStatusReplies());

        // Combo Boxes
        setDifficultyButton.addActionListener(e -> setDifficulty());
        setGamemodeButton.addActionListener(e -> setGamemode());

        // Text Fields
        setMaxPlayersButton.addActionListener(e -> setMaxPlayers());
        setPauseEmptyButton.addActionListener(e -> setPauseWhenEmptySeconds());
        setIdleTimeoutButton.addActionListener(e -> setPlayerIdleTimeout());
        setMotdButton.addActionListener(e -> setMotd());
        setSpawnProtectButton.addActionListener(e -> setSpawnProtectionRadius());
        setViewDistButton.addActionListener(e -> setViewDistance());
        setSimDistButton.addActionListener(e -> setSimulationDistance());
        setHeartbeatButton.addActionListener(e -> setStatusHeartbeatInterval());
        setOpLevelButton.addActionListener(e -> setOperatorUserPermissionLevel());
        setEntityRangeButton.addActionListener(e -> setEntityBroadcastRange());
    }

    // =================================================================================
    // --- CORE ACTIONS ---
    // =================================================================================

    /**
     * Fetches fresh data from the server and updates all lists.
     * This version only clears a list if the corresponding network call succeeds,
     * preventing timeouts from wiping the UI.
     */
    public void refreshLists() {
        System.out.println("Refreshing all lists...");

        // --- Get Online Players ---
        new Thread(() -> {
            Player[] players = server.players.getPlayers();
            SwingUtilities.invokeLater(() -> {
                if (players != null) {
                    onlinePlayersModel.clear(); // <-- Only clear on success
                    for (Player p : players) {
                        onlinePlayersModel.addElement(p);
                    }
                    System.out.println("Refreshed Online Players.");
                } else {
                    System.err.println("Failed to refresh Online Players (timeout or error).");
                }
            });
        }).start();

        // --- Get Banned Players ---
        new Thread(() -> {
            UserBan[] banList = server.bans.getBanlist();
            SwingUtilities.invokeLater(() -> {
                if (banList != null) {
                    bannedPlayersModel.clear(); // <-- Only clear on success
                    for (UserBan b : banList) {
                        bannedPlayersModel.addElement(b);
                    }
                    System.out.println("Refreshed Banned Players.");
                } else {
                    System.err.println("Failed to refresh Banned Players (timeout or error).");
                }
            });
        }).start();

        // --- Get Whitelist ---
        new Thread(() -> {
            Player[] whitelist = server.allowlist.getAllowlist();
            SwingUtilities.invokeLater(() -> {
                if (whitelist != null) {
                    whitelistModel.clear(); // <-- Only clear on success
                    for (Player p : whitelist) {
                        whitelistModel.addElement(p);
                    }
                    System.out.println("Refreshed Whitelist.");
                } else {
                    System.err.println("Failed to refresh Whitelist (timeout or error).");
                }
            });
        }).start();

        // --- Get Operators ---
        new Thread(() -> {
            Operator[] operators = server.operators.getOperators();
            SwingUtilities.invokeLater(() -> {
                if (operators != null) {
                    operatorsModel.clear(); // <-- Only clear on success
                    for (Operator o : operators) {
                        operatorsModel.addElement(o);
                    }
                    System.out.println("Refreshed Operators.");
                } else {
                    System.err.println("Failed to refresh Operators (timeout or error).");
                }
            });
        }).start();

        // --- Get IP Bans ---
        new Thread(() -> {
            IPBan[] ipBans = server.ipBans.getIpBans();
            SwingUtilities.invokeLater(() -> {
                if (ipBans != null) {
                    ipBansModel.clear(); // <-- Only clear on success
                    for (IPBan ipBan : ipBans) {
                        ipBansModel.addElement(ipBan);
                    }
                    System.out.println("Refreshed IP Bans.");
                } else {
                    System.err.println("Failed to refresh IP Bans (timeout or error).");
                }
            });
        }).start();

        // --- Get Gamerules ---
        new Thread(() -> {
            TypedGameRule[] gameRules = server.gamerules.getGameRules();
            SwingUtilities.invokeLater(() -> {
                if (gameRules != null) {
                    gameRulesModel.setRowCount(0); // Clear table
                    for (TypedGameRule rule : gameRules) {
                        gameRulesModel.addRow(new Object[]{rule.key, rule.value, rule.type});
                    }
                    System.out.println("Refreshed Gamerules.");
                } else {
                    System.err.println("Failed to refresh Gamerules (timeout or error).");
                }
            });
        }).start();

        // --- Get Server Settings ---
        refreshServerSettings();
    }

    /**
     * Refreshes all values in the settings panel.
     */
    private void refreshServerSettings() {
        new Thread(() -> {
            // Get all values from server in the background
            // Booleans
            boolean autosave = server.serverSettings.isAutosave();
            boolean enforce = server.serverSettings.isEnforceAllowlist();
            boolean use = server.serverSettings.isUseAllowlist();
            boolean flight = server.serverSettings.isAllowFlight();
            boolean forceGm = server.serverSettings.isForceGamemode();
            boolean transfers = server.serverSettings.isAcceptTransfers();
            boolean hide = server.serverSettings.isHideOnlinePlayers();
            boolean replies = server.serverSettings.isStatusReplies();

            // Enums
            Difficulty diff = server.serverSettings.getDifficulty();
            Gamemode gm = server.serverSettings.getGamemode();

            // Ints/Strings
            int max = server.serverSettings.getMaxPlayers();
            int pause = server.serverSettings.getPauseWhenEmptySeconds();
            int idle = server.serverSettings.getPlayerIdleTimeout();
            int spawn = server.serverSettings.getSpawnProtectionRadius();
            int op = server.serverSettings.getOperatorUserPermissionLevel();
            int view = server.serverSettings.getViewDistance();
            int sim = server.serverSettings.getSimulationDistance();
            int entity = server.serverSettings.getEntityBroadcastRange();
            int heart = server.serverSettings.getStatusHeartbeatInterval();
            String motd = server.serverSettings.getMotd();

            // Update UI on the main thread
            SwingUtilities.invokeLater(() -> {
                // Booleans
                autosaveCheck.setSelected(autosave);
                enforceAllowlistCheck.setSelected(enforce);
                useAllowlistCheck.setSelected(use);
                allowFlightCheck.setSelected(flight);
                forceGamemodeCheck.setSelected(forceGm);
                acceptTransfersCheck.setSelected(transfers);
                hidePlayersCheck.setSelected(hide);
                statusRepliesCheck.setSelected(replies);

                // Enums
                difficultyCombo.setSelectedItem(diff);
                gamemodeCombo.setSelectedItem(gm);

                // Ints/Strings
                maxPlayersLabel.setText(String.valueOf(max));
                pauseEmptyLabel.setText(String.valueOf(pause));
                idleTimeoutLabel.setText(String.valueOf(idle));
                spawnProtectLabel.setText(String.valueOf(spawn));
                opLevelLabel.setText(String.valueOf(op));
                viewDistanceLabel.setText(String.valueOf(view));
                simDistanceLabel.setText(String.valueOf(sim));
                entityRangeLabel.setText(String.valueOf(entity));
                heartbeatLabel.setText(String.valueOf(heart));
                motdLabel.setText(motd);

                System.out.println("Refreshed Server Settings.");
            });
        }).start();
    }

    // =================================================================================
    // --- ACTION HANDLERS (Players, Lists, Gamerules, Server) ---
    // =================================================================================

    /**
     * Handles banning a selected online player.
     */
    private void banSelectedPlayer() {
        Player selectedPlayer = onlinePlayersList.getSelectedValue();
        if (selectedPlayer == null) {
            JOptionPane.showMessageDialog(this, "Please select an online player to ban.", "No Player Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(this, "Enter ban reason for " + selectedPlayer.name + ":", "Ban Player", JOptionPane.QUESTION_MESSAGE);
        if (reason == null) return; // User cancelled

        UserBan newBan = new UserBan(reason, null, "AdminGUI", selectedPlayer);
        new Thread(() -> {
            boolean success = server.bans.addPlayers(new UserBan[]{newBan});
            System.out.println("Ban success: " + success);
            refreshLists();
        }).start();
    }

    /**
     * Handles unbanning a selected player.
     */
    private void unbanSelectedPlayer() {
        UserBan selectedBan = bannedPlayersList.getSelectedValue();
        if (selectedBan == null) {
            JOptionPane.showMessageDialog(this, "Please select a banned player to unban.", "No Player Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new Thread(() -> {
            boolean success = server.bans.removePlayers(new Player[]{selectedBan.player});
            System.out.println("Unban success: " + success);
            refreshLists();
        }).start();
    }

    /**
     * Handles kicking a selected online player.
     */
    private void kickSelectedPlayer() {
        Player selectedPlayer = onlinePlayersList.getSelectedValue();
        if (selectedPlayer == null) {
            JOptionPane.showMessageDialog(this, "Please select an online player to kick.", "No Player Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(this, "Enter kick reason for " + selectedPlayer.name + ":", "Kick Player", JOptionPane.QUESTION_MESSAGE);
        if (reason == null) return; // User cancelled

        Message message = new Message(null, null, reason); // Simple literal message
        KickPlayer kick = new KickPlayer(selectedPlayer, message);
        new Thread(() -> {
            boolean success = server.players.kickPlayers(new KickPlayer[]{kick});
            System.out.println("Kick success: " + success);
            refreshLists(); // Refresh lists as they will be kicked
        }).start();
    }

    /**
     * Handles IP banning a selected online player.
     */
    private void ipBanSelectedPlayer() {
        Player selectedPlayer = onlinePlayersList.getSelectedValue();
        if (selectedPlayer == null) {
            JOptionPane.showMessageDialog(this, "Please select an online player to IP ban.", "No Player Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(this, "Enter IP ban reason for " + selectedPlayer.name + ":", "IP Ban Player", JOptionPane.QUESTION_MESSAGE);
        if (reason == null) return; // User cancelled

        // Per user note, can IP ban with just player object
        IncomingIPBan newIpBan = new IncomingIPBan(null, selectedPlayer, reason, "AdminGUI", null);
        new Thread(() -> {
            boolean success = server.ipBans.addPlayers(new IncomingIPBan[]{newIpBan});
            System.out.println("IP Ban success: " + success);
            refreshLists(); // Refresh lists as they will be kicked
        }).start();
    }

    /**
     * Handles OP'ing a selected online player.
     */
    private void opSelectedPlayer() {
        Player selectedPlayer = onlinePlayersList.getSelectedValue();
        if (selectedPlayer == null) {
            JOptionPane.showMessageDialog(this, "Please select an online player to OP.", "No Player Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get OP Level
        Integer opLevel = null;
        while (opLevel == null) {
            String levelStr = JOptionPane.showInputDialog(this, "Enter OP permission level (1-4) for " + selectedPlayer.name + ":", "OP Player", JOptionPane.QUESTION_MESSAGE);
            if (levelStr == null) return; // User cancelled
            try {
                opLevel = Integer.parseInt(levelStr);
                if (opLevel < 1 || opLevel > 4) {
                    JOptionPane.showMessageDialog(this, "Please enter a number between 1 and 4.", "Invalid Level", JOptionPane.WARNING_MESSAGE);
                    opLevel = null;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        }

        // Get Bypass Player Limit
        int bypassResult = JOptionPane.showConfirmDialog(this, "Bypass player limit?", "OP Player", JOptionPane.YES_NO_OPTION);
        if (bypassResult == JOptionPane.CLOSED_OPTION) return; // User cancelled
        boolean bypass = (bypassResult == JOptionPane.YES_OPTION);

        Operator newOp = new Operator(opLevel, bypass, selectedPlayer);
        new Thread(() -> {
            boolean success = server.operators.addOperators(new Operator[]{newOp});
            System.out.println("OP success: " + success);
            refreshLists();
        }).start();
    }

    /**
     * Handles adding a player to the banlist by name.
     */
    private void addBanPlayer() {
        String name = addBanField.getText();
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a player name to add.", "No Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Player player = new Player(name.trim(), null); // ID can be null

        String reason = JOptionPane.showInputDialog(this, "Enter ban reason for " + player.name + ":", "Ban Player", JOptionPane.QUESTION_MESSAGE);
        if (reason == null) return; // User cancelled

        UserBan newBan = new UserBan(reason, null, "AdminGUI", player);
        new Thread(() -> {
            boolean success = server.bans.addPlayers(new UserBan[]{newBan});
            System.out.println("Ban success: " + success);
            if (success) {
                SwingUtilities.invokeLater(() -> addBanField.setText(""));
            }
            refreshLists();
        }).start();
    }

    /**
     * Handles adding a player to the whitelist by name.
     */
    private void addWhitelistPlayer() {
        String name = addWhitelistField.getText();
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a player name to add.", "No Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Player player = new Player(name.trim(), null); // ID can be null
        new Thread(() -> {
            boolean success = server.allowlist.addPlayers(new Player[]{player});
            System.out.println("Whitelist add success: " + success);
            if (success) {
                SwingUtilities.invokeLater(() -> addWhitelistField.setText(""));
            }
            refreshLists();
        }).start();
    }

    /**
     * Handles adding a player to the whitelist by name.
     */
    private void addOPPlayer() {
        String name = addOPField.getText();
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a player name to add.", "No Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Player player = new Player(name.trim(), null); // ID can be null


        // Get OP Level
        Integer opLevel = null;
        while (opLevel == null) {
            String levelStr = JOptionPane.showInputDialog(this, "Enter OP permission level (1-4) for " + player.name + ":", "OP Player", JOptionPane.QUESTION_MESSAGE);
            if (levelStr == null) return; // User cancelled
            try {
                opLevel = Integer.parseInt(levelStr);
                if (opLevel < 1 || opLevel > 4) {
                    JOptionPane.showMessageDialog(this, "Please enter a number between 1 and 4.", "Invalid Level", JOptionPane.WARNING_MESSAGE);
                    opLevel = null;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        }

        // Get Bypass Player Limit
        int bypassResult = JOptionPane.showConfirmDialog(this, "Bypass player limit?", "OP Player", JOptionPane.YES_NO_OPTION);
        if (bypassResult == JOptionPane.CLOSED_OPTION) return; // User cancelled
        boolean bypass = (bypassResult == JOptionPane.YES_OPTION);

        Operator newOp = new Operator(opLevel, bypass, player);
        new Thread(() -> {
            boolean success = server.operators.addOperators(new Operator[]{newOp});
            System.out.println("OP success: " + success);
            if (success) {
                SwingUtilities.invokeLater(() -> addOPField.setText(""));
            }
            refreshLists();
        }).start();
    }

    /**
     * Handles adding a player to the whitelist by name.
     */
    private void addIPBanPlayer() {
        String name = addIPBanField.getText();
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an ip to add.", "No IP", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ip = name.trim(); // ID can be null


        String reason = JOptionPane.showInputDialog(this, "Enter IP ban reason for " + ip + ":", "IP Ban", JOptionPane.QUESTION_MESSAGE);
        if (reason == null) return; // User cancelled

        // Per user note, can IP ban with just player object
        IncomingIPBan newIpBan = new IncomingIPBan(ip, null, reason, "AdminGUI", null);
        new Thread(() -> {
            boolean success = server.ipBans.addPlayers(new IncomingIPBan[]{newIpBan});
            System.out.println("IP Ban success: " + success);
            if (success) {
                SwingUtilities.invokeLater(() -> addIPBanField.setText(""));
            }
            refreshLists(); // Refresh lists as they will be kicked
        }).start();
    }

    /**
     * Handles removing a selected player from the whitelist.
     */
    private void removeSelectedWhitelistPlayer() {
        Player selectedPlayer = whitelistList.getSelectedValue();
        if (selectedPlayer == null) {
            JOptionPane.showMessageDialog(this, "Please select a player from the whitelist to remove.", "No Player Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new Thread(() -> {
            boolean success = server.allowlist.removePlayers(new Player[]{selectedPlayer});
            System.out.println("Whitelist remove success: " + success);
            refreshLists();
        }).start();
    }

    /**
     * Handles de-OP'ing a selected operator.
     */
    private void deopSelectedPlayer() {
        Operator selectedOp = operatorsList.getSelectedValue();
        if (selectedOp == null) {
            JOptionPane.showMessageDialog(this, "Please select an operator to de-op.", "No Operator Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new Thread(() -> {
            boolean success = server.operators.removePlayers(new Player[]{selectedOp.player});
            System.out.println("De-OP success: " + success);
            refreshLists();
        }).start();
    }

    /**
     * Handles removing a selected IP ban.
     */
    private void removeSelectedIpBan() {
        IPBan selectedBan = ipBansList.getSelectedValue();
        if (selectedBan == null) {
            JOptionPane.showMessageDialog(this, "Please select an IP ban to remove.", "No IP Ban Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new Thread(() -> {
            boolean success = server.ipBans.removeIPs(new String[]{selectedBan.ip});
            System.out.println("IP Ban remove success: " + success);
            refreshLists();
        }).start();
    }

    /**
     * Handles updating a selected gamerule.
     */
    private void updateSelectedGameRule() {
        int selectedRow = gameRulesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a gamerule from the table to update.", "No Gamerule Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String key = (String) gameRulesModel.getValueAt(selectedRow, 0);
        String currentValue = (String) gameRulesModel.getValueAt(selectedRow, 1);

        String newValue = JOptionPane.showInputDialog(this, "Enter new value for " + key + ":", "Update Gamerule", JOptionPane.QUESTION_MESSAGE, null, null, currentValue).toString();
        if (newValue == null) return; // User cancelled

        UntypedGameRule ruleUpdate = new UntypedGameRule(key, newValue);
        new Thread(() -> {
            boolean success = server.gamerules.updateGameRule(ruleUpdate);
            System.out.println("Gamerule update success: " + success);
            refreshLists(); // Refresh to see change
        }).start();
    }

    /**
     * Handles sending a system-wide message.
     */
    private void sendSystemMessage() {
        String text = sendSystemMessageField.getText();
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message to send.", "No Message", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Message message = new Message(null, null, text); // Simple literal message
        // receivingPlayers = null means send to all
        SystemMessage systemMessage = new SystemMessage(null, false, message);

        new Thread(() -> {
            boolean success = server.server.sendMessage(systemMessage);
            System.out.println("System message send success: " + success);
            if (success) {
                SwingUtilities.invokeLater(() -> sendSystemMessageField.setText(""));
            }
        }).start();
    }

    /**
     * Handles saving the server.
     */
    private void saveServer() {
        // Ask for confirmation (Save can cause lag)
        int result = JOptionPane.showConfirmDialog(this,
                "Saving the server (with flush) may cause a brief lag.\nAre you sure you want to proceed?",
                "Confirm Save", JOptionPane.YES_NO_OPTION);

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        new Thread(() -> {
            System.out.println("Attempting to save server...");
            boolean success = server.server.save(true); // true for flush
            if (success) {
                System.out.println("Server save initiated successfully.");
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Server save initiated.", "Save Started", JOptionPane.INFORMATION_MESSAGE));
            } else {
                System.err.println("Server save failed (or timed out).");
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Server save command failed (or timed out).", "Save Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    /**
     * Shows a confirmation dialog with a 5-second timer before stopping the server.
     */
    private void stopServerWithConfirm() {
        // Initial confirmation
        int result = JOptionPane.showConfirmDialog(this,
                "This will STOP the entire server. This action is irreversible.\nAre you 100% sure?",
                "DANGER: Confirm Server Stop", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        // Second confirmation with timer
        String stopMessage = "Press 'Confirm Stop' to stop the server.";
        JButton confirmButton = new JButton("Confirm Stop (5)");
        confirmButton.setEnabled(false);

        Object[] options = {confirmButton, "Cancel"};
        JOptionPane pane = new JOptionPane(stopMessage,
                JOptionPane.WARNING_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null, options, options[1]); // Default to Cancel
        JDialog dialog = pane.createDialog(this, "Final Confirmation");

        // Timer to count down
        javax.swing.Timer timer = new javax.swing.Timer(1000, null);
        timer.setInitialDelay(0);

        final int[] countdown = {5}; // Use an array to be final-in-lambda

        timer.addActionListener(e -> {
            countdown[0]--;
            if (countdown[0] > 0) {
                confirmButton.setText("Confirm Stop (" + countdown[0] + ")");
            } else {
                timer.stop();
                confirmButton.setText("Confirm Stop");
                confirmButton.setEnabled(true);
            }
        });

        // Handle the button click
        confirmButton.addActionListener(e -> {
            dialog.setVisible(false);
            // Call the actual stop method
            executeStopServer();
        });

        timer.start();
        dialog.setVisible(true); // This blocks until the dialog is closed
    }

    /**
     * Executes the actual server stop command.
     */
    private void executeStopServer() {
        new Thread(() -> {
            System.out.println("Attempting to STOP server...");
            boolean success = server.server.stop();
            if (success) {
                System.out.println("Server stop initiated successfully.");
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Server stop initiated. Connection will be lost.", "Stop Started", JOptionPane.INFORMATION_MESSAGE));
            } else {
                System.err.println("Server stop failed (or timed out).");
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Server stop command failed (or timed out).", "Stop Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }


    // =================================================================================
    // --- ACTION HANDLERS (Settings) ---
    // =================================================================================

    // --- Checkbox Setters ---
    private void setAutosave() {
        boolean enabled = autosaveCheck.isSelected();
        new Thread(() -> {
            server.serverSettings.setAutosave(enabled);
            refreshServerSettings();
        }).start();
    }

    private void setEnforceAllowlist() {
        boolean enabled = enforceAllowlistCheck.isSelected();
        new Thread(() -> {
            server.serverSettings.setEnforceAllowlist(enabled);
            refreshServerSettings();
        }).start();
    }

    private void setUseAllowlist() {
        boolean enabled = useAllowlistCheck.isSelected();
        new Thread(() -> {
            server.serverSettings.setUseAllowlist(enabled);
            refreshServerSettings();
        }).start();
    }

    private void setAllowFlight() {
        boolean enabled = allowFlightCheck.isSelected();
        new Thread(() -> {
            server.serverSettings.setAllowFlight(enabled);
            refreshServerSettings();
        }).start();
    }

    private void setForceGamemode() {
        boolean enabled = forceGamemodeCheck.isSelected();
        new Thread(() -> {
            server.serverSettings.setForceGamemode(enabled);
            refreshServerSettings();
        }).start();
    }

    private void setAcceptTransfers() {
        boolean enabled = acceptTransfersCheck.isSelected();
        new Thread(() -> {
            server.serverSettings.setAcceptTransfers(enabled);
            refreshServerSettings();
        }).start();
    }

    private void setHideOnlinePlayers() {
        boolean enabled = hidePlayersCheck.isSelected();
        new Thread(() -> {
            server.serverSettings.setHideOnlinePlayers(enabled);
            refreshServerSettings();
        }).start();
    }

    private void setStatusReplies() {
        boolean enabled = statusRepliesCheck.isSelected();
        new Thread(() -> {
            server.serverSettings.setStatusReplies(enabled);
            refreshServerSettings();
        }).start();
    }

    // --- ComboBox Setters ---
    private void setDifficulty() {
        Difficulty diff = (Difficulty) difficultyCombo.getSelectedItem();
        new Thread(() -> {
            server.serverSettings.setDifficulty(diff);
            refreshServerSettings();
        }).start();
    }

    private void setGamemode() {
        Gamemode gm = (Gamemode) gamemodeCombo.getSelectedItem();
        new Thread(() -> {
            server.serverSettings.setGamemode(gm);
            refreshServerSettings();
        }).start();
    }

    // --- TextField Setters (with helper) ---
    private void setIntSetting(JTextField field, Consumer<Integer> setter) {
        try {
            int value = Integer.parseInt(field.getText());
            new Thread(() -> {
                setter.accept(value);
                refreshServerSettings();
                SwingUtilities.invokeLater(() -> field.setText(""));
            }).start();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    @FunctionalInterface
    interface Consumer<T> {
        void accept(T t);
    }

    private void setMaxPlayers() {
        setIntSetting(maxPlayersField, server.serverSettings::setMaxPlayers);
    }

    private void setPauseWhenEmptySeconds() {
        setIntSetting(pauseEmptyField, server.serverSettings::setPauseWhenEmptySeconds);
    }

    private void setPlayerIdleTimeout() {
        setIntSetting(idleTimeoutField, server.serverSettings::setPlayerIdleTimeout);
    }

    private void setSpawnProtectionRadius() {
        setIntSetting(spawnProtectField, server.serverSettings::setSpawnProtectionRadius);
    }

    private void setOperatorUserPermissionLevel() {
        setIntSetting(opLevelField, server.serverSettings::setOperatorUserPermissionLevel);
    }

    private void setViewDistance() {
        setIntSetting(viewDistanceField, server.serverSettings::setViewDistance);
    }

    private void setSimulationDistance() {
        setIntSetting(simDistanceField, server.serverSettings::setSimulationDistance);
    }



    private void setEntityBroadcastRange() {
        setIntSetting(entityRangeField, server.serverSettings::setEntityBroadcastRange);
    }

    private void setStatusHeartbeatInterval() {
        setIntSetting(heartbeatField, server.serverSettings::setStatusHeartbeatInterval);
    }

    private void setMotd() {
        String motd = motdField.getText();
        if (motd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "MOTD cannot be empty.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new Thread(() -> {
            server.serverSettings.setMotd(motd);
            refreshServerSettings();
            SwingUtilities.invokeLater(() -> motdField.setText(""));
        }).start();
    }


    // =================================================================================
    // --- MAIN METHOD ---
    // =================================================================================

    public static void main(String[] args) {
        String initialURL = "";
        String initialSecret = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(System.getProperty("user.home"),".minecraftServerSecret")));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                if (i == 0) initialURL = line;
                else if (i == 1) {
                    initialSecret = line;
                    break;
                }
                i++;
            }
        } catch (Exception ignored) {
        }


        String serverURL = JOptionPane.showInputDialog(null, "Please enter the Server URL", initialURL);
        if (serverURL == null) return; // User cancelled
        String serverSecret = JOptionPane.showInputDialog(null, "Please enter the Server Secret", initialSecret);
        if (serverSecret == null) return; // User cancelled

        URL = serverURL;
        SECRET = serverSecret;

        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}