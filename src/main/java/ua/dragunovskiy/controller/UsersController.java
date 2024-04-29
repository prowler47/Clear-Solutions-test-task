package ua.dragunovskiy.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.dragunovskiy.entity.User;
import ua.dragunovskiy.service.AbstractService;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;


@RestController
public class UsersController {

    @Autowired
    private AbstractService<UUID, User> service;

    @Value("${permitted-age}")
    private int permittedAge;

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
            LocalDate birthDay = user.getBirthday();
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (checkAge(birthDay)) {
            service.create(user);
            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not 18 years");
        }
    }
    @PatchMapping("/users/{id}")
    public User partialUpdateUser(@PathVariable UUID id, @RequestBody User user) {
            try {
                service.partialUpdate(id, user);
                return user;
            } catch (NullPointerException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
    }

    @PutMapping("/users/{id}")
    public User allUpdateUser(@PathVariable UUID id, @RequestBody User user) {
        try {
            service.allUpdate(id, user);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable UUID id) {
        try {
            service.delete(id);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such user found");
        }
    }

    @GetMapping("/users")
    public List<User> getUsersByBirthday(@RequestParam(name = "from") String from, @RequestParam(name = "to") String to) {
        LocalDate localDateFrom = LocalDate.parse(from);
        LocalDate localDateTo = LocalDate.parse(to);
        if (service.getUsersByBirthday(localDateFrom, localDateTo).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such users found");
        }
        return service.getUsersByBirthday(localDateFrom, localDateTo);
    }

    private boolean checkAge(LocalDate birthDay) {
        LocalDate today = LocalDate.now();
        Period period = birthDay.until(today);
        return period.getYears() >= permittedAge;
    }
}
