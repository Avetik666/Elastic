import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *@author avetik.sarikyan@picsart.com
 */
public class SetTime {

    private String from;
    private String to;

    public SetTime(String from, String to){
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long[] time() {
        long date[] = new long[2];
        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS ");
        df.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        Date dateto = null;
        Date fromtime = null;
        try {
            fromtime = df.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calobj = Calendar.getInstance();

        if (to == null) {
            try {
                String current = df.format(calobj.getTime());
                dateto = df.parse(current);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                dateto = df.parse(to);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        long epochto = dateto.getTime();
        long epochfrom = fromtime.getTime();
        date[0] = epochto;
        date[1] = epochfrom;
        return date;
    }



}
