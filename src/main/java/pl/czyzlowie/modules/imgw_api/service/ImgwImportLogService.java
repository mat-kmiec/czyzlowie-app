package pl.czyzlowie.modules.imgw_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.czyzlowie.modules.imgw_api.entity.ImgwImportLog;
import pl.czyzlowie.modules.imgw_api.entity.enums.ImgwImportType;
import pl.czyzlowie.modules.imgw_api.repository.ImgwImportLogRepository;

/**
 * Service responsible for managing import logs related to IMiGW (Polish Institute of
 * Meteorology and Water Management) data processing activities.*
 * This service provides functionality to record import operations for different data
 * types (e.g., METEO, HYDRO, SYNOP) by saving relevant log information into the
 * associated repository. It also includes structured logging to track these operations.
 * The service interacts with the {@code ImgwImportLogRepository} to persist import log
 * entries and leverages the {@code ImgwImportType} to differentiate between the types
 * of data imports being logged. This ensures traceability and visibility into data-import
 * activities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImgwImportLogService {


    private final ImgwImportLogRepository imgwImportLogRepository;

    /**
     * Records an import operation with the specified type and count of records.
     * This method logs the import details and saves an import log entry into the repository.
     *
     * @param importType the type of the import, representing categories such as METEO, HYDRO, or SYNOP
     * @param count the number of records imported during the operation
     */
    public void recordImport(ImgwImportType importType, int count) {
        log.info("Recording import log: {} - {} records", importType, count);
        ImgwImportLog importLog = new ImgwImportLog(importType, count);
        imgwImportLogRepository.save(importLog);
    }
}
