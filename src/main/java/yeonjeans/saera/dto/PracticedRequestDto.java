package yeonjeans.saera.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class PracticedRequestDto {
    private Long id;
    private MultipartFile record;
}
