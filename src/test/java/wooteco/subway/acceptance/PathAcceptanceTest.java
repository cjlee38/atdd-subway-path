package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.acceptance.fixture.SimpleRestAssured;
import wooteco.subway.dto.response.PathResponse;

public class PathAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("최단경로를 조회한다.")
    public void findPath() {
        // given
        SimpleRestAssured.post("/stations", Map.of("name", "A역"));
        SimpleRestAssured.post("/stations", Map.of("name", "B역"));
        SimpleRestAssured.post("/stations", Map.of("name", "C역"));

        Map<String, String> lineParams = Map.of(
            "name", "신분당선",
            "color", "bg-red-600",
            "upStationId", "1",
            "downStationId", "2",
            "distance", "10");
        SimpleRestAssured.post("/lines", lineParams);
        Map<String, String> sectionParams = Map.of(
            "upStationId", "2",
            "downStationId", "3",
            "distance", "6"
        );
        SimpleRestAssured.post("/lines/1/sections", sectionParams);
        // when

        final ExtractableResponse<Response> response = SimpleRestAssured.get("/paths?source=1&target=3&age=123");
        final PathResponse pathResponse = SimpleRestAssured.toObject(response, PathResponse.class);

        // then
        Assertions.assertAll(
            () -> assertThat(pathResponse.getStations()).hasSize(3),
            () -> assertThat(pathResponse.getDistance()).isEqualTo(16),
            () -> assertThat(pathResponse.getFare()).isEqualTo(1450)
        );
    }
}
