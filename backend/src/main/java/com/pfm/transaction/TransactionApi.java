package com.pfm.transaction;

import static com.pfm.config.SwaggerConfig.SECURITY_SCHEME_NAME;
import static com.pfm.helpers.http.HttpCodesAsString.BAD_REQUEST;
import static com.pfm.helpers.http.HttpCodesAsString.NOT_FOUND;
import static com.pfm.helpers.http.HttpCodesAsString.OK;
import static com.pfm.helpers.http.HttpCodesAsString.UNAUTHORIZED;
import static com.pfm.swagger.ApiConstants.BAD_REQUEST_MESSAGE;
import static com.pfm.swagger.ApiConstants.NOT_FOUND_MESSAGE;
import static com.pfm.swagger.ApiConstants.OK_MESSAGE;
import static com.pfm.swagger.ApiConstants.UNAUTHORIZED_MESSAGE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("transactions")
@CrossOrigin
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@Tag(name = "Transaction Controller", description = "Controller used to list / add / update / delete transaction.")
public interface TransactionApi {

  String TRANSACTION_ID = "{transactionId}";

  @Operation(summary = "Find transaction by id")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          mediaType = "application/json", schema = @Schema(implementation = Transaction.class))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @GetMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<Transaction> getTransactionById(@PathVariable long transactionId);

  @Operation(summary = "Get list of all transactions")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = Transaction.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @GetMapping
  ResponseEntity<List<Transaction>> getTransactions();

  @Operation(summary = "Create a new transaction")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          schema = @Schema(implementation = Long.class))}),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @PostMapping
  ResponseEntity<?> addTransaction(TransactionRequest transactionRequest);

  @Operation(summary = "Update an existing transaction")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @PutMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<?> updateTransaction(@PathVariable long transactionId, TransactionRequest transactionRequest);

  @Operation(summary = "Delete an existing transaction")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @DeleteMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<?> deleteTransaction(@PathVariable long transactionId);

  @Operation(summary = "Commits (converts) planned transaction into transaction")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @PatchMapping(value = "/" + TRANSACTION_ID)
  ResponseEntity<?> commitPlannedTransaction(@PathVariable long transactionId, TransactionRequest preCommitUpdate);

  @Operation(summary = "Sets planned transaction status to recurrent")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @PatchMapping(value = "/" + TRANSACTION_ID + "/setAsRecurrent")
  ResponseEntity<?> setAsRecurrent(@PathVariable long transactionId, @RequestParam RecurrencePeriod recurrencePeriod);

}
