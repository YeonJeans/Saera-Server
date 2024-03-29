package yeonjeans.saera.dto;

import lombok.Data;
import yeonjeans.saera.domain.entity.Bookmark;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.domain.entity.custom.Custom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CustomListItemDto {
    private String content;
    private List<String> tags;
    private LocalDateTime date;

    private Long id;
    private Boolean practiced;
    private Boolean bookmarked;
    private Boolean isPublic;
    private Boolean isOwner;

    public CustomListItemDto(Object[] result, boolean isOwner) {
        Bookmark bookmark = result[1] instanceof Bookmark ? ((Bookmark) result[1]) : null;
        Practice practice = result[2] instanceof Practice ? ((Practice) result[2]) : null;
        this.practiced = practice != null;
        this.bookmarked = bookmark != null;

        this.date = practiced ? practice.getModifiedDate() : null;

        Custom custom = ((Custom) result[0]);

        this.id = custom.getId();
        this.content = custom.getContent();
        if(isOwner) this.tags = custom.getTags().stream()
                .map(customCtag -> customCtag.getTag().getName())
                .collect(Collectors.toList());
        this.isPublic = custom.getIsPublic();
        this.isOwner = isOwner;
    }

    public CustomListItemDto(Object[] result) {
        Bookmark bookmark = result[1] instanceof Bookmark ? ((Bookmark) result[1]) : null;
        Practice practice = result[2] instanceof Practice ? ((Practice) result[2]) : null;
        this.practiced = practice != null;
        this.bookmarked = bookmark != null;

        this.date = practiced ? practice.getModifiedDate() : null;

        Custom custom = ((Custom) result[0]);

        this.id = custom.getId();
        this.content = custom.getContent();
        this.tags = custom.getTags().stream()
                .map(customCtag -> customCtag.getTag().getName())
                .collect(Collectors.toList());
        this.isPublic = custom.getIsPublic();
        this.isOwner = false;
    }
}