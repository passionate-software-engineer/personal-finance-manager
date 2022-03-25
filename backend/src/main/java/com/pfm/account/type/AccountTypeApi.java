package com.pfm.account.type;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("accountTypes")
@CrossOrigin
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@Tag(name = "Account Type Controller", description = "Controller used to list / add / update / delete account type.")
public interface AccountTypeApi {

  @Operation(summary = "Find account type by id")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          mediaType = "application/json", schema = @Schema(implementation = AccountType.class))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @GetMapping(value = "/{accountTypeId}")
  ResponseEntity<?> getAccountTypeById(@PathVariable long accountId);

  @Operation(summary = "Get list of all account types")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = AccountType.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
  })
  @GetMapping
  ResponseEntity<List<AccountType>> getAccountTypes();

  @Operation(summary = "Create a new account type")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          schema = @Schema(implementation = Long.class))}),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @PostMapping
  ResponseEntity<?> addAccountType(AccountTypeRequest accountTypeRequest);

  @Operation(summary = "Update an existing account type")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @PutMapping(value = "/{accountTypeId}")
  ResponseEntity<?> updateAccountType(@PathVariable long accountTypeId, AccountTypeRequest accountTypeRequest);

  @Operation(summary = "Delete an existing account type")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @DeleteMapping(value = "/{accountTypeId}")
  ResponseEntity<?> deleteAccountType(@PathVariable long accountTypeId);

}

