package model;

public enum ReservationStatus {
    PENDING("Pendente"),
    CONFIRMED("Confirmada"),
    CANCELED("Cancelada"),
    CONCLUDED("Concluída");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
