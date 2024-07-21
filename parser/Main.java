package parser;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        String[] relevantMethods = {"POST /api/signDoc", "GET /api/sendMessage", "GET /api/getMessage", "POST /api/addDoc", "GET /api/getDocByName"};
        Parser parser = new Parser(relevantMethods, 100); // Указываем общее количество потоков
        parser.parseLogFile("logs/production_log.csv");

        RequestStatistics stats = parser.getStatistics();
        stats.printStatistics();
        stats.printRPS();

        String peakHour = stats.getPeakHour();
        stats.printPeakHourStatistics(peakHour);

        stats.calculatePacing(0.7); // Минимальное время пейсинга 0.7 секунды
        stats.calculateRPM();
        stats.generateJMeterConfig();
        
        stats.analyzeDatabaseMetrics("logs/db_metrics.csv");
        stats.analyzeKafkaMetrics("logs/kafka_metrics.csv");
        stats.analyzeJVMMetrics("logs/jvm_metrics.csv");
    }
}
