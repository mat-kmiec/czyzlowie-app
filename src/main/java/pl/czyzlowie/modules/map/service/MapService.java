package pl.czyzlowie.modules.map.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw.repository.ImgwSynopStationRepository;
import pl.czyzlowie.modules.map.dto.MapMarkerDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapService {


    private final ImgwSynopStationRepository imgwSynopStationRepository;

    public List<MapMarkerDto> getAllMarkers(){
        List<MapMarkerDto> markers = new ArrayList<>();

        markers.addAll(imgwSynopStationRepository.findAll()
                .stream()
                .map(synop -> MapMarkerDto.builder()
                        .id(synop.getId())
                        .name(synop.getName())
                        .type("SYNOP")
                        .slug(synop.getId())
                        .lat(synop.getLatitude())
                        .lng(synop.getLongitude())
                        .build())
                .collect(Collectors.toList()));



        return markers;
    }
}
