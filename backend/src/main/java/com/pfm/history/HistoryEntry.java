package com.pfm.history;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class HistoryEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "HistoryEntry id (generated by application)", required = true)
  private Long id;

  @Schema(description = "Time event happened", required = true)
  private ZonedDateTime date;

  @Enumerated(EnumType.STRING)
  private Type type;

  private String object;

  @OneToMany(cascade = CascadeType.ALL)
  private List<HistoryInfo> entries;

  private Long userId;

  public enum Type {
    ADD, DELETE, UPDATE
  }
}
