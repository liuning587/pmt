package com.sx.mmt.internal.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorTool {

    public static String getErrorInfoFromException(Throwable e) {  
        try {  
            StringWriter sw = new StringWriter();  
            PrintWriter pw = new PrintWriter(sw);  
            e.printStackTrace(pw);  
            return "\r\n" + sw.toString() + "\r\n";  
        } catch (Exception e2) {  
            return "bad getErrorInfoFromException";  
        }  
    } 
}
