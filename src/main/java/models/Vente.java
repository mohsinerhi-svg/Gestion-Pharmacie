package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une vente effectuée à un client.
 */
public class Vente {

    private int id;
    private Client client;
    private LocalDateTime dateVente;
    private double montantTotal;
    private List<DetailVente> details;

    public Vente() {
        this.details = new ArrayList<>();
    }

    public Vente(int id, Client client, LocalDateTime dateVente, double montantTotal) {
        this.id = id;
        this.client = client;
        this.dateVente = dateVente;
        this.montantTotal = montantTotal;
        this.details = new ArrayList<>();
    }

    public int getId() { return id; }
    public Client getClient() { return client; }
    public LocalDateTime getDateVente() { return dateVente; }
    public double getMontantTotal() { return montantTotal; }
    public List<DetailVente> getDetails() { return details; }

    public void setId(int id) { this.id = id; }
    public void setClient(Client client) { this.client = client; }
    public void setDateVente(LocalDateTime dateVente) { this.dateVente = dateVente; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }
    public void setDetails(List<DetailVente> details) { this.details = details; }

    /**
     * Recalcule le montant total à partir des détails de vente.
     */
    public void calculerMontantTotal() {
        this.montantTotal = details.stream()
                .mapToDouble(d -> d.getPrixUnitaire() * d.getQuantite())
                .sum();
    }

    @Override
    public String toString() {
        return "Vente #" + id + " — " + client.getPrenom() + " " + client.getNom()
                + " — " + montantTotal + " MAD";
    }
}