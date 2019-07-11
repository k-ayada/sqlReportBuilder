package pub.ayada.exeSqls.utils.string;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StringUtils {

	public static String[] splitByLength(String string, int len) {
		if (string == null || len <= 0)
			return null;

		if (string.length() < len) {
			String[] arr = new String[1];
			arr[0] = string;
			return arr;
		}

		int chunks = string.length() / len
				+ ((string.length() % len > 0) ? 1 : 0);
		String[] arr = new String[chunks];
		for (int i = 0, j = 0, l = string.length(); i < l; i += len, j++)

			// int xl = ((len > l-i) ? l-i : len)
			arr[j] = string.substring(i, (i + ((len > l - i) ? l - i : len)));
		return arr;
	}

	public static ArrayList<String> splitByLength(String string, int len,
			boolean splitAtNewLine, char Justify) {
		if (string == null || len <= 0)
			return (ArrayList<String>) null;

		String fmt = "";
		 if (Justify == 'l')
			  fmt = "%-"+  len + "s";
		 else 
			 fmt = "%"+  len + "s";		 
		 
		if (string.length() <= len) {
			String[] splits = string.split("\n");
			ArrayList<String> arr = new ArrayList<String>(splits.length);
			for (int i = 0; i < splits.length; i++)
				arr.add(String.format(fmt, splits[i]));

			return arr;
		}

		int chunks = string.length() / len
				+ ((string.length() % len > 0) ? 1 : 0);
		ArrayList<String> arr = new ArrayList<String>(chunks);
		char[] carr = string.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < carr.length; i++) {
			if (sb.length() == len) {
				arr.add(sb.toString());
				sb = new StringBuilder();
				sb.append(carr[i]);
			} else if (carr[i] == '\n' && splitAtNewLine) {
				arr.add(String.format(fmt, sb.toString().trim()));
				sb = new StringBuilder();
			} else
				sb.append(carr[i]);
		}
		arr.add(String.format(fmt, sb.toString().trim()));
		return  arr;
	}

	public String[] splitByLength(String string, int len, int SplitCount) {
		if (string == null || len <= 0)
			return null;

		if (string.length() <= len)
			return new String[] { string };

		int chunks = string.length() / len
				+ ((string.length() % len > 0) ? 1 : 0);
		chunks = chunks > SplitCount ? SplitCount : chunks;
		String[] arr = new String[chunks];
		int i = 0, j = 0;
		for (int l = string.length(); i < l && j < SplitCount - 1; i += len, j++) {
			if ((i + len) > l)
				break;
			else
				arr[j] = string.substring(i, i + len);
		}
		arr[j] = string.substring(i);
		return arr;
	}

	public static <T> String arrJoin(T[] stringArr) {
		return joinArr(stringArr, "");
	}

	public static <T> String joinArr(T[] stringArr, String delm) {

		if (stringArr.length == 0)
			return "";

		if (stringArr.length == 1)
			return String.valueOf(stringArr[0]);

		StringBuilder sb = new StringBuilder();

		for (T str : stringArr)
			sb.append(String.valueOf(str) + delm);

		sb.setLength(sb.length() - (delm.length()));

		return sb.toString();
	}

	public static String repeat(String str, int count) {
		StringBuilder sb = new StringBuilder(str.length() * count);
		for (int i = 0; i < count; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	public static String repeat(char c, int count) {
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	void splitByLengthWithWordWrap(String text, int len) {
		StringTokenizer st = new StringTokenizer(text);
		int SpaceLeft = len;
		int SpaceWidth = 1;

		StringBuilder sb = new StringBuilder(len);
		ArrayList<String> wrapList = new ArrayList<String>();

		while (st.hasMoreTokens()) {
			String nxtWord = st.nextToken();

			while (nxtWord.length() > len) {
				sb = new StringBuilder(len);
				String[] arr = splitByLength(nxtWord, len, 2);
				int i = 0;
				for (; i < arr.length - 1; i++) {
					wrapList.add(arr[i]);
				}
				nxtWord = arr[i];
				SpaceLeft = len - nxtWord.length();
			}

			if ((nxtWord.length() + SpaceWidth) > SpaceLeft) {
				wrapList.add(sb.toString());
				sb = new StringBuilder(len);
				sb.append(nxtWord + " ");
				SpaceLeft = len - (nxtWord.length() + SpaceWidth);
			} else {
				sb.append(nxtWord + " ");
				SpaceLeft -= (nxtWord.length() + SpaceWidth);
			}
		}
		wrapList.add(sb.toString());
		// for (String s : wrapList) System.out.println(s);
	}

}
