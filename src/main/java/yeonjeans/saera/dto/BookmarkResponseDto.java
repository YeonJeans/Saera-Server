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
    Long statement_id;
    Long bookmarked;

    public BookmarkResponseDto(Bookmark bookmark) {
        this.bookmarked = bookmark.getId();
        this.statement_id = bookmark.getStatement().getId();
    }
}
