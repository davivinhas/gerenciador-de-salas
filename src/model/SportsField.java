package model;

public class SportsField extends Space{
    private int numOfGoals;
    private int numOfNets;
    private int numOfHoops;

    public SportsField(){
        super();
    }

    public SportsField(String name, String localization, int capacity, String availableHours, int numOfGoals, int numOfVolleyballNets, int numOfBasketballHoops){
        super(name, localization, capacity, availableHours);
        this.numOfGoals = numOfGoals;
        this.numOfNets = numOfVolleyballNets;
        this.numOfHoops = numOfBasketballHoops;
    }

    // Métodos utilitários
    @Override
    public String getType() {
        return "SportsField";
    }

    @Override
    public String getSpecifiedDescription(){
        return String.format("Quadra - Gols: %s, Redes: %s, Aros: %s",
                numOfGoals, numOfNets, numOfHoops);
    }

    // Getters e Setters
    public int getNumOfGoals() {
        return numOfGoals;
    }

    public void setNumOfGoals(int numOfGoals) {
        this.numOfGoals = numOfGoals;
    }

    public int getNumOfHoops() {
        return numOfHoops;
    }

    public void setNumOfHoops(int numOfHoops) {
        this.numOfHoops = numOfHoops;
    }

    public int getNumOfNets() {
        return numOfNets;
    }

    public void setNumOfNets(int numOfNets) {
        this.numOfNets = numOfNets;
    }
}
