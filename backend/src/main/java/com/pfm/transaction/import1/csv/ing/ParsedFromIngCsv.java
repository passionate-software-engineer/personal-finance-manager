package com.pfm.transaction.import1.csv.ing;

import static com.pfm.transaction.import1.ParsedTransactionPropertyFilter.notEmpty;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;
import com.opencsv.bean.processor.PreAssignmentProcessor;
import com.pfm.transaction.import1.csv.PreAssigmentProcessor;
import com.pfm.transaction.import1.csv.PreAssigmentProcessorForBankAccountNumbers;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ParsedFromIngCsv {

  @PreAssignmentProcessor(processor = PreAssigmentProcessor.class)
  @CsvBindByPosition(position = 0)
  @Getter
  @CsvDate("yyyy-MM-dd")
  private LocalDate transactionDate;

  @CsvBindByPosition(position = 2)
  @PreAssignmentProcessor(processor = PreAssigmentProcessor.class)
  private String contractorDetails;

  @CsvBindByPosition(position = 3)
  @PreAssignmentProcessor(processor = PreAssigmentProcessor.class)
  private String title;

  @CsvBindByPosition(position = 4)
  @Getter
  @PreAssignmentProcessor(processor = PreAssigmentProcessorForBankAccountNumbers.class)
  private String receiverBankAccountNumber;

  @CsvBindByPosition(position = 6)
  @PreAssignmentProcessor(processor = PreAssigmentProcessor.class)
  private String details;

  @CsvBindByPosition(position = 7)
  @Getter
  @PreAssignmentProcessor(processor = PreAssigmentProcessor.class)
  private String importId;

  @CsvBindByPosition(position = 8)
  @PreAssignmentProcessor(processor = PreAssigmentProcessor.class)
  @CsvNumber("0.00")
  private BigDecimal transactionAmount;

  @CsvBindByPosition(position = 10)
  @PreAssignmentProcessor(processor = PreAssigmentProcessor.class)
  @CsvNumber("0.00")
  private BigDecimal lockedReleasedAmount;

  public Stream<String> getTransactionDescriptionCandidates() {
    List<String> candidates = List.of(
        this.contractorDetails,
        this.title,
        this.details
    );
    return candidates.stream()
        .filter(notEmpty());
  }

  public BigDecimal getTransactionAmount() {
    return Stream.of(this.transactionAmount, this.lockedReleasedAmount)
        .filter(Objects::nonNull)
        .findFirst()
        .get();
  }

}
