package models;

/**
 * Représente une ligne de détail dans une vente (médicament + quantité).
 */
public class DetailVente {

    private int id;
    private int venteId;
    private Medicament medicament;
    private int quantite;
    private double prixUnitaire;

    public DetailVente() {}

    public DetailVente(int id, int venteId, Medicament medicament,
                       int quantite, double prixUnitaire) {
        this.id = id;
        this.venteId = venteId;
        this.medicament = medicament;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public int getId() { return id; }
    public int getVenteId() { return venteId; }
    public Medicament getMedicament() { return medicament; }
    public int getQuantite() { return quantite; }
    public double getPrixUnitaire() { return prixUnitaire; }

    public void setId(int id) { this.id = id; }
    public void setVenteId(int venteId) { this.venteId = venteId; }
    public void setMedicament(Medicament medicament) { this.medicament = medicament; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    /**
     * Retourne le sous-total pour cette ligne.
     */
    public double getSousTotal() {
        return quantite * prixUnitaire;
    }

    @Override
    public String toString() {
        return medicament.getNom() + " x" + quantite + " — " + getSousTotal() + " MAD";
    }
}