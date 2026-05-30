package com.example.project101;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

// Official Java Database Imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SuppressWarnings("CallToPrintStackTrace")
public class Controller {

    @FXML private TextField txtName;
    @FXML private TextField txtCourse;
    @FXML private ChoiceBox<Object> cbYear; // Generic Object type stops SceneBuilder from crashing

    @FXML private TableView<Student> table;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colCourse;
    @FXML private TableColumn<Student, String> colYear;

    private final ObservableList<Student> list = FXCollections.observableArrayList();
    private Connection conn;
    private int selectedId = -1;

    @FXML
    public void initialize() {
        // This opens up the link to your database when the app loads
        conn = DBConnection.connect();

        // Load Enum values to ChoiceBox
        cbYear.getItems().setAll((Object[]) YearLevel.values());

        // Table Columns Binding
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colCourse.setCellValueFactory(data -> data.getValue().courseProperty());
        colYear.setCellValueFactory(data -> data.getValue().yearLevelProperty());

        loadData();

        // Row click event
        table.setOnMouseClicked(e -> {
            Student s = table.getSelectionModel().getSelectedItem();
            if (s != null) {
                selectedId = s.getId();
                txtName.setText(s.getName());
                txtCourse.setText(s.getCourse());

                // Convert String back to Enum mapping
                for (YearLevel y : YearLevel.values()) {
                    if (y.toString().equals(s.getYearLevel())) {
                        cbYear.setValue(y);
                    }
                }
            }
        });
    }

    private void loadData() {
        if (conn == null) return; // Prevent crashing if database isn't connected
        list.clear();
        try {
            String query = "SELECT * FROM students";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getString("year_level")
                ));
            }
            table.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addStudent() {
        if (conn == null) {
            System.out.println("Database not connected. Check DBConnection.java credentials.");
            return;
        }
        if (cbYear.getValue() == null || txtName.getText().trim().isEmpty() || txtCourse.getText().trim().isEmpty()) {
            System.out.println("Please complete all fields first.");
            return;
        }
        try {
            String query = "INSERT INTO students(name, course, year_level) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);

            pst.setString(1, txtName.getText());
            pst.setString(2, txtCourse.getText());
            pst.setString(3, cbYear.getValue().toString());

            pst.executeUpdate();
            loadData();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateStudent() {
        if (selectedId == -1 || cbYear.getValue() == null || txtName.getText().trim().isEmpty() || txtCourse.getText().trim().isEmpty()) {
            System.out.println("Select a student record to modify.");
            return;
        }
        try {
            String query = "UPDATE students SET name=?, course=?, year_level=? WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(query);

            pst.setString(1, txtName.getText());
            pst.setString(2, txtCourse.getText());
            pst.setString(3, cbYear.getValue().toString());
            pst.setInt(4, selectedId);

            pst.executeUpdate();
            loadData();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteStudent() {
        if (selectedId == -1) {
            System.out.println("Choose a student record from the table first.");
            return;
        }
        try {
            String query = "DELETE FROM students WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(query);

            pst.setInt(1, selectedId);
            pst.executeUpdate();

            loadData();
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFields() {
        txtName.clear();
        txtCourse.clear();
        cbYear.setValue(null);
        selectedId = -1;
    }
}