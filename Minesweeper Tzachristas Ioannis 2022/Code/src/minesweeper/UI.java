package minesweeper;


import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Button;


//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;

public class UI extends JFrame
{
    public static final String gameBoard = null;

    // The buttons
    JButton[][] buttons;
    
    // Number of Buttons in Grid
    private int rows;
    private int cols;
    
    // Labels 
    private JLabel minesLabel;
    private int mines;
    private int smines;
    private int constmines;
    private JLabel timePassedLabel;    
    private Thread timer;
    private int timePassed;
    private boolean stopTimer;
    public boolean timeisup = false;
    private int countdowner;


    // Frame settings
    private final String FRAME_TITLE = "MediaLab Minesweeper - Tzachristas Ioannis - el19090";
    
    private int FRAME_WIDTH = 520;
    private int FRAME_HEIGHT = 550;
    private int FRAME_LOC_X = 430;
    private int FRAME_LOC_Y = 50;

    // Icons
    private Icon redMine;
    private Icon mine;
    private Icon flag;
    private Icon tile;
    
    
    // Menu Bar and Items
    
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem newGame;
    private JMenuItem statistics;
    private JMenuItem loading;
    private JMenuItem create;
    private JMenuItem exit;
// Menu Bar and Items
    
//private JMenuBar menuBar2;
private JMenu Details;
private JMenuItem Rounds;
private JMenuItem Solution;

    
    
    //---------------------------------------------------------------//
    public UI(int r, int c, int m, int mytime, int smines)
    {                
        this.rows = r;
        this.cols = c;
        
        this.countdowner = mytime;
        this.mines = m;
        this.smines = smines;
        buttons = new JButton [rows][cols];

        // Set frame
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setTitle(FRAME_TITLE);
        setLocation(FRAME_LOC_X, FRAME_LOC_Y);
               
        // The layout of the frame:

        JPanel gameBoard;        
        JPanel tmPanel;        
        JPanel scorePanel;
        
        //----------------GAME BOARD---------------------//
        // Build the "gameBoard".
        gameBoard = new JPanel();
        gameBoard.setLayout(new GridLayout(rows,cols,0,0));
        
        for( int y=0 ; y<rows ; y++ ) 
        {
            for( int x=0 ; x<cols ; x++ ) 
            {
                // Set button text.
                buttons[x][y] = new JButton("");

                // Set button name (x,y).
                buttons[x][y].setName(Integer.toString(x) + "," + Integer.toString(y));
                buttons[x][y].setFont(new Font("Serif", Font.BOLD, 24));
                
                buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.black, 1, true));

                // Add this button to the gameboard.
                gameBoard.add(buttons[x][y]);
            }
        }
        //-----------------------------------------------//
                
                
        //-------------TIME AND MINE------------------------//
        
        JPanel timePassedPanel = new JPanel();
        timePassedPanel.setLayout(new BorderLayout(10,0));
        
        // Initialize the time passed label.
        this.timePassedLabel = new JLabel ("  LET'S GO  " , SwingConstants.CENTER);
        timePassedLabel.setFont(new Font("Serif", Font.BOLD, 20));
                
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        
        timePassedLabel.setBorder(loweredetched);
        timePassedLabel.setBackground(new Color(110,110,255));
        timePassedLabel.setForeground(Color.white);
        timePassedLabel.setOpaque(true);
        
        JLabel iT = new JLabel("",SwingConstants.CENTER);
        iT.setIcon(new ImageIcon(getClass().getResource("/resources/clock.png"))); 

        timePassedPanel.add(iT, BorderLayout.WEST);
        timePassedPanel.add(timePassedLabel, BorderLayout.CENTER);
        timePassedPanel.setOpaque(false);
        
        this.timePassed = 0;
        this.stopTimer = true;

        
        JPanel minesPanel = new JPanel();
        minesPanel.setLayout(new BorderLayout(10,0));
        
        
        // Initialize mines label.
        this.minesLabel = new JLabel ("  0  " , SwingConstants.CENTER);
        minesLabel.setFont(new Font("Serif", Font.BOLD, 20));
        minesLabel.setBorder(loweredetched);
        minesLabel.setBackground(new Color(110,110,255));
        minesLabel.setForeground(Color.white);
        
        minesLabel.setOpaque(true);
        constmines = m;
        setMines(m);
        
        JLabel mT = new JLabel("", SwingConstants.CENTER);
        mT.setIcon(new ImageIcon(getClass().getResource("/resources/mine.png")));

        minesPanel.add(minesLabel, BorderLayout.WEST);
        minesPanel.add(mT, BorderLayout.CENTER);
        minesPanel.setOpaque(false);
        
        // Build the "tmPanel".
        tmPanel = new JPanel();
        tmPanel.setLayout(new BorderLayout(0,20));
        
        tmPanel.add(timePassedPanel, BorderLayout.WEST);
        tmPanel.add(minesPanel, BorderLayout.EAST);
        tmPanel.setOpaque(false);
        
        //--------------------------------------------//
                        
        
        //------------------Menu Application--------------------------//
        menuBar = new JMenuBar();
        
        gameMenu = new JMenu("Application");
         
        newGame = new JMenuItem("Start");
        loading = new JMenuItem("Load");
        create = new JMenuItem("Create");
        exit = new JMenuItem("Exit");

        newGame.setName("Start");
        loading.setName("Load");
        create.setName("Create");
        exit.setName("Exit");

        gameMenu.add(create);
        gameMenu.add(loading);
        gameMenu.add(newGame);
        gameMenu.add(exit);
        
        menuBar.add(gameMenu);                        
        //----------------------------------------------------//

        
        
