package com.pfm.transaction.import1;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import com.pfm.config.csv.CsvIngParserConfig;
import com.pfm.transaction.import1.csv.ing.ParsedFromIngCsv;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE")
@SuppressWarnings({"PMD.UseVarArgs", "PMD.AvoidFileStream"})
public class CsvParser implements CsvToBeanFilter {

  private final FileHelper fileHelper;
  private final CsvIngParserConfig csvIngParserConfig;

  @Override
  public boolean allowLine(String[] line) {
    Pattern lineStartsWithDatePattern = Pattern.compile("\\s*(19|20)\\d\\d(-)(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])\\s*");

    Matcher matcher = lineStartsWithDatePattern.matcher(line[0]);
    return matcher.matches();
  }

  public List<ParsedFromIngCsv> parseBeans(File file) throws TransactionsParsingException {
    // FIXME: 29/04/2020 lukasz  Still not getting polish letters
    List<ParsedFromIngCsv> parsedValuesFromCsv;
    try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(fileHelper.getFileEncoding(file))))) {

      CsvToBean<ParsedFromIngCsv> csvToBean = new CsvToBeanBuilder<ParsedFromIngCsv>(reader)
          .withFilter(this)
          .withType(ParsedFromIngCsv.class)
          .withSeparator(csvIngParserConfig.columnSeparator)
          .withIgnoreQuotations(true)
          .withIgnoreLeadingWhiteSpace(true)
          .build();

      parsedValuesFromCsv = csvToBean.parse();
    } catch (IOException ex) {
      log.error("An error occurred during parsing csv file {} {}", file.getName(), ex.getMessage());
      throw new TransactionsParsingException(ex);
    }

    return parsedValuesFromCsv;
  }
}
