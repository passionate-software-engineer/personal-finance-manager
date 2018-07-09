package com.pfm.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfm.account.Account;

import java.io.IOException;

public class JsonConverter {

  private final ObjectMapper mapper;

  public JsonConverter(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public String convertFromAccountToJson(Account account) throws JsonProcessingException {

    String jsonInString = mapper.writeValueAsString(account);
    return jsonInString;
  }

  public Account convertFromJsonToAccount(String thisLine) {
    Account obj = null;
    try {
      obj = mapper.readValue(thisLine, Account.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return obj;
  }

}



