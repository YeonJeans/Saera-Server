package yeonjeans.saera.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@AllArgsConstructor
public class MemberUpdateRequestDto {
    private String nickname;
    private MultipartFile image;
}