package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.statement.Statement;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Data
public class StatementResponseDto {
    private Long id;
    private String content;
    private String graphX;
    private String graphY;

    List<String> tags;
    private Long bookmarked;

    public StatementResponseDto(Statement state) {
        this.id = state.getId();
        this.content = state.getContent();
        this.graphX = state.getPitchX();
        this.graphY = state.getPitchY();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
        try{
            this.bookmarked = state.getBookmarks().stream()
                    .filter(bookmark -> bookmark.getMember().getId().equals(1L))
                    .findFirst().orElseThrow().getId();
        }catch (NoSuchElementException exception){
            this.bookmarked = null;
        }
    }

}
