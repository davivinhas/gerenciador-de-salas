package model;

public class Auditorium extends Space {
    private int numOfSoundSystems;
    private int numOfStages;

    public Auditorium(){
        super();
    }

    public Auditorium(String name, String localization, int capacity, String availableHours, int numOfSoundSystems, int numOfStages) {
        super(name, localization, capacity, availableHours);
        this.numOfSoundSystems = numOfSoundSystems;
        this.numOfStages = numOfStages;
    }

    // Métodos utilitários
    @Override
    public String getType() {
        return "Auditorium";
    }

    @Override
    public String getSpecifiedDescription() {
        return String.format("Auditório - Soms: %d, Palcos: %d", numOfSoundSystems, numOfStages );
    }

    // Getters e Setters
    public int getNumOfStages() {
        return numOfStages;
    }

    public void setNumOfStages(int numOfStages) {
        this.numOfStages = numOfStages;
    }

    public int getNumOfSoundSystems() {
        return numOfSoundSystems;
    }

    public void setNumOfSoundSystems(int numOfSoundSystems) {
        this.numOfSoundSystems = numOfSoundSystems;
    }
}
