package ru.javawebinar.topjava.repository.jdbc;

import org.javatuples.Pair;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.ResultSetExtractorImpl;
import org.simpleflatmapper.util.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);
    private static final BeanPropertyRowMapper<Role> ROLES_ROW_MAPPER = BeanPropertyRowMapper.newInstance(Role.class);

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleJdbcInsert insertUser;

//    private final ResultSetExtractor<List<User>> resultSetExtractor =
//            JdbcTemplateMapperFactory
//                    .newInstance()
//                    .addKeys("id")
//                    .newResultSetExtractor(User.class);

    private final ResultSetExtractorImpl<Pair<User, List<Role>>> resultSetExtractor =
            JdbcTemplateMapperFactory
                    .newInstance()
                    .addKeys("id") // the column name you expect the user id to be on
                    .newResultSetExtractor(new TypeReference<>() {
                    });


    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        }
        List<Role> roles = jdbcTemplate.query("SELECT role FROM user_roles LEFT JOIN users on users.id = user_roles.user_id", ROLES_ROW_MAPPER);
        user.setRoles(roles);
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {


        List<Pair<User, List<Role>>> userRolesPair = jdbcTemplate.query("SELECT users.id as id, users.email as email, users.name as name, users.password as password, user_roles.role as roles" +
                " FROM users LEFT OUTER JOIN user_roles ON user_roles.user_id = users.id WHERE users.id=?", resultSetExtractor, id);
        Map<User, List<Role>> userRolesMap = userRolesPair.stream()
                .distinct()
                .collect(Collectors.toMap(Pair::getValue0, Pair::getValue1));
        return userRolesMap.keySet().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

//       "SELECT u.id as id, u.username, u.id as adverts_id, ad.text as adverts_text"
//        + "FROM user u LEFT OUTER JOIN advert ad ON ad.account_id = ac.id order by id "
//        List<Tuple2<User, List<Role>>> results = template.query(query, resultSetExtractor);
    @Override
    public User getByEmail(String email) {
        List<Pair<User, List<Role>>> userRolesPair = jdbcTemplate.query("SELECT users.id as id, users.email as email, users.name as name, users.password as password, user_roles.role as roles" +
                " FROM users LEFT OUTER JOIN user_roles ON user_roles.user_id = users.id WHERE email=?", resultSetExtractor, email);
        Map<User, List<Role>> userRolesMap = userRolesPair.stream()
                .distinct()
                .collect(Collectors.toMap(Pair::getValue0, Pair::getValue1));
        return userRolesMap.keySet().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow();

//        List<Role> roles= jdbcTemplate.query("SELECT role FROM user_roles WHERE user_id=?", ((resultSet, rowNum) -> Role.valueOf(resultSet.getString("role"), user.getId())));
//        return jdbcTemplate.queryForObject("SELECT * FROM users INNER JOIN FETCH users.role WHERE user_roles.user_id = users.id AND email=?", ROW_MAPPER, email);
//        List<User> users = jdbcTemplate.query("SELECT * FROM users INNER JOIN user_roles ON user_roles.user_id = users.id WHERE email=?", ROW_MAPPER, email);
//        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        List<Pair<User, List<Role>>> userRolesPair = jdbcTemplate.query("SELECT users.id as id, users.email as email, users.name as name, users.password as password, user_roles.role as roles" +
                " FROM users LEFT OUTER JOIN user_roles ON user_roles.user_id = users.id ORDER BY name, email", resultSetExtractor);
        Map<User, List<Role>> userRolesMap = userRolesPair.stream()
                .distinct()
                .collect(Collectors.toMap(Pair::getValue0, Pair::getValue1));
        return userRolesMap.keySet().stream().toList();
    }

    public User setRoles(User user) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("role", user.getRoles());
        return user;
    }

    public void insertRoles(User user) {
        Set<Role> roles = user.getRoles();
        Iterator<Role> iterator = roles.iterator();
    }
}
