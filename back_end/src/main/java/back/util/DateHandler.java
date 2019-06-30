package back.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DateHandler {

    public static String FrontDateToSQLDate(String frontDate){
        return frontDate;
    }

    public static String SQLDateToFrontDate(String sqlDate){
        return sqlDate;
    }

    public static String getSQLDateNow(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        return dtf.format(localDate);
    }

    public static String getSQLDateTimeNow(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        return dtf.format(localDateTime);
    }

}
