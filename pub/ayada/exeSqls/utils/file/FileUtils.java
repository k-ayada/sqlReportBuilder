package pub.ayada.exeSqls.utils.file;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtils {

	public static BufferedWriter getBufferedWriter(String path) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(path);
		OutputStreamWriter out = new OutputStreamWriter(fileOutputStream, "utf-8");
		return new BufferedWriter(out);
	}

}
