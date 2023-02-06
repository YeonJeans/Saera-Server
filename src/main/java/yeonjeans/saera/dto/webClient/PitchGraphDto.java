package yeonjeans.saera.dto.webClient;

import lombok.Getter;

import java.util.List;

@Getter
public class PitchGraphDto {
    List<Integer> pitch_x;
    List<Double> pitch_y;
}
