package f@irview.users;

public abstract class User { 
    private final String name;
    private final String affiliation;

    public User(String name, String affiliation) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (affiliation == null || affiliation.isBlank()) {
            throw new IllegalArgumentException("Affiliation cannot be empty.");
        }

        this.name = name;
        this.affiliation = affiliation;
    }

    public String getName() {
        return name;
    }

    public String getAffiliation() {
        return affiliation;
    }
}