package app.blog.standard.standardblogapp.model.util.database.dao;

import java.util.ArrayList;

/**
 * @author victor
 */
public interface DAO<E> {

    boolean create(E e);
    E read(E e);
    ArrayList<E> read(int skip, int take);
    ArrayList<E> readAll();
    boolean update(E e);
    boolean delete(E e);

}
