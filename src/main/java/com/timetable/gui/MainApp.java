package com.timetable.gui;

import com.timetable.db.DatabaseManager;
import com.timetable.generator.TimetableGenerator;
import com.timetable.model.SemesterConfig;
import com.timetable.model.Staff;
import com.timetable.model.Subject;
import com.timetable.model.TimetableEntry;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private final List<Subject> subjects  = new ArrayList<>();
    private final List<Staff>   staffList = new ArrayList<>();
    private final DatabaseManager db      = new DatabaseManager();

    private static final String SIDEBAR_BG   = "#1a1f2e";
    private static final String SIDEBAR_TEXT = "#a0aec0";
    private static final String ACTIVE_COLOR = "#63b3ed";
    private static final String ACCENT_BLUE  = "#2b6cb0";
    private static final String BORDER_COLOR = "#e2e8f0";

    private StackPane contentArea;

    // track nav buttons to reset styles
    private Button btnDash;
    private Button btnSubjects;
    private Button btnStaff;
    private Button btnTimetable;

    @Override
    public void start(Stage stage) {
        try {
            db.connect();
            subjects.addAll(db.loadAllSubjects());
            staffList.addAll(db.loadAllStaff());
        } catch (Exception e) {
            showAlert("DB Error", "Could not connect to MySQL:\n" + e.getMessage());
        }

        HBox root = new HBox();
        root.setStyle("-fx-background-color: #f0f4f8;");

        VBox sidebar = buildSidebar();
        contentArea  = new StackPane();
        contentArea.setStyle("-fx-background-color: #f0f4f8;");
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        root.getChildren().addAll(sidebar, contentArea);
        showPage(buildDashboard());

        Scene scene = new Scene(root, 1100, 680);
        stage.setTitle("College Timetable Generator");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> db.close());
        stage.show();
    }

    // ─────────────────────────────────────────────────────────────
    // SIDEBAR
    // ─────────────────────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(210);
        sidebar.setMinWidth(210);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + ";");

        VBox logo = new VBox(2);
        logo.setPadding(new Insets(20, 16, 16, 16));
        logo.setStyle("-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 0 0 1 0;");
        Label title = new Label("Timetable Gen");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label sub = new Label("v2.0 — College Edition");
        sub.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 10px;");
        logo.getChildren().addAll(title, sub);

        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 0, 0, 0));

        Label navLabel = navSectionLabel("Menu");
        btnDash      = navButton("Dashboard", "# ");
        btnSubjects  = navButton("Subjects",  "S ");
        btnStaff     = navButton("Staff",     "P ");
        btnTimetable = navButton("Generate",  "G ");

        setActiveNav(btnDash);

        btnDash.setOnAction(e -> {
            setActiveNav(btnDash);
            showPage(buildDashboard());
        });
        btnSubjects.setOnAction(e -> {
            setActiveNav(btnSubjects);
            showPage(buildSubjectPage());
        });
        btnStaff.setOnAction(e -> {
            setActiveNav(btnStaff);
            showPage(buildStaffPage());
        });
        btnTimetable.setOnAction(e -> {
            setActiveNav(btnTimetable);
            showPage(buildGeneratePage());
        });

        nav.getChildren().addAll(navLabel, btnDash, btnSubjects, btnStaff, btnTimetable);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox dbStatus = new HBox(6);
        dbStatus.setPadding(new Insets(12, 16, 16, 16));
        dbStatus.setAlignment(Pos.CENTER_LEFT);
        dbStatus.setStyle("-fx-border-color: rgba(255,255,255,0.08); -fx-border-width: 1 0 0 0;");
        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: #48bb78; -fx-font-size: 8px;");
        Label dbLabel = new Label("MySQL connected");
        dbLabel.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 10px;");
        dbStatus.getChildren().addAll(dot, dbLabel);

        sidebar.getChildren().addAll(logo, nav, spacer, dbStatus);
        return sidebar;
    }

    private Button navButton(String text, String icon) {
        Button btn = new Button(icon + "  " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(9, 16, 9, 16));
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + SIDEBAR_TEXT + ";" +
            "-fx-font-size: 12px;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;"
        );
        return btn;
    }

    private void setActiveNav(Button active) {
        String inactive =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + SIDEBAR_TEXT + ";" +
            "-fx-font-size: 12px;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;";
        String activeStyle =
            "-fx-background-color: rgba(99,179,237,0.15);" +
            "-fx-text-fill: " + ACTIVE_COLOR + ";" +
            "-fx-font-size: 12px;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: " + ACTIVE_COLOR + ";" +
            "-fx-border-width: 0 3 0 0;" +
            "-fx-max-width: 999999;";

        btnDash.setStyle(inactive);
        btnSubjects.setStyle(inactive);
        btnStaff.setStyle(inactive);
        btnTimetable.setStyle(inactive);
        active.setStyle(activeStyle);
    }

    private Label navSectionLabel(String text) {
        Label l = new Label(text.toUpperCase());
        l.setPadding(new Insets(4, 16, 4, 16));
        l.setStyle("-fx-text-fill: #2d3748; -fx-font-size: 9px; -fx-font-weight: bold;");
        return l;
    }

    private void showPage(ScrollPane page) {
        contentArea.getChildren().setAll(page);
    }

    // ─────────────────────────────────────────────────────────────
    // DASHBOARD
    // ─────────────────────────────────────────────────────────────
    private ScrollPane buildDashboard() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(24));

        page.getChildren().add(pageHeading("Dashboard"));

        HBox cards = new HBox(12);
        cards.getChildren().addAll(
            statCard("Subjects",  String.valueOf(subjects.size()),  "added so far",  "#ebf8ff", "#2b6cb0"),
            statCard("Staff",     String.valueOf(staffList.size()), "assigned",      "#f0fff4", "#276749"),
            statCard("Sections",  "—",                             "not generated", "#fffbeb", "#7b341e"),
            statCard("DB Status", "Live",                          "MySQL 8.0",     "#faf5ff", "#553c9a")
        );

        VBox help = card();
        Label helpTitle = new Label("Getting Started");
        helpTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        Label s1 = stepLabel("1", "Go to Subjects  —  add all your subjects with credits");
        Label s2 = stepLabel("2", "Go to Staff  —  assign a teacher to each subject");
        Label s3 = stepLabel("3", "Go to Generate  —  set periods per day and click Generate");
        help.getChildren().addAll(helpTitle, s1, s2, s3);

        page.getChildren().addAll(cards, help);
        return scrollWrap(page);
    }

    // ─────────────────────────────────────────────────────────────
    // SUBJECTS PAGE
    // ─────────────────────────────────────────────────────────────
    private ScrollPane buildSubjectPage() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(24));
        page.getChildren().add(pageHeading("Subjects"));

        VBox formCard = card();
        formCard.getChildren().add(sectionTitle("Add Subject"));

        TextField codeF    = styledField("Subject Code  e.g. CS101");
        TextField nameF    = styledField("Subject Name");
        TextField creditsF = styledField("Credits  e.g. 4");
        creditsF.setMaxWidth(120);
        CheckBox labCB = new CheckBox("Has Lab Component");
        labCB.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a5568;");

        Button addBtn = primaryButton("+ Add Subject");
        Label  status = new Label();

        addBtn.setOnAction(e -> {
            try {
                String code  = codeF.getText().trim();
                String name  = nameF.getText().trim();
                int credits  = Integer.parseInt(creditsF.getText().trim());
                boolean lab  = labCB.isSelected();
                if (code.isEmpty() || name.isEmpty())
                    throw new IllegalArgumentException("Code and name are required.");
                Subject s = new Subject(code, name, credits, lab);
                subjects.add(s);
                db.saveSubject(s);
                codeF.clear(); nameF.clear(); creditsF.clear(); labCB.setSelected(false);
                setStatus(status, "Saved: " + name, true);
                showPage(buildSubjectPage());
            } catch (Exception ex) {
                setStatus(status, ex.getMessage(), false);
            }
        });

        HBox row1 = new HBox(10, codeF, nameF, creditsF);
        HBox.setHgrow(nameF, Priority.ALWAYS);
        HBox row2 = new HBox(12, labCB, addBtn, status);
        row2.setAlignment(Pos.CENTER_LEFT);
        formCard.getChildren().addAll(row1, row2);

        VBox tableCard = card();
        tableCard.getChildren().add(sectionTitle("All Subjects  (" + subjects.size() + ")"));

        TableView<Subject> table = new TableView<>(FXCollections.observableList(subjects));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 12px;");
        table.setPrefHeight(280);

        TableColumn<Subject, String> cCode = new TableColumn<>("Code");
        cCode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        cCode.setMinWidth(120);

        TableColumn<Subject, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        cName.setMinWidth(200);

        TableColumn<Subject, String> cCredits = new TableColumn<>("Credits");
        cCredits.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCredits())));
        cCredits.setMinWidth(80);

        TableColumn<Subject, String> cLab = new TableColumn<>("Has Lab?");
        cLab.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().hasLab() ? "Yes" : "No"));
        cLab.setMinWidth(80);

        table.getColumns().addAll(cCode, cName, cCredits, cLab);
        tableCard.getChildren().add(table);

        page.getChildren().addAll(formCard, tableCard);
        return scrollWrap(page);
    }

    // ─────────────────────────────────────────────────────────────
    // STAFF PAGE
    // ─────────────────────────────────────────────────────────────
    private ScrollPane buildStaffPage() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(24));
        page.getChildren().add(pageHeading("Staff"));

        VBox formCard = card();
        formCard.getChildren().add(sectionTitle("Add Staff Member"));

        TextField nameF = styledField("Staff Name");

        ComboBox<String> subjectCB = new ComboBox<>();
        subjectCB.setPromptText("Select subject");
        subjectCB.setStyle("-fx-font-size: 12px;");
        subjectCB.setPrefWidth(240);
        for (Subject s : subjects) {
            subjectCB.getItems().add(s.getCode() + " — " + s.getName());
        }

        Button addBtn = primaryButton("+ Add Staff");
        Label  status = new Label();

        addBtn.setOnAction(e -> {
            try {
                String name = nameF.getText().trim();
                String sel  = subjectCB.getValue();
                if (name.isEmpty() || sel == null)
                    throw new IllegalArgumentException("Name and subject required.");
                String code = sel.split(" — ")[0];
                Staff st = new Staff("S" + (staffList.size() + 1), name, code);
                staffList.add(st);
                db.saveStaff(st);
                nameF.clear();
                subjectCB.setValue(null);
                setStatus(status, "Saved: " + name, true);
                showPage(buildStaffPage());
            } catch (Exception ex) {
                setStatus(status, ex.getMessage(), false);
            }
        });

        HBox row = new HBox(10, nameF, subjectCB, addBtn, status);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameF, Priority.ALWAYS);
        formCard.getChildren().add(row);

        VBox tableCard = card();
        tableCard.getChildren().add(sectionTitle("All Staff  (" + staffList.size() + ")"));

        TableView<Staff> table = new TableView<>(FXCollections.observableList(staffList));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 12px;");
        table.setPrefHeight(280);

        TableColumn<Staff, String> sId = new TableColumn<>("ID");
        sId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getId()));
        sId.setMinWidth(60);

        TableColumn<Staff, String> sName = new TableColumn<>("Name");
        sName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        sName.setMinWidth(180);

        TableColumn<Staff, String> sCode = new TableColumn<>("Subject Code");
        sCode.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSubjectCode()));
        sCode.setMinWidth(130);

        table.getColumns().addAll(sId, sName, sCode);
        tableCard.getChildren().add(table);

        page.getChildren().addAll(formCard, tableCard);
        return scrollWrap(page);
    }

    // ─────────────────────────────────────────────────────────────
    // GENERATE PAGE
    // ─────────────────────────────────────────────────────────────
    private ScrollPane buildGeneratePage() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(24));
        page.getChildren().add(pageHeading("Generate Timetable"));

        VBox configCard = card();
        configCard.getChildren().add(sectionTitle("Configuration"));

        TextField periodsF  = styledField("6");  periodsF.setMaxWidth(80);
        TextField weeksF    = styledField("16"); weeksF.setMaxWidth(80);
        TextField sectionsF = styledField("2");  sectionsF.setMaxWidth(80);

        HBox configRow = new HBox(20,
            labeledField("Periods / day", periodsF),
            labeledField("Working weeks", weeksF),
            labeledField("Sections",      sectionsF)
        );
        configCard.getChildren().add(configRow);

        VBox ttCard = card();
        ttCard.getChildren().add(sectionTitle("Generated Schedule"));

        TableView<String[]> table = new TableView<>();
        table.setStyle("-fx-font-size: 11px;");
        table.setPrefHeight(360);
        ttCard.getChildren().add(table);

        Label status = new Label();

        Button genBtn = new Button("Generate & Save to MySQL");
        genBtn.setStyle(
            "-fx-background-color: " + ACCENT_BLUE + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 22 10 22;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );

        genBtn.setOnAction(e -> {
            try {
                int periods  = Integer.parseInt(periodsF.getText().trim());
                int weeks    = Integer.parseInt(weeksF.getText().trim());
                int sections = Integer.parseInt(sectionsF.getText().trim());

                if (subjects.isEmpty())  throw new IllegalStateException("Add subjects first.");
                if (staffList.isEmpty()) throw new IllegalStateException("Add staff first.");

                List<String> times = new ArrayList<>();
                int h = 9;
                for (int i = 0; i < periods; i++) {
                    times.add(String.format("%02d:00-%02d:00", h, h + 1));
                    h++;
                    if (h == 13) h = 14;
                }

                SemesterConfig cfg = new SemesterConfig(
                    weeks, 0, "09:00", "13:00", "14:00", "17:00", periods, times);

                // Build columns
                table.getColumns().clear();
                table.getItems().clear();

                TableColumn<String[], String> dayCol = new TableColumn<>("Day");
                dayCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
                dayCol.setMinWidth(80);
                table.getColumns().add(dayCol);

                for (int p = 0; p < periods; p++) {
                    final int pi = p + 1;
                    final String timeLabel = times.get(p);
                    TableColumn<String[], String> col = new TableColumn<>(timeLabel);
                    col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[pi]));
                    col.setMinWidth(140);

                    col.setCellFactory(tc -> new TableCell<String[], String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setStyle("");
                            } else if (item.contains("LAB-1")) {
                                setText(item);
                                setStyle("-fx-background-color: #f0fff4; -fx-text-fill: #276749; -fx-font-weight: bold;");
                            } else if (item.contains("LAB-2")) {
                                setText(item);
                                setStyle("-fx-background-color: #fffbeb; -fx-text-fill: #7b341e; -fx-font-weight: bold;");
                            } else if (item.equals("--")) {
                                setText(item);
                                setStyle("-fx-text-fill: #cbd5e0;");
                            } else {
                                setText(item);
                                setStyle("-fx-background-color: #ebf8ff; -fx-text-fill: #2c5282;");
                            }
                        }
                    });
                    table.getColumns().add(col);
                }

                String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

                for (int sec = 0; sec < sections; sec++) {
                    String sectionName = "Section-" + (char)('A' + sec);
                    db.clearTimetableForSection(sectionName);

                    TimetableGenerator gen = new TimetableGenerator(subjects, staffList, cfg);
                    TimetableEntry[][] tt  = gen.generate();

                    // Section header row
                    String[] hdr = new String[periods + 1];
                    hdr[0] = "-- " + sectionName + " --";
                    for (int p = 1; p <= periods; p++) hdr[p] = "";
                    table.getItems().add(hdr);

                    for (int d = 0; d < 5; d++) {
                        String[] row = new String[periods + 1];
                        row[0] = dayNames[d];
                        for (int p = 0; p < periods; p++) {
                            if (tt[d][p] != null) {
                                row[p + 1] = tt[d][p].toString();
                                db.saveTimetableEntry(sectionName, d, p, tt[d][p]);
                            } else {
                                row[p + 1] = "--";
                            }
                        }
                        table.getItems().add(row);
                    }
                }

                setStatus(status, "Timetable saved to MySQL successfully!", true);

            } catch (Exception ex) {
                setStatus(status, ex.getMessage(), false);
            }
        });

        HBox btnRow = new HBox(14, genBtn, status);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        page.getChildren().addAll(configCard, btnRow, ttCard);
        return scrollWrap(page);
    }

    // ─────────────────────────────────────────────────────────────
    // UI HELPERS
    // ─────────────────────────────────────────────────────────────
    private Label pageHeading(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a202c;");
        return l;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2d3748; -fx-padding: 0 0 6 0;");
        return l;
    }

    private VBox card() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(18));
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        return box;
    }

    private VBox statCard(String label, String value, String sub,
                          String bgColor, String textColor) {
        VBox c = new VBox(4);
        c.setPadding(new Insets(16));
        c.setMinWidth(140);
        c.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        Label lbl = new Label(label.toUpperCase());
        lbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #718096;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        Label subLbl = new Label(sub);
        subLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #a0aec0;");
        c.getChildren().addAll(lbl, val, subLbl);
        HBox.setHgrow(c, Priority.ALWAYS);
        return c;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-padding: 8 12 8 12;" +
            "-fx-background-color: #f7fafc;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + ACCENT_BLUE + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 18 8 18;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        return btn;
    }

    private VBox labeledField(String label, TextField field) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: #718096;");
        return new VBox(4, l, field);
    }

    private Label stepLabel(String num, String text) {
        Label l = new Label("  " + num + ".  " + text);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a5568; -fx-padding: 3 0 3 0;");
        return l;
    }

    private void setStatus(Label l, String msg, boolean ok) {
        l.setText(msg);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (ok ? "#276749" : "#c53030") + ";");
    }

    private ScrollPane scrollWrap(VBox content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}