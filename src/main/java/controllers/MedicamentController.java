package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Medicament;
import models.MedicamentDAO;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Contrôleur JavaFX pour la gestion des médicaments.
 */
public class MedicamentController {

    @FXML private TableView<Medicament> table;
    @FXML private TableColumn<Medicament, Integer> colId;
    @FXML private TableColumn<Medicament, String>  colNom;
    @FXML private TableColumn<Medicament, String>  colCategorie;
    @FXML private TableColumn<Medicament, Double>  colPrix;
    @FXML private TableColumn<Medicament, Integer> colStock;
    @FXML private TableColumn<Medicament, LocalDate> colExpiration;

    @FXML private TextField     fieldNom;
    @FXML private TextField     fieldDescription;
    @FXML private TextField     fieldPrix;
    @FXML private TextField     fieldStock;
    @FXML private DatePicker    fieldExpiration;
    @FXML private TextField     fieldCategorie;
    @FXML private TextField     fieldRecherche;
    @FXML private Label         labelMessage;

    private MedicamentDAO dao;
    private ObservableList<Medicament> data = FXCollections.observableArrayList();
    private Medicament selected = null;

    @FXML
    public void initialize() {
        try {
            dao = new MedicamentDAO();
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
            colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
            colStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
            colExpiration.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
            table.setItems(data);
            loadAll();

            table.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
                if (val != null) fillForm(val);
            });
        } catch (SQLException e) {
            showMessage("Erreur de connexion : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            Medicament m = buildFromForm();
            dao.insert(m);
            loadAll();
            clearForm();
            showMessage("Médicament ajouté avec succès.", false);
        } catch (Exception e) {
            showMessage("Erreur : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleModifier() {
        if (selected == null) { showMessage("Sélectionnez un médicament.", true); return; }
        try {
            Medicament m = buildFromForm();
            m.setId(selected.getId());
            dao.update(m);
            loadAll();
            clearForm();
            showMessage("Médicament modifié avec succès.", false);
        } catch (Exception e) {
            showMessage("Erreur : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleSupprimer() {
        if (selected == null) { showMessage("Sélectionnez un médicament.", true); return; }
        try {
            dao.delete(selected.getId());
            loadAll();
            clearForm();
            showMessage("Médicament supprimé.", false);
        } catch (SQLException e) {
            showMessage("Erreur : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleRechercher() {
        try {
            String terme = fieldRecherche.getText().trim();
            if (terme.isEmpty()) {
                loadAll();
            } else {
                data.setAll(dao.findByNom(terme));
            }
        } catch (SQLException e) {
            showMessage("Erreur : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleEffacer() {
        clearForm();
        fieldRecherche.clear();
        try { loadAll(); } catch (SQLException e) { showMessage("Erreur : " + e.getMessage(), true); }
    }

    private void loadAll() throws SQLException {
        data.setAll(dao.findAll());
    }

    private Medicament buildFromForm() {
        Medicament m = new Medicament();
        m.setNom(fieldNom.getText().trim());
        m.setDescription(fieldDescription.getText().trim());
        m.setPrix(Double.parseDouble(fieldPrix.getText().trim()));
        m.setQuantiteStock(Integer.parseInt(fieldStock.getText().trim()));
        m.setDateExpiration(fieldExpiration.getValue());
        m.setCategorie(fieldCategorie.getText().trim());
        return m;
    }

    private void fillForm(Medicament m) {
        selected = m;
        fieldNom.setText(m.getNom());
        fieldDescription.setText(m.getDescription());
        fieldPrix.setText(String.valueOf(m.getPrix()));
        fieldStock.setText(String.valueOf(m.getQuantiteStock()));
        fieldExpiration.setValue(m.getDateExpiration());
        fieldCategorie.setText(m.getCategorie());
    }

    private void clearForm() {
        selected = null;
        fieldNom.clear();
        fieldDescription.clear();
        fieldPrix.clear();
        fieldStock.clear();
        fieldExpiration.setValue(null);
        fieldCategorie.clear();
        table.getSelectionModel().clearSelection();
    }

    private void showMessage(String msg, boolean error) {
        labelMessage.setText(msg);
        labelMessage.setStyle(error ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
}