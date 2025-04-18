package tn.esprit.services;

import tn.esprit.entities.Event;
import tn.esprit.entities.Participant;
import tn.esprit.util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements IService<Event> {
         Connection connection = MyConnection.getInstance().getConnection();

        public ServiceEvent() {
        }

    public void ajouter_t(Event event) throws SQLException {
        if (event.getDate() == null) {
            throw new SQLException("Erreur : la date de l'événement ne peut pas être null !");
        }

        String sql = "INSERT INTO event (nom, image, description, lieu, date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = MyConnection.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, event.getNom());
            statement.setString(2, event.getImage());
            statement.setString(3, event.getDesc());
            statement.setString(4, event.getLieu());
            statement.setTimestamp(5, Timestamp.valueOf(event.getDate()));

            statement.executeUpdate();
            System.out.println("✅ Événement ajouté avec succès !");
        }
    }


    public void modifier_t(Event event) throws SQLException {
        String sql = "UPDATE event SET nom=?, image=?, description=?, lieu=?, date=? WHERE id_event=?";

        try (Connection conn = MyConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, event.getNom());
            ps.setString(2, event.getImage());
            ps.setString(3, event.getDesc());
            ps.setString(4, event.getLieu());
            ps.setTimestamp(5, Timestamp.valueOf(event.getDate()));
            ps.setInt(6, event.getId_event());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Événement mis à jour !");
            } else {
                System.out.println("⚠ Aucun événement mis à jour !");
            }
        }
    }


    public void supprimer_t(int id) throws SQLException {
            String sql = "DELETE FROM `event` WHERE id_event =?";
            PreparedStatement ps = this.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("bien supprimer");
        }

    public List<Event> afficher_t() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event";

        try (Connection conn = MyConnection.getInstance().getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id_event"),
                        rs.getString("nom"),
                        rs.getString("image"),
                        rs.getString("description"),
                        rs.getString("lieu"),
                        rs.getTimestamp("date").toLocalDateTime()
                );
                events.add(event);
            }
        }
        return events;
    }


    @Override
    public int modifier(Event event, int id) throws SQLException {
        return 0;
    }

    @Override
    public boolean ajouteru(Event event) throws SQLException {
        return false;
    }

    @Override
    public boolean modifieru(Event event) throws SQLException {
        return false;
    }

    @Override
    public boolean supprimeru(int id) throws SQLException {
        return false;
    }

    @Override
    public void ajouterEquipeAMatch(int idMatch, int idEquipe) throws SQLException {

    }

    @Override
    public void supprimerEquipeDuMatch(int idMatch, int idEquipe) throws SQLException {

    }

    @Override
    public List<Integer> getEquipesParMatch(int idMatch) throws SQLException {
        return List.of();
    }

    @Override
    public List<Integer> getMatchsParEquipe(int idEquipe) throws SQLException {
        return List.of();
    }

    public void participer(int idUser, int idEvent) throws SQLException {
        String checkSql = "SELECT u.id_user, e.id_event FROM user u CROSS JOIN event e WHERE u.id_user = ? AND e.id_event = ?";

        try (Connection conn = MyConnection.getInstance().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, idUser);
            checkStmt.setInt(2, idEvent);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Erreur : Utilisateur ou événement inexistant !");
                }
            }

            String sql = "INSERT INTO participant (id_user, id_event) VALUES (?, ?)";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, idUser);
                statement.setInt(2, idEvent);
                statement.executeUpdate();
                System.out.println("✅ Participation ajoutée !");
            }
        }
    }


    public Event getEventById(int idEvent) {
        Event event = null;
        String query = "SELECT * FROM event WHERE id_event = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, idEvent);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    event = new Event();
                    event.setId_event(rs.getInt("id_event"));
                    event.setNom(rs.getString("nom"));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return event;
    }
    public List<Participant> getParticipantsByEvent(int id_event) throws SQLException {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT id_part, id_user, id_event FROM participant WHERE id_event = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id_event);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id_part = rs.getInt("id_part");
                int id_user = rs.getInt("id_user");
                int id_event_fetched = rs.getInt("id_event");

                System.out.println("🔍 Chargé depuis DB (par Event) : ID = " + id_part);

                participants.add(new Participant(id_part, id_user, id_event_fetched));
            }
        }
        return participants;
    }


}


