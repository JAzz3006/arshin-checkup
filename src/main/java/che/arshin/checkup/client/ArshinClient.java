package che.arshin.checkup.client;
import che.arshin.checkup.client.dto.ArshinResponse;
import che.arshin.checkup.web.dto.ArshinQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ArshinClient {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${app.integration.base-url}")
    private String baseUrl;

    public ArshinResponse getArshinResponse (ArshinQuery arshinQuery){
        Request request = new Request.Builder()
                .url(baseUrl + "?fq=mi.mitype:*%D1%8D%D0%BC%D0%B8%D1%81*&fq=mi.modification:*143*&fq=mi.number:*75381*&q=*&fl=vri_id,org_title,mi.mitnumber,mi.mititle,mi.mitype,mi.modification,mi.number,verification_date,valid_date,applicability,result_docnum,sticker_num&sort=verification_date+desc,org_title+asc&rows=20&start=0")
                .build();
        return processResponse(request, new TypeReference<>(){
        });
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