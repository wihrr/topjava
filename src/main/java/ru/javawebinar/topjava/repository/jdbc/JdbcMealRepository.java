package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final String UPDATE_MEAL_QUERY = "UPDATE meal SET date_time=:dateTime, description=:description, calories=:calories WHERE id=:id AND user_id=:user_id";

    private static final String GET_MEAL_QUERY = "SELECT * FROM meal WHERE id=? AND user_id=?";

    private static final String GET_ALL_MEAL_QUERY = "SELECT * FROM meal WHERE user_id=? ORDER BY date_time DESC";

    private static final String GET_ALL_MEAL_FOR_TIME_PERIOD_QUERY = "SELECT * FROM meal WHERE user_id=? AND date_time >= ? AND date_time < ? ORDER BY date_time DESC";

    private static final String DELETE_MEAL_QUERY = "DELETE FROM meal WHERE id=? AND user_id=?";

    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertMeal;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertMeal = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meal")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("dateTime", meal.getDateTime())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("user_id", userId);

        if (meal.isNew()) {
            Number newKey = insertMeal.executeAndReturnKey(map);
            meal.setId(newKey.intValue());
            return meal;
        } else {
            int updateResult = namedParameterJdbcTemplate.update(UPDATE_MEAL_QUERY, map);
            return updateResult == 0 ? null : meal;
        }
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update(DELETE_MEAL_QUERY, id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals = jdbcTemplate.query(GET_MEAL_QUERY, ROW_MAPPER, id, userId);
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query(GET_ALL_MEAL_QUERY, ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return jdbcTemplate.query(GET_ALL_MEAL_FOR_TIME_PERIOD_QUERY, ROW_MAPPER, userId, startDateTime, endDateTime);
    }
}
