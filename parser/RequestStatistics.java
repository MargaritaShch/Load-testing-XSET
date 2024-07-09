package parser;

import java.util.HashMap;
import java.util.Map;

public class RequestStatistics {
    private int totalCalls;
    private Map<String, Integer> methodCounts;
    private String[] relevantMethods;
    private Map<String, Integer> hourlyCounts;
    private Map<String, Map<String, Integer>> hourlyMethodCounts;
    private Map<String, Double> rpsValues;
    private Map<String, Double> pacingIntervals;
    private Map<String, Double> rpmValues;

    // Конструктор инициализации статистики запросов
    public RequestStatistics(String[] relevantMethods) {
        this.totalCalls = 0;
        this.methodCounts = new HashMap<>();
        this.relevantMethods = relevantMethods;
        this.hourlyCounts = new HashMap<>();
        this.hourlyMethodCounts = new HashMap<>();
        this.rpsValues = new HashMap<>();
        this.pacingIntervals = new HashMap<>();
        this.rpmValues = new HashMap<>();
    }

    public void addRequest(String methodPath, String hour) {
        methodCounts.put(methodPath, methodCounts.getOrDefault(methodPath, 0) + 1);
        hourlyCounts.put(hour, hourlyCounts.getOrDefault(hour, 0) + 1);

        if (!hourlyMethodCounts.containsKey(hour)) {
            hourlyMethodCounts.put(hour, new HashMap<>());
        }
        Map<String, Integer> methodCountsForHour = hourlyMethodCounts.get(hour);
        methodCountsForHour.put(methodPath, methodCountsForHour.getOrDefault(methodPath, 0) + 1);

        totalCalls++;
    }

    public int getTotalCalls() {
        return totalCalls;
    }

    public Map<String, Integer> getMethodCounts() {
        return methodCounts;
    }

    public String[] getRelevantMethods() {
        return relevantMethods;
    }

    public boolean isRelevantRequest(String methodPath) {
        for (String method : relevantMethods) {
            if (methodPath.equals(method)) {
                return true;
            }
        }
        return false;
    }

    public void printStatistics() {
        System.out.println("Количество вызовов методов:");
        for (String method : relevantMethods) {
            int count = methodCounts.getOrDefault(method, 0);
            double percentage = (count / (double) totalCalls) * 100;
            System.out.printf("%s: %d (%.2f%%)\n", method, count, percentage);
        }
        System.out.println("Всего вызовов: " + totalCalls);
    }

    public void printRPS() {
        final int SECONDS_IN_HOUR = 3600;
        double totalRPS = totalCalls / (double) SECONDS_IN_HOUR;

        System.out.println("\nРасчетная интенсивность (RPS):");
        for (String method : relevantMethods) {
            int count = methodCounts.getOrDefault(method, 0);
            double rps = totalRPS * (count / (double) totalCalls);
            rpsValues.put(method, rps); // Store RPS values for pacing calculation
            System.out.printf("%s: %.2f RPS\n", method, rps);
        }
    }

    public String getPeakHour() {
        String peakHour = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : hourlyCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                peakHour = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return peakHour;
    }

    public void printPeakHourStatistics(String peakHour) {
        Map<String, Integer> methodCountsForPeakHour = hourlyMethodCounts.getOrDefault(peakHour, new HashMap<>());
        int totalCallsForPeakHour = hourlyCounts.getOrDefault(peakHour, 0);

        System.out.println("Количество вызовов методов за " + peakHour + ":");
        for (String method : relevantMethods) {
            int count = methodCountsForPeakHour.getOrDefault(method, 0);
            double percentage = (count / (double) totalCallsForPeakHour) * 100;
            System.out.printf("%s: %d (%.2f%%)\n", method, count, percentage);
        }
        System.out.println("Всего вызовов за " + peakHour + ": " + totalCallsForPeakHour);

        final int SECONDS_IN_HOUR = 3600;
        double totalRPS = totalCallsForPeakHour / (double) SECONDS_IN_HOUR;

        System.out.println("\nРасчетная интенсивность (RPS) за " + peakHour + ":");
        for (String method : relevantMethods) {
            int count = methodCountsForPeakHour.getOrDefault(method, 0);
            double rps = totalRPS * (count / (double) totalCallsForPeakHour);
            System.out.printf("%s: %.2f RPS\n", method, rps);
        }
    }

    public void calculatePacing() {
        for (String method : relevantMethods) {
            double rps = rpsValues.getOrDefault(method, 0.0);
            if (rps > 0) {
                pacingIntervals.put(method, 1 / rps);
            }
        }
    }

    public void calculateRPM() {
        for (String method : relevantMethods) {
            double rps = rpsValues.getOrDefault(method, 0.0);
            if (rps > 0) {
                rpmValues.put(method, rps * 60);
            }
        }
    }

    public void generateJMeterConfig() {
        System.out.println("\nJMeter Configuration (Intervals):");
        for (String method : relevantMethods) {
            double pacing = pacingIntervals.getOrDefault(method, 0.0);
            if (pacing > 0) {
                System.out.printf("%s: %.2f seconds interval\n", method, pacing);
            }
        }

        System.out.println("\nJMeter Configuration (RPM):");
        for (String method : relevantMethods) {
            double rpm = rpmValues.getOrDefault(method, 0.0);
            if (rpm > 0) {
                System.out.printf("%s: %.2f samplers per minute\n", method, rpm);
            }
        }
    }
}
