package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.Service.BookmarkServiceImpl;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;
import yeonjeans.saera.exception.ErrorResponse;
import yeonjeans.saera.security.dto.AuthMember;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkServiceImpl bookmarkService;

    @Operation(summary = "즐겨찾기 문장 조회", description = "즐겨찾기 된 문장 리스트가 제공됩니다.", tags = { "Bookmark Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StatementResponseDto.class)))}),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/bookmark")
    public ResponseEntity<?> returnBookmarkList(@RequestHeader String authorization, @RequestHeader String RefreshToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        List<StateListItemDto> list = bookmarkService.getList(principal.getId());
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "즐겨찾기 생성", description = "statement_id를 사용하여 bookmark를 등록합니다.", tags = { "Bookmark Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/bookmark/{id}")
    public ResponseEntity<?> createBookmark(@PathVariable Long id, @RequestHeader String authorization, @RequestHeader String RefreshToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        bookmarkService.create(id, principal.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "즐겨찾기 삭제", description = "statement_id를 사용하여 Bookmark를 삭제합니다.", tags = { "Bookmark Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/bookmark/{id}")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long id, @RequestHeader String authorization, @RequestHeader String RefreshToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        bookmarkService.delete(id, principal.getId());
        return ResponseEntity.ok().build();
    }
}