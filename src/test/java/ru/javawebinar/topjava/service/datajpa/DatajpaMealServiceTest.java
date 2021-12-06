package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MatcherFactory;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.datajpa.DataJpaMealRepository;
import ru.javawebinar.topjava.service.MealServiceTest;

import java.time.Month;

import static java.time.LocalDateTime.of;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(Profiles.DATAJPA)
public class DatajpaMealServiceTest extends MealServiceTest {

    public static final Meal meal = new Meal(MEAL1_ID, of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500, user);

    @Autowired
    private DataJpaMealRepository repository;

    @Test
    public void getWithUser() {
        Meal actual = repository.getWithUser(MEAL1_ID, USER_ID);
        UserTestData.MATCHER.assertMatch(actual.getUser(), meal.getUser());
    }
}
