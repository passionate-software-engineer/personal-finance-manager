package com.pfm.auth;

import static com.pfm.swagger.ApiConstants.BAD_REQUEST_MESSAGE;
import static com.pfm.swagger.ApiConstants.CONTAINER_LIST;
import static com.pfm.swagger.ApiConstants.OK_MESSAGE;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("users")
@CrossOrigin
@Api(tags = {"user-controller"})
public interface UserApi {

  @ApiOperation(value = "Authenticate user")
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = UserDetails.class),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
  })
  @PostMapping("/authenticate")
  ResponseEntity<?> authenticateUser(@RequestBody User userToAuthenticate);

  @ApiOperation(value = "Register user")
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Long.class),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
  })
  @PostMapping("/register")
  ResponseEntity<?> registerUser(@RequestBody User user);

  @ApiOperation(value = "Refresh user's token")
  @ApiResponses({
      @ApiResponse(code = 200, message = OK_MESSAGE, response = Token.class),
      @ApiResponse(code = 400, message = BAD_REQUEST_MESSAGE, response = String.class, responseContainer = CONTAINER_LIST),
  })
  @PostMapping("/refresh")
  ResponseEntity<?> refreshToken(@RequestBody String refreshToken);

}
