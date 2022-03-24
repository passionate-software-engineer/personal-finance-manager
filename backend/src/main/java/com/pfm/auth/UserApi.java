package com.pfm.auth;

import static com.pfm.helpers.http.HttpCodesAsString.BAD_REQUEST;
import static com.pfm.helpers.http.HttpCodesAsString.OK;
import static com.pfm.swagger.ApiConstants.BAD_REQUEST_MESSAGE;
import static com.pfm.swagger.ApiConstants.OK_MESSAGE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("users")
@CrossOrigin
@Tag(name = "User Controller", description = "Controller used to register / refresh / authenticate user")
public interface UserApi {

  @Operation(summary = "Authenticate user")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          mediaType = "application/json", schema = @Schema(implementation = UserDetails.class))}),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))})
  })
  @PostMapping("/authenticate")
  ResponseEntity<?> authenticateUser(@RequestBody User userToAuthenticate);

  @Operation(summary = "Register user")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          schema = @Schema(implementation = Long.class))}),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))})
  })
  @PostMapping("/register")
  ResponseEntity<?> registerUser(@RequestBody User user);

  @Operation(summary = "Refresh user's token")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          mediaType = "application/json", schema = @Schema(implementation = Token.class))}),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))})
  })
  @PostMapping("/refresh")
  ResponseEntity<?> refreshToken(@RequestBody String refreshToken);

}
