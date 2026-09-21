package che.arshin.checkup.client;
import che.arshin.checkup.client.dto.ArshinResponse;
import che.arshin.checkup.web.dto.ArshinQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
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

    //https://fgis.gost.ru/fundmetrology/cm/xcdb/vri/select?
    // fq=mi.mitype:*%D0%BC%D0%B5%D1%82%D1%80%D0%B0%D0%BD*&
    // fq=mi.modification:*150TA2*&
    // fq=mi.number:*6183862*
    // &q=*&fl=vri_id,org_title,mi.mitnumber,mi.mititle,mi.mitype,mi.modification,mi.number,
    // verification_date,valid_date,applicability,result_docnum,sticker_num
    // &sort=verification_date+desc,org_title+asc&rows=20&start=0

    private HttpUrl buildUrl(ArshinQuery arshinQuery) {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(baseUrl))
                .newBuilder()
                .addQueryParameter("fq", "mi.mitype:*" + arshinQuery.getMiType() + "*")
                .addQueryParameter("fq", "mi.modification:*" +arshinQuery.getModification() + "*")
                .addQueryParameter("fq", "mi.number:" + arshinQuery.getSerialNumber())
                .addQueryParameter("q", "*")
                .addQueryParameter("fl",
                        "vri_id,org_title,mi.mitnumber,mi.mititle," +
                                "mi.mitype,mi.modification,mi.number," +
                                "verification_date,valid_date,applicability," +
                                "result_docnum,sticker_num")
                .addQueryParameter("sort", "verification_date desc,org_title asc")
                .addQueryParameter("rows", "20")
                .addQueryParameter("start", "0")
                .build();
        return url;
    }

    private <T> T processResponse (Request request, TypeReference<T> typeReference) {
        try(Response response = httpClient.newCall(request).execute()){
            if (!response.isSuccessful()){
                throw new RuntimeException("Unexpected response code " + response);
            }

            ResponseBody responseBody = response.body();

            if (responseBody != null){
                String stringBody = responseBody.string();
                return objectMapper.readValue(stringBody, typeReference);
            }else {
                throw new RuntimeException("ResponseBody is empty!");
            }
        }catch (IOException e){
            throw new RuntimeException("Error processing Arshin response", e);
        }
    }
}