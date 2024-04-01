package com.app.webtier.utils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for reading and formatting data from a CSV file
 *
 * @author Moreshwar Nabar
 */
public class WebTierUtils {

    private static final Logger LOG = LoggerFactory.getLogger(WebTierUtils.class);

    /**
     * Reads the data from the CSV file and formats it into a HashMap
     *
     * @param path The path of the classification file
     * @return The classification data
     */
    public static Map<String, String> readData(String path) {
        LOG.info("Path: {}", path);
        ClassPathResource resource = new ClassPathResource(path);
        try {
            URL url = resource.getURL();
            try (Reader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                // create the parser for reading the CSV file
                CSVParser parser = new CSVParserBuilder()
                        .withSeparator(',')
                        .withIgnoreQuotations(true)
                        .build();
                // reader to fetch the data from the CSV file
                CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader)
                        .withSkipLines(0)
                        .withCSVParser(parser);
                try (CSVReader csvReader = csvReaderBuilder.build()) {
                    return parseCsvData(csvReader.readAll());
                }
            }
        } catch (IOException | CsvException e) {
            LOG.error("encountered error:, ", e);
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> parseCsvData(List<String[]> data) {
        Map<String, String> csvData = new HashMap<>();
        // remove the first entry as it is simply the column names
        data.remove(0);
        // format the read data into a map with mapping: image_name --> classification_result
        for (String[] row : data) {
            csvData.put(row[0], row[1]);
        }

        return csvData;
    }

}
