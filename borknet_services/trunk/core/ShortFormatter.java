package borknet_services.core;
import java.util.logging.*;
class ShortFormatter extends Formatter
{
	public String format(LogRecord record)
	{
		return record.getMessage() + "\n";
	}
}