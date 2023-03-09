package yeonjeans.saera.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import yeonjeans.saera.domain.entity.custom.CTag;

@Getter
public class CTagListItemDto {
    private String name;
    private Long id;

    public CTagListItemDto(CTag cTag) {
        this.name = cTag.getName();
        this.id = cTag.getId();
    }
}
