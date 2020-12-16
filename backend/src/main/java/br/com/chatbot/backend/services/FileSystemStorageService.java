package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.FileDownloadProductionDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileSystemStorageService {
    void init();
    String saveFileDevelopment(MultipartFile file);
    String saveFileProduction(MultipartFile file);
    Resource loadFileDevelopment(String fileName);
    FileDownloadProductionDto loadFileProduction(String fileId);
}
