package gpl;
 
public class StringUtils {
    
	public static String toAsciiString(byte[] buffer) {
	    return toAsciiString(buffer, 0, buffer.length);
	  }
	  
	  public static String toAsciiString(byte[] buffer, int startPos, int length) {
	    char[] charArray = new char[length];
	    int readpoint = startPos;
	    for (int i = 0; i < length; i++) {
	      charArray[i] = (char)buffer[readpoint];
	      readpoint++;
	    } 
	    return new String(charArray);
	  }

}