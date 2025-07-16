package dao;

import java.util.List;

public interface DataAccessObject<T> {
    boolean insert(T object);
    boolean update(T object);
    boolean delete(T object);
    T findById(int id);
    List<T> listAll();
}
