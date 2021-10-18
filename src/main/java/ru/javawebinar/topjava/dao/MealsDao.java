package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;
import java.util.List;

public interface MealsDao {
    Meal save(Meal meal);

    void delete (int id);

    Collection<Meal> readAll();

    Meal getMealById(int id);
}

