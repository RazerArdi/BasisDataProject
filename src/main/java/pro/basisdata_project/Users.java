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
            String userId = autoGenerateIdCheckBox.isSelected() ? generateUserId() : userIdText.getText();
            String name = nameText.getText();
            String role = roleText.getText();
            int accessLevel = accessLevelComboBox.getValue();
            java.time.LocalDate lastLoginDate = lastLoginPicker.getValue();

            if (name.isEmpty() || role.isEmpty() || lastLoginDate == null || (!autoGenerateIdCheckBox.isSelected() && userId.isEmpty())) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            if (!autoGenerateIdCheckBox.isSelected() && userIdExists(userId)) {
                errorLabel.setText("User ID already exists!");
                return;
            }

            long lastLogin = lastLoginDate.toEpochDay();

            Users user = new Users(userId, name, role, accessLevel, lastLogin);

            saveUserToDatabase(user);

            tableView.getItems().add(user);

            showAlert(Alert.AlertType.INFORMATION, "Success", "User Created", "User has been created successfully.");

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
                String userId = selectedUser.getUserId();
                String name = nameText.getText();
                String role = roleText.getText();
                int accessLevel = accessLevelComboBox.getValue();
                java.time.LocalDate lastLoginDate = lastLoginPicker.getValue();

                // Validation
                if (userId.isEmpty()) {
                    errorLabel.setText("User ID is required!");
                    return;
                }

                if (name.isEmpty() || role.isEmpty() || lastLoginDate == null) {
                    errorLabel.setText("Fields marked with * are required!");
                    return;
                }

                selectedUser.setName(name);
                selectedUser.setRole(role);
                selectedUser.setAccessLevel(accessLevel);
                selectedUser.setLastLogin(lastLoginDate.toEpochDay());

                updateUserInDatabase(selectedUser);

                tableView.refresh();

                showAlert(Alert.AlertType.INFORMATION, "Success", "User Updated", "User details have been updated successfully.");

                userIdText.clear();
                nameText.clear();
                roleText.clear();
                accessLevelComboBox.setValue(1);
                lastLoginPicker.getEditor().clear();
                errorLabel.setText("");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No User Selected", "Please select a user to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Users selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String userId = selectedUser.getUserId();

                deleteUserFromDatabase(selectedUser);

                tableView.getItems().remove(selectedUser);

                showAlert(Alert.AlertType.INFORMATION, "Success", "User Deleted", "User has been deleted successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No User Selected", "Please select a user to delete.");
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

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static boolean userIdExists(String userId) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM \"C4ISR PROJECT (BASIC) V2\".USERS WHERE USER_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static String generateUserId() {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT \"C4ISR PROJECT (BASIC) V2\".USER_SEQ.NEXTVAL FROM dual";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "AUTO_GENERATED";
    }

    private static void saveUserToDatabase(Users user) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".USERS (USER_ID, NAME, ROLE, ACCESS_LEVEL, LAST_LOGIN) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getRole());
            pstmt.setInt(4, user.getAccessLevel());
            pstmt.setDate(5, new Date(user.getLastLogin()));
            pstmt.executeUpdate();
            System.out.println("User saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void updateUserInDatabase(Users user) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".USERS " +
                    "SET NAME = ?, ROLE = ?, ACCESS_LEVEL = ?, LAST_LOGIN = ? " +
                    "WHERE USER_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getRole());
            pstmt.setInt(3, user.getAccessLevel());
            pstmt.setDate(4, new Date(user.getLastLogin()));
            pstmt.setString(5, user.getUserId());
            pstmt.executeUpdate();
            System.out.println("User updated in database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteUserFromDatabase(Users user) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".USERS WHERE USER_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.executeUpdate();
            System.out.println("User deleted from database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
                long lastLogin = rs.getDate("LAST_LOGIN").getTime();

                Users user = new Users(userId, name, role, accessLevel, lastLogin);
                usersList.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return usersList;
    }
}
