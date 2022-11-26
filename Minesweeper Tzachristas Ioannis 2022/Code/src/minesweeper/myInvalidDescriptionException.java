package minesweeper;

// class representing custom exception  
class InvalidDescriptionException  extends Exception  
{  
    public InvalidDescriptionException (String str)  
    {  
        // calling the constructor of parent Exception  
        super(str);  
    }  
}  
    
// class that uses custom exception myInvalidDescriptionException  
public class myInvalidDescriptionException 
{  
  
    // method to check the Description  
    static void validate (String line1 , String line2, String line3, String line4, String line5) throws InvalidDescriptionException{    
       if(((line1==null)||(!(line5==null)||(line5==""))||(line1=="")||(line2=="")||(line3=="")||(line4=="")||(line2==null)||(line3==null)||(line4==null))){  
  
        // throw an object of user defined exception  
        throw new InvalidDescriptionException("Description is not valid to play..."); 
   
    }  
       else {
        System.out.println("welcome to medialab game for real..."); 

        }   
     }    
  
    
}  