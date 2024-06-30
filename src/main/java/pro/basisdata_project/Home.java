package pro.basisdata_project;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home {

    public static ScrollPane getHomeUI() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox homeContent = new VBox();
        homeContent.setAlignment(Pos.CENTER);
        homeContent.setSpacing(20);
        homeContent.setPadding(new Insets(20));

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");

        ChoiceBox<String> tableChoiceBox = new ChoiceBox<>();
        tableChoiceBox.getItems().addAll(
                "USERS", "AIR", "ANALYSIS", "ASSIGNMENTS", "COMMUNICATION_LOG",
                "contractors", "DATA", "ENLISTED", "EQUIPMENT", "LAND",
                "MAINTENANCE_LOGS", "MISSIONS", "OFFICER", "PERSONNEL", "PLATFORMS",
                "SEA", "SENSORS", "SPACE"
        );
        tableChoiceBox.setValue("USERS");

        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setEditable(true);

        Button loadDataButton = new Button("Load Data");
        loadDataButton.setOnAction(event -> {
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            try {
                Connection connection = OracleAPEXConnection.getConnection();

                String selectedTable = tableChoiceBox.getValue();

                String query = "SELECT * FROM \"C4ISR PROJECT (BASIC) V2\"." + selectedTable;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                List<TableColumn<ObservableList<String>, String>> columns = new ArrayList<>();
                ResultSetMetaData rsMetaData = resultSet.getMetaData();
                int columnCount = rsMetaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    final int index = i;
                    TableColumn<ObservableList<String>, String> column = new TableColumn<>(rsMetaData.getColumnName(i));
                    column.setCellValueFactory(param -> {
                        ObservableList<String> row = param.getValue();
                        return new SimpleStringProperty(row.get(index - 1));
                    });
                    columns.add(column);
                }

                tableView.getColumns().clear();
                tableView.getColumns().addAll(columns);

                while (resultSet.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(resultSet.getString(i));
                    }
                    data.add(row);
                }

                statement.close();
                tableView.setItems(data);
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        HBox loadBox = new HBox(10, new Label("Select Table:"), tableChoiceBox, loadDataButton);
        loadBox.setAlignment(Pos.CENTER);
        loadBox.setPadding(new Insets(10));

        HBox searchBox = new HBox(searchField);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        homeContent.getChildren().addAll(loadBox, searchBox, tableView);

        homeContent.getChildren().addAll(
                createDataTypeAnalysis(),
                createPersonnelAvailabilityAnalysis(),
                createUserAccessAnalysis(),
                createMaintenanceAnalysis(),
                createTaskLocationAnalysis(),
                createMissionAnalysis(),
                createSensorAnalysis()
        );

        scrollPane.setContent(homeContent);
        scrollPane.setMinHeight(600);

        return scrollPane;
    }

    private static VBox createDataTypeAnalysis() {
        VBox dataTypeBox = new VBox();
        dataTypeBox.setAlignment(Pos.CENTER);
        dataTypeBox.setSpacing(10);
        dataTypeBox.setPadding(new Insets(20));
        Label dataTypeLabel = new Label("Analisis Distribusi Data");
        dataTypeLabel.setFont(Font.font("Arial", 16));

        try (Connection connection = OracleAPEXConnection.getConnection()) {
            String query = "SELECT COUNT(CASE WHEN data_type = 'Raw' THEN 1 END) as raw_count, " +
                    "COUNT(CASE WHEN data_type = 'Processed' THEN 1 END) as processed_count " +
                    "FROM \"C4ISR PROJECT (BASIC) V2\".DATA";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            double total = 0;
            while (resultSet.next()) {
                int rawCount = resultSet.getInt("raw_count");
                int processedCount = resultSet.getInt("processed_count");
                total = rawCount + processedCount;

                pieChartData.add(new PieChart.Data("Raw Data", rawCount));
                pieChartData.add(new PieChart.Data("Processed Data", processedCount));
            }

            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Distribusi Data");

            for (PieChart.Data data : pieChart.getData()) {
                double percentage = (data.getPieValue() / total) * 100;
                data.setName(data.getName() + " (" + String.format("%.1f%%", percentage) + ")");
            }

            dataTypeBox.getChildren().addAll(dataTypeLabel, pieChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataTypeBox;
    }


    private static VBox createPersonnelAvailabilityAnalysis() {
        VBox personnelBox = new VBox();
        personnelBox.setAlignment(Pos.CENTER);
        personnelBox.setSpacing(10);
        personnelBox.setPadding(new Insets(20));
        Label personnelLabel = new Label("Analisis Ketersediaan Personel");
        personnelLabel.setFont(Font.font("Arial", 16));

        try (Connection connection = OracleAPEXConnection.getConnection()) {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Jumlah Personel berdasarkan Pangkat");
            xAxis.setLabel("Pangkat");
            yAxis.setLabel("Jumlah");

            XYChart.Series<String, Number> series = new XYChart.Series<>();

            String query = "SELECT rank, COUNT(*) as count FROM \"C4ISR PROJECT (BASIC) V2\".PERSONNEL GROUP BY rank";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                series.getData().add(new XYChart.Data<>(resultSet.getString("rank"), resultSet.getInt("count")));
            }

            barChart.getData().addAll(series);

            for (XYChart.Series<String, Number> s : barChart.getData()) {
                for (XYChart.Data<String, Number> data : s.getData()) {
                    Tooltip.install(data.getNode(), new Tooltip(
                            String.format("%s : %d", data.getXValue(), data.getYValue().intValue())
                    ));
                }
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            query = "SELECT role, COUNT(*) as count FROM \"C4ISR PROJECT (BASIC) V2\".ASSIGNMENTS GROUP BY role";
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                pieChartData.add(new PieChart.Data(resultSet.getString("role"), resultSet.getInt("count")));
            }

            addPieChartPercentage(pieChartData);

            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Jumlah Personel Aktif");

            personnelBox.getChildren().addAll(personnelLabel, barChart, pieChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return personnelBox;
    }

    private static VBox createUserAccessAnalysis() {
        VBox accessBox = new VBox();
        accessBox.setAlignment(Pos.CENTER);
        accessBox.setSpacing(10);
        accessBox.setPadding(new Insets(20));
        Label accessLabel = new Label("Analisis Pengguna dan Akses");
        accessLabel.setFont(Font.font("Arial", 16));

        try (Connection connection = OracleAPEXConnection.getConnection()) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            String query = "SELECT access_level, COUNT(*) as count FROM \"C4ISR PROJECT (BASIC) V2\".USERS GROUP BY access_level";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                pieChartData.add(new PieChart.Data("Access Level " + resultSet.getInt("access_level"), resultSet.getInt("count")));
            }

            addPieChartPercentage(pieChartData);

            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Distribusi Tingkat Akses");

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Jumlah Login Terakhir");
            xAxis.setLabel("Waktu");
            yAxis.setLabel("Jumlah");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            query = "SELECT TO_CHAR(last_login, 'HH24:MI') as login_time, COUNT(*) as count FROM \"C4ISR PROJECT (BASIC) V2\".USERS GROUP BY TO_CHAR(last_login, 'HH24:MI')";
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                series.getData().add(new XYChart.Data<>(resultSet.getString("login_time"), resultSet.getInt("count")));
            }

            lineChart.getData().addAll(series);

            accessBox.getChildren().addAll(accessLabel, pieChart, lineChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accessBox;
    }



    private static VBox createMaintenanceAnalysis() {
        VBox maintenanceBox = new VBox();
        maintenanceBox.setAlignment(Pos.CENTER);
        maintenanceBox.setSpacing(10);
        maintenanceBox.setPadding(new Insets(20));
        Label maintenanceLabel = new Label("Analisis Pemeliharaan");
        maintenanceLabel.setFont(Font.font("Arial", 16));

        try (Connection connection = OracleAPEXConnection.getConnection()) {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Frekuensi Pemeliharaan");
            xAxis.setLabel("Peralatan");
            yAxis.setLabel("Frekuensi");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            String maintenanceQuery = "SELECT EQUIPMENT_EQUIPMENT_ID, COUNT(*) as count FROM \"C4ISR PROJECT (BASIC) V2\".maintenance_logs GROUP BY EQUIPMENT_EQUIPMENT_ID";
            Statement maintenanceStatement = connection.createStatement();
            ResultSet maintenanceResultSet = maintenanceStatement.executeQuery(maintenanceQuery);

            while (maintenanceResultSet.next()) {
                series.getData().add(new XYChart.Data<>(maintenanceResultSet.getString("EQUIPMENT_EQUIPMENT_ID"), maintenanceResultSet.getInt("COUNT")));
            }

            barChart.getData().addAll(series);

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            String statusQuery = "SELECT STATUS, COUNT(*) as count FROM \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT GROUP BY STATUS";
            Statement statusStatement = connection.createStatement();
            ResultSet statusResultSet = statusStatement.executeQuery(statusQuery);

            while (statusResultSet.next()) {
                pieChartData.add(new PieChart.Data(statusResultSet.getString("STATUS"), statusResultSet.getInt("COUNT")));
            }

            addPieChartPercentage(pieChartData);

            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Status Peralatan");
            pieChart.setLabelLineLength(10);

            maintenanceBox.getChildren().addAll(maintenanceLabel, barChart, pieChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maintenanceBox;
    }


    private static void addPieChartPercentage(ObservableList<PieChart.Data> data) {
        double total = 0;
        for (PieChart.Data d : data) {
            total += d.getPieValue();
        }
        for (PieChart.Data d : data) {
            double percentage = (d.getPieValue() / total) * 100;
            d.setName(d.getName() + " (" + String.format("%.1f", percentage) + "%)");
        }
    }


    public static VBox createTaskLocationAnalysis() {
        VBox taskLocationBox = new VBox();
        taskLocationBox.setAlignment(Pos.CENTER);
        taskLocationBox.setSpacing(10);
        taskLocationBox.setPadding(new Insets(20));
        Label taskLocationLabel = new Label("Analisis Lokasi Tugas");
        taskLocationLabel.setFont(Font.font("Arial", 16));

        try (Connection connection = OracleAPEXConnection.getConnection()) {
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Distribusi Lokasi Tugas");

            Map<String, Integer> locationCountMap = new HashMap<>();
            String[] tables = {"land", "sea", "air", "space"};
            for (String table : tables) {
                String query = "SELECT LOCATION FROM \"C4ISR PROJECT (BASIC) V2\"." + table;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    Clob clob = resultSet.getClob("LOCATION");
                    if (clob != null) {
                        try (Reader reader = clob.getCharacterStream();
                             BufferedReader bufferedReader = new BufferedReader(reader)) {
                            StringBuilder locationBuilder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                locationBuilder.append(line);
                            }
                            String location = locationBuilder.toString().trim();
                            if (!location.isEmpty()) {
                                locationCountMap.put(location, locationCountMap.getOrDefault(location, 0) + 1);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            double total = 0;
            for (Map.Entry<String, Integer> entry : locationCountMap.entrySet()) {
                total += entry.getValue();
            }
            for (Map.Entry<String, Integer> entry : locationCountMap.entrySet()) {
                double percentage = (entry.getValue() / total) * 100;
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            addPieChartPercentage(pieChartData);

            pieChart.setData(pieChartData);

            taskLocationBox.getChildren().addAll(taskLocationLabel, pieChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return taskLocationBox;
    }


    private static VBox createMissionAnalysis() {
        VBox missionBox = new VBox();
        missionBox.setAlignment(Pos.CENTER);
        missionBox.setSpacing(10);
        missionBox.setPadding(new Insets(20));
        Label missionLabel = new Label("Analisis Misi");
        missionLabel.setFont(Font.font("Arial", 16));

        try (Connection connection = OracleAPEXConnection.getConnection()) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            String pieQuery = "SELECT status, COUNT(*) as count FROM \"C4ISR PROJECT (BASIC) V2\".missions GROUP BY status";
            Statement pieStatement = connection.createStatement();
            ResultSet pieResultSet = pieStatement.executeQuery(pieQuery);

            while (pieResultSet.next()) {
                String status = pieResultSet.getString("status");
                int count = pieResultSet.getInt("count");
                pieChartData.add(new PieChart.Data(status, count));
            }

            addPieChartPercentage(pieChartData);

            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Status Misi");

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Progress Misi");
            xAxis.setLabel("Tanggal");
            yAxis.setLabel("Jumlah Misi");

            String[] statuses = {"Planning", "In Progress", "Completed", "Cancelled"};
            List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();

            for (String status : statuses) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(status);

                String lineQuery = "SELECT TO_CHAR(start_date, 'DD-MON-YYYY') as date_str, COUNT(*) as count " +
                        "FROM \"C4ISR PROJECT (BASIC) V2\".missions " +
                        "WHERE status = '" + status + "' " +
                        "GROUP BY TO_CHAR(start_date, 'DD-MON-YYYY')";
                Statement lineStatement = connection.createStatement();
                ResultSet lineResultSet = lineStatement.executeQuery(lineQuery);

                while (lineResultSet.next()) {
                    series.getData().add(new XYChart.Data<>(lineResultSet.getString("date_str"), lineResultSet.getInt("count")));
                }

                seriesList.add(series);
            }

            lineChart.getData().addAll(seriesList);

            missionBox.getChildren().addAll(missionLabel, pieChart, lineChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return missionBox;
    }


    private static VBox createSensorAnalysis() {
        VBox sensorBox = new VBox();
        sensorBox.setAlignment(Pos.CENTER);
        sensorBox.setSpacing(10);
        sensorBox.setPadding(new Insets(20));
        Label sensorLabel = new Label("Analisis Sensor");
        sensorLabel.setFont(Font.font("Arial", 16));

        try (Connection connection = OracleAPEXConnection.getConnection()) {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Tipe Sensor");
            xAxis.setLabel("Sensor");
            yAxis.setLabel("Jumlah");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            String query = "SELECT TYPE, COUNT(*) AS COUNT FROM \"C4ISR PROJECT (BASIC) V2\".SENSORS GROUP BY TYPE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String type = resultSet.getString("TYPE");
                int count = resultSet.getInt("COUNT");
                series.getData().add(new XYChart.Data<>(type, count));
            }

            barChart.getData().addAll(series);

            for (XYChart.Series<String, Number> s : barChart.getData()) {
                for (XYChart.Data<String, Number> data : s.getData()) {
                    int totalCount = getTotalCount(series);
                    double percentage = ((double)data.getYValue().intValue() / totalCount) * 100;
                    String label = String.format("%.1f%%", percentage);

                    StackPane stackPane = new StackPane();
                    stackPane.getChildren().addAll(data.getNode(), new Label(label));
                    StackPane.setAlignment(new Label(label), Pos.TOP_CENTER);

                    Tooltip.install(stackPane, new Tooltip(
                            String.format("%s : %d", data.getXValue(), data.getYValue().intValue())
                    ));

                    data.setNode(stackPane);
                }
            }

            sensorBox.getChildren().addAll(sensorLabel, barChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sensorBox;
    }

    private static int getTotalCount(XYChart.Series<String, Number> series) {
        int totalCount = 0;
        for (XYChart.Data<String, Number> data : series.getData()) {
            totalCount += data.getYValue().intValue();
        }
        return totalCount;
    }

}
