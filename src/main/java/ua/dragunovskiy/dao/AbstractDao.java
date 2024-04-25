package ua.dragunovskiy.dao;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public interface AbstractDao<T, E> {
    void create(E entity);
    void partialUpdate(T updatedId, E entityForUpdate);
    void allUpdate(T updatedId, E entityForUpdate);

    void delete(T id);
    List<E> getUsersByBirthday(LocalDate from, LocalDate to);
}
