package pub.ayada.exeSqls.utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Timer {

	private Date StartTime, EndTime;

	public Timer() {
	}

	/**
	 * Start the timer
	 */
	public void start() {
		this.StartTime = Calendar.getInstance().getTime();
	}

	/**
	 * End the timer
	 */
	public void end() {
		this.EndTime = Calendar.getInstance().getTime();
	}

	/**
	 * Return the Timer's Start Time (number of milSecs since 1/1/1970 00:00:00
	 * GMT)
	 * @return (long) Start time
	 */
	public long getStartTime() {
		return this.StartTime.getTime();
	}

	/**
	 * Return the Timer's Start Time per the specified format
	 * @return (String) Start time per the specified format
	 * @throws IllegalArgumentException
	 */
	public String getStartTime(String format) {
		 return new SimpleDateFormat(format).format(this.StartTime);
	}

	/**
	 * Return the timer's End Time (number of milSecs since 1/1/1970 00:00:00
	 * GMT)
	 * @return (long) End time
	 */
	public long getEndTime() {
		return this.EndTime.getTime();
	}

	/**
	 * Return the Timer's End Time per the specified format
	 * @return (String) End time per the specified format
	 * @throws IllegalArgumentException
	 */
	public String getEndTime(String format) {
		return new SimpleDateFormat(format).format(this.EndTime);
	}

	/**
	 * Return the Timer's elapsed Time (End - start) in milSecs
	 * @return (long) elapsed time (end - start)
	 */
	public long getELapsedTime() {
		return this.EndTime.getTime() - this.StartTime.getTime();
	}

	/**
	 * Return the Timer's elapsed Time (End - start) in Hrs, Mins & Secs
	 * @return (String) Elapsed time [Hrs: hh] [Mins: mm] [Secs: ss]
	 */
	public String getELapsedTimeHHmmss() {
		long time = this.EndTime.getTime() - this.StartTime.getTime();
		return "[Hrs:" + String.format("%04.2f", (double)(time) / (60 * 60 * 1000))
				+ "] [Mins:" + String.format("%04.2f", (double)(time) / (60 * 1000))
				+ "] [Secs:" + String.format("%04.2f", (double)(time) / 1000) 
				+ "] [milSecs:" + String.format("%02d", (time - (time/1000)))
				+ "]";
	}
}
