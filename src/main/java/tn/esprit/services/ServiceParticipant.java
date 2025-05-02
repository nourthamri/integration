package tn.esprit.services;

import tn.esprit.entities.Participant;
import tn.esprit.util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceParticipant implements IService<Participant> {
    private Connection connection = MyConnection.getInstance().getConnection();

    public ServiceParticipant() {
    }

    public boolean ajouter_t(Participant participant) throws SQLException {
        String sql = "INSERT INTO participant (id_user,id_event) VALUES ( ?, ?)";
        PreparedStatement statement = this.connection.prepareStatement(sql);
        statement.setInt(1, participant.getId_user());
        statement.setInt(2, participant.getId_event());
        statement.executeUpdate();
        System.out.println("bien ajouté");
        return false;
    }

    public void modifier_t(Participant participant) throws SQLException {
        String sql = "UPDATE `participant` SET `id_user`=?,`id_event`=? WHERE id_part = ?";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setInt(1, participant.getId_user());
        ps.setInt(2, participant.getId_event());
        ps.setInt(3, participant.getId_part());
        ps.executeUpdate();
        System.out.println("bien modifie");
    }

    public void supprimer_t(int id) throws SQLException {
        String sql = "DELETE FROM `participant` WHERE id_part =?";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("bien supprimer");
    }
    public void annulerParticipation(int idUser) throws SQLException {
        String sql = "DELETE FROM participant WHERE id_user = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            stmt.executeUpdate();
        }
    }


    public List<Participant> afficher_t() throws SQLException {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT id_part, id_user, id_event FROM participant";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id_part = rs.getInt("id_part");
                int id_user = rs.getInt("id_user");
                int id_event = rs.getInt("id_event");
                System.out.println("🔍 Chargé depuis DB : ID = " + id_part);
                participants.add(new Participant(id_part, id_user, id_event));
            }
        }
        return participants;
    }
    public boolean aDejaParticipe(int idUser ) throws SQLException {
        String sql = "SELECT COUNT(*) FROM participant WHERE id_user = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, idUser );
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }

    @Override
    public int modifier(Participant participant, int id) throws SQLException {
        return 0;
    }

    @Override
    public boolean ajouteru(Participant participant) throws SQLException {
        return false;
    }

    @Override
    public boolean modifieru(Participant participant) throws SQLException {
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

    public String getUserNameById(int idUser) throws SQLException {
        String sql = "SELECT username, lastname FROM user WHERE id_user = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, idUser);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String nom = resultSet.getString("nom");
            String prenom = resultSet.getString("prenom");
            return nom + " " + prenom;
        }
        return "Utilisateur inconnu";
    }
}

