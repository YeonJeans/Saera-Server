package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.Service.StudyServiceImpl;
import yeonjeans.saera.dto.CompleteStudyRequestDto;
import yeonjeans.saera.dto.StatementResponseDto;
import yeonjeans.saera.exception.ErrorResponse;
import yeonjeans.saera.security.dto.AuthMember;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StudyController {
    private final StudyServiceImpl studyService;

    @Operation(summary = "완료한 학습 리스트 조회", description = "type, fk list를 이용하여 완료한 학습 목록을 제공 받습니다.", tags = { "Study Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = StatementResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "499", description = "토큰 만료로 인한 인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/complete")
    public ResponseEntity<?> returnStatement(@ModelAttribute CompleteStudyRequestDto requestDto, @RequestHeader String authorization){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        List<Object> list = studyService.completeStudy(requestDto, principal.getId());
        return ResponseEntity.ok().body(list);
    }
}
