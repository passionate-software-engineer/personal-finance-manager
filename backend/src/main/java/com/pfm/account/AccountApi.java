package com.pfm.account;

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

@RequestMapping("accounts")
@CrossOrigin
@Tag(name = "Account Controller", description = "Controller used to list / add / update / delete accounts.")
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
public interface AccountApi {

  //  @ApiOperation(value = "Find account by id", authorizations = {@Authorization(value = BEARER)})
  @Operation(summary = "Find account by id")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          mediaType = "application/json", schema = @Schema(implementation = Account.class))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @GetMapping(value = "/{accountId}")
  ResponseEntity<?> getAccountById(@PathVariable long accountId);

  @Operation(summary = "Get list of all accounts")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = Account.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @GetMapping
  ResponseEntity<List<Account>> getAccounts();

  @Operation(summary = "Create a new account")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          schema = @Schema(implementation = Long.class))}),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @PostMapping
  ResponseEntity<?> addAccount(AccountRequest accountRequest);

  @Operation(summary = "Update an existing account")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @PutMapping(value = "/{accountId}")
  ResponseEntity<?> updateAccount(@PathVariable long accountId, AccountRequest accountRequest);

  @Operation(summary = "Update an existing account by setting lastVerificationDate to today")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @PatchMapping(value = "/{accountId}/markAccountAsVerifiedToday")
  ResponseEntity<?> markAccountAsVerifiedToday(@PathVariable long accountId);

  @Operation(summary = "Archive the account")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @PatchMapping(value = "/{accountId}/markAsArchived")
  ResponseEntity<?> markAccountAsArchived(@PathVariable long accountId);

  @Operation(summary = "Restore the account")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @PatchMapping(value = "/{accountId}/markAsActive")
  ResponseEntity<?> markAccountAsActive(@PathVariable long accountId);

  @Operation(summary = "Delete an existing account")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @DeleteMapping(value = "/{accountId}")
  ResponseEntity<?> deleteAccount(@PathVariable long accountId);
}
