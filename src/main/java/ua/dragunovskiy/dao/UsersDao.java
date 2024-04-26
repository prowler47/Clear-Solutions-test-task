package ua.dragunovskiy.dao;

import jakarta.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.dragunovskiy.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class UsersDao implements AbstractDao<UUID, User> {
    @Autowired
    private EntityManager entityManager;
    @Override
    @Transactional
    public void create(User entity) {
        Session session = entityManager.unwrap(Session.class);
        session.merge(entity);
    }

    @Override
    @Transactional
    public User partialUpdate(UUID id, User userForUpdate) {
        Session session = entityManager.unwrap(Session.class);
        User updatedUser = session.get(User.class, id);
        if (userForUpdate.getEmail() != null) {
            updatedUser.setEmail(userForUpdate.getEmail());
        }
        if (userForUpdate.getAddress() != null) {
            updatedUser.setAddress(userForUpdate.getAddress());
        }
        if (userForUpdate.getBirthday() != null) {
            updatedUser.setBirthday(userForUpdate.getBirthday());
        }
        if (userForUpdate.getLastName() != null) {
            updatedUser.setLastName(userForUpdate.getLastName());
        }
        if (userForUpdate.getFirstName() != null) {
            updatedUser.setFirstName(userForUpdate.getFirstName());
        }
        if (userForUpdate.getPhone() != null) {
            updatedUser.setPhone(userForUpdate.getPhone());
        }
        session.merge(updatedUser);
        return updatedUser;
    }

    @Override
    @Transactional
    public void allUpdate(UUID id, User userForUpdate) {
        Session session = entityManager.unwrap(Session.class);
        User updatedUser = session.get(User.class, id);
        if (userForUpdate.getEmail() != null && userForUpdate.getAddress() != null &&
                userForUpdate.getBirthday() != null && userForUpdate.getLastName() != null &&
                userForUpdate.getFirstName() != null && userForUpdate.getPhone() != null) {
            updatedUser.setEmail(userForUpdate.getEmail());
            updatedUser.setAddress(userForUpdate.getAddress());
            updatedUser.setBirthday(userForUpdate.getBirthday());
            updatedUser.setLastName(userForUpdate.getLastName());
            updatedUser.setFirstName(userForUpdate.getFirstName());
            updatedUser.setPhone(userForUpdate.getPhone());
        } else {
            throw new RuntimeException("There are not all fields");
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Session session = entityManager.unwrap(Session.class);
        User userForDelete = session.get(User.class, id);
        if (userForDelete == null) {
            throw new RuntimeException();
        } else {
            session.remove(userForDelete);
        }
    }

    @Override
    @Transactional
    public List<User> getUsersByBirthday(LocalDate from, LocalDate to) {
        Session session = entityManager.unwrap(Session.class);
        Query<User> query = session.createQuery("from User", User.class);
        List<User> usersList = query.getResultList();
        return usersList.stream()
                .filter(user -> user.getBirthday().isAfter(from.minusDays(1)) && user.getBirthday().isBefore(to.plusDays(1)))
                .collect(Collectors.toList());
    }
}
