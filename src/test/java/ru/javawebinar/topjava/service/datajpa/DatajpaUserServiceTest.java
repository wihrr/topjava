package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MatcherFactory;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.datajpa.DataJpaUserRepository;
import ru.javawebinar.topjava.service.UserServiceTest;

import static ru.javawebinar.topjava.UserTestData.user;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles(Profiles.DATAJPA)
public class DatajpaUserServiceTest extends UserServiceTest {
    public static final MatcherFactory<Meal> MATCHER = MatcherFactory.usingIgnoringFieldsComparator();

    public static final User userWithMeal = new User(USER_ID, "User", "user@yandex.ru", "password", MealTestData.meals, Role.USER);

    @Autowired
    private DataJpaUserRepository repository;

    @Test
    public void getWithMeal() {
        User actual = repository.getWithMeal(USER_ID);
        MATCHER.assertMatchCollectionElements(userWithMeal.getMeals(), actual.getMeals());
        UserTestData.MATCHER.assertMatch(user, actual);
    }
}
