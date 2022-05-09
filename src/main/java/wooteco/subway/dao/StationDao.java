package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(resultSet.getLong("id"),
            resultSet.getString("name"));

    public Station save(Station station) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into station (name) values (?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
        long insertedId = keyHolder.getKey().longValue();

        return new Station(insertedId, station.getName());
    }

    public List<Station> findAll() {
        String sql = "select id, name from station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Long deleteById(Long id) {
        String sql = "delete from station where id = ?";
        this.jdbcTemplate.update(sql,id);
        return id;
    }

    public Optional<Station> findById(Long id) {
        String sql = "select id, name from station where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, stationRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
