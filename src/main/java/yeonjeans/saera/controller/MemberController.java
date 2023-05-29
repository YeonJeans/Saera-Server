package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.Service.MemberService;
import yeonjeans.saera.dto.MemberInfoResponseDto;
import yeonjeans.saera.dto.MemberUpdateRequestDto;
import yeonjeans.saera.dto.PracticeDaysResponseDto;
import yeonjeans.saera.exception.ErrorResponse;
import yeonjeans.saera.security.dto.AuthMember;

@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "유저 정보 조회", description = "Access Token을 이용하여 유저 정보 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberInfoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "499", description = "토큰 만료로 인한 인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/member")
    public ResponseEntity<?> returnMemberInfo(@RequestHeader String Authorization){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        MemberInfoResponseDto dto = memberService.getMemberInfo(principal.getId());
        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "유저 정보 수정", description = "Access Token을 이용하여 유저 정보 수정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MemberInfoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "499", description = "토큰 만료로 인한 인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping(value="/member", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMemberInfo(
            @ModelAttribute MemberUpdateRequestDto requestDto,
            @RequestHeader String Authorization
    ) {
        System.out.println("?");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        MemberInfoResponseDto dto = memberService.updateMember(principal.getId(), requestDto);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "연속 학습 일수 조회", description = "Access Token을 이용하여 유저의 연속 학습 일수를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PracticeDaysResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "499", description = "토큰 만료로 인한 인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/get-attendance-days")
    public ResponseEntity<?> returnAttendanceDays(@RequestHeader String Authorization) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        PracticeDaysResponseDto dto = memberService.getPracticeDays(principal.getId());
        return ResponseEntity.ok().body(dto);
    }
}