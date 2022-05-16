package wooteco.subway.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Sql("classpath:test-schema.sql")
class SectionControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionService sectionService;
    @Autowired
    private LineService lineService;

    private Station testStation1;
    private Station testStation2;
    private Station testStation3;
    private Station testStation4;
    private Station testStation5;
    private Station testStation6;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        testStation1 = stationDao.save(new Station("testStation1"));
        testStation2 = stationDao.save(new Station("testStation2"));
        testStation3 = stationDao.save(new Station("testStation3"));
        testStation4 = stationDao.save(new Station("testStation4"));
        testStation5 = stationDao.save(new Station("testStation5"));
        testStation6 = stationDao.save(new Station("testStation6"));

        LineResponse lineResponse = lineService
                .createLineAndRegisterSection(new LineRequest("testLine", "color", testStation3.getId(), testStation4.getId(), 10L));

        sectionService.insertSection(lineResponse.getId(), new SectionRequest(testStation4.getId(), testStation5.getId(), 20L));
    }

    @DisplayName("구간을 저장한다")
    @Test
    void createSection_success() {
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10L);

        RestAssured.
                given().log().all().
                    body(sectionRequest).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines/{lineId}/sections", 1).
                then().log().all().
                    statusCode(HttpStatus.OK.value());
    }

    @DisplayName("구간을 제거한다")
    @Test
    void deleteSection_success() {
        RestAssured.
                given().log().all().
                when().
                    delete("/lines/{lineId}/sections?stationId=3", 1).
                then().log().all().
                    statusCode(HttpStatus.OK.value());
    }
}
