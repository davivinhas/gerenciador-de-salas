package model;

public class Laboratory extends Space{
    private int numOfComputers;
    private String labType;

    public Laboratory(){
        super();
    }

    public Laboratory(String name, String localization, int capacity, String availableHours, int numOfComputers, String labType){
        super(name, localization, capacity, availableHours);
        this.numOfComputers = numOfComputers;
        this.labType = labType;
    }

    public String getType() {
        return "Laboratory";
    }

    public String getSpecifiedDescription(){
        return String.format("Laboratório de %s - Numero de Computadores: %d",
                labType, numOfComputers);
    }

    public String getLabType() {
        return labType;
    }

    public void setLabType(String labType) {
        this.labType = labType;
    }

    public int getNumOfComputers() {
        return numOfComputers;
    }

    public void setNumOfComputers(int numOfComputers) {
        this.numOfComputers = numOfComputers;
    }
}
