import java.time.LocalDate;
class Card {
    String id;
    String fingerprint;
    LocalDate expiryDate;

    public Card(String id, String fingerprint, LocalDate expiryDate) {
        this.id = id;
        this.fingerprint = fingerprint;
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    @Override
    public String toString() {
        return id;
    }

    
}
