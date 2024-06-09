package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Users {

    private String userId;
    private String name;
    private String role;
    private int accessLevel;
    private long lastLogin;

    // Constructor
    public Users(String userId, String name, String role, int accessLevel, long lastLogin) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.accessLevel = accessLevel;
        this.lastLogin = lastLogin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Method to display the Users GUI
    public static VBox getUsersUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Button searchAnalysisIdButton = new Button("Search Analysis ID");

        searchAnalysisIdButton.setOnAction(e -> {
            Optional<String> result = showSearchDialog();
            if (result.isPresent()) {
                String analysisId = result.get();
                if (isDataFound(analysisId)) {
                    displayDataFound();
                } else {
                    analysisIdText.setText(analysisId);
                }
            }
        });

        Label userIdLabel = new Label("User ID:");
        TextField userIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label roleLabel = new Label("Role:");
        TextField roleText = new TextField();
        Label accessLevelLabel = new Label("Access Level:");
        ComboBox<Integer> accessLevelComboBox = new ComboBox<>();
        accessLevelComboBox.getItems().addAll(1, 2, 3, 4, 5);
        accessLevelComboBox.setValue(1); // Default value
        Label lastLoginLabel = new Label("Time Registered (auto-generated):");

        // Create TableView and columns
        TableView<Users> tableView = new TableView<>();
        TableColumn<Users, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        TableColumn<Users, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Users, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        TableColumn<Users, Integer> accessLevelCol = new TableColumn<>("Access Level");
        accessLevelCol.setCellValueFactory(new PropertyValueFactory<>("accessLevel"));
        TableColumn<Users, Long> lastLoginCol = new TableColumn<>("Last Login");
        lastLoginCol.setCellValueFactory(new PropertyValueFactory<>("lastLogin"));

        tableView.getColumns().addAll(userIdCol, nameCol, roleCol, accessLevelCol, lastLoginCol);

        // Create button for delete operation
        Button deleteSelectedButton = new Button("Delete Selected");
        deleteSelectedButton.setOnAction(e -> {
            Users selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String userIdToDelete = selectedUser.getUserId();
                // Code to delete user from database or list
                System.out.println("Deleted User ID: " + userIdToDelete);
                tableView.getItems().remove(selectedUser); // Remove from the table
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText("No User Selected");
                alert.setContentText("Please select a user to delete.");
                alert.showAndWait();
            }
        });

        // Add table view and delete button to the VBox
        vbox.getChildren().addAll(tableView, deleteSelectedButton);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            String userId = userIdText.getText();
            String name = nameText.getText();
            String role = roleText.getText();
            int accessLevel = accessLevelComboBox.getValue();
            long lastLogin = System.currentTimeMillis();

            Users user = new Users(userId, name, role, accessLevel, lastLogin);
            System.out.println("User Created: " + user.getName());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%s,Users,%s,%s,%s,%d,%d%n", analysisId, userId, name, role, accessLevel, lastLogin));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new user to the table view
            tableView.getItems().add(user);

            // Clear input fields after adding user
            userIdText.clear();
            nameText.clear();
            roleText.clear();
            accessLevelComboBox.setValue(1); // Reset to default value
        });

        // Button for refreshing table data
        Button refreshDataButton = new Button("Refresh Data (Table)");
        refreshDataButton.setOnAction(e -> {
            tableView.getItems().clear(); // Clear existing data
            // Load fresh data to the table view
            loadData(tableView);
        });

        vbox.getChildren().addAll(analysisIdLabel, analysisIdText, searchAnalysisIdButton, userIdLabel, userIdText, nameLabel, nameText, roleLabel, roleText, accessLevelLabel, accessLevelComboBox, lastLoginLabel, createButton, refreshDataButton);

        // Load initial data to the table view
        loadData(tableView);

        return vbox;
    }

    private static Optional<String> showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Analysis ID");
        dialog.setHeaderText("Enter Analysis ID to search:");
        dialog.setContentText("Analysis ID:");

        return dialog.showAndWait();
    }

    static boolean isDataFound(String analysisId) {
        // Implement logic to check if data with the given analysisId exists in the database
        return true; // For demonstration purposes, always return true
    }

    static void displayDataFound() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data Found");
        alert.setHeaderText(null);
        alert.setContentText("Data Found!");

        alert.showAndWait();
    }

    private static void loadData(TableView<Users> tableView) {
        try {
            // Load data from file or database
            List<String> lines = Files.readAllLines(Paths.get("Database.txt"));
            ObservableList<Users> data = FXCollections.observableArrayList();

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 7 && parts[1].equals("Users")) {
                    try {
                        int analysisId = Integer.parseInt(parts[0].trim());
                        String userId = parts[2].trim();
                        String name = parts[3].trim();
                        String role = parts[4].trim();
                        int accessLevel = Integer.parseInt(parts[5].trim());
                        long lastLogin = Long.parseLong(parts[6].trim());
                        data.add(new Users(userId, name, role, accessLevel, lastLogin));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data format in line: " + line);
                    }
                } else {
                    System.out.println("Invalid data format in line: " + line);
                }
            }

            tableView.setItems(data);
            tableView.refresh(); // Refresh the table view
        } catch (IOException e) {
            System.out.println("Error loading data from Database.txt.");
        }
    }
}
