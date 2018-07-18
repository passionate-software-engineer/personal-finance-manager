package com.pfm.helpers;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class TestHelper {

  private TestHelper() {
  }

  // TODO externalize URL
  private static final String URL = "http://localhost:8088/accounts";

  public static void addSampleAccount() throws IOException {
    String sampleJson = "{\"name\":\"ideaBank\", \"balance\":320 };";
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
