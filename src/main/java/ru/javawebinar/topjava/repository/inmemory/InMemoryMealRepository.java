package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private User user;

    {
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(user.getId());
            return meal;
        }
        if(!meal.getUserId().equals(user.getId())){
            return null;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id) {
        Meal meal = repository.get(id);
        if (meal.getUserId().equals(user.getId())){
        return repository.remove(id) != null;
        } else
            return false;
    }

    @Override
    public Meal get(int id) {
        Meal meal = repository.get(id);
        if (meal.getUserId().equals(user.getId())) {
            return repository.get(id);
        } else
            return null;
    }

    @Override
    public List<Meal> getAll() {
        List<Meal> currentUserMeal = repository.values().stream()
                .filter(meal -> meal.getUserId().equals(user.getId()))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
        return currentUserMeal;
    }
}

