package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;


@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    private MealService service;

    public Meal create(Meal meal, int userId) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal, userId);
    }

    public Meal get(int id, int userId){
        log.info("get {}", id);
        return service.get(id, userId);
    }

    public List<Meal> getAll(int userId) {
        log.info("getAll");
        return service.getAll(userId);
    }

    public void delete(int id, int userId) {
        log.info("delete {}", id);
        service.delete(id, userId);
    }

    public void update(Meal meal, int id, int userId) {
        log.info("update {} with id {}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal, userId);
    }
}