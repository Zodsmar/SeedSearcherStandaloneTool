package sassa.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static List<Long> getSeedsAsListFromFile(String filePath) {
        List<Long> seeds = new ArrayList<>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                seeds.add(Long.parseLong(line));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return seeds;
    }
}
