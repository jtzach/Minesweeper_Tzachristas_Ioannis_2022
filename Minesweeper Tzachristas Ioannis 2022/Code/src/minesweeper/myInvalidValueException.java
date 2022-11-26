package minesweeper;
// class representing custom exception  
class InvalidValueException  extends Exception  
{  
    public InvalidValueException (String str)  
    {  
        // calling the constructor of parent Exception  
        super(str);  
    }  
}  
    
// class that uses custom exception myInvalidValueException  
public class myInvalidValueException 
{  
  
    // method to check the Value  
    static void validate (int level , int mines, int mytime, int smines) throws InvalidValueException{    
       if(!(((level==1)&&(mines<12)&&(mines>8)&&(mytime<181)&&(mytime>119)&&(smines==0))||((level==2)&&(mines<46)&&(mines>34)&&(mytime<361)&&(mytime>239)&&(smines==1)))){  
  
        // throw an object of user defined exception  
        throw new InvalidValueException("Value is not valid to play...");    
    }  
       else {   
        System.out.println("welcome to medialab game..."); 

        }   
     }    
  
    
}  