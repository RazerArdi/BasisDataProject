package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;

public class Users {

    private String userId;
    private String name;
    private String role;
    private int accessLevel;
    private long lastLogin;

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

    public static VBox getUsersUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label userIdLabel = new Label("User ID *:");
        TextField userIdText = new TextField();
        Label nameLabel = new Label("Name *:");
        TextField nameText = new TextField();
        Label roleLabel = new Label("Role *:");
        TextField roleText = new TextField();
        Label accessLevelLabel = new Label("Access Level *:");
        ComboBox<Integer> accessLevelComboBox = new ComboBox<>();
        accessLevelComboBox.getItems().addAll(1, 2, 3, 4, 5);
        accessLevelComboBox.setValue(1); // Default value
        Label lastLoginLabel = new Label("Last Login *:");
        DatePicker lastLoginPicker = new DatePicker();

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

        CheckBox autoGenerateIdCheckBox = new CheckBox("Auto Generate ID");
        autoGenerateIdCheckBox.setSelected(true);

        HBox userIdBox = new HBox();
        userIdBox.getChildren().addAll(userIdText, new Label("*"), autoGenerateIdCheckBox);
        userIdBox.setSpacing(10);

        autoGenerateIdCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                userIdText.setDisable(true);
                userIdText.clear();
            } else {
                userIdText.setDisable(false);
            }
        });

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String userId = autoGenerateIdCheckBox.isSelected() ? "AUTO_GENERATED" : userIdText.getText();
            String name = nameText.getText();
            String role = roleText.getText();
            int accessLevel = accessLevelComboBox.getValue();
            java.time.LocalDate lastLoginDate = lastLoginPicker.getValue();

            if (userId.isEmpty() || name.isEmpty() || role.isEmpty() || lastLoginDate == null) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            long lastLogin = lastLoginDate.toEpochDay();

            Users user = new Users(userId, name, role, accessLevel, lastLogin);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".USERS (USER_ID, NAME, ROLE, ACCESS_LEVEL, LAST_LOGIN) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, userId);
                pstmt.setString(2, name);
                pstmt.setString(3, role);
                pstmt.setInt(4, accessLevel);
                pstmt.setDate(5, Date.valueOf(lastLoginDate));
                pstmt.executeUpdate();
                System.out.println("User saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            tableView.getItems().add(user);

            userIdText.clear();
            nameText.clear();
            roleText.clear();
            accessLevelComboBox.setValue(1);
            lastLoginPicker.getEditor().clear();
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Users selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String userId = autoGenerateIdCheckBox.isSelected() ? "AUTO_GENERATED" : userIdText.getText();
                String name = nameText.getText();
                String role = roleText.getText();
                int accessLevel = accessLevelComboBox.getValue();
                java.time.LocalDate lastLoginDate = lastLoginPicker.getValue();

                selectedUser.setUserId(userId);
                selectedUser.setName(name);
                selectedUser.setRole(role);
                selectedUser.setAccessLevel(accessLevel);
                selectedUser.setLastLogin(lastLoginDate.toEpochDay());

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".USERS SET NAME = ?, ROLE = ?, ACCESS_LEVEL = ?, LAST_LOGIN = ? WHERE USER_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, name);
                    pstmt.setString(2, role);
                    pstmt.setInt(3, accessLevel);
                    pstmt.setDate(4, Date.valueOf(lastLoginDate));
                    pstmt.setString(5, userId);
                    pstmt.executeUpdate();
                    System.out.println("User updated in database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.refresh();

                userIdText.clear();
                nameText.clear();
                roleText.clear();
                accessLevelComboBox.setValue(1);
                lastLoginPicker.getEditor().clear();
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Users selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String userId = selectedUser.getUserId();

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".USERS WHERE USER_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, userId);
                    pstmt.executeUpdate();
                    System.out.println("User deleted from database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.getItems().remove(selectedUser);
            }
        });


        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        ObservableList<Users> usersList = fetchUsersFromDatabase();
        tableView.setItems(usersList);

        vbox.getChildren().addAll(
                userIdLabel, userIdBox, nameLabel, nameText,
                roleLabel, roleText, accessLevelLabel, accessLevelComboBox,
                lastLoginLabel, lastLoginPicker,
                errorLabel, tableView, buttonBox);

        return vbox;
    }



    private static ObservableList<Users> fetchUsersFromDatabase() {
        ObservableList<Users> usersList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT USER_ID, NAME, ROLE, ACCESS_LEVEL, LAST_LOGIN FROM \"C4ISR PROJECT (BASIC) V2\".USERS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String userId = rs.getString("USER_ID");
                String name = rs.getString("NAME");
                String role = rs.getString("ROLE");
                int accessLevel = rs.getInt("ACCESS_LEVEL");
                long lastLogin = rs.getDate("LAST_LOGIN").toLocalDate().toEpochDay();

                Users user = new Users(userId, name, role, accessLevel, lastLogin);
                usersList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return usersList;
    }
}
