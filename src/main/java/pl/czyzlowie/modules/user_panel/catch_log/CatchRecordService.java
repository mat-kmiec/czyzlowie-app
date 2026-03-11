package pl.czyzlowie.modules.user_panel.catch_log;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.czyzlowie.modules.user.entity.User;
import pl.czyzlowie.modules.user.repository.UserRepository;

import java.io.IOException;

/**
 * The CatchRecordService class provides functionalities for managing fishing catch records.
 * It includes methods for creating, retrieving, and deleting catch records, as well as
 * mapping database entities to response objects. This service is designed to work with
 * repositories and auxiliary services such as telemetry and file storage.
 */
@Service
@RequiredArgsConstructor
public class CatchRecordService {

    private final CatchRecordRepository catchRepository;
    private final TelemetryService telemetryService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    /**
     * Creates a new catch record for the specified user and stores it in the database. Optionally associates a photo with the catch.
     *
     * @param username  the email/username of the user creating the catch record
     * @param request   the details of the catch record to be created
     * @param photo     an optional photo file associated with the catch
     * @throws IOException if an error occurs while saving the photo
     */
    @Transactional
    public void createCatch(String username, CatchRecordCreateRequest request, MultipartFile photo) throws IOException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

        CatchRecord record = CatchRecord.builder()
                .user(user)
                .catchDate(request.getCatchDate())
                .locationName(request.getLocation())
                .latitude(request.getLat())
                .longitude(request.getLng())
                .species(request.getSpecies())
                .weight(request.getWeight())
                .length(request.getLength())
                .lureMethod(request.getLure())
                .note(request.getNote())
                .ignoreHydro(request.isIgnoreHydro())
                .ignoreTelemetry(request.isIgnoreTelemetry())
                .build();

        if (!request.isIgnoreTelemetry()) {
            telemetryService.enrichWithMeteoAndMoon(record);
        }
        if (!request.isIgnoreHydro()) {
            telemetryService.enrichWithHydro(record);
        }

        CatchRecord savedRecord = catchRepository.save(record);

        if (photo != null && !photo.isEmpty()) {
            String photoUrl = fileStorageService.saveCatchPhoto(photo, savedRecord.getId());
            savedRecord.setPhotoUrl(photoUrl);
        }
    }

    /**
     * Retrieves a paginated list of catch records for a specific user, ordered by catch date in descending order.
     *
     * @param username the email address of the user whose catches are being retrieved
     * @param pageable the pagination information
     * @return a paginated list of catch records in the form of CatchRecordResponse objects
     * @throws UsernameNotFoundException if no user is found with the given email address
     */
    @Transactional(readOnly = true)
    public Page<CatchRecordResponse> getUserCatches(String username, Pageable pageable) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

        return catchRepository.findAllByUserIdOrderByCatchDateDesc(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    /**
     * Deletes a catch record based on the given catch ID and username.
     * Validates if the user performing the action owns the catch record.
     *
     * @param catchId The ID of the catch record to be deleted.
     * @param username The username of the user attempting to delete the catch record.
     * @throws IllegalArgumentException if no catch record is found with the given ID.
     * @throws SecurityException if the username does not match the owner of the catch record.
     */
    @Transactional
    public void deleteCatch(Long catchId, String username) {
        CatchRecord catchRecord = catchRepository.findById(catchId)
                .orElseThrow(() -> new IllegalArgumentException("Taka zdobycz nie istnieje."));

        if (!catchRecord.getUser().getEmail().equals(username)) {
            throw new SecurityException("Odmowa dostępu. Nie możesz usunąć cudzego połowu!");
        }

        catchRepository.delete(catchRecord);
        fileStorageService.deleteCatchPhotoDirectory(catchId);
    }

    /**
     * Maps a given {@link CatchRecord} object to a {@link CatchRecordResponse} object.
     * Transforms the fields in a {@link CatchRecord} into a {@link CatchRecordResponse},
     * including details such as catch information, environmental conditions, and metadata.
     *
     * @param record the {@link CatchRecord} instance containing the details of a fishing event
     *               to be transformed into a response DTO.
     * @return a {@link CatchRecordResponse} object that encapsulates the data
     *         for client consumption.
     */
    private CatchRecordResponse mapToResponse(CatchRecord record) {
        return CatchRecordResponse.builder()
                .id(record.getId())
                .catchDate(record.getCatchDate())
                .locationName(record.getLocationName())
                .photoUrl(record.getPhotoUrl())
                .species(record.getSpecies())
                .weight(record.getWeight())
                .length(record.getLength())
                .lureMethod(record.getLureMethod())
                .note(record.getNote())
                .airTemperature(record.getAirTemperature())
                .pressure(record.getPressure())
                .moonPhase(record.getMoonPhase())
                .waterLevel(record.getWaterLevel())
                .waterTemperature(record.getWaterTemperature())
                .humidity(record.getHumidity())
                .precipitation(record.getPrecipitation())
                .windSpeed(record.getWindSpeed())
                .windDirection(record.getWindDirection())
                .discharge(record.getDischarge())
                .build();
    }
}