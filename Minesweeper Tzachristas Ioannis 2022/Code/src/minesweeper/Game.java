package minesweeper;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.border.TitledBorder;
import minesweeper.Score.Time;
import java.util.Scanner;

import java.io.*;

import java.io.File; 

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Button;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.lang.String;
 





// This is the main controller class
public class Game implements MouseListener, ActionListener, WindowListener
{
    public static String dbPath;
    // "playing" indicates whether a game is running (true) or not (false).
    private boolean playing; 

    public Board board;

    public UI gui;
    
    public Score score;



    private int numofclicks = 0;
    public int numoflclicks = 0;
    public int currentID = -1;
    public int myidinteger =-1;
    public int LEVEL = 0;
    public int MINES = 0 ;
    public int SMINES = 0 ;
    public int TIME = 0;

    private boolean gamewonbool;
    private String messageround1 = "Not yet played...";
    private String messageround2 = "Not yet played...";
    private String messageround3 = "Not yet played...";
    private String messageround4 = "Not yet played...";
    private String messageround5 = "Not yet played...";


    public int myavailabletime;
    //----------------------Local Timer--------------------------------------//      
    private Thread localtimer;
    private int localtimePassed;
    private boolean stoplocalTimer;
    public boolean localtimeisup = false;
    private int countdowner;
    public boolean clickedsmine = false;
    public int xsmine;
    public int ysmine;
    //------------------------------------------------------------------//        

    /**
     * 
     */
    public Game()
    {

        // set db path
        String p = "";

        try 
        {
            p = new File(Game.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() + "\\db.accdb";
        }
        catch (URISyntaxException ex) 
        {
            System.out.println("Error loading database file.");
        }

        dbPath =   "jdbc:ucanaccess://" + p;

        
        score = new Score();
        score.populate();
        
        UI.setLook("Nimbus");
                        
        createBoard();
        
        this.gui = new UI(board.getRows(), board.getCols(), board.getNumberOfMines(), board.getmytime(), board.getNumberOfSuperMines());        
        this.gui.setButtonListeners(this);
        
        this.playing = false;
        
        
        gui.setVisible(true);
        
        gui.setIcons();        
        gui.hideAll();
        
        resumeGame();
        
    }
    //-----------------Load Save Game (if any)--------------------------//
    
    public void resumeGame()
    {
        if(board.checkSave())
        {
            ImageIcon question = new ImageIcon(getClass().getResource("/resources/question.png"));      

            int option = JOptionPane.showOptionDialog(null, "Do you want to continue your saved game?", 
                            "Saved Game Found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, question,null,null);

            switch(option) 
            {
                case JOptionPane.YES_OPTION:      
      
                    //load board's state
                    Pair p = board.loadSaveGame();
                    
                    //set button's images
                    setButtonImages();
                    
                    //load timer's value                                        
                    gui.setTimePassed((int)p.getKey());
                    setlocalTimePassed((int)p.getKey());

                    //load mines value
                    gui.setMines((int)p.getValue());
                    
                    gui.startTimer();
                    startlocalTimer();

                    
                    playing = true;
                    break;

                    case JOptionPane.NO_OPTION:
                    board.deleteSavedGame();
                    break;
                    
                    case JOptionPane.CLOSED_OPTION:
                    board.deleteSavedGame();
                    break; 
            }
        }
    }


    //-------------------------------------------------//
    public void setButtonImages()
    {
        Cell cells[][] = board.getCells();
        JButton buttons[][] = gui.getButtons();
        
        for( int y=0 ; y<board.getRows() ; y++ ) 
        {
            for( int x=0 ; x<board.getCols() ; x++ ) 
            {
                buttons[x][y].setIcon(null);
                
                if (cells[x][y].getContent().equals(""))
                {
                    buttons[x][y].setIcon(gui.getIconTile());
                }
                else if (cells[x][y].getContent().equals("F"))
                {
                    buttons[x][y].setIcon(gui.getIconFlag());
                    buttons[x][y].setBackground(Color.blue);	                    
                }
                else if (cells[x][y].getContent().equals("0"))
                {
                    buttons[x][y].setBackground(Color.lightGray);
                }
                else
                {
                    buttons[x][y].setBackground(Color.lightGray);                    
                    buttons[x][y].setText(cells[x][y].getContent());
                    gui.setTextColor(buttons[x][y]);                                        
                }
            }
        }
    }
    
    
    //------------------------------------------------------------//
        
    public void createBoard()
    {       

        //boolean valid=true;
        String number_of_file = Integer.toString(2);
        String temp =  "medialab/SCENARIO-"+number_of_file;
        String name_of_file = temp+".txt";
        

            try(BufferedReader reader = new BufferedReader(new FileReader( "medialab/SCENARIO-ID"+".txt"))){
                        
                boolean valid=true;
                String line1 = reader.readLine();
                        String line2 = reader.readLine();
                        String line3 = reader.readLine();
                        String line4 = reader.readLine();
                        String line5 = reader.readLine();
                        reader.close();

                        int level = Integer.parseInt(line1);
                        int r;
                        int c;
                        
                        if (level==1){
                            r = 9;
                            c = 9;
                                        }
                        
                         else if (level==2){
                            r = 16;
                            c = 16;
                         }
                        
                         else{r=1; c=1;}
                                                        
                        int mines = Integer.parseInt(line2);
                        
                        int mytime = Integer.parseInt(line3);
                        int smines = Integer.parseInt(line4);
                        
                         
                        try  
                        {  
                            // calling the method   
                            myInvalidDescriptionException.validate(line1,line2, line3, line4, line5);
                            valid = true;
                        }  
                        catch (InvalidDescriptionException ex)  
                        {  
                           System.out.println("Caught the exception...Try declaring the SCENARIO-ID File correclty...Sorry...");  
                                                valid=false;
                            warningpopup("sos");
                            // printing the messValue from InvalidValueException object  
                            System.out.println("Exception occured: " + ex);  
                        }  
    
                        try  
                        {  
                            // calling the method   
                            myInvalidValueException.validate(level, mines,mytime,smines);
                            //this.board = new Board(mines, r, c, mytime, smines);
                        }  
                        catch (InvalidValueException ex)  
                        {  
                            System.out.println("Caught the exception...Try declaring the Values in a Valid way...Sorry...");  
                            valid=false;
                            warningpopup("SOS");
                            // printing the messValue from InvalidValueException object  
                            System.out.println("Exception occured: " + ex);  
                        }  
                      
        
  
      //  System.out.println("Anyway... rest of the code...");    

//                        try {
//                            if ((line5!=null)||(line1==null)||(line2==null)||(line3==null)||(line4==null))
//                             throw new InvalidDescriptionException("Invalid Description my man...");
//                            else
//                             System.out.println("Valid Description");
//                           }
//                           catch (InvalidDescriptionException a) {
//                            System.out.println(a);
//
//                              }


//                        if ((line5!=null)||(line1==null)||(line2==null)||(line3==null)||(line4==null)){
//                            throw new Exception("InvalidDescriprionException");
//                       }

//                        else if (!(((level==1)&&(mines<12)&&(mines>8)&&(mytime<181)&&(mytime>119)&&(smines==0))||((level==2)&&(mines<46)&&(mines>34)&&(mytime<361)&&(mytime>239)&&(smines==1)))){
//                            throw new Exception("InvalidValidException");
//                        }


              //          while (line != null) {
              
                if (valid){
                System.out.println("");
                System.out.println("");
                System.out.println("The SCENARIO-ID file is VALID and is the following:");
                System.out.println(line1);
                System.out.println(line2);
                System.out.println(line3);
                System.out.println(line4);
                System.out.println(line5);
                System.out.println("");
                  // read next line
              //      line = reader.readLine();
              
        

System.out.println("Number of mines:");
System.out.println(Integer.toString(mines));
System.out.println("");
System.out.println("Sizes of board:");
System.out.println(Integer.toString(r));
System.out.println(Integer.toString(c));
System.out.println("");
System.out.println("Available playing time (in seconds):");
System.out.println(Integer.toString(mytime));
System.out.println("");
System.out.println("Number of SUPER-mines:");
System.out.println(Integer.toString(smines));
System.out.println("");
System.out.println("");

           this.board = new Board(mines, r, c, mytime, smines);}

        }
                
        catch (IOException e) {
          // e.printStackTrace();
          warningpopup("SOS");
       }
              
    }
    
//???????????????????????????????????????????????????????????????????????//
public void warningpopup(String mymessage){

    JDialog dialog = new JDialog(gui, Dialog.ModalityType.DOCUMENT_MODAL);
        
    //------MESSAGE-----------//
    JLabel message = new JLabel("HEY! CHECK THE FILE!", SwingConstants.CENTER);
            
    Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);        
    
    
    //--------BUTTONS----------//
    JPanel buttons = new JPanel();
    buttons.setLayout(new GridLayout(1,2,10,0));
    
    JButton exit = new JButton("Exit");
    JButton playAgain = new JButton("Create");


    exit.addActionListener((ActionEvent e) -> {

        dialog.dispose();
        windowClosing(null);
    });        
    playAgain.addActionListener((ActionEvent e) -> {
       
        dialog.dispose();            
        createbuttonpressed();
        });        
    
    
    buttons.add(exit);
    buttons.add(playAgain);
    
    //--------DIALOG-------------//
    
    JPanel c = new JPanel();
    c.setLayout(new BorderLayout(20,20));
    c.add(message, BorderLayout.NORTH);
    c.add(buttons, BorderLayout.SOUTH);
    
    c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
    dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dialog.dispose();
                newGameIDdefaulting();;
        }
        }
    );

    dialog.setTitle("WARNING");
    dialog.add(c);
    dialog.pack();
    dialog.setLocationRelativeTo(gui);
    dialog.setVisible(true);   
    
}
//???????????????????????????????????????????????????????????????????????//
class FileWrite {
	public void writeToFile(String text, int myIID) {
		try {
            String numberfile = Integer.toString(myIID);

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					"medialab/SCENARIO-" + numberfile +".txt"), true));
        
            bw.flush();
            bw.write(text);
			bw.newLine();
			bw.close();
		} catch (Exception e) {
		}
}
}
public void createScenarioID(int myID, int mylevel, int mymines, int mysmines, int mytime)
{
        String numberoffile = Integer.toString(myID);
        String temporary =  "medialab/SCENARIO-"+numberoffile;
        String nameoffile = temporary+".txt";

    FileWrite filew = new FileWrite();
        File myObject = new File(nameoffile);
        myObject.delete();

        
        filew.writeToFile(Integer.toString(mylevel), myID);
        filew.writeToFile(Integer.toString(mymines),myID);
        filew.writeToFile(Integer.toString(mytime),myID);
        filew.writeToFile(Integer.toString(mysmines),myID);	                    


}

