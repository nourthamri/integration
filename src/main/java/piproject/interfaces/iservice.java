package piproject.interfaces;

import java.util.List;

public interface iservice<T> {
    void add(T t);
    void delete(T t);
    void update(T t);
    T find(T t);
    T find(String s);
    List<T> getAll();
}