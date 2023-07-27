package helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeZones {
    public static String localToUTC(ZonedDateTime date) {
        /** https://stackoverflow.com/questions/37390080/convert-local-time-to-utc-and-vice-versa */
        ZoneId utcZoneId = ZoneId.of("UTC");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime utc = date.withZoneSameInstant(utcZoneId);
        return utc.format(formatter);
    }
    public static ZonedDateTime localToEst(ZonedDateTime date) {
        ZoneId estZoneId = ZoneId.of("America/New_York");
        ZonedDateTime est = date.withZoneSameInstant(estZoneId);
        return est;
    }
    public static String utcToLocal(String date) {
        ZoneId utcZoneId = ZoneId.of("UTC");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime local = dateTime.atZone(utcZoneId).withZoneSameInstant(zoneId);
        return local.format(formatter);
    }
    public static ZonedDateTime convertToLocal(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        ZoneId zoneId = ZoneId.systemDefault();
        return dateTime.atZone(zoneId);
    }
    public static boolean checkBusinessHours(String start, String end) throws ParseException {
        ZonedDateTime startDateTime = convertToLocal(start);
        ZonedDateTime endDateTime = convertToLocal(end);
        ZonedDateTime startZonedDateTime = localToEst(startDateTime);
        ZonedDateTime endZonedDateTime = localToEst(endDateTime);
        boolean startDayOfWeek = startZonedDateTime.getDayOfWeek() != DayOfWeek.SATURDAY && startZonedDateTime.getDayOfWeek() != DayOfWeek.SUNDAY;
        boolean endDayOfWeek = endZonedDateTime.getDayOfWeek() != DayOfWeek.SATURDAY && endZonedDateTime.getDayOfWeek() != DayOfWeek.SUNDAY;
        if(!startDayOfWeek || !endDayOfWeek) {
            return false;
        }
        String startBusiness = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+" 08:00:00";
        String endBusiness = ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+" 22:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("America/New_York"));
        LocalTime startBusinessHour = ZonedDateTime.parse(startBusiness, formatter).toLocalTime();
        LocalTime endBusinessHour = ZonedDateTime.parse(endBusiness, formatter).toLocalTime();
        LocalTime startZonedLocalTime = startZonedDateTime.toLocalTime();
        LocalTime endZonedLocalTime = endZonedDateTime.toLocalTime();
        LocalDate startZonedLocalDate = startZonedDateTime.toLocalDate();
        LocalDate endZonedLocalDate = endZonedDateTime.toLocalDate();
        if(!startZonedLocalDate.equals(endZonedLocalDate)) {
            return false;
        }
        boolean checkStartingHours = startZonedLocalTime.isAfter(startBusinessHour) || startZonedLocalTime.equals(startBusinessHour);
        boolean checkEndingHours = endZonedLocalTime.isBefore(endBusinessHour) || endZonedLocalTime.equals(endBusinessHour);

        return checkStartingHours && checkEndingHours;
    }
    public static boolean checkOverlappingAppointments(String start, String end) throws SQLException {
        String sql = "SELECT * FROM APPOINTMENTS WHERE NOT ((Start >= ? AND Start >= ?) OR (End <= ? AND End <= ?))";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, localToUTC(convertToLocal(start)));
        ps.setString(2, localToUTC(convertToLocal(end)));
        ps.setString(3, localToUTC(convertToLocal(start)));
        ps.setString(4, localToUTC(convertToLocal(end)));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return false;
        }
        return true;
    }
    public static boolean checkOverlappingUpdateAppointments(int appointmentId, String start, String end) throws SQLException {
        String sql = "SELECT * FROM APPOINTMENTS WHERE Appointment_ID != ? AND NOT ((Start >= ? AND Start >= ?) OR (End <= ? AND End <= ?))";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, appointmentId);
        ps.setString(2, localToUTC(convertToLocal(start)));
        ps.setString(3, localToUTC(convertToLocal(end)));
        ps.setString(4, localToUTC(convertToLocal(start)));
        ps.setString(5, localToUTC(convertToLocal(end)));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return false;
        }
        return true;
    }
}
