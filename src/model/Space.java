package model;
import java.util.List;
import java.time.LocalDateTime;

public abstract class Space {
    protected int id;
    protected String name;
    protected String localization;
    protected int capacity;
    protected String availableHours;
    protected LocalDateTime creationDate;

    public Space(){
        this.creationDate = LocalDateTime.now();
    }

    public Space(String name, String localization, int capacity, String availableHours){
        this();
        this.name = name;
        this.localization = localization;
        this.capacity = capacity;
        this.availableHours = availableHours;
    }


    // Métodos abstratos
    public abstract String getType();
    public abstract String getSpecifiedDescription();

    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLocalization() {
        return localization;
    }
    public void setLocalization(String localization) {
        this.localization = localization;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(String availableHours) {
        this.availableHours = availableHours;
    }

    // Métodos utilitários
    public String getDescription(){
        return String.format("%s - %s (Capacidade: %d)", this.name, this.localization, this.capacity);
    }

    @Override
    public String toString() {
        return "Space{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", localization='" + localization + '\'' +
                ", capacity=" + capacity +
                ", availableHours='" + availableHours + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Space space = (Space) obj;
        return id == space.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

} // Fim da classe abstrata Space
