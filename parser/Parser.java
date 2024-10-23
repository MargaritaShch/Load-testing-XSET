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
    private int totalThreads; //хранение общего количества потоков

    //конструктор принимает массив методов и общее количество потоков
    public Parser(String[] relevantMethods, int totalThreads) {
        this.stats = new RequestStatistics(relevantMethods, totalThreads); //инициализация статистики
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");//формат даты для разбора временных меток
        this.totalThreads = totalThreads; //инициализация общего количества потоков
    }

    //метод парсинг файла логов
    public void parseLogFile(String filePath) {
        //чтение файла построчно с помощью BufferedReader
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            //чтение каждой строки в файле
            while ((line = br.readLine()) != null) {
                //разделяем строку по символу "~"
                String[] parts = line.split("~");
                if (parts.length > 1) {
                    String requestPart = parts[1].trim(); //извлекаем часть запроса
                    String[] requestParts = requestPart.split(" ");//разделяем запрос по пробелам
                    if (requestParts.length > 1) {
                        String method = requestParts[0].replaceAll("\"", "").trim();//извлекаем HTTP метод
                        String path = requestParts[1].replaceAll("\"", "").trim();//извлекаем путь запроса
                        path = path.split("\\?")[0];//убираем параметры запроса, если они есть

                        //нормализирование пути для конкретного эндпоинта /api/sendMessage
                        if (path.startsWith("/api/sendMessage/")) {
                            path = "/api/sendMessage";
                        }

                        String methodPath = method + " " + path;//формирование строку метода и пути
                        //проверка на релевантность запроса
                        if (stats.isRelevantRequest(methodPath)) {
                            String timestamp = parts[0].trim();//извлечение временной метку
                            String hour = extractHour(timestamp);//извлечение часа из временной метки
                            if (hour != null) {
                                stats.addRequest(methodPath, hour);//добавление запроса в статистику
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();//обрабатка возможных ошибок при чтении файла
        }
    }

    //метод для извлечения часа из временной метки
    private String extractHour(String timestamp) {
        try {
            Date date = dateFormat.parse(timestamp); //парсим временную метку в объект Date
            SimpleDateFormat hourFormat = new SimpleDateFormat("yyyy-MM-dd HH");//формат для извлечения часа
            return hourFormat.format(date);/возврат даты в формате с точностью до часа
        } catch (ParseException e) {
            System.err.println("Ошибка парсинга метки времени: " + timestamp);
            return null;//возврат null в случае ошибки
        }
    }

    //метод для получения статистики запросов
    public RequestStatistics getStatistics() {
        return stats;//возврат объекта статистики
    }
}
