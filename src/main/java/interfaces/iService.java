package interfaces;

import javafx.collections.ObservableList;

public interface iService<T> {
    void add(T t);
    void update(T t);
    boolean delete(int id);
    ObservableList<T> getAll();
    T getById(int id);
}