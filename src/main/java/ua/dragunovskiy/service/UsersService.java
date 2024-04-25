package ua.dragunovskiy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.dragunovskiy.dao.AbstractDao;
import ua.dragunovskiy.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class UsersService implements AbstractService<UUID, User> {

    @Autowired
    private AbstractDao<UUID, User> dao;
    @Override
    public void create(User entity) {
        dao.create(entity);
        System.out.println("creating user...");
    }

    @Override
    public void partialUpdate(UUID updatedId, User entityForUpdate) {
        dao.partialUpdate(updatedId, entityForUpdate);
    }

    @Override
    public void allUpdate(UUID updatedId, User entityForUpdate) {
        dao.allUpdate(updatedId, entityForUpdate);
    }

    @Override
    public void delete(UUID id) {
        dao.delete(id);
    }

    @Override
    public List<User> getUsersByBirthday(LocalDate from, LocalDate to) {
       return dao.getUsersByBirthday(from, to);
    }
}
