package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.bookmark.Bookmark;
import yeonjeans.saera.domain.practiced.Practiced;
import yeonjeans.saera.domain.statement.Statement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class StateListItemDto {
    private String content;
    private List<String> tags;
    private LocalDateTime date;

    private Long statement_id;
    private Long practiced_id;
    private Long Bookmark_id;


    public StateListItemDto(Practiced practiced) {
        this.practiced_id = practiced.getId();

        this. date = practiced.getModifiedDate()!=null? practiced.getModifiedDate() : practiced.getCreatedDate();

        Statement state = practiced.getStatement();
        this.content = state.getContent();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
        this.statement_id = state.getId();
    }

    public void setBookmark_id(Long bookmark_id) {
        Bookmark_id = bookmark_id;
    }
}
