package parser;

public class Main {
    public static void main(String[] args) {
        String[] relevantMethods = {"POST /api/signDoc", "GET /api/sendMessage", "GET /api/getMessage", "POST /api/addDoc", "GET /api/getDocByName"};
        Parser parser = new Parser(relevantMethods);
        parser.parseLogFile("logs/production_log.csv");
    
        RequestStatistics stats = parser.getStatistics();
        stats.printStatistics();
        stats.printRPS();
        
        String peakHour = stats.getPeakHour();
        stats.printPeakHourStatistics(peakHour);
    
        stats.calculatePacing();
        stats.calculateRPM();
        stats.generateJMeterConfig();
    }    
}
