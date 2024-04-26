package ua.dragunovskiy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.dragunovskiy.entity.User;
import ua.dragunovskiy.service.AbstractService;
import ua.dragunovskiy.service.UsersService;

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
    public ResponseEntity<User> createUser(@RequestBody User user) {
        LocalDate birthDay = user.getBirthday();
        if (checkAge(birthDay)) {
            service.create(user);
            return ResponseEntity.ok(null);

        } else {
            System.out.println("You are not 18 years");
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("/users/{id}")
    public ResponseEntity<User> partialUpdateUser(@PathVariable UUID id, @RequestBody User user) {
            try {
                service.partialUpdate(id, user);
                return ResponseEntity.ok(null);
            } catch (NullPointerException e) {
                return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
            }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> allUpdateUser(@PathVariable UUID id, @RequestBody User user) {
        try {
            service.allUpdate(id, user);
        } catch (RuntimeException e) {
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<User>(HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> delete(@PathVariable UUID id) {
        try {
            service.delete(id);
        } catch (RuntimeException e) {
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<User>(HttpStatus.OK);
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
