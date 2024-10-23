package parser;

import java.util.HashMap;
import java.util.Map;

public class RequestStatistics {
    //общее количество запросов
    private int totalCalls;
    //для подсчета вызовов каждого метода
    private Map<String, Integer> methodCounts;
    //массив релевантных методов для анализа
    private String[] relevantMethods;
    //для подсчета запросов по часам
    private Map<String, Integer> hourlyCounts;
    //для подсчета вызовов каждого метода по часам
    private Map<String, Map<String, Integer>> hourlyMethodCounts;
    //для хранения значений запросов в секунду (RPS)
    private Map<String, Double> rpsValues;
    //для хранения интервалов пейсинга
    private Map<String, Double> pacingIntervals;
    //для хранения значений запросов в минуту (RPM)
    private Map<String, Double> rpmValues;
    private int totalThreads; //переменная для общего количества потоков

    //конструктор инициализации статистики запросов
    public RequestStatistics(String[] relevantMethods, int totalThreads) {
        this.totalCalls = 0;
        this.methodCounts = new HashMap<>();
        this.relevantMethods = relevantMethods;
        this.hourlyCounts = new HashMap<>();
        this.hourlyMethodCounts = new HashMap<>();
        this.rpsValues = new HashMap<>();
        this.pacingIntervals = new HashMap<>();
        this.rpmValues = new HashMap<>();
        this.totalThreads = totalThreads; // Инициализируем общее количество потоков
    }

    // Метод добавления запроса в статистику
    public void addRequest(String methodPath, String hour) {
        // Обновляем количество вызовов для метода
        methodCounts.put(methodPath, methodCounts.getOrDefault(methodPath, 0) + 1);
        // Обновляем количество вызовов для указанного часа
        hourlyCounts.put(hour, hourlyCounts.getOrDefault(hour, 0) + 1);

        // Если для указанного часа еще нет данных по методам, создаем новую карту
        if (!hourlyMethodCounts.containsKey(hour)) {
            hourlyMethodCounts.put(hour, new HashMap<>());
        }
        // Обновляем количество вызовов метода для указанного часа
        Map<String, Integer> methodCountsForHour = hourlyMethodCounts.get(hour);
        methodCountsForHour.put(methodPath, methodCountsForHour.getOrDefault(methodPath, 0) + 1);

        totalCalls++;
    }
    // Получение общего количества запросов
    public int getTotalCalls() {
        return totalCalls;
    }

