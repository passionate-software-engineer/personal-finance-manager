import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

// TODO you use too much of "default" - class should be public and should be in helpers package
class TestHelper {

  private TestHelper() {
  }

  // TODO externalize URL
  private static final String URL = "http://localhost:8081/accounts";

  static void addSampleAccount() throws IOException {
    String sampleJson = "{\"name\":\"mbank\", \"balance\":500 };";
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
    return String.valueOf(httpResponse.code()); // TODO would be good to verify if response was 200, otherwise precondition failed
  }
}
