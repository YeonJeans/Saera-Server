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
import yeonjeans.saera.Service.PracticedServiceImpl;
import yeonjeans.saera.domain.entity.Practice;
import yeonjeans.saera.dto.PracticedRequestDto;
import yeonjeans.saera.dto.PracticedResponseDto;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.exception.ErrorResponse;
import yeonjeans.saera.security.dto.AuthMember;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PracticedController {
    private final PracticedServiceImpl practicedService;

    @Operation(summary = "학습한 문장 조회", description = "학습한 문장 리스트가 제공됩니다.", tags = { "Practiced Controller" },
            responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StateListItemDto.class)))}),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @GetMapping("/practiced")
    public ResponseEntity<?> returnPracticedList(@RequestHeader String authorization, @RequestHeader String RefreshToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        List<StateListItemDto> list = practicedService.getList(principal.getId());
            return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "유저 음성 파일 조회", description = "statement_id를 통해 유저의 음성 녹음 파일을 제공합니다.", tags = { "Practiced Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PracticedResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/practiced/record/{id}")
    public ResponseEntity returnPracticedRecord(@PathVariable(required = false) Long id, @RequestHeader String authorization, @RequestHeader String RefreshToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        Resource resource = practicedService.getRecord(id, principal.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("audio", "wav"));
        return ResponseEntity.ok().headers(headers).body(resource);

    }

    @Operation(summary = "학습 정보 조회", description = "문장 id를 통해 학습정보를 제공합니다..", tags = { "Practiced Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PracticedResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/practiced/{id}")
    public ResponseEntity<?> returnPracticed(@PathVariable(required = false) Long id, @RequestHeader String authorization, @RequestHeader String RefreshToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        PracticedResponseDto dto = practicedService.read(id, principal.getId());

        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "학습 정보 생성", description = "statement_id를 사용하여 학습 정보 생성합니다.", tags = { "Practiced Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PracticedResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping(value = "/practiced", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPracticed(@ModelAttribute PracticedRequestDto requestDto, @RequestHeader String authorization, @RequestHeader String RefreshToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthMember principal = (AuthMember) authentication.getPrincipal();

        Practice practice = practicedService.create(requestDto, principal.getId());

        return ResponseEntity.ok().body(new PracticedResponseDto(practice));
    }
}
