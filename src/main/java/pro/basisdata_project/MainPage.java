package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MainPage extends StackPane {

    public MainPage(String accountName, Stage primaryStage, Scene loginScene) {
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #EEF5FF;");

        // Top bar
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setSpacing(20);
        topBar.setStyle("-fx-background-color: #DF591A;");
        topBar.setPadding(new Insets(10));

        Button homeButton = new Button("HOME");
        homeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 15;");

        // Add vertical separator next to the home button
        Separator homeSeparator = new Separator();
        homeSeparator.setOrientation(javafx.geometry.Orientation.VERTICAL);

        Label systemLabel = new Label("SYSTEM");
        systemLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");

        Hyperlink accountLink = new Hyperlink(accountName); // Hyperlink for account name

        HBox.setMargin(homeButton, new Insets(0, 0, 0, 40));
        HBox.setMargin(homeSeparator, new Insets(0, 10, 0, 60));
        HBox.setMargin(accountLink, new Insets(0, 10, 0, 0));

        topBar.getChildren().addAll(homeButton, homeSeparator, systemLabel, accountLink);
        HBox.setHgrow(systemLabel, Priority.ALWAYS);
        systemLabel.setMaxWidth(Double.MAX_VALUE);
        systemLabel.setAlignment(Pos.CENTER);

        // Separator line below top bar
        Separator topSeparator = new Separator();

        // Sidebar
        VBox sidebar = new VBox();
        sidebar.setAlignment(Pos.TOP_LEFT);
        sidebar.setStyle("-fx-background-color: #EEF5FF; -fx-font-size: 15;");
        sidebar.setPrefWidth(200);
        VBox.setVgrow(sidebar, Priority.ALWAYS);

        // Create a ListView for the features
        ListView<String> featureList = new ListView<>();
        featureList.getItems().addAll(
                "Data", "Users", "Platforms", "Personnels", "Assignments",
                "Equipments", "Missions", "Maintenancelogs", "Analysis", "CommLog", "Sensors"
        );

        // Set custom cell factory to adjust spacing between items
        featureList.setCellFactory(list -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(item);
                    label.setPadding(new Insets(10)); // Adjust padding as needed
                    setGraphic(label);
                }
            }
        });

        VBox.setVgrow(featureList, Priority.ALWAYS); // Ensure featureList expands vertically
        sidebar.getChildren().add(featureList);

        // Main content area (VBox instead of ListView)
        VBox mainContent = new VBox();
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 20;");
        VBox.setVgrow(mainContent, Priority.ALWAYS); // Ensure mainContent expands vertically

        Label welcomeLabel = new Label("Welcome to the Main Page");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-text-fill: black;");
        mainContent.getChildren().add(welcomeLabel);

        // Add listener to feature list
        featureList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                mainContent.getChildren().clear();
                switch (newValue) {
                    case "Users":
                        mainContent.getChildren().add(Users.getUsersUI());
                        break;
                    case "Sensors":
                        mainContent.getChildren().add(Sensors.getSensorsUI());
                        break;
                    case "Platforms":
                        mainContent.getChildren().add(Platforms.getPlatformsUI());
                        break;
                    case "Personnels":
                        mainContent.getChildren().add(Personnels.getPersonnelsUI());
                        break;
                    case "Maintenancelogs":
                        mainContent.getChildren().add(Maintenancelogs.getMaintenancelogsUI());
                        break;
                    case "CommLog":
                        mainContent.getChildren().add(CommLog.getCommLogUI());
                        break;
                    case "Analysis":
                        mainContent.getChildren().add(Analysis.getAnalysisUI());
                        break;
                    case "Assignments":
                        mainContent.getChildren().add(Assignments.getAssignmentsUI());
                        break;
                    case "Equipments":
                        mainContent.getChildren().add(Equipments.getEquipmentsUI());
                        break;
                    case "Missions":
                        mainContent.getChildren().add(Missions.getMissionsUI());
                        break;
                    case "Data":
                        mainContent.getChildren().add(Data.getDataUI());
                        break;
                    default:
                        mainContent.getChildren().add(new Label("Select an option from the sidebar."));
                        break;
                }
            }
        });

        homeButton.setOnAction(event -> {
            mainContent.getChildren().clear();
            Home home = new Home(); // Buat instance dari kelas Home
            mainContent.getChildren().add(home.getHomeUI()); // Gunakan instance untuk memanggil metode getHomeUI()
            featureList.getSelectionModel().clearSelection(); // Menghapus pemilihan dari ListView saat tombol HOME ditekan
        });


        // Handling click event on accountLink (Hyperlink for account name)
        accountLink.setOnAction(e -> {
            // Create a context menu with options Profil and Log Out
            ContextMenu contextMenu = new ContextMenu();
            MenuItem profilItem = new MenuItem("Profil");
            MenuItem logOutItem = new MenuItem("Log Out");

            profilItem.setOnAction(event -> {
                // Handle action for Profil option
                // Load and display profile data from DataUser.txt for the current account
                String profileData = loadProfileData(accountName);
                if (profileData != null) {
                    // Show profile data in a dialog or somewhere in the UI
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Profil");
                    alert.setHeaderText(null);
                    alert.setContentText(profileData);
                    alert.showAndWait();
                } else {
                    showErrorDialog("Error loading profile data.");
                }
            });

            logOutItem.setOnAction(event -> {
                // Handle action for Log Out option
                // Return to the login page
                primaryStage.setScene(loginScene);
            });

            // Add menu items to the context menu
            contextMenu.getItems().addAll(profilItem, logOutItem);
            accountLink.setOnMouseClicked(o -> {
                ContextMenu CM = new ContextMenu();
                MenuItem profileItem = new MenuItem("Profil");
                MenuItem logoutItem = new MenuItem("Log Out");

                profileItem.setOnAction(event -> {
                    try {
                        // Baca file DataUser.txt
                        List<String> userData = Files.readAllLines(Paths.get("DataUser.txt"));

                        // Cari data profil sesuai dengan akun yang aktif
                        for (String line : userData) {
                            String[] parts = line.split(",");
                            String storedUsername = parts[1]; // Indeks 1 berisi username

                            if (storedUsername.equals(accountName)) {
                                String fullName = parts[0];
                                String Username = parts[1];
                                String email = parts[2];
                                String message = "Full Name: " + fullName + "Username: "+ Username +"\nEmail: " + email;

                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Profile Information");
                                alert.setHeaderText("Profile Data");
                                alert.setContentText(message);
                                alert.showAndWait();
                                break;
                            }
                        }
                    } catch (IOException s) {
                        showErrorDialog("Error reading user data.");
                    }
                });

                logoutItem.setOnAction(event -> {
                    // Tambahkan logika untuk keluar ke halaman login
                    primaryStage.setScene(loginScene);
                });

                CM.getItems().addAll(profileItem, logoutItem);
                CM.show(accountLink, o.getScreenX(), o.getScreenY());
            });

        });

        // BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(new VBox(topBar, topSeparator));
        borderPane.setLeft(sidebar);
        borderPane.setCenter(mainContent);

        // Set alignment constraints for the components
        BorderPane.setAlignment(topBar, Pos.TOP_CENTER);
        BorderPane.setAlignment(sidebar, Pos.TOP_LEFT);

        this.getChildren().add(borderPane); // Adding the BorderPane to the StackPane
    }

    // Method to load profile data from DataUser.txt for the specified account
    private String loadProfileData(String accountName) {
        // Logic to load and return profile data for the account
        // Example:
        // Read the DataUser.txt file and find the profile data for the accountName
        // Return the profile data as a String

        // Placeholder return value for demonstration
        return "Name: John Doe\nEmail: johndoe@example.com\nRole: Administrator";
    }

    // Method to show an error dialog
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
