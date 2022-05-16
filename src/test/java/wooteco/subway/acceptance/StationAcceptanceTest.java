package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private Station testStation1 = new Station("강남역");
    private Station testStation2 = new Station("역삼역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        Station testStation1 = new Station("강남역");
        RestAssured.
                given().log().all().
                    body(testStation1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().log().all().
                    statusCode(HttpStatus.CREATED.value()).
                    header("Location",is(not("")));
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        RestAssured.
                given().log().all().
                    body(testStation1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().log().all();

        // when
        RestAssured.
                given().log().all().
                    body(testStation1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.
                given().log().all().
                    body(testStation1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().log().all().
                    extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.
                given().log().all().
                    body(testStation2).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().log().all().
                    extract();

        // when
        ExtractableResponse<Response> response = RestAssured.
                given().log().all().
                when().
                    get("/stations").
                then().log().all().
                    statusCode(HttpStatus.OK.value()).
                    extract();

        // then
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured.
                given().log().all().
                    body(testStation1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().log().all().
                    extract();

        // when
        String uri = createResponse.header("Location");
        RestAssured.
                given().log().all().
                when().
                    delete(uri).
                then().
                    statusCode(HttpStatus.NO_CONTENT.value()).log().all();
    }
}
