package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Client;
import models.ClientDAO;

import java.sql.SQLException;

/**
 * Contrôleur JavaFX pour la gestion des clients.
 */
public class ClientController {

    @FXML private TableView<Client> table;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String>  colNom;
    @FXML private TableColumn<Client, String>  colPrenom;
    @FXML private TableColumn<Client, String>  colTelephone;
    @FXML private TableColumn<Client, String>  colEmail;

    @FXML private TextField fieldNom;
    @FXML private TextField fieldPrenom;
    @FXML private TextField fieldTelephone;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldRecherche;
    @FXML private Label     labelMessage;

    private ClientDAO dao;
    private ObservableList<Client> data = FXCollections.observableArrayList();
    private Client selected = null;

    @FXML
    public void initialize() {
        try {
            dao = new ClientDAO();
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
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
            Client c = buildFromForm();
            dao.insert(c);
            loadAll();
            clearForm();
            showMessage("Client ajouté avec succès.", false);
        } catch (Exception e) {
            showMessage("Erreur : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleModifier() {
        if (selected == null) { showMessage("Sélectionnez un client.", true); return; }
        try {
            Client c = buildFromForm();
            c.setId(selected.getId());
            dao.update(c);
            loadAll();
            clearForm();
            showMessage("Client modifié avec succès.", false);
        } catch (Exception e) {
            showMessage("Erreur : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleSupprimer() {
        if (selected == null) { showMessage("Sélectionnez un client.", true); return; }
        try {
            dao.delete(selected.getId());
            loadAll();
            clearForm();
            showMessage("Client supprimé.", false);
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

    private Client buildFromForm() {
        Client c = new Client();
        c.setNom(fieldNom.getText().trim());
        c.setPrenom(fieldPrenom.getText().trim());
        c.setTelephone(fieldTelephone.getText().trim());
        c.setEmail(fieldEmail.getText().trim());
        return c;
    }

    private void fillForm(Client c) {
        selected = c;
        fieldNom.setText(c.getNom());
        fieldPrenom.setText(c.getPrenom());
        fieldTelephone.setText(c.getTelephone());
        fieldEmail.setText(c.getEmail());
    }

    private void clearForm() {
        selected = null;
        fieldNom.clear();
        fieldPrenom.clear();
        fieldTelephone.clear();
        fieldEmail.clear();
        table.getSelectionModel().clearSelection();
    }

    private void showMessage(String msg, boolean error) {
        labelMessage.setText(msg);
        labelMessage.setStyle(error ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
}