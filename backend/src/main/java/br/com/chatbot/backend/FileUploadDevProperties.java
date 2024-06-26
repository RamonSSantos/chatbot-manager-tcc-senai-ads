package br.com.chatbot.backend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadDevProperties {
    private String location;
}
