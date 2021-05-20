package wooteco.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void checkAndAddSection(Section newSection, Line line) {
        List<Section> beforeSections = sectionDao.findBeforeSection(newSection);
        if( beforeSections.size() > 1) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }

        if (beforeSections.size() == 0) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
        Section beforeSection = beforeSections.get(0);
        List<Section> newSections = beforeSection.update(newSection, line);
        sectionDao.delete(beforeSection);
        sectionDao.save(newSections.get(0));
        sectionDao.save(newSections.get(1));
    }

    public void checkSectionCount(long lineId) {
        if (sectionDao.count(lineId) == 1) {
            throw new IllegalArgumentException("구간이 하나뿐이라 더이상 지울 수 없습니다.");
        }
    }

    public void deleteStation(long lineId, long stationId) {
        List<Section> sections = sectionDao.findSections(lineId, stationId);
        for (Section section : sections) {
            sectionDao.delete(section);
        }

        if (sections.size() == 1) {
           return;
        }

        sectionDao.save(sections.get(0).deleteStation(sections.get(1)));
    }
}
