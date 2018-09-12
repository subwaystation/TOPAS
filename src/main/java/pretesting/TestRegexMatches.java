package pretesting;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegexMatches
{
    public static void main( String args[] ){

      // String to be scanned to find the pattern.
      String test = "gi|86553276|gb|ABC98234.1|";
      String test1 = ".";
      String test2 = "C";
      
      String pattern = "[a-zA-Z0-9\\.\\:\\^\\*\\$\\@\\!\\+ \\_\\?\\-\\|\\%]";
      String pattern1 = "\\+|\\-|\\.";
      
      // Create a Pattern object
      Pattern r = Pattern.compile(pattern);
      
      // Now create matcher object.
      Matcher m = r.matcher(test2);
      while(!m.hitEnd()) {
    	  if(m.find()) {
    		  System.out.println(m.group());
    		  System.out.println("hit found");
    	  } else {
    		  System.out.println("no hit found");
    	  }
      }
      
      Charset charset = Charset.defaultCharset();
      System.out.println("Default encoding: " + charset + " (Aliases: "
          + charset.aliases() + ")");
      
    }
}