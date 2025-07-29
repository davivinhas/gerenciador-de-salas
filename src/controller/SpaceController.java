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
        boolean sucess = spaceDao.insert(classroom);
        Logger.logIfSuccess(sucess, "Espaço criado: Sala de Aula - " + name);
        return sucess;
    }

    //laboratorio
    public boolean createLaboratory(String name, String localization, int capacity, String availableHours, int numOfComputers, String labType) {
        Laboratory lab = new Laboratory(name, localization, capacity, availableHours, numOfComputers, labType);
        boolean sucess = spaceDao.insert(lab);
        Logger.logIfSuccess(sucess, "Espaço criado: Laboratorio - " + name);
        return sucess;
    }

    //sala de reunião
    public boolean createMeetingRoom(String name, String localization, int capacity, String availableHours, int numOfProjectors, int numOfVideoConferences) {
        MeetingRooms room = new MeetingRooms(name, localization, capacity, availableHours, numOfProjectors, numOfVideoConferences);
        boolean sucess = spaceDao.insert(room);
        Logger.logIfSuccess(sucess, "Espaço criado: Sala de reuniao - " + name);
        return sucess;
    }

    //quadra esportiva
    public boolean createSportsField(String name, String localization, int capacity, String availableHours, int numOfGoals, int numOfNets, int numOfHoops) {
        SportsField field = new SportsField(name, localization, capacity, availableHours, numOfGoals, numOfNets, numOfHoops);
        boolean sucess = spaceDao.insert(field);
        Logger.logIfSuccess(sucess, "Espaço criado: Quadra Esportiva - " + name);
        return sucess;
    }

    //auditorio
    public boolean createAuditorium(String name, String localization, int capacity, String availableHours, int numOfSoundSystems, int numOfStages) {
        Auditorium auditorium = new Auditorium(name, localization, capacity, availableHours, numOfSoundSystems, numOfStages);
        boolean sucess = spaceDao.insert(auditorium);
        Logger.logIfSuccess(sucess, "Espaço criado: Auditorio - " + name);
        return sucess;
    }

    //atualizar um espaço
    public boolean updateSpace(Space space) {
        return spaceDao.update(space);
    }

    //apagar um espaço
    public boolean deleteSpace(Space space) {
        return spaceDao.delete(space);
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
