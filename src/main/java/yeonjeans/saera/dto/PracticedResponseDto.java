package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.example.Statement;
import yeonjeans.saera.util.Parsing;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PracticedResponseDto {
    String content;
    LocalDateTime date;
    Double score;
    private List<Integer> pitch_x;
    private List<Double> pitch_y;

    Long id;

    public PracticedResponseDto(Practice practice) {
        this.date = practice.getModifiedDate()!=null? practice.getModifiedDate() : practice.getCreatedDate();
        this.score = practice.getScore();
        this.pitch_x = Parsing.stringToIntegerArray(practice.getPitchX());
        this.pitch_y = Parsing.stringToDoubleArray(practice.getPitchY());

//        Statement state = practice.getStatement();
//        this.content = state.getContent();
//        this.id = state.getId();
    }
}
