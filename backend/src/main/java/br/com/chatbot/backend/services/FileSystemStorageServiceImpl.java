package br.com.chatbot.backend.services;

import br.com.chatbot.backend.FileUploadDevProperties;
import br.com.chatbot.backend.dtos.FileDownloadProductionDto;
import br.com.chatbot.backend.exception.FileNotFoundException;
import br.com.chatbot.backend.exception.FileStorageException;
import br.com.chatbot.backend.utils.GoogleDriveUtils;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class FileSystemStorageServiceImpl implements FileSystemStorageService {
    @Value("${backend.environment.dev.enabled:true}")
    private boolean environmentDevEnabled;

    @Value("${backend.environment.prod.enabled:true}")
    private boolean environmentProdEnabled;

    private final Path dirLocation;

    private static final List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/bmp", "application/pdf");

    @Autowired(required = false)
    public FileSystemStorageServiceImpl(FileUploadDevProperties fileUploadDevProperties) {
        this.dirLocation = Paths.get(fileUploadDevProperties.getLocation())
                .toAbsolutePath()
                .normalize();
    }

    @Override
    @PostConstruct
    public void init() {
        if (environmentDevEnabled) {
            try {
                Files.createDirectories(dirLocation);
            } catch (Exception e) {
                throw new FileStorageException("Não foi possível criar o diretório de arquivos.");
            }
        }
    }

    @Override
    public String saveFileDevelopment(MultipartFile file) {
        String fileContentType = file.getContentType();
        validateFileContentType(fileContentType);

        try {
            String newFilename = generateFilename(file.getOriginalFilename());
            Path dirFile = dirLocation.resolve(newFilename);

            Files.copy(file.getInputStream(), dirFile, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (Exception e) {
            throw new FileStorageException("Não foi possível salvar o arquivo.");
        }
    }

    @Override
    public String saveFileProduction(MultipartFile file) {
        String fileContentType = file.getContentType();
        validateFileContentType(fileContentType);

        try {
            String newFilename = generateFilename(file.getOriginalFilename());

            java.io.File tempFile = new java.io.File(System.getProperty("java.io.tmpdir") + "/" + newFilename);
            file.transferTo(tempFile);

            File fileMetadata = new File();
            fileMetadata.setName(newFilename);
            FileContent mediaContent = new FileContent(fileContentType, new java.io.File(tempFile.getPath()));

            Drive driveService = GoogleDriveUtils.getDriveService();
            File savedfile = driveService.files().create(fileMetadata, mediaContent).setFields("id").execute();

            return savedfile.getId();
        } catch (Exception e) {
            throw new FileStorageException("Não foi possível salvar o arquivo.");
        }
    }

    @Override
    public Resource loadFileDevelopment(String fileName) {
        try {
            Path file = dirLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Não foi possível encontrar o arquivo.");
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Não foi possível fazer o download do arquivo.");
        }
    }

    @Override
    public FileDownloadProductionDto loadFileProduction(String fileId) {
        try {
            Drive driveService = GoogleDriveUtils.getDriveService();
            File file = driveService.files().get(fileId).execute();

            FileDownloadProductionDto fileDownloadProductionDto = new FileDownloadProductionDto();
            fileDownloadProductionDto.setName(file.getName());
            fileDownloadProductionDto.setMimeType(file.getMimeType());
            fileDownloadProductionDto.setInputStream(driveService.files().get(fileId).executeMediaAsInputStream());

            return fileDownloadProductionDto;
        } catch (Exception e) {
            throw new FileStorageException("Não foi possível encontrar o arquivo.");
        }
    }

    private void validateFileContentType(String fileContentType) {
        if (!contentTypes.contains(fileContentType)) {
            throw new FileStorageException("Formatos de arquivos permitidos: PNG, JPEG, BMP e PDF.");
        }
    }

    private String generateFilename(String filename) {
        Objects.requireNonNull(filename, "Informe o nome do arquivo.");

        int position = filename.lastIndexOf('.');
        Date date = new Date();

        StringBuilder sb = new StringBuilder(filename);
        sb.insert(position, date.getTime());

        return sb.toString();
    }
}
