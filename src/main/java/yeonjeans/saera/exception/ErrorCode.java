package yeonjeans.saera.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    UPLOAD_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ""),
    GOOGLE_AUTH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "구글 서버에서 Token을 받아오는 데 실패했습니다."),

    /* 401 Unauthorized: 인증 실패*/
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰 형식입니다."),
    REISSUE_FAILURE(HttpStatus.UNAUTHORIZED, "토큰 재발급에 실패했습니다."),
    BEARER_ERROR(HttpStatus.UNAUTHORIZED, "BEARER TOKEN 형식으로 요청해야 합니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, ""),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    STATEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 예시 문장을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "즐겨찾기 정보를 찾을 수 없습니다."),
    PRACTICED_NOT_FOUND(HttpStatus.NOT_FOUND, "학습 내역을 찾을 수 없습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다"),
    ALREADY_BOOKMARKED(HttpStatus.CONFLICT, "이미 즐겨찾기 된 문장입니다.");
    ;

    private final HttpStatus httpStatus;
    private final String detail;
}
