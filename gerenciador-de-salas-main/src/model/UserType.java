package model;

public enum UserType {
    ADMIN("Administrador"),
    USER("Usuário");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
