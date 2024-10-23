package parser;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        //массив HTTP методов и путей для анализа
        String[] relevantMethods = {
            "POST /api/signDoc", 
            "GET /api/sendMessage", 
            "GET /api/getMessage", 
            "POST /api/addDoc", 
            "GET /api/getDocByName"};
        //создание объект Parser с методами и указанием общего количества потоков (100)
        Parser parser = new Parser(relevantMethods, 100); //указываем общее количество потоков
        //запуск парсинга файла логов
        parser.parseLogFile("logs/production_log.csv");
        //получение объекта статистики после парсинга
        RequestStatistics stats = parser.getStatistics();
        //вывод статистики по запросам
        stats.printStatistics();
        //вывод количества запросов в секунду (RPS)
        stats.printRPS();
        //получение часа с наибольшей нагрузкой (пиковый час)
        String peakHour = stats.getPeakHour();
        //вывод статистики по пиковому часу
        stats.printPeakHourStatistics(peakHour);
        //рассчет пейсинга запросов (интервалы между запросами) с минимальным значением 0.7 секунды
        stats.calculatePacing(0.7); // Минимальное время пейсинга 0.7 секунды
        //рассчет запросов в минуту (RPM)
        stats.calculateRPM();
        //генерация конфигурации для JMeter на основе статистики
        stats.generateJMeterConfig();
        //анализ метрик базы данных из указанного файла
        stats.analyzeDatabaseMetrics("logs/db_metrics.csv");
        //анализ метрик Kafka из указанного файла
        stats.analyzeKafkaMetrics("logs/kafka_metrics.csv");
        //анализ метрикк JVM из указанного файла
        stats.analyzeJVMMetrics("logs/jvm_metrics.csv");
    }
}
