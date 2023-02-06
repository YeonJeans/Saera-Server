package yeonjeans.saera.dto.webClient;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ScoreRequestDto {
    int[] target_pitch_x;
    double[] target_pitch_y;
    int[] user_pitch_x;
    double[] user_pitch_y;

    @Builder
    public ScoreRequestDto(int[] target_pitch_x, double[] target_pitch_y, int[] user_pitch_x, double[] user_pitch_y) {
        this.target_pitch_x = target_pitch_x;
        this.target_pitch_y = target_pitch_y;
        this.user_pitch_x = user_pitch_x;
        this.user_pitch_y = user_pitch_y;
    }
}
