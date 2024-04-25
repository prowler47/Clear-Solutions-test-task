package ua.dragunovskiy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
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
    public void createUser(@RequestBody User user) {
        LocalDate birthDay = user.getBirthday();
        if (checkAge(birthDay)) {
            service.create(user);
        } else {
            System.out.println("You are not 18 years");
        }
    }
    @PatchMapping("/users/{id}")
    public void partialUpdateUser(@PathVariable UUID id, @RequestBody User user) {
        service.partialUpdate(id, user);
    }

    @PutMapping("/users/{id}")
    public void allUpdateUser(@PathVariable UUID id, @RequestBody User user) {
        service.allUpdate(id, user);
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/users")
    public List<User> getUsersByBirthday(@RequestParam(name = "from") String from, @RequestParam(name = "to") String to) {
        LocalDate localDateFrom = LocalDate.parse(from);
        LocalDate localDateTo = LocalDate.parse(to);
        return service.getUsersByBirthday(localDateFrom, localDateTo);
    }

    private boolean checkAge(LocalDate birthDay) {
        LocalDate today = LocalDate.now();
        Period period = birthDay.until(today);
        return period.getYears() >= permittedAge;
    }
}
