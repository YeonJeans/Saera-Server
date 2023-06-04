package yeonjeans.saera.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import yeonjeans.saera.dto.ML.PitchGraphDto;
import yeonjeans.saera.dto.ML.ScoreRequestDto;
import yeonjeans.saera.exception.CustomException;
import yeonjeans.saera.exception.ErrorCode;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebClientService {
    private final WebClient webClient;
    private final String MLserverBaseUrl;
    @Value("${ml.secret}")
    private String ML_SECRET;
    @Value("${clova.client-id}")
    private String CLOVA_ID;
    @Value("${clova.client-secret}")
    private String CLOVA_SECRET;

    public Double getScore(PitchGraphDto userGraph, PitchGraphDto targetGraph ){
        ScoreRequestDto requestDto = ScoreRequestDto.builder()
                .target_pitch_x(targetGraph.getPitch_x())
                .target_pitch_y(targetGraph.getPitch_y())
                .user_pitch_x(userGraph.getPitch_x())
                .user_pitch_y(userGraph.getPitch_y())
                .build();

        String response = webClient.post()
                .uri(MLserverBaseUrl + "score")
                .header("access-token", ML_SECRET)
                .body(BodyInserters.fromValue(requestDto))
                .retrieve()
                .onStatus(HttpStatus::isError, res -> {
                    if(res.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
                        throw new CustomException(ErrorCode.UNPROCESSABLE_ENTITY);
                    throw new CustomException(ErrorCode.COMMUNICATION_FAILURE);
                })
                .bodyToMono(String.class)
                .block();

        Double score = new JSONObject(response).getDouble("DTW_score");
        return score;
    }

    public Boolean getWordScore(String notation, MultipartFile record){
        try{
            byte[] audioBytes = StreamUtils.copyToByteArray(record.getInputStream());
            ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
                @Override
                public String getFilename() {
                    return "audio.wav";
                }
            };
            String response = webClient.post()
                    .uri(MLserverBaseUrl + "/word-score?target_word="+notation)
                    .header("access-token", ML_SECRET)
                    .body(BodyInserters.fromMultipartData("audio", audioResource))
                    .retrieve()
                    .onStatus(HttpStatus::isError, res -> {
                        if(res.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
                            throw new CustomException(ErrorCode.UNPROCESSABLE_ENTITY);
                        throw new CustomException(ErrorCode.COMMUNICATION_FAILURE);
                    })
                    .bodyToMono(String.class)
                    .block();

            return new JSONObject(response).getBoolean("result");
        }catch(IOException e){
        }
            return false;
    }

    public PitchGraphDto getPitchGraph(Resource resource){
        PitchGraphDto graphDto = webClient.post()
                .uri(MLserverBaseUrl + "pitch-graph")
                .header("access-token", ML_SECRET)
                .body(BodyInserters.fromMultipartData("audio", resource))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.error(response.toString());
                    if(response.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
                        throw new CustomException(ErrorCode.UNPROCESSABLE_ENTITY);
                    throw new CustomException(ErrorCode.COMMUNICATION_FAILURE);
                })
                .bodyToMono(PitchGraphDto.class)
                .block();
        return graphDto;
    }

    public byte[] getTTS(String content){
        Resource resource = webClient.post()
                .uri("https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts")
                .headers(headers -> {
                    headers.set("Content-Type", "application/x-www-form-urlencoded");
                    headers.set("X-NCP-APIGW-API-KEY-ID", CLOVA_ID);
                    headers.set("X-NCP-APIGW-API-KEY", CLOVA_SECRET);
                })
                .body(BodyInserters.fromFormData
                                ("speaker", "vhyeri")
                        .with("text", content)
                        .with("format", "wav"))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    if(response.statusCode() == HttpStatus.BAD_REQUEST)
                        log.error("\n\n[in getTTSfromClova to create Custom] BAD_REQUEST\n\n");
                    throw new CustomException(ErrorCode.COMMUNICATION_FAILURE);
                })
                .bodyToMono(Resource.class)
                .block();

        byte[] audioBytes = new byte[0];
        try {
            InputStream inputStream = resource.getInputStream();
            audioBytes = StreamUtils.copyToByteArray(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioBytes;
    }
}
