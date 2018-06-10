package account;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String currency;
  private Long accountNumber;
  private BigDecimal balance;
  private LocalDateTime lastModifiedTS;
  private Integer userId;

}
