package back.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class DateHandler {

    public static String FrontDateToSQLDate(String frontDate){
        //TODO: Do we need to format it?
        return frontDate;
    }

    public static String SQLDateToFrontDate(String sqlDate){
        //TODO: Do we need to format it?
        return sqlDate;
    }

    public static String getSQLDateNow(){
        //TODO: Check format
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.now();
        return dtf.format(localDate);
    }

    public static String getSQLDateTimeNow(){
        //TODO: Check format
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDate localDate = LocalDate.now();
        return dtf.format(localDate);
    }

}
