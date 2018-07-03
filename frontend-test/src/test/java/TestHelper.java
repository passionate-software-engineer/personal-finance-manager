import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

class TestHelper {
  //TODO externalize
  private static final String URL = "http://localhost:8081/accounts";

  static void addSampleAccount() throws IOException {
    String sampleJson = "{ \"id\":1, \"name\":\"mbank\", \"balance\":500 };";
     postJson(sampleJson);
  }

  private static String postJson(String json) throws IOException {
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody
        .create(mediaType, json);
    Request request = new Request.Builder()
        .url(URL)
        .post(body)
        .build();

    Response httpResponse = client.newCall(request).execute();
    return String.valueOf(httpResponse.code());
  }
}