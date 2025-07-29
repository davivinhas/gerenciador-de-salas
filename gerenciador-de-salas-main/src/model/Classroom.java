package model;

public class Classroom extends Space{
    private int numOfBoards;
    private int numOfDataShows;
    public Classroom(){
        super();
    }
    public Classroom(String name, String localization, int capacity, String availableHours, int numOfWhiteBoards, int numOfDataShows){
        super(name, localization, capacity, availableHours);
        this.numOfBoards = numOfWhiteBoards;
        this.numOfDataShows = numOfDataShows;
    }

    // Métodos utilitários
    @Override
    public String getType() {
        return "Classroom";
    }

    @Override
    public String getSpecifiedDescription(){
        return String.format("Sala de Aula - DataShows: %s, Quadros: %s",
                numOfDataShows, numOfBoards);
    }

    // Getters e Setters
    public int getNumOfBoards() {
        return numOfBoards;
    }

    public void setNumOfBoards(int numOfWhiteBoards) {
        this.numOfBoards = numOfWhiteBoards;
    }

    public int getNumOfDataShows() {
        return numOfDataShows;
    }

    public void setNumOfDataShows(int numOfDataShows) {
        this.numOfDataShows = numOfDataShows;
    }
}
