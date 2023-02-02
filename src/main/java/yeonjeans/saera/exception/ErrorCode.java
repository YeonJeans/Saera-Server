package yeonjeans.saera.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    UPLOAD_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    STATEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 예시 문장을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다"),

    ;

    private final HttpStatus httpStatus;
    private final String detail;
}
