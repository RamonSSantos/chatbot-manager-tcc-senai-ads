package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.dtos.BasicReportDto;
import br.com.chatbot.backend.dtos.ContentDto;
import br.com.chatbot.backend.dtos.FileDownloadProductionDto;
import br.com.chatbot.backend.dtos.FileRespondeDto;
import br.com.chatbot.backend.models.ContentEntity;
import br.com.chatbot.backend.services.ContentService;
import br.com.chatbot.backend.services.FileSystemStorageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@RequestMapping(value = "/api/content", method = RequestMethod.GET)
@RestController
public class ContentController {
    @Value("${backend.environment.dev.enabled:true}")
    private boolean environmentDevEnabled;

    @Value("${backend.environment.prod.enabled:true}")
    private boolean environmentProdEnabled;

    @Value("${file.download.location}")
    private String fileDownloadLocation;

    private final ContentService contentService;
    private final FileSystemStorageService fileSystemStorageService;

    @Autowired
    public ContentController(ContentService contentService, FileSystemStorageService fileSystemStorageService) {
        this.contentService = contentService;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @GetMapping
    public Page<ContentEntity> getAllContents(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "false") Boolean sortDesc
    ) {
        return this.contentService.findAll(pageNumber - 1, pageSize, sortBy, sortDesc);
    }

    @GetMapping(value = "/view/{id}")
    public Optional<ContentEntity> getById(@PathVariable int id) {
        return this.contentService.findById(id);
    }

    @GetMapping(value = "/search")
    public List<ContentEntity> getByTitle(@RequestParam String title) {
        return this.contentService.findByTitle(title);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> save(@RequestBody ContentDto contentDto) {
        this.contentService.save(contentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Conteúdo cadastrado com sucesso!");
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<String> edit(@RequestBody ContentDto contentDto) {
        this.contentService.edit(contentDto);
        return ResponseEntity.status(HttpStatus.OK).body("Conteúdo alterado com sucesso!");
    }

    @PutMapping(value = "/edit-content-status/id/{id}/status/{status}")
    public ResponseEntity<String> editContentStatus(@PathVariable int id, @PathVariable int status) {
        this.contentService.editContentStatus(id, status);
        return ResponseEntity.status(HttpStatus.OK).body("Conteúdo alterado com sucesso!");
    }

    @GetMapping(value = "/report/get-all-group-by-topic-description")
    public ResponseEntity<List<BasicReportDto>> getAllGroupByTopicDescription() {
        return ResponseEntity.status(HttpStatus.OK).body(contentService.getAllTopicDescriptionAndCountIdGroupByTopicDescription());
    }

    @GetMapping(value = "/report/get-all-group-by-sector-description")
    public ResponseEntity<List<BasicReportDto>> getAllGroupBySectorDescription() {
        return ResponseEntity.status(HttpStatus.OK).body(contentService.getAllSectorDescriptionAndCountIdGroupBySectorDescription());
    }

    @PostMapping(value = "/upload-attachment")
    public ResponseEntity<FileRespondeDto> uploadAttachment(@RequestParam("file") MultipartFile file) {
        String savedFile = null;
        String fileDownloadUri = null;

        if (environmentDevEnabled) {
            savedFile = fileSystemStorageService.saveFileDevelopment(file);

            fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(fileDownloadLocation)
                    .path(savedFile)
                    .toUriString();

        } else if (environmentProdEnabled) {
            savedFile = fileSystemStorageService.saveFileProduction(file);

            fileDownloadUri = fromPath(fileDownloadLocation)
                    .path(savedFile)
                    .toUriString();
        }

        return ResponseEntity.status(HttpStatus.OK).body(new FileRespondeDto(savedFile, fileDownloadUri));
    }

    @GetMapping(value = "/download-attachment/{filename:.+}")
    public ResponseEntity<?> downloadAttachment(@PathVariable String filename, HttpServletRequest request) {
        ResponseEntity responseEntity = null;
        if (environmentDevEnabled) {
            Resource resource = fileSystemStorageService.loadFileDevelopment(filename);

            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert contentType != null;

            responseEntity = ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } else if (environmentProdEnabled) {
            FileDownloadProductionDto downloadFile = fileSystemStorageService.loadFileProduction(filename);

            try {
                byte[] media = IOUtils.toByteArray(downloadFile.getInputStream());

                responseEntity = ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(downloadFile.getMimeType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFile.getName() + "\"")
                        .body(media);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return responseEntity;
    }
}
