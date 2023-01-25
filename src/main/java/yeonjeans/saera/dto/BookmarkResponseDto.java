package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.bookmark.Bookmark;
import yeonjeans.saera.domain.statement.Statement;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BookmarkResponseDto {
    String content;
    List<String> tags;
    LocalDateTime date;
    Long statement_id;
    Long bookmark_id;

    public BookmarkResponseDto(Bookmark bookmark) {
        this.bookmark_id = bookmark.getId();
        if(bookmark.getModifiedDate()!=null) {
            this.date = bookmark.getModifiedDate();
        }else{
            this.date = bookmark.getCreatedDate();
        }
        Statement state = bookmark.getStatement();
        this.content = state.getContent();
        this.tags = state.getTags().stream()
                .map(statementTag -> statementTag.getTag().getName())
                .collect(Collectors.toList());
        this.statement_id = state.getId();
    }
}
