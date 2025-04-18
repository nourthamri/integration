package tn.esprit.entities;

public class Participant {

    public int id_part;
    public int id_user;
    public int id_event;

    public Participant(int id_user, int id_event) {
        this.id_user = id_user;
        this.id_event = id_event;
    }

    public Participant(int id_part, int id_user, int id_event) {
        this.id_part = id_part;
        this.id_user = id_user;
        this.id_event = id_event;
        System.out.println("🔹 Constructeur Participant : ID = " + id_part);
    }

    public Participant(int idUser) {
        this.id_user = idUser;
    }

    public int getId_part() {
        return this.id_part;
    }

    public int getId_user() {
        return this.id_user;
    }

    public int getId_event() {
        return this.id_event;
    }

    public void setId_part(int id_part) {
        this.id_part = id_part;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    @Override
    public String toString() {
        return "Participant ID: " + id_part + ", User ID: " + id_user + ", Event ID: " + id_event;
    }

}
