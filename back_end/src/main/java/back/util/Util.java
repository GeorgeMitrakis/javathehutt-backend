package back.util;

import back.api.JTHInputException;

public class Util {
    public static void validateArgs(String ... args) throws JTHInputException {
        for(String s: args){
            if(s == null){
                throw new JTHInputException();
            }
        }
    }
}