//???????????????????????????????????????????????????????///
public void createBoardID(int myID)
  {       

        //boolean valid=true;
        String number_of_file = Integer.toString(myID);
        String temp =  "medialab/SCENARIO-"+number_of_file;
        String name_of_file = temp+".txt";
        

            try(BufferedReader reader = new BufferedReader(new FileReader( name_of_file))){
                        
                boolean valid=true;
                String line1 = reader.readLine();
                        String line2 = reader.readLine();
                        String line3 = reader.readLine();
                        String line4 = reader.readLine();
                        String line5 = reader.readLine();
                        reader.close();

                        int level = Integer.parseInt(line1);
                        int r;
                        int c;
                        
                        if (level==1){
                            r = 9;
                            c = 9;
                                        }
                        
                         else if (level==2){
                            r = 16;
                            c = 16;
                         }
                        
                         else{r=1; c=1;}
                                                        
                        int mines = Integer.parseInt(line2);
                        
                        int mytime = Integer.parseInt(line3);
                        int smines = Integer.parseInt(line4);
                        
                         
                        try  
                        {  
                            // calling the method   
                            myInvalidDescriptionException.validate(line1,line2, line3, line4, line5);
                            valid = true;
                        }  
                        catch (InvalidDescriptionException ex)  
                        {  
                           System.out.println("Caught the exception...Try declaring the SCENARIO-ID File correclty...Sorry...");  
                                                valid=false;

                            // printing the messValue from InvalidValueException object  
                            System.out.println("Exception occured: " + ex);  
                        }  
    
                        try  
                        {  
                            // calling the method   
                            myInvalidValueException.validate(level, mines,mytime,smines);
                            //this.board = new Board(mines, r, c, mytime, smines);
                        }  
                        catch (InvalidValueException ex)  
                        {  
                            System.out.println("Caught the exception...Try declaring the Values in a Valid way...Sorry...");  
                            valid=false;
                            // printing the messValue from InvalidValueException object  
                            System.out.println("Exception occured: " + ex);  
                        }  
                      
     
                if (valid){
                System.out.println("");
                System.out.println("");
                System.out.println("The SCENARIO-ID file is VALID and is the following:");
                System.out.println(line1);
                System.out.println(line2);
                System.out.println(line3);
                System.out.println(line4);
                System.out.println(line5);
                System.out.println("");
                  // read next line
              //      line = reader.readLine();
              
        

System.out.println("Number of mines:");
System.out.println(Integer.toString(mines));
System.out.println("");
System.out.println("Sizes of board:");
System.out.println(Integer.toString(r));
System.out.println(Integer.toString(c));
System.out.println("");
System.out.println("Available playing time (in seconds):");
System.out.println(Integer.toString(mytime));
System.out.println("");
System.out.println("Number of SUPER-mines:");
System.out.println(Integer.toString(smines));
System.out.println("");
System.out.println("");
           
//!!! After many mistakes hahaha!!!//
UI.setLook("Nimbus");                      
this.board = new Board(mines, r, c, mytime, smines);
this.gui.dispose();
this.gui = new UI(board.getRows(), board.getCols(), board.getNumberOfMines(), board.getmytime(), board.getNumberOfSuperMines());        
this.gui.setButtonListeners(this);

this.playing = false;


gui.setVisible(true);

gui.setIcons();        
gui.hideAll();

resumeGame();


           }
            }
        catch (IOException e) {
           e.printStackTrace();
           warningpopup("SOS");
           System.out.println("THE FILE "+ name_of_file+" WAS NOT FOUND.");
           System.out.println("HEY PLAYER! THERE IS NOT SUCH FILE... PLEASE PRESS ON THE CREATE BUTTON TO MAKE YOUR OWN GAME DESCRIPTION!!!");
        }
    }       

    



