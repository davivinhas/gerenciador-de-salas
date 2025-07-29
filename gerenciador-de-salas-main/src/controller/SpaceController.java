package controller;

import dao.SpaceDao;
import model.*;
import java.util.*;
import util.Logger;

public class SpaceController {
    private final SpaceDao spaceDao;

    public SpaceController() {
        this.spaceDao = new SpaceDao();
    }

    //cadastrar espaços

    //sala de aula
    public boolean createClassroom(String name, String localization, int capacity, String availableHours, int numOfWhiteBoards, int numOfDataShows){
        Classroom classroom = new Classroom(name, localization, capacity, availableHours, numOfWhiteBoards, numOfDataShows);
        boolean success = spaceDao.insert(classroom);
        Logger.logIfSuccess(success, "Espaço criado: Sala de Aula - " + name);
        return success;
    }

    //laboratorio
    public boolean createLaboratory(String name, String localization, int capacity, String availableHours, int numOfComputers, String labType) {
        Laboratory lab = new Laboratory(name, localization, capacity, availableHours, numOfComputers, labType);
        boolean success = spaceDao.insert(lab);
        Logger.logIfSuccess(success, "Espaço criado: Laboratorio - " + name);
        return success;
    }

    //sala de reunião
    public boolean createMeetingRoom(String name, String localization, int capacity, String availableHours, int numOfProjectors, int numOfVideoConferences) {
        MeetingRooms room = new MeetingRooms(name, localization, capacity, availableHours, numOfProjectors, numOfVideoConferences);
        boolean success = spaceDao.insert(room);
        Logger.logIfSuccess(success, "Espaço criado: Sala de reuniao - " + name);
        return success;
    }

    //quadra esportiva
    public boolean createSportsField(String name, String localization, int capacity, String availableHours, int numOfGoals, int numOfNets, int numOfHoops) {
        SportsField field = new SportsField(name, localization, capacity, availableHours, numOfGoals, numOfNets, numOfHoops);
        boolean success = spaceDao.insert(field);
        Logger.logIfSuccess(success, "Espaço criado: Quadra Esportiva - " + name);
        return success;
    }

    //auditorio
    public boolean createAuditorium(String name, String localization, int capacity, String availableHours, int numOfSoundSystems, int numOfStages) {
        Auditorium auditorium = new Auditorium(name, localization, capacity, availableHours, numOfSoundSystems, numOfStages);
        boolean success = spaceDao.insert(auditorium);
        Logger.logIfSuccess(success, "Espaço criado: Auditorio - " + name);
        return success;
    }

    //atualizar um espaço
    public boolean updateSpace(Space space) {
        boolean success = spaceDao.update(space);
        Logger.logIfSuccess(success, "Espaço atualizado: " + space.getType() + " - " + space.getName());
        return success;
    }

    //apagar um espaço
    public boolean deleteSpace(Space space) {
        boolean success = spaceDao.delete(space);
        Logger.logIfSuccess(success, "Espaço removido: " + space.getType() + " - " + space.getName());
        return success;
    }

    //procurar por ID
    public Space getSpaceById(int id) {
        return spaceDao.findById(id);
    }

    //listar todos os espaços
    public List<Space> listAllSpaces() {
        return spaceDao.listAll();
    }

    //listar os espaços por tipo
    public List<Space> listSpacesByType(String type) {
        return spaceDao.findByType(type);
    }
}
