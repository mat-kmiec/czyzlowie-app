package pl.czyzlowie.modules.user_panel.catch_log;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service for managing file storage operations related to catch photos.
 * Provides functionality to save and delete photos associated with specific catches.
 */
@Service
@Slf4j
public class FileStorageService {

    private final String BASE_UPLOAD_DIR = "uploads/catches/";

    /**
     * Saves the given photo file to the server, resizing and optimizing the image,
     * and stores it in a directory specific to the provided catch ID.
     * The photo is saved in a standard `.jpg` format.
     *
     * @param file the photo file to be saved, provided as a {@code MultipartFile}.
     *             Must not be null or empty.
     * @param catchId the unique identifier of the catch, used to create a
     *                specific directory for storing the photo.
     * @return the relative file path of the saved photo, or {@code null} if the input file is null or empty.
     * @throws IOException if an error occurs during file processing or directory creation.
     */
    public String saveCatchPhoto(MultipartFile file, Long catchId) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String catchDirName = "catch_fish_" + catchId;
        Path directoryPath = Paths.get(BASE_UPLOAD_DIR + catchDirName);

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        Path targetLocation = directoryPath.resolve("photo.jpg");

        Thumbnails.of(file.getInputStream())
                .size(1280, 1280)
                .outputFormat("jpg")
                .outputQuality(0.75)
                .toFile(targetLocation.toFile());

        return "/uploads/catches/" + catchDirName + "/photo.jpg";
    }

    /**
     * Deletes the directory associated with the photo of a specific catch, identified by its ID.
     * The directory name is dynamically constructed based on the provided catch ID.
     * Any errors encountered during the deletion process are logged.
     *
     * @param catchId the unique identifier of the catch whose photo directory is to be deleted
     */
    public void deleteCatchPhotoDirectory(Long catchId) {
        String catchDirName = "catch_fish_" + catchId;
        Path directoryPath = Paths.get(BASE_UPLOAD_DIR + catchDirName);

        try {
            if (Files.exists(directoryPath)) {
                FileSystemUtils.deleteRecursively(directoryPath);
                log.info("Pomyślnie usunięto katalog ze zdjęciem: {}", catchDirName);
            }
        } catch (IOException e) {
            log.error("Błąd podczas usuwania katalogu ze zdjęciem: {}", directoryPath, e);
        }
    }
}