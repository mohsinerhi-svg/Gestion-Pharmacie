package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur JavaFX pour la gestion des ventes.
 */
public class VenteController {

    @FXML private TableView<Vente>              tableVentes;
    @FXML private TableColumn<Vente, Integer>   colVenteId;
    @FXML private TableColumn<Vente, String>    colVenteClient;
    @FXML private TableColumn<Vente, String>    colVenteDate;
    @FXML private TableColumn<Vente, Double>    colVenteTotal;

    @FXML private TableView<DetailVente>             tableDetails;
    @FXML private TableColumn<DetailVente, String>   colDetailMedicament;
    @FXML private TableColumn<DetailVente, Integer>  colDetailQuantite;
    @FXML private TableColumn<DetailVente, Double>   colDetailPrix;
    @FXML private TableColumn<DetailVente, Double>   colDetailSousTotal;

    @FXML private ComboBox<Client>      comboClient;
    @FXML private ComboBox<Medicament>  comboMedicament;
    @FXML private TextField             fieldQuantite;
    @FXML private Label                 labelTotal;
    @FXML private Label                 labelMessage;

    private VenteDAO      venteDAO;
    private ClientDAO     clientDAO;
    private MedicamentDAO medicamentDAO;

    private ObservableList<Vente>       ventesData  = FXCollections.observableArrayList();
    private ObservableList<DetailVente> detailsData = FXCollections.observableArrayList();
    private List<DetailVente>           panier      = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            venteDAO      = new VenteDAO();
            clientDAO     = new ClientDAO();
            medicamentDAO = new MedicamentDAO();

            colVenteId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colVenteTotal.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
            colVenteClient.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getClient().getPrenom() + " " + data.getValue().getClient().getNom()
                ));
            colVenteDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getDateVente().toString()
                ));

            colDetailMedicament.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getMedicament().getNom()
                ));
            colDetailQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
            colDetailPrix.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
            colDetailSousTotal.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getSousTotal()));

            tableVentes.setItems(ventesData);
            tableDetails.setItems(detailsData);

            comboClient.setItems(FXCollections.observableArrayList(clientDAO.findAll()));
            comboMedicament.setItems(FXCollections.observableArrayList(medicamentDAO.findAll()));

            tableVentes.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
                if (val != null) detailsData.setAll(val.getDetails());
            });

            loadAll();
        } catch (SQLException e) {
            showMessage("Erreur de connexion : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleAjouterAuPanier() {
        Medicament m = comboMedicament.getValue();
        if (m == null) { showMessage("Sélectionnez un médicament.", true); return; }
        try {
            int qte = Integer.parseInt(fieldQuantite.getText().trim());
            if (qte <= 0 || qte > m.getQuantiteStock()) {
                showMessage("Quantité invalide ou stock insuffisant (stock : " + m.getQuantiteStock() + ").", true);
                return;
            }
            DetailVente d = new DetailVente(0, 0, m, qte, m.getPrix());
            panier.add(d);
            detailsData.setAll(panier);
            updateTotal();
            fieldQuantite.clear();
            showMessage("Médicament ajouté au panier.", false);
        } catch (NumberFormatException e) {
            showMessage("Quantité invalide.", true);
        }
    }

    @FXML
    private void handleValiderVente() {
        Client client = comboClient.getValue();
        if (client == null) { showMessage("Sélectionnez un client.", true); return; }
        if (panier.isEmpty()) { showMessage("Le panier est vide.", true); return; }
        try {
            Vente v = new Vente();
            v.setClient(client);
            v.setDateVente(LocalDateTime.now());
            v.setDetails(new ArrayList<>(panier));
            v.calculerMontantTotal();
            venteDAO.insert(v);
            loadAll();
            panier.clear();
            detailsData.clear();
            comboClient.setValue(null);
            labelTotal.setText("Total : 0.00 MAD");
            comboMedicament.setItems(FXCollections.observableArrayList(medicamentDAO.findAll()));
            showMessage("Vente enregistrée avec succès.", false);
        } catch (SQLException e) {
            showMessage("Erreur : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleSupprimerVente() {
        Vente v = tableVentes.getSelectionModel().getSelectedItem();
        if (v == null) { showMessage("Sélectionnez une vente.", true); return; }
        try {
            venteDAO.delete(v.getId());
            loadAll();
            detailsData.clear();
            showMessage("Vente supprimée.", false);
        } catch (SQLException e) {
            showMessage("Erreur : " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleViderPanier() {
        panier.clear();
        detailsData.clear();
        labelTotal.setText("Total : 0.00 MAD");
    }

    private void loadAll() throws SQLException {
        ventesData.setAll(venteDAO.findAll());
    }

    private void updateTotal() {
        double total = panier.stream().mapToDouble(DetailVente::getSousTotal).sum();
        labelTotal.setText(String.format("Total : %.2f MAD", total));
    }

    private void showMessage(String msg, boolean error) {
        labelMessage.setText(msg);
        labelMessage.setStyle(error ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
}