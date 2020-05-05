package com.pfm.account.type;

import com.pfm.auth.Token;
import com.pfm.auth.User;
import com.pfm.auth.UserDetails;
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

  @ApiOperation(value = "Authenticate user", response = Void.class)
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = UserDetails.class),
      @ApiResponse(code = 400, message = "Bad request", response = String.class, responseContainer = "list"),
  })
  @PostMapping("/authenticate")
  ResponseEntity<?> authenticateUser(@RequestBody User userToAuthenticate);

  @ApiOperation(value = "Register user", response = Void.class)
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = Long.class),
      @ApiResponse(code = 400, message = "Bad request", response = String.class, responseContainer = "list"),
  })
  @PostMapping("/register")
  ResponseEntity<?> registerUser(@RequestBody User user);

  @ApiOperation(value = "Refresh user's token", response = Void.class)
  @ApiResponses( {
      @ApiResponse(code = 200, message = "OK", response = Token.class),
      @ApiResponse(code = 400, message = "Bad request", response = String.class, responseContainer = "list"),
  })
  @PostMapping("/refresh")
  ResponseEntity<?> refreshToken(@RequestBody String refreshToken);

}