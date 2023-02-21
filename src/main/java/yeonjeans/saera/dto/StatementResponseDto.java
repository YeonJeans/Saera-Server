package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.statement.Statement;
import yeonjeans.saera.util.Parsing;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StatementResponseDto<Bookmarked> {
    private Long id;
    private String content;
    private List<Integer> pitch_x;
    private List<Double> pitch_y;

    List<String> tags;
    private Boolean bookmarked;
    private Boolean practiced;

    public StatementResponseDto(Statement state, Long memberId) {
        this.id = state.getId();
        this.content = state.getContent();
        this.pitch_x = Parsing.stringToIntegerArray(state.getPitchX());
        this.pitch_y = Parsing.stringToDoubleArray(state.getPitchY());
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());

        this.bookmarked = state.getBookmarks().stream().anyMatch(bookmark -> bookmark.getMember().getId().equals(memberId));
        this.practiced = state.getPracticeds().stream().anyMatch(practiced -> practiced.getMember().getId().equals(memberId));
    }

}
