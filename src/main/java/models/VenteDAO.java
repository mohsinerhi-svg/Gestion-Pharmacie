package models;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les opérations CRUD sur les tables vente et detail_vente.
 */
public class VenteDAO {

    private Connection conn;
    private ClientDAO clientDAO;
    private MedicamentDAO medicamentDAO;

    public VenteDAO() throws SQLException {
        this.conn          = DatabaseConnection.getInstance();
        this.clientDAO     = new ClientDAO();
        this.medicamentDAO = new MedicamentDAO();
    }

    public void insert(Vente v) throws SQLException {
        conn.setAutoCommit(false);
        try {
            String sqlVente = "INSERT INTO vente (client_id, date_vente, montant_total) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlVente, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, v.getClient().getId());
                ps.setTimestamp(2, Timestamp.valueOf(v.getDateVente()));
                ps.setDouble(3, v.getMontantTotal());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) v.setId(keys.getInt(1));
            }

            String sqlDetail = "INSERT INTO detail_vente (vente_id, medicament_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
            String sqlStock  = "UPDATE medicament SET quantite_stock = quantite_stock - ? WHERE id = ?";
            for (DetailVente d : v.getDetails()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                    ps.setInt(1, v.getId());
                    ps.setInt(2, d.getMedicament().getId());
                    ps.setInt(3, d.getQuantite());
                    ps.setDouble(4, d.getPrixUnitaire());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(sqlStock)) {
                    ps.setInt(1, d.getQuantite());
                    ps.setInt(2, d.getMedicament().getId());
                    ps.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Vente> findAll() throws SQLException {
        List<Vente> list = new ArrayList<>();
        String sql = "SELECT * FROM vente ORDER BY date_vente DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Vente v = mapVente(rs);
                v.setDetails(findDetailsByVenteId(v.getId()));
                list.add(v);
            }
        }
        return list;
    }

    public Vente findById(int id) throws SQLException {
        String sql = "SELECT * FROM vente WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Vente v = mapVente(rs);
                v.setDetails(findDetailsByVenteId(v.getId()));
                return v;
            }
        }
        return null;
    }

    public List<DetailVente> findDetailsByVenteId(int venteId) throws SQLException {
        List<DetailVente> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_vente WHERE vente_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, venteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Medicament m = medicamentDAO.findById(rs.getInt("medicament_id"));
                list.add(new DetailVente(
                        rs.getInt("id"),
                        venteId,
                        m,
                        rs.getInt("quantite"),
                        rs.getDouble("prix_unitaire")
                ));
            }
        }
        return list;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM vente WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Vente mapVente(ResultSet rs) throws SQLException {
        Client client = clientDAO.findById(rs.getInt("client_id"));
        LocalDateTime dateVente = rs.getTimestamp("date_vente").toLocalDateTime();
        return new Vente(
                rs.getInt("id"),
                client,
                dateVente,
                rs.getDouble("montant_total")
        );
    }
}