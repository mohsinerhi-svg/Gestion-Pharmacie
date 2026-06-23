package models;

import java.time.LocalDate;

/**
 * Représente un médicament dans la pharmacie.
 */
public class Medicament {

    private int id;
    private String nom;
    private String description;
    private double prix;
    private int quantiteStock;
    private LocalDate dateExpiration;
    private String categorie;

    public Medicament() {}

    public Medicament(int id, String nom, String description, double prix,
                      int quantiteStock, LocalDate dateExpiration, String categorie) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.dateExpiration = dateExpiration;
        this.categorie = categorie;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public double getPrix() { return prix; }
    public int getQuantiteStock() { return quantiteStock; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public String getCategorie() { return categorie; }

    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setPrix(double prix) { this.prix = prix; }
    public void setQuantiteStock(int quantiteStock) { this.quantiteStock = quantiteStock; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    @Override
    public String toString() {
        return nom + " (" + categorie + ") — " + prix + " MAD";
    }
}