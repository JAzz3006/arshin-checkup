package che.arshin.checkup.client;
import che.arshin.checkup.client.dto.ArshinResponse;
import che.arshin.checkup.exception.BadArshinResponseException;
import che.arshin.checkup.web.dto.ArshinQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArshinClient {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${app.integration.base-url}")
    private String baseUrl;

    public ArshinResponse getArshinResponse (ArshinQuery arshinQuery){
        Request request = new Request.Builder()
                .url(buildUrl(arshinQuery))
                .get()
                .build();
        return processResponse(request, new TypeReference<>(){
        });
    }

    private HttpUrl buildUrl(ArshinQuery arshinQuery) {
        String searchNumber = arshinQuery.getMiSearchNumber() != null &&
                !arshinQuery.getMiSearchNumber().isBlank() ?
                arshinQuery.getMiSearchNumber() : arshinQuery.getSerialNumber();

        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseUrl))
                .newBuilder()
                //.addQueryParameter("fq", "mi.mitype:*" + arshinQuery.getMiType() + "*")
                //.addQueryParameter("fq", "mi.modification:*" +arshinQuery.getModification() + "*")
                .addQueryParameter("fq", "mi.number:" + searchNumber)
                .addQueryParameter("q", "*")
                .addQueryParameter("fl",
                        "vri_id,org_title,mi.mitnumber,mi.mititle," +
                                "mi.mitype,mi.modification,mi.number," +
                                "verification_date,valid_date,applicability," +
                                "result_docnum,sticker_num")
                .addQueryParameter("sort", "verification_date desc,org_title asc") //it is crucial for further logic to maintain this sort type - the latest validation must come first
                .addQueryParameter("rows", "20")
                .addQueryParameter("start", "0");

        if (arshinQuery.getModification() != null && !arshinQuery.getModification().isBlank()){
            builder.addQueryParameter(
                    "fq",
                    "mi.modification:" + arshinQuery.getModification()
            );
        }
        if (arshinQuery.getMiType() != null && !arshinQuery.getMiType().isBlank()){
            builder.addQueryParameter(
                    "fq",
                    "mi.mitype:" + arshinQuery.getMiType()
            );
        }

        HttpUrl url = builder.build();
        log.info("Запрос: {}", url);
        return url;
    }

    private <T> T processResponse (Request request, TypeReference<T> typeReference) {
        try(Response response = httpClient.newCall(request).execute()){
            if (!response.isSuccessful()){
                throw new BadArshinResponseException("Неожиданный код ответа " + response);
            }

            ResponseBody responseBody = response.body();

            if (responseBody != null){
                String stringBody = responseBody.string();
                return objectMapper.readValue(stringBody, typeReference);
            }else {
                throw new BadArshinResponseException("Ответ ГИС Аршин пустой");
            }
        }catch (IOException e){
            log.error("Ошибка обращения к ГИС Аршин", e);
            throw new BadArshinResponseException("Ошибка обработки ответа ГИС Аршин");
        }
    }
}