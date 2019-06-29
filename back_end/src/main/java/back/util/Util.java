package back.util;

import back.exceptions.JTHInputException;

public class Util {

    public static void validateArgs(String ... args) throws JTHInputException {
        for(String s: args){
            if(s == null){
                throw new JTHInputException();
            }
        }
    }

}
