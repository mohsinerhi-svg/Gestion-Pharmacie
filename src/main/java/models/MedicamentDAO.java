package models;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les opérations CRUD sur la table medicament.
 */
public class MedicamentDAO {

    private Connection conn;

    public MedicamentDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance();
    }

    public void insert(Medicament m) throws SQLException {
        String sql = "INSERT INTO medicament (nom, description, prix, quantite_stock, date_expiration, categorie) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            ps.setDouble(3, m.getPrix());
            ps.setInt(4, m.getQuantiteStock());
            ps.setDate(5, m.getDateExpiration() != null ? Date.valueOf(m.getDateExpiration()) : null);
            ps.setString(6, m.getCategorie());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) m.setId(keys.getInt(1));
        }
    }

    public List<Medicament> findAll() throws SQLException {
        List<Medicament> list = new ArrayList<>();
        String sql = "SELECT * FROM medicament";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Medicament findById(int id) throws SQLException {
        String sql = "SELECT * FROM medicament WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public List<Medicament> findByNom(String nom) throws SQLException {
        List<Medicament> list = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE nom LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void update(Medicament m) throws SQLException {
        String sql = "UPDATE medicament SET nom=?, description=?, prix=?, quantite_stock=?, "
                   + "date_expiration=?, categorie=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            ps.setDouble(3, m.getPrix());
            ps.setInt(4, m.getQuantiteStock());
            ps.setDate(5, m.getDateExpiration() != null ? Date.valueOf(m.getDateExpiration()) : null);
            ps.setString(6, m.getCategorie());
            ps.setInt(7, m.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM medicament WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Medicament map(ResultSet rs) throws SQLException {
        LocalDate dateExp = rs.getDate("date_expiration") != null
                ? rs.getDate("date_expiration").toLocalDate() : null;
        return new Medicament(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("description"),
                rs.getDouble("prix"),
                rs.getInt("quantite_stock"),
                dateExp,
                rs.getString("categorie")
        );
    }
}