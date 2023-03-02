package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.Service.BookmarkServiceImpl;
import yeonjeans.saera.Service.PracticedServiceImpl;
import yeonjeans.saera.Service.StatementService;
import yeonjeans.saera.domain.entity.member.Member;
import yeonjeans.saera.domain.repository.MemberRepository;
import yeonjeans.saera.domain.entity.Statement;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;
import yeonjeans.saera.exception.ErrorResponse;
import yeonjeans.saera.security.dto.AuthMember;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StatementController {

    private final StatementService statementService;
    private final MemberRepository memberRepository;
    private final PracticedServiceImpl practicedService;
    private final BookmarkServiceImpl bookmarkService;

    @Operation(summary = "문장 세부 조회", description = "statement_id를 이용하여 statement 레코드를 단건 조회합니다.", tags = { "Statement Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = StatementResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/statements/{id}")
    public ResponseEntity<StatementResponseDto> returnStatement(@PathVariable Long id, @RequestHeader String authorization, @RequestHeader String RefreshToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        Statement statement = statementService.searchById(id);
        Member member = memberRepository.findById(principal.getId()).orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return ResponseEntity.ok().body(new StatementResponseDto(statement, principal.getId(), member.getNickname(), member.getProfile()));
    }

    @Operation(summary = "예시 음성 조회", description = "statement id를 이용하여 예시 음성을 조회 합니다.", tags = { "Statement Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = StatementResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    @GetMapping("/statements/record/{id}")
    public ResponseEntity<?> returnExampleRecord(@PathVariable Long id){
        Resource resource = statementService.getTTS(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("audio", "wav"));

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @Operation(summary = "문장 리스트 조회", description = "문장 내용(content)나 tag이름을 이용하여 문장리스트를 검색합니다.", tags = { "Statement Controller" },
            responses = {
                @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StatementResponseDto.class)))}),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @GetMapping("/statements")
    public ResponseEntity<?> returnStatementList(
            @RequestParam(value = "bookmarked", defaultValue = "false") boolean bookmarked,
            @RequestParam(value = "practiced", defaultValue = "false") boolean practiced,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "tags", required= false) ArrayList<String> tags,
            @RequestHeader String authorization, @RequestHeader String RefreshToken
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        List<StateListItemDto> list;
        if(bookmarked) list = bookmarkService.getList(principal.getId());
        else if(practiced) list = practicedService.getList(principal.getId());
        else list = statementService.search(content, tags, principal.getId());

        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "최근 검색 내역 조회", description = "최근 검색한 문장 3개 제공", tags = { "Statement Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StatementResponseDto.class)))}),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/search")
    public ResponseEntity<?> searchHistory(@RequestHeader String authorization, @RequestHeader String RefreshToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        List<StateListItemDto> list = statementService.searchHistory(principal.getId());
        return ResponseEntity.ok().body(list);
    }
}
