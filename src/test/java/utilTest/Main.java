package utilTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {     
    public static void main(String[] args)
    {        
        try{
            String[] cmd ={"tasklist"};
            Process proc = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc
                    .getInputStream()));                        
            String string_Temp = in.readLine();
            while (string_Temp != null)
            {
            	String[] temp=string_Temp.split("\\s+");
            	String name="";
            	int pid=999999;
            	if(temp.length>2){
	            	try{
	            		name=temp[0];
	            		pid=Integer.parseInt(temp[1]);
	            	}catch(Exception e){}
	            	
	                System.out.println(name+pid);
	                if(string_Temp.indexOf(".tmp")!=-1 && pid<999999-1)
	                    Runtime.getRuntime().exec("Taskkill /PID "+pid+" /F /T");
            	}
                string_Temp = in.readLine();
            }
        }catch (Exception e){
        	e.printStackTrace();
        }
    }
}
