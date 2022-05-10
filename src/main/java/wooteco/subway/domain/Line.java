package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Station upStation;
    private Station downStation;
    private Long distance;
    private Sections sections;

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance,
                Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.sections = sections;
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance) {
        this(id, name, color, upStation, downStation, distance, new Sections());
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section addSection(Section section) {
        validateCanAddSection(section);
        boolean upStationExist = sections.containsStation(section.getUpStation());
        boolean downStationExist = sections.containsStation(section.getUpStation());
        // 상행 종점 등록 || 상행 구간 사이에 들어가기
        if (!upStationExist && downStationExist) {
            Station lineUpEndStation = sections.calculateUpStation();
            // 상행 종점 등록
            if (section.getDownStation().equals(lineUpEndStation)) {
                sections.add(section);
                return section;
            }
            // TODO: 갈래길 방지 및 거리 validate (기존 section 갱신)

        }

        // 하행 종점 등록 || 하행 구간 사이에 들어가기
        if (upStationExist && !downStationExist) {
            Station lineDownEndStation = sections.calculateDownStation();
            // 하행 종점 등록
            if (section.getUpStation().equals(lineDownEndStation)) {
                sections.add(section);
                return section;
            }
            // TODO: 갈래길 방지 및 거리 validate  (기존 section 갱신)

        }
        return section;
    }

    private void validateCanAddSection(Section section) {
        boolean upStationExist = sections.containsStation(section.getUpStation());
        boolean downStationExist = sections.containsStation(section.getUpStation());
        if (!upStationExist && !downStationExist) {
            throw new IllegalArgumentException(
                    String.format("%s와 %s 모두 존재하지 않아 구간을 등록할 수 없습니다.", section.getUpStationName(),
                            section.getDownStationName()));
        }
        if (upStationExist && downStationExist) {
            throw new IllegalArgumentException(
                    String.format("%s와 %s 이미 모두 등록 되어있어 구간을 등록할 수 없습니다.", section.getUpStationName(),
                            section.getDownStationName()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }

    public Sections getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return "Line{" +
                "upStation=" + upStation.getName() +
                ", downStation=" + downStation.getName() +
                '}';
    }
}
