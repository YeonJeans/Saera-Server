package yeonjeans.saera.dto.ML;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PitchGraphDto {
    ArrayList<Integer> pitch_x;
    ArrayList<Double> pitch_y;
}
