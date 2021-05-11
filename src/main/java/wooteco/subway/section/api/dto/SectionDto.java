package wooteco.subway.section.api.dto;

import wooteco.subway.line.model.Line;
import wooteco.subway.section.model.Section;
import wooteco.subway.station.model.Station;

public class SectionDto {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionDto(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

//    public static Section ofSection(Long lineId, Line line, Station upStation, Station downStation, int distance) {
//        return new Section(sectionDto.getId(),
//                lineDao.findLineById(sectionDto.getLineId()),
//                stationDao.findStationById(sectionDto.getUpStationId()),
//                stationDao.findStationById(sectionDto.getDownStationId()),
//                sectionDto.getDistance()))
//    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