//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
public void createBoardIDdefault()
  {       

        /*boolean valid=true;
        String number_of_file = Integer.toString(myID);
        String temp =  "medialab/SCENARIO-"+number_of_file;
        String name_of_file = temp+".txt";
        */

            try(BufferedReader reader = new BufferedReader(new FileReader( "medialab/SCENARIO-ID.txt"))){
                        
                boolean valid=true;
                String line1 = reader.readLine();
                        String line2 = reader.readLine();
                        String line3 = reader.readLine();
                        String line4 = reader.readLine();
                        String line5 = reader.readLine();
                        reader.close();

                        int level = Integer.parseInt(line1);
                        int r;
                        int c;
                        
                        if (level==1){
                            r = 9;
                            c = 9;
                                        }
                        
                         else if (level==2){
                            r = 16;
                            c = 16;
                         }
                        
                         else{r=1; c=1;}
                                                        
                        int mines = Integer.parseInt(line2);
                        
                        int mytime = Integer.parseInt(line3);
                        int smines = Integer.parseInt(line4);
                        
                         
                        try  
                        {  
                            // calling the method   
                            myInvalidDescriptionException.validate(line1,line2, line3, line4, line5);
                            valid = true;
                        }  
                        catch (InvalidDescriptionException ex)  
                        {  
                           System.out.println("Caught the exception...Try declaring the SCENARIO-ID File correclty...Sorry...");  
                                                valid=false;

                            // printing the messValue from InvalidValueException object  
                            System.out.println("Exception occured: " + ex);  
                        }  
    
                        try  
                        {  
                            // calling the method   
                            myInvalidValueException.validate(level, mines,mytime,smines);
                            //this.board = new Board(mines, r, c, mytime, smines);
                        }  
                        catch (InvalidValueException ex)  
                        {  
                            System.out.println("Caught the exception...Try declaring the Values in a Valid way...Sorry...");  
                            valid=false;
                            // printing the messValue from InvalidValueException object  
                            System.out.println("Exception occured: " + ex);  
                        }  
                      
     
                if (valid){
                System.out.println("");
                System.out.println("");
                System.out.println("The SCENARIO-ID file is VALID and is the following:");
                System.out.println(line1);
                System.out.println(line2);
                System.out.println(line3);
                System.out.println(line4);
                System.out.println(line5);
                System.out.println("");
                  // read next line
              //      line = reader.readLine();
              
        

System.out.println("Number of mines:");
System.out.println(Integer.toString(mines));
System.out.println("");
System.out.println("Sizes of board:");
System.out.println(Integer.toString(r));
System.out.println(Integer.toString(c));
System.out.println("");
System.out.println("Available playing time (in seconds):");
System.out.println(Integer.toString(mytime));
System.out.println("");
System.out.println("Number of SUPER-mines:");
System.out.println(Integer.toString(smines));
System.out.println("");
System.out.println("");
           
//!!! After many mistakes hahaha!!!//
UI.setLook("Nimbus");                      
this.board = new Board(mines, r, c, mytime, smines);
this.gui.dispose();
this.gui = new UI(board.getRows(), board.getCols(), board.getNumberOfMines(), board.getmytime(), board.getNumberOfSuperMines());        
this.gui.setButtonListeners(this);

this.playing = false;


gui.setVisible(true);

gui.setIcons();        
gui.hideAll();

resumeGame();


           }
            }
        catch (IOException e) {
          // e.printStackTrace();
       
       }
    }       

