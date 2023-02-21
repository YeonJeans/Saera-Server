package yeonjeans.saera.dto.ML;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class PitchGraphDto {
    ArrayList<Integer> pitch_x;
    ArrayList<Double> pitch_y;
}
