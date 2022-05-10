package wooteco.subway.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.entity.LineEntity;
import wooteco.subway.domain.entity.SectionEntity;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.exceptions.LineNotFoundException;
import wooteco.subway.utils.exceptions.StationNotFoundException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = convertLineRequestToLine(lineRequest);
        LineEntity newLine = lineDao.save(line);

        sectionDao.save( new Section(newLine.getId(), line.getUpStation(), line.getDownStation(),
                lineRequest.getDistance()));

        List<StationResponse> stations = extractUniqueStationsFromSections(newLine);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stations);
    }

    private Line convertLineRequestToLine(LineRequest lineRequest) {
        Station upStation = stationDao.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        lineRequest.getUpStationId()));
        Station downStation = stationDao.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        lineRequest.getDownStationId()));

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation,
                lineRequest.getDistance());
        return line;
    }

    public List<LineResponse> findAll() {
        List<LineEntity> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(
                        line.getId(),
                        line.getName(),
                        line.getColor(),
                        extractUniqueStationsFromSections(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        LineEntity lineEntity = lineDao.findById(id).orElseThrow(() -> new LineNotFoundException(id));
        List<StationResponse> stations = extractUniqueStationsFromSections(lineEntity);
        return new LineResponse(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), stations);
    }

    public void changeField(LineResponse findLine, LineRequest lineRequest) {
        lineDao.changeLineName(findLine.getId(), lineRequest.getName());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    private ArrayList<StationResponse> extractUniqueStationsFromSections(LineEntity line) {
        List<SectionEntity> sections = sectionDao.findAllByLineId(line.getId());
        Set<StationResponse> stations = new LinkedHashSet<>();

        for (SectionEntity sectionEntity : sections) {
            Station upStation = stationDao.findById(sectionEntity.getUpStationId())
                    .orElseThrow(() -> new StationNotFoundException(
                            sectionEntity.getUpStationId()));
            Station downStation = stationDao.findById(sectionEntity.getDownStationId())
                    .orElseThrow(() -> new StationNotFoundException(
                            sectionEntity.getDownStationId()));
            stations.add(new StationResponse(upStation));
            stations.add(new StationResponse(downStation));
        }
        return new ArrayList<>(stations);
    }
}
