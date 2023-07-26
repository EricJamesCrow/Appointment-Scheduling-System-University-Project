package helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeZones {
    public static Date localToUTC() {
        /** https://stackoverflow.com/questions/37390080/convert-local-time-to-utc-and-vice-versa */
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utc = new Date(sdf.format(date));
        return utc;
    }
    public static Date localToEst(LocalDateTime date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        Date est = new Date(sdf.format(date));
        return est;
    }
    public static Date utcToLocalDate(Date date) {
        String timeZone = Calendar.getInstance().getTimeZone().getID();
        Date local = new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
        return local;
    }
    public static boolean checkBusinessHours(String start, String end) {
        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = LocalDateTime.parse(end);
        Date startDateEst = localToEst(startDateTime);
        Date endDateEst = localToEst(endDateTime);
    }
    public static boolean checkOverlappingAppointments(String start, String end) throws SQLException {
        String sql = "SELECT * FROM APPOINTMENTS WHERE NOT ((Start >= ? AND Start >= ?) OR (End <= ? AND End <= ?))";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, start);
        ps.setString(2, end);
        ps.setString(3, start);
        ps.setString(4, end);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return false;
        }
        return true;
    }
}