//???????????????????????????????????????????????????????????????????????//


    //---------------------------------------------------------------//
    public void newGame()
    {            
        numofclicks =0;
        numoflclicks =0;
        
    
        clickedsmine=false;
        this.playing = false;        
        createBoard();
        
        
        gui.interruptTimer();
        //interruptlocalTimer();
        
        gui.resetTimer();        
        resetlocalTimer();        
        gui.initGame();
        gui.setMines(board.getNumberOfMines());
    }

    public void newGameID(int IDofgame)
    {            
        numofclicks =0;
        numoflclicks =0;
        
        clickedsmine=false;
        this.playing = false;        
                                
        createBoardID(IDofgame);
        
        gui.interruptTimer();
        //interruptlocalTimer();
        //gui = new UI(board.getRows(), board.getCols(), board.getNumberOfMines(), board.getmytime(), board.getNumberOfSuperMines());        
        gui.resetTimer();        
        resetlocalTimer();        
        gui.initGame();
        gui.setMines(board.getNumberOfMines());
    }
    //------------------------------------------------------------------------------//
    public void newGameIDdefault()
    {            
        numofclicks =0;
        numoflclicks =0;
        
        clickedsmine=false;
        this.playing = false;        
                                
        createBoardIDdefault();
        
        gui.interruptTimer();
        gui.resetTimer();        
        resetlocalTimer();        
        gui.initGame();
        gui.setMines(board.getNumberOfMines());
    }

    public void restartGame()
    {
        this.playing = false;
        numofclicks =0;
        numoflclicks =0;
        clickedsmine=false;
        board.resetBoard();
        
        gui.interruptTimer();
        //interruptlocalTimer();

        gui.resetTimer();
        resetlocalTimer();        
        
        gui.initGame();
        gui.setMines(board.getNumberOfMines());
    }
      
    

    //------------------------------------------------------------------------------//    
    public void endGame()
    {
        String messagewon;
        playing = false;
        if (gamewonbool) messagewon="YOU WON!";
        else messagewon="YOU LOST...";
        messageround5=messageround4;
        messageround4=messageround3;
        messageround3=messageround2;
        messageround2=messageround1;
        messageround1 = ("#MINES="+Integer.toString(board.getNumberOfMines())+" --#clicks="+Integer.toString(numoflclicks)+" --time="+Integer.toString(gui.getTimePassed())+" --"+messagewon);
        System.out.println(messageround1);
        System.out.println(messageround2);
        System.out.println(messageround3);
        System.out.println(messageround4);
        System.out.println(messageround5);
        numofclicks =0;
        numoflclicks =0;
        clickedsmine=false;

        showAll();

        score.save();
        
        resetlocalTimer(); 

        //Αυτό το έψαχνα ώρες.....

        //interruptlocalTimer();
       
        //File myObj = new File("medialab/output.txt");
        // myObj.delete();

    }

    
    //-------------------------GAME WON AND GAME LOST ---------------------------------//
    
    public void gameWon()
    {
        gamewonbool=true;
        numofclicks =0;
        //numoflclicks =0;
        clickedsmine=false;

        score.incCurrentStreak();
        score.incCurrentWinningStreak();
        score.incGamesWon();
        score.incGamesPlayed();
        
        gui.interruptTimer();
        //interruptlocalTimer();
        gui.setwontimePassedLabel();
        
        endGame();
        //----------------------------------------------------------------//
        
        
        JDialog dialog = new JDialog(gui, Dialog.ModalityType.DOCUMENT_MODAL);
        
        //------MESSAGE-----------//
        JLabel message = new JLabel("Congratulations, you won the game!", SwingConstants.CENTER);
                
        //-----STATISTICS-----------//
        JPanel statistics = new JPanel();
        statistics.setLayout(new GridLayout(6,1,0,10));
        
        ArrayList<Time> bTimes = score.getBestTimes();
        
        if (bTimes.isEmpty() || (bTimes.get(0).getTimeValue() > gui.getTimePassed()))
        {
            statistics.add(new JLabel("    You have the fastest time for this difficulty level!    "));
        }
        
        score.addTime(gui.getTimePassed(), new Date(System.currentTimeMillis()));
                
        JLabel time = new JLabel("  Time:  " + Integer.toString(gui.getTimePassed()) + " seconds            Date:  " + new Date(System.currentTimeMillis()));
        
        JLabel bestTime = new JLabel();
        
        
        if (bTimes.isEmpty())
        {
            bestTime.setText("  Best Time:  ---                  Date:  ---");
        }
        else
        {
            bestTime.setText("  Best Time:  " + bTimes.get(0).getTimeValue() + " seconds            Date:  " + bTimes.get(0).getDateValue());
        }
        
        JLabel gPlayed = new JLabel("  Games Played:  " + score.getGamesPlayed());
        JLabel gWon = new JLabel("  Games Won:  " + score.getGamesWon());
        JLabel gPercentage = new JLabel("  Win Percentage:  " + score.getWinPercentage() + "%");
        
        statistics.add(time);
        statistics.add(bestTime);
        statistics.add(gPlayed);
        statistics.add(gWon);
        statistics.add(gPercentage);
        
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);        
        statistics.setBorder(loweredetched);
        
        
        //--------BUTTONS----------//
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,2,10,0));
        
        JButton exit = new JButton("Exit");
        JButton playAgain = new JButton("Play Again");

        
        exit.addActionListener((ActionEvent e) -> {

            dialog.dispose();
            windowClosing(null);
        });        
        playAgain.addActionListener((ActionEvent e) -> {
           
            dialog.dispose();            
            newGameIDdefaulting();;
        });        
        
        
        buttons.add(exit);
        buttons.add(playAgain);
        
        //--------DIALOG-------------//
        
        JPanel c = new JPanel();
        c.setLayout(new BorderLayout(20,20));
        c.add(message, BorderLayout.NORTH);
        c.add(statistics, BorderLayout.CENTER);
        c.add(buttons, BorderLayout.SOUTH);
        
        c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    dialog.dispose();
                    newGameIDdefaulting();;
            }
            }
        );

        dialog.setTitle("Game Won");
        dialog.add(c);
        dialog.pack();
        dialog.setLocationRelativeTo(gui);
        dialog.setVisible(true);                        
    }
    
    private void gameLost()
    {
        gamewonbool = false;
        numofclicks =0;
        //numoflclicks =0;
        clickedsmine=false;

        score.decCurrentStreak();
        score.incCurrentLosingStreak();
        score.incGamesPlayed();
        
        gui.interruptTimer();
        //interruptlocalTimer();
        gui.setlosttimePassedLabel();

        endGame();

        
        //----------------------------------------------------------------//

        JDialog dialog = new JDialog(gui, Dialog.ModalityType.DOCUMENT_MODAL);
        
        //------MESSAGE-----------//
        JLabel message = new JLabel("Sorry, you lost this game. Better luck next time!", SwingConstants.CENTER);
                
        //-----STATISTICS-----------//
        JPanel statistics = new JPanel();
        statistics.setLayout(new GridLayout(5,1,0,10));
        
        JLabel time = new JLabel("  Time:  " + Integer.toString(gui.getTimePassed()) + " seconds");
        
        JLabel bestTime = new JLabel();
        
        ArrayList<Time> bTimes = score.getBestTimes();
        
        if (bTimes.isEmpty())
        {
            bestTime.setText("                        ");
        }
        else
        {
            bestTime.setText("  Best Time:  " + bTimes.get(0).getTimeValue() + " seconds            Date:  " + bTimes.get(0).getDateValue());
        }
        
        JLabel gPlayed = new JLabel("  Games Played:  " + score.getGamesPlayed());
        JLabel gWon = new JLabel("  Games Won:  " + score.getGamesWon());
        JLabel gPercentage = new JLabel("  Win Percentage:  " + score.getWinPercentage() + "%");
        
        statistics.add(time);
        statistics.add(bestTime);
        statistics.add(gPlayed);
        statistics.add(gWon);
        statistics.add(gPercentage);
        
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);        
        statistics.setBorder(loweredetched);
        
        
        //--------BUTTONS----------//
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,3,2,0));
        
        JButton exit = new JButton("Exit");
       // JButton restart = new JButton("Restart");
        JButton playAgain = new JButton("Play Again");

        
        exit.addActionListener((ActionEvent e) -> {
            dialog.dispose();
            windowClosing(null);
        });        

        playAgain.addActionListener((ActionEvent e) -> {
            dialog.dispose();            
            newGameIDdefaulting();;
        });        
        
        
        buttons.add(exit);
       // buttons.add(restart);
        buttons.add(playAgain);
        
        //--------DIALOG-------------//
        
        JPanel c = new JPanel();
        c.setLayout(new BorderLayout(20,20));
        c.add(message, BorderLayout.NORTH);
        c.add(statistics, BorderLayout.CENTER);
        c.add(buttons, BorderLayout.SOUTH);

        
        c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    dialog.dispose();
                    newGameIDdefaulting();;
            }
            }
        );
        
        dialog.setTitle("Game Lost");
        dialog.add(c);
        dialog.pack();
        dialog.setLocationRelativeTo(gui);
        dialog.setVisible(true);      

    }
    
    
    //--------------------------------SCORE BOARD--------------------------------------//
    public void showScore()
    {
        //----------------------------------------------------------------//
                
        JDialog dialog = new JDialog(gui, Dialog.ModalityType.DOCUMENT_MODAL);

        //-----BEST TIMES--------//
        
        JPanel bestTimes = new JPanel();
        bestTimes.setLayout(new GridLayout(5,1));
        
        ArrayList<Time> bTimes = score.getBestTimes();
        
        for (int i = 0; i < bTimes.size(); i++)
        {
            JLabel t = new JLabel("  " + bTimes.get(i).getTimeValue() + "           " + bTimes.get(i).getDateValue());            
            bestTimes.add(t);
        }
        
        if (bTimes.isEmpty())
        {
            JLabel t = new JLabel("                               ");            
            bestTimes.add(t);
        }
        
        TitledBorder b = BorderFactory.createTitledBorder("Best Times");
        b.setTitleJustification(TitledBorder.LEFT);

        bestTimes.setBorder(b);
                
        //-----STATISTICS-----------//
        JPanel statistics = new JPanel();
        
        statistics.setLayout(new GridLayout(6,1,0,10));        
        
        JLabel gPlayed = new JLabel("  Games Played:  " + score.getGamesPlayed());
        JLabel gWon = new JLabel("  Games Won:  " + score.getGamesWon());
        JLabel gPercentage = new JLabel("  Win Percentage:  " + score.getWinPercentage() + "%");
        JLabel lWin = new JLabel("  Longest Winning Streak:  " + score.getLongestWinningStreak());
        JLabel lLose = new JLabel("  Longest Losing Streak:  " + score.getLongestLosingStreak());
        JLabel currentStreak = new JLabel("  Current Streak:  " + score.getCurrentStreak());

        
        statistics.add(gPlayed);
        statistics.add(gWon);
        statistics.add(gPercentage);
        statistics.add(lWin);
        statistics.add(lLose);
        statistics.add(currentStreak);
                        
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);        
        statistics.setBorder(loweredetched);
        
        
        //--------BUTTONS----------//
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,2,10,0));
        
        JButton close = new JButton("Close");
        JButton reset = new JButton("Reset");

        
        close.addActionListener((ActionEvent e) -> {
            dialog.dispose();
        });        
        reset.addActionListener((ActionEvent e) -> {
            ImageIcon question = new ImageIcon(getClass().getResource("/resources/question.png"));      

            int option = JOptionPane.showOptionDialog(null, "Do you want to reset all your statistics to zero?", 
                            "Reset Statistics", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, question,null,null);

            switch(option) 
            {
                case JOptionPane.YES_OPTION:      

                    score.resetScore();
                    score.save();
                    dialog.dispose();
                    showScore();
                    break;

                case JOptionPane.NO_OPTION: 
                    break;
            }
        });        
        
        buttons.add(close);
        buttons.add(reset);
        
        if (score.getGamesPlayed() == 0)
            reset.setEnabled(false);
        
        //--------DIALOG-------------//
        
        JPanel c = new JPanel();
        c.setLayout(new BorderLayout(20,20));
        c.add(bestTimes, BorderLayout.WEST);
        c.add(statistics, BorderLayout.CENTER);        
        c.add(buttons, BorderLayout.SOUTH);
        
        c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        dialog.setTitle("Minesweeper Statistics");
        dialog.add(c);
        dialog.pack();
        dialog.setLocationRelativeTo(gui);
        dialog.setVisible(true);                        
    }
    
    //------------------------------------------------------------------------------//
	public void roundspressed()
    {
        System.out.println(messageround1);
        System.out.println(messageround2);
        System.out.println(messageround3);
        System.out.println(messageround4);
        System.out.println(messageround5);             
  
        JDialog dialog = new JDialog(gui, Dialog.ModalityType.DOCUMENT_MODAL);

        //-----STATISTICS-----------//
        JPanel rounds = new JPanel();
        
        rounds.setLayout(new GridLayout(6,1,0,10));        
        
        JLabel message0 = new JLabel("LAST 5 ROUNDS:");        
        JLabel message1 = new JLabel(messageround1);        
        JLabel message2 = new JLabel(messageround2);
        JLabel message3 = new JLabel(messageround3);
        JLabel message4 = new JLabel(messageround4);
        JLabel message5 = new JLabel(messageround5);


        
        rounds.add(message0);
        rounds.add(message1);
        rounds.add(message2);
        rounds.add(message3);
        rounds.add(message4);
        rounds.add(message5);
        
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);        
        rounds.setBorder(loweredetched);
        
        
        //--------BUTTONS----------//
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,2,10,0));
        
        JButton close = new JButton("Close");
        JButton reset = new JButton("Statistics");

        
        close.addActionListener((ActionEvent e) -> {
            dialog.dispose();
        });        
        reset.addActionListener((ActionEvent e) -> {
            dialog.dispose();
            showScore();
        });        
        
        buttons.add(close);
        buttons.add(reset);
        //--------DIALOG-------------//
        
        JPanel c = new JPanel();
        c.setLayout(new BorderLayout(20,20));
        c.add(rounds, BorderLayout.CENTER);        
        c.add(buttons, BorderLayout.SOUTH);
        c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        dialog.setTitle("Minesweeper Rounds");
        dialog.add(c);
        dialog.pack();
        dialog.setLocationRelativeTo(gui);
        dialog.setVisible(true);                        
    }
    
    
    //''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''//
    
    
    public void newGameIDdefaulting()
    {if (myidinteger==-1){newGameIDdefault();}
    else newGameID(myidinteger);
    }

    //''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''//
    // Shows the "solution" of the game.
    private void showAll()
    {
        String cellSolution;
        
        Cell cells[][] = board.getCells();
        JButton buttons[][] = gui.getButtons();

        for (int x=0; x<board.getCols(); x++ ) 
        {
            for (int y=0; y<board.getRows(); y++ ) 
            {
                cellSolution = cells[x][y].getContent();

                // Is the cell still unrevealed
                if( cellSolution.equals("") ) 
                {
                    buttons[x][y].setIcon(null);
                    
                    // Get Neighbours
                    cellSolution = Integer.toString(cells[x][y].getSurroundingMines());

                    // Is it a mine?
                    if(cells[x][y].getMine()) 
                    {
                        cellSolution = "M";
                        
                        //mine
                        buttons[x][y].setIcon(gui.getIconMine());
                        buttons[x][y].setBackground(Color.lightGray);                        
                    }
                    else
                    {
                        if(cellSolution.equals("0"))
                        {
                            buttons[x][y].setText("");                           
                            buttons[x][y].setBackground(Color.lightGray);
                        }
                        else
                        {
                            buttons[x][y].setBackground(Color.lightGray);
                            buttons[x][y].setText(cellSolution);
                            gui.setTextColor(buttons[x][y]);
                        }
                    }
                }

                // This cell is already flagged!
                else if( cellSolution.equals("F") ) 
                {
                    // Is it correctly flagged?
                    if(!cells[x][y].getMine()) 
                    {
                        buttons[x][y].setBackground(Color.orange);
                    }
                    else
                        buttons[x][y].setBackground(Color.green);
                }
                
            }
        }
    }
    

    //-------------------------------------------------------------------------//
    
    //-------------------------------------------------------------------------//    
    

    //-------------------------------------------------------------------------//

    
    //--------------------------------------------------------------------------//
    
    public boolean isFinished()
    {
        boolean isFinished = true;
        String cellSolution;

        Cell cells[][] = board.getCells();
        
        for( int x = 0 ; x < board.getCols() ; x++ ) 
        {
            for( int y = 0 ; y < board.getRows() ; y++ ) 
            {
                // If a game is solved, the content of each Cell should match the value of its surrounding mines
                cellSolution = Integer.toString(cells[x][y].getSurroundingMines());
                
                if(cells[x][y].getMine()) 
                    cellSolution = "F";

                // Compare the player's "answer" to the solution.
                //Δεν χρειάζομαι flags για να νικήσω
                if((!cells[x][y].getContent().equals(cellSolution))&&(!cells[x][y].getMine())) 
                {
                    //This cell is not solved yet
                    isFinished = false;
                    break;
                }
            }
        }

        return isFinished;
    }

 
    //Check the game to see if its finished or not
    private void checkGame()
    {		
        if(isFinished()) 
        {            
            gameWon();
        }
    }
   
    //----------------------------------------------------------------------//
    
    /*
     * If a player clicks on a zero, all surrounding cells ("neighbours") must revealed.
     * This method is recursive: if a neighbour is also a zero, his neighbours must also be revealed.
     */
    public void findZeroes(int xCo, int yCo)
    {
        int neighbours;
        
        Cell cells[][] = board.getCells();
        JButton buttons[][] = gui.getButtons();

        // Columns
        for(int x = board.makeValidCoordinateX(xCo - 1) ; x <= board.makeValidCoordinateX(xCo + 1) ; x++) 
        {			
            // Rows
            for(int y = board.makeValidCoordinateY(yCo - 1) ; y <= board.makeValidCoordinateY(yCo + 1) ; y++) 
            {
                // Only unrevealed cells need to be revealed.
                if(cells[x][y].getContent().equals("")) 
                {
                    // Get the neighbours of the current (neighbouring) cell.
                    neighbours = cells[x][y].getSurroundingMines();

                    // Reveal the neighbours of the current (neighbouring) cell
                    cells[x][y].setContent(Integer.toString(neighbours));

                    if (!cells[x][y].getMine())
                        buttons[x][y].setIcon(null);                        
                    
                    // Is this (neighbouring) cell a "zero" cell itself?
                    if(neighbours == 0)
                    {                        
                        // Yes, give it a special color and recurse!
                        buttons[x][y].setBackground(Color.lightGray);
                        buttons[x][y].setText("");
                        findZeroes(x, y);
                    }
                    else
                    {
                        // No, give it a boring gray color.
                        buttons[x][y].setBackground(Color.lightGray);
                        buttons[x][y].setText(Integer.toString(neighbours));
                        gui.setTextColor(buttons[x][y]);                        
                    }
                }
            }
        }
    }
    
    public void nonrecursivefindZeroesx(int xCo, int yCo)
    {
        int neighbours;
        
        Cell cells[][] = board.getCells();
        JButton buttons[][] = gui.getButtons();

        // Columns
        for(int x = board.makeValidCoordinateX(0) ; x <= board.makeValidCoordinateX(board.getCols() ) ; x++) 
        {			
            // Rows
            for(int y = board.makeValidCoordinateY(yCo) ; y <= board.makeValidCoordinateY(yCo) ; y++) 
            {

                // Only unrevealed cells need to be revealed.
                if((cells[x][y].getContent().equals(""))||(cells[x][y].getContent().equals("F"))) 
                {

                    if((cells[x][y].getContent().equals("F"))){
                        
                        gui.incMines();
                    } 
                    // Get the neighbours of the current (neighbouring) cell.
                    neighbours = cells[x][y].getSurroundingMines();

                    // Reveal the neighbours of the current (neighbouring) cell
                   // cells[x][y].setContent(Integer.toString(neighbours));

                    if (!cells[x][y].getMine())
                        
                    cells[x][y].setContent(Integer.toString(neighbours));
                    buttons[x][y].setIcon(null);  
                        
                    if (cells[x][y].getMine()){
                        buttons[x][y].setIcon(gui.getIconFlag());  
                        gui.decMines();
                    }
                    // Is this (neighbouring) cell a "zero" cell itself?
                    if((neighbours == 0)&&(!cells[x][y].getMine()))
                    {                        
                        // Yes, give it a special color and recurse!
                        buttons[x][y].setBackground(Color.lightGray);
                        buttons[x][y].setText("");
                        
                        //findZeroes(x, y);
                    }
                    else if((neighbours != 0)&&(!cells[x][y].getMine()))
                    {
                        // No, give it a boring gray color.
                        buttons[x][y].setBackground(Color.lightGray);
                        buttons[x][y].setText(Integer.toString(neighbours));
                        gui.setTextColor(buttons[x][y]);                        
                    }
                }
                buttons[x][y].setEnabled(false);

            }
        }
    }

    public void nonrecursivefindZeroesy(int xCo, int yCo)
    {
        int neighbours;
        
        Cell cells[][] = board.getCells();
        JButton buttons[][] = gui.getButtons();

        // Columns
        for(int y = board.makeValidCoordinateY(0) ; y <= board.makeValidCoordinateY(board.getRows() ) ;y++) 
        {			
            // Rows
            for(int x = board.makeValidCoordinateX(xCo) ; x <= board.makeValidCoordinateX(xCo) ; x++) 
            {
  
                // Only unrevealed cells need to be revealed.
                if((cells[x][y].getContent().equals(""))||(cells[x][y].getContent().equals("F"))) 
                {

                    if((cells[x][y].getContent().equals("F"))){
                        
                        gui.incMines();
                    } 
                    // Get the neighbours of the current (neighbouring) cell.
                    neighbours = cells[x][y].getSurroundingMines();

                    // Reveal the neighbours of the current (neighbouring) cell
                   // cells[x][y].setContent(Integer.toString(neighbours));

                    if (!cells[x][y].getMine())
                        
                    cells[x][y].setContent(Integer.toString(neighbours));
                    buttons[x][y].setIcon(null);  
                        
                    if (cells[x][y].getMine()){
                        buttons[x][y].setIcon(gui.getIconFlag());  
                        gui.decMines();
                    }
                    // Is this (neighbouring) cell a "zero" cell itself?
                    if((neighbours == 0)&&(!cells[x][y].getMine()))
                    {                        
                        // Yes, give it a special color and recurse!
                        buttons[x][y].setBackground(Color.lightGray);
                        buttons[x][y].setText("");
                        
                        //findZeroes(x, y);
                    }
                    else if((neighbours != 0)&&(!cells[x][y].getMine()))
                    {
                        // No, give it a boring gray color.
                        buttons[x][y].setBackground(Color.lightGray);
                        buttons[x][y].setText(Integer.toString(neighbours));
                        gui.setTextColor(buttons[x][y]);                        
                    }
                }
            
            buttons[x][y].setEnabled(false);
            }
        }

    }
    
    
    //-----------------------------------------------------------------------------//
    //This function is called when clicked on closed button or exit
    @Override
    public void windowClosing(WindowEvent e) 
    {
        File myObj = new File("medialab/output.txt");
        myObj.delete();
       /* if (playing)
        {
           ImageIcon question = new ImageIcon(getClass().getResource("/resources/question.png"));      

            Object[] options = {"Save","Don't Save","Cancel"};

            int quit = JOptionPane.showOptionDialog(null, "What do you want to do with the game in progress?", 
                            "New Game", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, question, options, options[2]);

            switch(quit) 
            {
                //save
                case JOptionPane.YES_OPTION:
                    
                    gui.interruptTimer();
                    interruptlocalTimer();

                    score.save();
                    
                    JDialog dialog = new JDialog(gui, Dialog.ModalityType.DOCUMENT_MODAL);
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    panel.add(new JLabel("Saving.... Please Wait", SwingConstants.CENTER));
                    dialog.add(panel);
                    dialog.setTitle("Saving Game...");
                    dialog.pack();
                    dialog.setLocationRelativeTo(gui);                    
                    dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
                       @Override
                       protected Void doInBackground() throws Exception 
                       {
                            board.saveGame(gui.getTimePassed(), gui.getMines());                
                            return null;
                       }
                       
                       @Override
                       protected void done(){
                           dialog.dispose();                           
                       }                       
                    };
                            
                    worker.execute();
                    dialog.setVisible(true);
                                                            
                    System.exit(0);
                    break;
                
                //dont save                    
                case JOptionPane.NO_OPTION:
                    score.incGamesPlayed();
                    score.save();
                    System.exit(0);
                    break;
                    
                case JOptionPane.CANCEL_OPTION: break;
            }
        }
        else*/
            System.exit(0);
    }
    
    //-----------------------------------------------------------------------//

    @Override
    public void actionPerformed(ActionEvent e) {        
        JMenuItem menuItem = (JMenuItem) e.getSource();

        if (menuItem.getName().equals("Start"))
        {
            if (playing)
            {
              
                newGameIDdefault();
                score.incGamesPlayed();
                score.save();
                
                
            }
        }
        
        else if (menuItem.getName().equals("Exit"))
        {
            windowClosing(null);
        }
        
        else if (menuItem.getName().equals("Rounds"))
        {
            roundspressed();
        }
        else if (menuItem.getName().equals("Statistics"))
        {
            showScore();
        }

        else if (menuItem.getName().equals("Create"))
        {
            createbuttonpressed();
           
        }

        else if (menuItem.getName().equals("Load"))
        {
            JButton jb = new JButton("Enter");
            JFrame f= new JFrame("Type the ID and Press ENTER");  
            JTextField t1,t2,t3,t4,t5;  
           t1=new JTextField("Game ID");  
           t1.setBounds(50,70, 200,30);  
          /*t2=new JTextField("LEVEL");  
           t2.setBounds(50,130, 200,30);  
           t3=new JTextField("# OF MINES");  
           t3.setBounds(50,160, 200,30);  
           t4=new JTextField("# OF SUPER MINES");  
           t4.setBounds(50,190, 200,30);  
           t5=new JTextField("TIME (IN SECONDS)");  
           t5.setBounds(50,210, 200,30);  */
          f.add(t1); 
          /*f.add(t2); 
          f.add(t3); 
          f.add(t4); 
          f.add(t5);  */
             
          t1.addActionListener(new ActionListener()
          {
                 public void actionPerformed(ActionEvent e)
                 {
                       String myid = t1.getText();
                       myidinteger = Integer.parseInt(myid);
                       t1.setText(myid);
                       newGameID(myidinteger);
                       currentID=myidinteger; 
                 }
          });

          jb.addActionListener(new ActionListener()
          {
                 public void actionPerformed(ActionEvent e)
                 {
                       String myid = t1.getText();
                       myidinteger = Integer.parseInt(myid);
                       t1.setText(myid);
                       newGameID(myidinteger);
                       currentID=myidinteger; 
                 }
          });

          f.setSize(400,200);  
          f.setLayout(null);  
          f.setVisible(true);  
            if (playing) {
                
        score.decCurrentStreak();
        score.incCurrentLosingStreak();
        score.incGamesPlayed();
        
        gui.interruptTimer();
        //interruptlocalTimer();
        gui.setlosttimePassedLabel();
        score.save();
        
        resetlocalTimer(); 
            }

        



            
        }

        else if (menuItem.getName().equals("Solution"))
        {
            showAll();
            gameLost();
        }
        
        //Statistics & Rounds
        else
        {
            showScore();
        }        
    }
    
    
 



    //--------------------------------------------------------------------------//
    public void createbuttonpressed()
    {


        JButton jb = new JButton("Enter");
        JFrame f= new JFrame("Type the DETAILS and Press ENTER after every one of them");  
        JTextField t1,t2,t3,t4,t5;  
       t1=new JTextField("Game ID");  
       t1.setBounds(50,100, 200,30);  
       t2=new JTextField("LEVEL");  
       t2.setBounds(50,130, 200,30);  
       t3=new JTextField("# OF MINES");  
       t3.setBounds(50,160, 200,30);  
       t4=new JTextField("# OF SUPER MINES");  
       t4.setBounds(50,190, 200,30);  
       t5=new JTextField("TIME (IN SECONDS)");  
       t5.setBounds(50,210, 200,30);  
      f.add(t1); 
      f.add(t2); 
      f.add(t3); 
      f.add(t4); 
      f.add(t5);  
         
      t1.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);
             }
      });

      jb.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);
             }
      });

      t2.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);
             }
      });

      jb.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);
             }
      });


      t3.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);
             }
      });

      jb.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);
             }
      });


      t4.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);
                   
             }
      });

      jb.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);

             }
      });

      t5.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);
             }
      });

      jb.addActionListener(new ActionListener()
      {
             public void actionPerformed(ActionEvent e)
             {
                String myid = t1.getText();
                myidinteger = Integer.parseInt(myid);
                t1.setText(myid);
                currentID=myidinteger; 
                String LEVELstr = t2.getText();
                LEVEL = Integer.parseInt(LEVELstr);
                t2.setText(LEVELstr); 
                String MINESstr = t3.getText();
                MINES = Integer.parseInt(MINESstr);
                t3.setText(MINESstr);
                String SMINESstr = t4.getText();
                SMINES = Integer.parseInt(SMINESstr);
                t4.setText(SMINESstr);
                String TIMEstr = t5.getText();
                TIME = Integer.parseInt(TIMEstr);
                t5.setText(TIMEstr);
                createScenarioID(myidinteger ,LEVEL ,MINES ,SMINES ,TIME);
             }
      });

      



      f.setSize(400,400);  
      f.setLayout(null);  
      f.setVisible(true);  

        //createScenarioID(3, 1, 10, 0, 150);

    }
    //--------------------------------------------------------------------------//
    
    //Mouse Click Listener
    @Override
    public void mouseClicked(MouseEvent e)
    {
        // start timer on first click
        if(!playing)
        {
            //gui.getTimePassed();
            gui.startTimer();            
            startlocalTimer();

            playing = true;
        }
        
        if (playing)
        {
            //Get the button's name
            JButton button = (JButton)e.getSource();

            // Get coordinates (button.getName().equals("x,y")).
            String[] co = button.getName().split(",");

            int x = Integer.parseInt(co[0]);
            int y = Integer.parseInt(co[1]);
            int tempx = board.getXSMines();
            int tempy = board.getYSMines();
            boolean realclicksmine=clickedsmine;
            // Get cell information.
            boolean isMine = board.getCells()[x][y].getMine();

            boolean isSMine = board.getCells()[x][y].getSMine();

            int neighbours = board.getCells()[x][y].getSurroundingMines();
           
            
          //  boolean isactivated= buttons[x][y].isEnabled();
           
            
            // Left Click
            if (SwingUtilities.isLeftMouseButton(e)) 
            {
                if ((!clickedsmine)||(x!=tempx)){
                    if ((!clickedsmine)||(y!=tempy)){

                if (board.getCells()[x][y].getContent().equals("F")){
                    gui.incMines();
                }
                //if (!board.getCells()[x][y].getContent().equals("F"))
                //{
                    button.setIcon(null);

                    //Mine is clicked.
                    if(isMine) 
                    {  
                       // boolean isactivated=buttons[x][y].isEnabled();
                       // if (!isactivated){
                        //red mine
                        button.setIcon(gui.getIconRedMine());
                        button.setBackground(Color.red);
                        board.getCells()[x][y].setContent("M");

                        gameLost();
                        resetlocalTimer();
                        //interruptlocalTimer();
                  // }
                }
                    else 
                    {

                        numofclicks++;
                        numoflclicks++;
                        // The player has clicked on a number.
                        board.getCells()[x][y].setContent(Integer.toString(neighbours));
                        button.setText(Integer.toString(neighbours));
                        gui.setTextColor(button);

                        if( neighbours == 0 ) 
                        {
                            // Show all surrounding cells.
                            button.setBackground(Color.lightGray);
                            button.setText("");
                            findZeroes(x, y);
                        } 
                        else 
                        {
                            button.setBackground(Color.lightGray);
                        }
                    }
              //  }
            }
        }
        }
            // Right Click
            else if (SwingUtilities.isRightMouseButton(e)) 
            {
                if ((!clickedsmine)||(x!=tempx)){
                    if ((!clickedsmine)||(y!=tempy)){

                if(board.getCells()[x][y].getContent().equals("F")) 
                {   
                   // numofclicks--;

                    board.getCells()[x][y].setContent("");
                    button.setText("");
                    button.setBackground(new Color(0,110,140));

                    //simple blue

                    button.setIcon(gui.getIconTile());
                    gui.incMines();
                }
                else if ((board.getCells()[x][y].getContent().equals(""))&& ((gui.getMines())>0)) 
                {
                    numofclicks++;
                    board.getCells()[x][y].setContent("F");
                    button.setBackground(Color.blue);	

                    button.setIcon(gui.getIconFlag());
                    gui.decMines();

                    if ((numofclicks<5)&&(isSMine)){
                        clickedsmine=true;
                        button.setIcon(gui.getIconRedMine());
                        nonrecursivefindZeroesx(x, y);
                        nonrecursivefindZeroesy(x, y);
                        //gui.disableAll();
                        //gui.disableAll();
                        
                    }
                }
            }
        }
        }
            checkGame();
        }
    }

 //----------------------Local Timer--------------------------------------//      
 
        
 

 //-----------------------Related to local Timer------------------------//
    
    // Starts the timer
    public void startlocalTimer()
    {        
        countdowner = board.getmytime();

        stoplocalTimer = false;
        
        localtimer = new Thread() {
                @Override
                public void run()
                {
                    while(!stoplocalTimer)
                    {
                        localtimePassed++;
                        
                        if (localtimePassed==(board.getmytime()))
                        {
                            gui.setTimePassed(localtimePassed);
                           // gui.setTimePassed(42);
                           // localtimeisup = true;
                          // stoplocalTimer=true;
                           gameLost();
                       
                            
                        }
                        // Update the time passed label.
                        
                        //localtimePassedLabel.setText("  " + (countdowner-localtimePassed) + "  ");

                        //timePassedLabel.setText("  " + timePassed + "  ");

                        // Wait 1 second.
                        try{
                            sleep(1000); 
                        }
                        catch(InterruptedException ex){}
                    }
                   // resetlocalTimer();

                }
            
        
        };                

       localtimer.start();
    }

   

    public void interruptlocalTimer()
    {
        stoplocalTimer = true;
        localtimeisup = false;

        try 
        {
            if (localtimer!= null)
                localtimer.join();
        } 
        catch (InterruptedException ex) 
        {

        }        
    }
    
    public void resetlocalTimer()
    {
        countdowner = board.getmytime();
        localtimePassed = 0;
        stoplocalTimer = true;

        localtimeisup = false;
        
        //localtimePassedLabel.setText("  " + localtimePassed + "  ");        
    }

    public void setlocalTimePassed(int t)
    {
        countdowner = board.getmytime() - t;
        localtimePassed = t;
        stoplocalTimer = true;

        localtimeisup = false;

        //localltimePassedLabel.setText("  " + timePassed + "  ");                
    }












    //---------------------EMPTY FUNCTIONS-------------------------------//
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }    

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}


