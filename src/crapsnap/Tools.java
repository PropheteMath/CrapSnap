package crapsnap;


import java.util.Vector;

public class Tools {
	public static String[] split(String str, char c) {
        int index = str.indexOf(c);
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c)
                count++;
        }
        
        String[] aWards = new String[++count];
        int counter = 0;
        while (index >= 0) {
            aWards[counter] = str.substring(0, index);
            str = str.substring(index+1,str.length()).trim();
            counter++;
            index = str.indexOf(c);
        }           
        aWards[counter] = str;          
        return aWards;
    }
	
	public String[] split(String original, String separator) {

	    Vector nodes = new Vector();               
	    int index = original.indexOf(separator);      
	    while (index >= 0) {                   
	        nodes.addElement(original.substring(0, index));           
	        original = original.substring(index + separator.length());          
	        index = original.indexOf(separator);       
	    }       
	    nodes.addElement(original);              
	    String[] result = new String[nodes.size()];       
	    if (nodes.size() > 0) {           
	        for (int loop = 0; loop < nodes.size(); loop++) {               
	            result[loop] = (String) nodes.elementAt(loop);               
	            System.out.println("Value inside result is ........ "+ result[loop]);           
	        }       
	    }      
	    return result;   
	}
}
