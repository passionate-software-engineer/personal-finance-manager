package com.pfm.export.importValidate;

import com.pfm.export.ExportResult;

import java.util.List;

public class Filters {

    private static final String EMPTY = "";

    List<String> validate(ExportResult inputData, List<String> validationsResult) {
        if (inputData.getFilters() == null) {
            validationsResult.add("Filters are missing");
            return validationsResult;
        }
        for (ExportResult.ExportFilter filter : inputData.getFilters()) {
            if (checkDataMissing(filter.getName())) {
                validationsResult.add("Filter name is missing");
            } else {

                if (checkDataMissing(filter.getAccounts())) {
                    validationsResult.add(filter.getName() + " accounts are missing");
                }

                if (checkDataMissing(filter.getCategories())) {
                    validationsResult.add(filter.getName() + " categories are missing");
                }

                if (checkDataMissing(filter.getDateFrom())) {
                    validationsResult.add(filter.getName() + " dateFrom is missing");
                }

                if (checkDataMissing(filter.getDateTo())) {
                    validationsResult.add(filter.getName() + " dataTo is missing");
                }

                if (checkDataMissing(filter.getDescription())) {
                    validationsResult.add(filter.getName() + " description is missing");
                }

                if (checkDataMissing(filter.getPriceFrom())) {
                    validationsResult.add(filter.getName() + " priceFrom is missing");
                }

                if (checkDataMissing(filter.getPriceTo())) {
                    validationsResult.add(filter.getName() + " priceTo is missing");
                }

            }
        }
        return validationsResult;
    }

    private boolean checkDataMissing(Object data) {
        return data == null || EMPTY.equals(data);
    }
}
