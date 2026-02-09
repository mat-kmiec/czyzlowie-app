package pl.czyzlowie.modules.moon.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class AstronomyResponse {
    private DataBody data;

    @Data
    public static class DataBody {
        private String date;
        private List<MoonBody> moon;
    }

    @Data
    public static class MoonBody {
        private PhaseBody phase;
        private List<EventBody> events;
        private DistanceBody distance;
    }

    @Data
    public static class PhaseBody {
        private String name;
        private Double illumination;
        private Double age;
    }

    @Data
    public static class EventBody {
        private String type;
        private String time;
    }

    @Data
    public static class DistanceBody {
        private Double km;
    }
}