    public Map<String, Integer> getMethodCounts() {
        return methodCounts;
    }
    // Получение статистики вызовов методов
    public String[] getRelevantMethods() {
        return relevantMethods;
    }
    // Проверка, является ли запрос релевантным
    public boolean isRelevantRequest(String methodPath) {
        // Проходим по каждому методу из списка релевантных методов
        for (String method : relevantMethods) {
            // Если метод и путь запроса совпадают с релевантным методом
            if (methodPath.equals(method)) {
                // Возвращаем true, если запрос релевантен
                return true;
            }
        }
        // Если ни один релевантный метод не совпал, возвращаем false
        return false;
    }
    // Печать общей статистики по методам
    public void printStatistics() {
        System.out.println("Количество вызовов методов:");
        // Проходим по каждому релевантному методу
        for (String method : relevantMethods) {
            // Получаем количество вызовов для данного метода (если нет, то 0)
            int count = methodCounts.getOrDefault(method, 0);
            // Вычисляем процент вызовов данного метода от общего числа запросов
            double percentage = (count / (double) totalCalls) * 100;
            // Выводим метод, количество вызовов и процент вызовов от общего числа
            System.out.printf("%s: %d (%.2f%%)\n", method, count, percentage);
        }
        System.out.println("Всего вызовов: " + totalCalls);
    }
     // Печать количества запросов в секунду (RPS) по каждому методу
    public void printRPS() {
        // Константа: количество секунд в часе
        final int SECONDS_IN_HOUR = 3600;
        // Рассчитываем общий RPS как общее количество запросов, деленное на 3600 секунд
        double totalRPS = totalCalls / (double) SECONDS_IN_HOUR;

        System.out.println("\nРасчетная интенсивность (RPS):");
        // Проходим по каждому релевантному методу
        for (String method : relevantMethods) {
            // Получаем количество вызовов для метода (если нет, то 0)
            int count = methodCounts.getOrDefault(method, 0);
            // Рассчитываем RPS для данного метода как долю от общего RPS
            double rps = totalRPS * (count / (double) totalCalls);
            // Сохраняем рассчитанное значение RPS для дальнейшего использования (например, для пейсинга)
            rpsValues.put(method, rps); 
            // Выводим метод и его RPS
            System.out.printf("%s: %.2f RPS\n", method, rps);
        }
    }
    // Получение часа с наибольшим количеством запросов (пикового часа)
    public String getPeakHour() {
        // Переменная для хранения пикового часа
        String peakHour = null;
        // Переменная для хранения максимального числа запросов за час
        int maxCount = 0;
        // Проходим по каждому часу из карты hourlyCounts
        for (Map.Entry<String, Integer> entry : hourlyCounts.entrySet()) {
            // Если для данного часа больше запросов, чем текущий максимум
            if (entry.getValue() > maxCount) {
                // Обновляем пиковый час и максимальное количество запросов
                peakHour = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        // Возвращаем строку с пиковым часом
        return peakHour;
    }
    // Печать статистики по пиковому часу
    public void printPeakHourStatistics(String peakHour) {
        // Получаем количество вызовов каждого метода за пиковый час (или пустую карту, если данных нет)
        Map<String, Integer> methodCountsForPeakHour = hourlyMethodCounts.getOrDefault(peakHour, new HashMap<>());
        // Получаем общее количество вызовов за пиковый час (или 0, если данных нет)
        int totalCallsForPeakHour = hourlyCounts.getOrDefault(peakHour, 0);

        System.out.println("Количество вызовов методов за " + peakHour + ":");
        // Проходим по каждому релевантному методу
        for (String method : relevantMethods) {
            // Получаем количество вызовов метода за пиковый час (или 0, если данных нет)
            int count = methodCountsForPeakHour.getOrDefault(method, 0);
            // Вычисляем процент вызовов данного метода от общего числа вызовов за пиковый час
            double percentage = (count / (double) totalCallsForPeakHour) * 100;
            // Выводим метод, количество вызовов и процент от общего числа вызовов за пиковый час
            System.out.printf("%s: %d (%.2f%%)\n", method, count, percentage);
        }
        // Выводим общее количество вызовов за пиковый час
        System.out.println("Всего вызовов за " + peakHour + ": " + totalCallsForPeakHour);
        // Константа: количество секунд в часе
        final int SECONDS_IN_HOUR = 3600;
        // Рассчитываем общий RPS для пикового часа
        double totalRPS = totalCallsForPeakHour / (double) SECONDS_IN_HOUR;

        System.out.println("\nРасчетная интенсивность (RPS) за " + peakHour + ":");
         // Проходим по каждому релевантному методу
        for (String method : relevantMethods) {
            // Получаем количество вызовов метода за пиковый час (или 0, если данных нет)
            int count = methodCountsForPeakHour.getOrDefault(method, 0);
            // Рассчитываем RPS для данного метода за пиковый час
            double rps = totalRPS * (count / (double) totalCallsForPeakHour);
            // Выводим метод и его RPS за пиковый час
            System.out.printf("%s: %.2f RPS\n", method, rps);
        }
    }
    // Рассчет интервалов пейсинга (времени между запросами)
    public void calculatePacing() {
        // Проходим по каждому релевантному методу
        for (String method : relevantMethods) {
            // Получаем RPS для метода (или 0, если данных нет)
            double rps = rpsValues.getOrDefault(method, 0.0);
             // Если RPS больше нуля, рассчитываем интервал пейсинга как обратную величину от RPS
            if (rps > 0) {
                // Интервал пейсинга в секундах
                pacingIntervals.put(method, 1 / rps);
            }
        }
    }
    // Рассчет запросов в минуту (RPM)
    public void calculateRPM() {
        for (String method : relevantMethods) {
            // Получаем RPS для метода (или 0, если данных нет)
            double rps = rpsValues.getOrDefault(method, 0.0);
             // Если RPS больше нуля, рассчитываем RPM (Requests Per Minute) как RPS, умноженное на 60
            if (rps > 0) {
                // Сохраняем RPM для метода
                rpmValues.put(method, rps * 60);
            }
        }
    }
    // Генерация конфигурации для JMeter
    public void generateJMeterConfig() {
        System.out.println("\nJMeter Configuration (Intervals):");
          // Проходим по каждому релевантному методу
        for (String method : relevantMethods) {
            // Получаем интервал пейсинга для метода (или 0, если данных нет)
            double pacing = pacingIntervals.getOrDefault(method, 0.0);
            // Если интервал пейсинга больше нуля, выводим его
            if (pacing > 0) {
                System.out.printf("%s: %.2f seconds interval\n", method, pacing);
            }
        }

        System.out.println("\nJMeter Configuration (RPM):");
        // Проходим по каждому релевантному методу
        for (String method : relevantMethods) {
            // Получаем RPM для метода (или 0, если данных нет
            double rpm = rpmValues.getOrDefault(method, 0.0);
            // Если RPM больше нуля, выводим его
            if (rpm > 0) {
                System.out.printf("%s: %.2f samplers per minute\n", method, rpm);
            }
        }

        System.out.println("\nJMeter Thread Group Configuration:");
        // Проходим по каждому релевантному методу
        for (String method : relevantMethods) {
            // Получаем RPS для метода (или 0, если данных нет)
            double rps = rpsValues.getOrDefault(method, 0.0);
            // Рассчитываем количество потоков для данного метода, основываясь на общем числе потоков
            int threads = (int) Math.ceil((rps * totalThreads) / 100);
            // Выводим метод и рассчитанное количество потоков
            System.out.printf("%s: %d threads\n", method, threads);
        }
    }
}
