package com.pfm.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  private Long id;
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String token;

  public Long getId() {
    return this.id;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public String getToken() {
    return this.token;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }
    final User other = (User) o;
    if (!other.canEqual((Object) this)) {
      return false;
    }
    final Object this$id = this.getId();
    final Object other$id = other.getId();
    if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
      return false;
    }
    final Object this$username = this.getUsername();
    final Object other$username = other.getUsername();
    if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
      return false;
    }
    final Object this$password = this.getPassword();
    final Object other$password = other.getPassword();
    if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
      return false;
    }
    final Object this$firstName = this.getFirstName();
    final Object other$firstName = other.getFirstName();
    if (this$firstName == null ? other$firstName != null : !this$firstName.equals(other$firstName)) {
      return false;
    }
    final Object this$lastName = this.getLastName();
    final Object other$lastName = other.getLastName();
    if (this$lastName == null ? other$lastName != null : !this$lastName.equals(other$lastName)) {
      return false;
    }
    final Object this$token = this.getToken();
    final Object other$token = other.getToken();
    if (this$token == null ? other$token != null : !this$token.equals(other$token)) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    final int prime = 59;
    int result = 1;
    final Object $id = this.getId();
    result = result * prime + ($id == null ? 43 : $id.hashCode());
    final Object $username = this.getUsername();
    result = result * prime + ($username == null ? 43 : $username.hashCode());
    final Object $password = this.getPassword();
    result = result * prime + ($password == null ? 43 : $password.hashCode());
    final Object $firstName = this.getFirstName();
    result = result * prime + ($firstName == null ? 43 : $firstName.hashCode());
    final Object $lastName = this.getLastName();
    result = result * prime + ($lastName == null ? 43 : $lastName.hashCode());
    final Object $token = this.getToken();
    result = result * prime + ($token == null ? 43 : $token.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof User;
  }

  public String toString() {
    return "User(id=" + this.getId() + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", firstName=" + this.getFirstName()
        + ", lastName=" + this.getLastName() + ", token=" + this.getToken() + ")";
  }
}
