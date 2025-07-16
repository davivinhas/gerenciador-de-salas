package model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private int userId;
    private int spaceId;
    private ReservationStatus status;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private LocalDateTime creationDate;

    public Reservation(){
        this.creationDate = LocalDateTime.now();
        this.status = ReservationStatus.PENDING;
    }

    public Reservation(int userId, int spaceId, LocalDateTime startDateTime, LocalDateTime endDateTime, String description){
        this();
        this.userId = userId;
        this.spaceId = spaceId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.description = description;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getSpaceId() {
        return spaceId;
    }
    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }
    public ReservationStatus getStatus() {
        return status;
    }
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    // Métodos utilitários
    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", spaceId=" + spaceId +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reservation that = (Reservation) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}
