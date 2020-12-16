package br.com.chatbot.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadProductionDto {
    private String name;
    private String mimeType;
    private InputStream inputStream;
}
