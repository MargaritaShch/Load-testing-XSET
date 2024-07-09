package parser;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Parser {
    private RequestStatistics stats;
    private SimpleDateFormat dateFormat;

    public Parser(String[] relevantMethods) {
        this.stats = new RequestStatistics(relevantMethods);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    public void parseLogFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("~");
                if (parts.length > 1) {
                    String requestPart = parts[1].trim();
                    String[] requestParts = requestPart.split(" ");
                    if (requestParts.length > 1) {
                        String method = requestParts[0].replaceAll("\"", "").trim();
                        String path = requestParts[1].replaceAll("\"", "").trim();
                        path = path.split("\\?")[0];

                        if (path.startsWith("/api/sendMessage/")) {
                            path = "/api/sendMessage";
                        }

                        String methodPath = method + " " + path;
                        if (stats.isRelevantRequest(methodPath)) {
                            String timestamp = parts[0].trim();
                            String hour = extractHour(timestamp);
                            if (hour != null) {
                                stats.addRequest(methodPath, hour);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractHour(String timestamp) {
        try {
            Date date = dateFormat.parse(timestamp);
            SimpleDateFormat hourFormat = new SimpleDateFormat("yyyy-MM-dd HH");
            return hourFormat.format(date);
        } catch (ParseException e) {
            System.err.println("Ошибка парсинга метки времени: " + timestamp);
            return null;
        }
    }

    public RequestStatistics getStatistics() {
        return stats;
    }
}
