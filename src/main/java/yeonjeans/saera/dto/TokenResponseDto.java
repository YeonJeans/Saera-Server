package yeonjeans.saera.dto;

public class TokenResponseDto {
    private String accessToken;
    private String tokenType;

    public TokenResponseDto(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }
}
