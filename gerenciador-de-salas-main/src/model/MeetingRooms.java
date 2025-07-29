package model;

public class MeetingRooms extends Space{
    private int numOfProjectors;
    private int numOfVideoConferences;

    public MeetingRooms(){
        super();
    }

    public MeetingRooms(String name, String localization, int capacity, String availableHours, int numOfProjectors, int numOfVideoConferences){
        super(name, localization, capacity, availableHours);
        this.numOfProjectors = numOfProjectors;
        this.numOfVideoConferences = numOfVideoConferences;
    }

    @Override
    public String getType() {
        return "MeetingRooms";
    }

    @Override
    public String getSpecifiedDescription(){
        return String.format("Sala de Reunião - Projetores: %d, Videoconferências: %d", numOfProjectors, numOfVideoConferences);
    }

    public int getNumOfVideoConferences() {
        return numOfVideoConferences;
    }

    public void setNumOfVideoConferences(int numOfVideoConferences) {
        this.numOfVideoConferences = numOfVideoConferences;
    }

    public int getNumOfProjectors() {
        return numOfProjectors;
    }

    public void setNumOfProjectors(int numOfProjectors) {
        this.numOfProjectors = numOfProjectors;
    }
}
