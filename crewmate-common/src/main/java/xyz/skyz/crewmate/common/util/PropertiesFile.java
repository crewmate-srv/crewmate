package xyz.skyz.crewmate.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PropertiesFile {

    private String fileName;
    private Map<String, String> values = new HashMap<>();

    public PropertiesFile(String fileName) {
        this.fileName = fileName;
        if (!new File("./server.properties").isFile()) {
            createPropertiesFile();
        }
        load();
    }

    public String get(String key) {
        if (this.values.size() == 0) {
            this.load();
        }
        return this.values.getOrDefault(key, null);
    }

    public int getInteger(String key) {
        if (get(key) == null) {
            return 0;
        } else {
            return Integer.parseInt(get(key));
        }
    }

    private void load() {
        this.values.clear();
        try {
            File file = new File(this.fileName);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(this.fileName);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String stringLine;
                try {
                    while ((stringLine = bufferedReader.readLine()) != null)   {
                        if (stringLine.startsWith("#") || stringLine.startsWith(" ") || stringLine.length() < 3) {
                            continue;
                        }
                        if (stringLine.contains("=")) {
                            String property = stringLine.substring(0, stringLine.indexOf("="));
                            String value = stringLine.substring(stringLine.indexOf("=") + 1);
                            this.values.put(property, value);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                fileInputStream.close();
            }
        } catch (Exception e) {
            System.out.println("Couldn't load properties: " + this.fileName);
        }
    }

    public static boolean createPropertiesFile() {
        boolean success = true;
        try {
            Files.copy(PropertiesFile.class.getResourceAsStream("/server.properties"), Paths.get("./server.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }
}