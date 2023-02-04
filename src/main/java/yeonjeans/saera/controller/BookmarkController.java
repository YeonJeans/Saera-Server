package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.Service.BookmarkServiceImpl;
import yeonjeans.saera.dto.BookmarkResponseDto;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkServiceImpl bookmarkService;

    @Operation(summary = "즐겨찾기 문장 조회", description = "즐겨찾기 된 문장 리스트가 제공됩니다.", tags = { "Bookmark Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StatementResponseDto.class)))}),
                    @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
            }
    )
    @GetMapping("/statements/bookmark")
    public ResponseEntity<?> returnBookmarkList(){
        List<StateListItemDto> list = bookmarkService.getList(1L);
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "즐겨찾기 생성", description = "statement_id를 사용하여 bookmark를 등록합니다.", tags = { "Bookmark Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = BookmarkResponseDto.class))),
            }
    )
    @PostMapping("/statements/{id}/bookmark")
    public ResponseEntity<?> createBookmark(@PathVariable Long id){
            return ResponseEntity.ok().body(bookmarkService.create(id));
    }

    @Operation(summary = "즐겨찾기 삭제", description = "statement_id를 사용하여 Bookmark를 삭제합니다.", tags = { "Bookmark Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
            }
    )
    @DeleteMapping("/statements/bookmark/{id}")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long id){
        bookmarkService.delete(id);
        return ResponseEntity.ok().build();
    }
}
