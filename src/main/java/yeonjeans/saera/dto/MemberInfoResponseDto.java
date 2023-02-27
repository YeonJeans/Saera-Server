package yeonjeans.saera.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class MemberInfoResponseDto {
    private String name;
    private String email;
    private String profileUrl;
    private int xp;
}
