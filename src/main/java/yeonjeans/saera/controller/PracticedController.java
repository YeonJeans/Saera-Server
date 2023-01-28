package yeonjeans.saera.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeonjeans.saera.domain.member.Member;
import yeonjeans.saera.domain.member.MemberRepository;
import yeonjeans.saera.domain.practiced.Practiced;
import yeonjeans.saera.domain.practiced.PracticedRepository;
import yeonjeans.saera.dto.PracticedRequestDto;
import yeonjeans.saera.dto.PracticedResponseDto;
import yeonjeans.saera.dto.StateListItemDto;
import yeonjeans.saera.dto.StatementResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class PracticedController {

    private final PracticedRepository practicedRepository;
    private final MemberRepository memberRepository;

    @Operation(summary = "학습한 문장 조회", description = "학습한 문장 리스트가 제공됩니다.", tags = { "Practiced Controller" },
            responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = { @Content(array = @ArraySchema(schema = @Schema(implementation = StateListItemDto.class)))}),
            @ApiResponse(responseCode = "204", description = "존재하지 않는 리소스 접근")
        }
    )
    @GetMapping("/statements/practiced")
    public ResponseEntity returnPracticedList(){
        Optional<Member> member = memberRepository.findById(1L);
        if(member.isPresent()){
            List<StateListItemDto> list = practicedRepository.findAllByMember(member.get())
                    .stream()
                    .map(StateListItemDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(list);
        }else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "학습정보만 조회", description = "practiced_id를 통해 학습정보를 제공합니다..(학습진행후)", tags = { "Practiced Controller" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PracticedResponseDto.class))),
                    @ApiResponse(responseCode = "204", description = "존재하지 않는 리소스 접근")
            }
    )
    @GetMapping("/practiced/{id}")
    public ResponseEntity returnPracticed(@PathVariable Long id){
        Optional<Practiced> practiced = practicedRepository.findById(id);
        if(practiced.isPresent()){
            return ResponseEntity.ok().body(new PracticedResponseDto(practiced.get()));
        }else{
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "학습 정보 생성", description = "statement_id를 사용하여 학습 정보 생성합니다.", tags = { "Practiced Controller" })
    @PostMapping("/statements/{id}/practiced")
    public ResponseEntity createPracticed(@PathVariable Long id, @RequestBody PracticedRequestDto requestDto){
        return null;
    }

    @Operation(summary = "학습 정보 업데이트", description = "학습 정보 업데이트(복습)", tags = { "Practiced Controller" })
    @PatchMapping("/statements/practiced/{id}")
    public ResponseEntity updatedPracticed(@PathVariable Long id, @RequestBody PracticedRequestDto requestDto){
        return null;
    }
}
