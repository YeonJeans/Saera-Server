package yeonjeans.saera.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.practiced.Practiced;
import yeonjeans.saera.domain.statement.Statement;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Getter
@NoArgsConstructor
public class PracticedRequestDto {
    private Member member;
    private Statement statement;

    private String record;
    private String graphX;
    private String graphY;

    private Integer accuracy;

    @Builder
    public PracticedRequestDto(String record) {
    }

    public Practiced toEntity() {
        return null;
    }
}
