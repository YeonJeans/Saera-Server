package yeonjeans.saera.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import yeonjeans.saera.Service.MemberService;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);

        if (accessToken != null && refreshToken != null) {
            try {
                tokenProvider.validateToken(accessToken);
            } catch (ExpiredJwtException e) {
                logger.info(e);
//                tokenProvider.validateToken(refreshToken);
//                JSONObject reissued = memberService.reIssueToken(refreshToken);
//
//                response.setStatus(499);
//                response.setContentType("application/json");
//                response.setCharacterEncoding("UTF-8");
//                response.getWriter().print(reissued);
//                return;
            }

            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (!StringUtils.hasText(bearerToken)) return null;

        if (bearerToken.length() > 7 && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        throw new CustomException(ErrorCode.BEARER_ERROR);
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("RefreshToken");
        if (!StringUtils.hasText(bearerToken)) return null;

        if (bearerToken.length() > 7 && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        throw new CustomException(ErrorCode.BEARER_ERROR);
    }

}