//------------------Menu Details--------------------------//

        
Details = new JMenu("Details");
 
Rounds = new JMenuItem("Rounds");
Solution = new JMenuItem("Solution");

Rounds.setName("Rounds");
Solution.setName("Solution");

Details.add(Rounds);
Details.add(Solution);

menuBar.add(Details); 



        //======================================================//
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout(0,10));
        p.add(gameBoard, BorderLayout.CENTER);
        p.add(tmPanel, BorderLayout.NORTH);
    
 
        p.setBorder(BorderFactory.createEmptyBorder(60, 60, 14, 60));        
        p.setOpaque(false);
      
        
        setLayout(new BorderLayout());
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/resources/2.jpg")));
        
        add(background);        
        
        background.setLayout(new BorderLayout(0,0));
        
        background.add(menuBar,BorderLayout.NORTH);
        background.add(p, BorderLayout.CENTER);        
        
        
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/mine.png")));
               
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    
    }
    
    
    
                        
 //----------------------------------------------------//
    
	
    //-----------------------------------------------------------------//



 
    //-----------------------Related to Timer------------------------//
    
    // Starts the timer
    public void startTimer()
    {        
        stopTimer = false;
        
        timer = new Thread() {
                @Override
                public void run()
                {
                    while(!stopTimer)
                    {
                        timePassed++;
                        
                        if (timePassed==20){
                            timeisup = true;
                        }
                        // Update the time passed label.
                        if ((countdowner-timePassed)>0){
                        timePassedLabel.setText("  " + (countdowner-timePassed) + "  ");
                        }
        

                        if ((countdowner-timePassed)<1){
                            timePassedLabel.setText("  " + ("You Lost") + "  ");
                            }


        
                        //timePassedLabel.setText("  " + timePassed + "  ");

                        // Wait 1 second.
                        try{
                            sleep(1000); 
                        }
                        catch(InterruptedException ex){}
                    }
                }
            
        
        };                

       timer.start();
    }

    public int getCountdowner()
    {
        return countdowner;
    }
   
   
    public boolean gettimeisup()
    {
        return timeisup;
    }
   
    public void setlosttimePassedLabel()
    {

        timePassedLabel.setText("  " + ("You Lost") + "  ");

    }
    public void setwontimePassedLabel()
    {

        timePassedLabel.setText("  " + ("You Won") + "  ");

    }
   
    

    public void interruptTimer()
    {
        stopTimer = true;
                
        try 
        {
            if (timer!= null)
                timer.join();
        } 
        catch (InterruptedException ex) 
        {

        }        
    }
    
    public void resetTimer()
    {
        timePassed = 0;

        
        
        timePassedLabel.setText("  " + ("  LET'S GO  ") + "  ");        
    }

    public void setTimePassed(int t)
    {
        timePassed = t;
        
        
        timePassedLabel.setText("  " + timePassed + "  ");                
    }
    

    //-----------------------------------------------------------//
    
    
    public void initGame()
    {
        hideAll();
        enableAll();
    }
    
    //------------------HELPER FUNCTIONS-----------------------//

    //Makes buttons clickable
    public void enableAll()
    {
        for( int x=0 ; x<cols ; x++ ) 
        {
            for( int y=0 ; y<rows ; y++ ) 
            {
                buttons[x][y].setEnabled(true);
                //buttons[5][7].setEnabled(false);
            }
        }
    }

    //Makes buttons non-clickable
    public void disableAll()
    {
        for( int x=0 ; x<cols ; x++ ) 
        {
            for( int y=0 ; y<rows ; y++ ) 
            {
                buttons[x][y].setEnabled(false);
            }
        }
    }

    public void disableAlly(int yy)
    {
        for( int xx=0 ; xx<cols ; xx++ ) 
        {
                buttons[xx][yy].setEnabled(false);
            
        }
    }

    public void disableAllx(int xx)
    {
        
            for( int yy=0 ; yy<rows ; yy++ ) 
            {
                buttons[xx][yy].setEnabled(false);
                //buttons[xx][yy].removeMouseListener(game);
            }
        
    }

    //Resets the content of all buttons
    public void hideAll()
    {
        for( int x=0 ; x<cols ; x++ ) 
        {
            for( int y=0 ; y<rows ; y++ ) 
            {
                buttons[x][y].setText("");                
                buttons[x][y].setBackground(new Color(0,103,200));
                buttons[x][y].setIcon(tile);                
            }
        }
    }

    
    //---------------SET LISTENERS--------------------------//
    
    public void setButtonListeners(Game game)
    {
        addWindowListener(game);
    
        // Set listeners for all buttons in the grid in gameBoard
        for( int x=0 ; x<cols ; x++ ) 
        {
            for( int y=0 ; y<rows ; y++ ) 
            {
                buttons[x][y].addMouseListener(game);
            }
        }
        
        // Set listeners for menu items in menu bar
       newGame.addActionListener(game);
       Rounds.addActionListener(game);
       exit.addActionListener(game);
       create.addActionListener(game);
       loading.addActionListener(game);
       Solution.addActionListener(game);
      
       //newGame.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
       //exit.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
       //statistics.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask();νortcutKeyMask()));       
    }
    
    
    //-----------------GETTERS AND SETTERS--------------------//
    
    public JButton[][] getButtons()
    {
        return buttons;
    }
    
    public int getTimePassed()
    {
        return timePassed;
    }    


    //----------------------SET LOOK------------------------------//
    
    public static void setLook(String look)
    {
        try {

            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (look.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
        } catch (Exception ex) { }            
    }

    //-------------------------------------------------------------//
    
    public void setMines(int m)
    {
        mines = m;
        minesLabel.setText("  " + Integer.toString(m) + "/" + Integer.toString(constmines) + "  ");
    }
    
    public void incMines()
    {
        mines++;
        setMines(mines);
    }
    
    public void decMines()
    {
        mines--;
        setMines(mines);
    }
    
    public int getMines()
    {
        return mines;
    }
            
    //--------------------Related to Icons----------------------------//
    private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) 
    {
        Image img = icon.getImage();  
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);  
        return new ImageIcon(resizedImage);
    }    
    
    public void setIcons()
    {
       //---------------------Set Icons-----------------------------//

        int bOffset = buttons[0][1].getInsets().left;
        int bWidth = buttons[0][1].getWidth();
        int bHeight = buttons[0][1].getHeight();
        
        ImageIcon d;
        
        d = new ImageIcon(getClass().getResource("/resources/redmine.png"));                
        redMine =   resizeIcon(d, bWidth - bOffset, bHeight - bOffset);        

        d = new ImageIcon(getClass().getResource("/resources/mine.png"));                
        mine =   resizeIcon(d, bWidth - bOffset, bHeight - bOffset);        
        
        d = new ImageIcon(getClass().getResource("/resources/flag.png"));                
        flag =   resizeIcon(d, bWidth - bOffset, bHeight - bOffset);        
        
        d = new ImageIcon(getClass().getResource("/resources/tile.png"));                
        tile =   resizeIcon(d, bWidth - bOffset, bHeight - bOffset);        
                
        //-------------------------------------------------------//
        
    }
    
    public Icon getIconMine()
    {
        return mine;
    }

    public Icon getIconRedMine()
    {
        return redMine;
    }
    
    public Icon getIconFlag()
    {
        return flag;
    }
    
    public Icon getIconTile()
    {
        return tile;       
    }        
    
    
    //---------------------------------------------------------------------//
    public void setTextColor(JButton b)
    {
        if (b.getText().equals("1"))
            b.setForeground(Color.blue);
        else if (b.getText().equals("2"))
            b.setForeground(new Color(76,153,0));
        else if (b.getText().equals("3"))
            b.setForeground(Color.red);
        else if (b.getText().equals("4"))
            b.setForeground(new Color(153,0,0));
        else if (b.getText().equals("5"))
            b.setForeground(new Color(153,0,153));
        else if (b.getText().equals("6"))
            b.setForeground(new Color(96,96,96));
        else if (b.getText().equals("7"))
            b.setForeground(new Color(0,0,102));
        else if (b.getText().equals("8"))
            b.setForeground(new Color(153,0,76));        
    }
    //------------------------------------------------------------------------//
    
    
}
