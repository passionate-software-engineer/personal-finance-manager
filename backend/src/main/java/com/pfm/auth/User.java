package com.pfm.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
public final class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "User id (generated by application)", required = true, example = "1")
  private Long id;

  @Schema(description = "User name", required = true, example = "Marian")
  private String username;

  @Schema(description = "User password", required = true, example = "123456789Marian")
  private String password;

  @Schema(description = "User first name", required = true, example = "Marian")
  private String firstName;

  @Schema(description = "User last name", required = true, example = "Pazdzioch")
  private String lastName;

}
