import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;

//include constructor to set a background image
public class Grid extends JComponent implements KeyListener, MouseListener
{
  private Cell[][] cells;
  private JFrame frame;
  private int lastKeyPressed;
  private Location lastLocationClicked;
  private Color lineColor;
  private Color cellColor;
  private Image background = null;
  
  
  public Grid(int numRows, int numCols)
  {
    init(numRows, numCols, null, null);
  }
  
  // can be used to provided a background color (paint all the cells) 
  public Grid(int numRows, int numCols, Color bcolor)
  {
    init(numRows, numCols, bcolor, null);
  }
  
  // can be used to abjust the background color (cell) and line color (in between)
  public Grid(int numRows, int numCols, Color bcolor, Color lcolor)
  {
    init(numRows, numCols, bcolor, lcolor);
  }
  
  public Grid(String imageFileName, int numRows, int numCols)
  {
    // image = loadImage(imageFileName);
    
    background = new ImageIcon(getClass().getResource(imageFileName)).getImage();
    
    init(numRows, numCols, null, null);
    //showImage(image);
    //setTitle(imageFileName);
  }
  
  private BufferedImage loadImage(String imageFileName)
  {
    URL url = getClass().getResource(imageFileName);
    if (url == null)
      throw new RuntimeException("cannot find file:  " + imageFileName);
    try
    {
      return ImageIO.read(url);
    }
    catch(IOException e)
    {
      throw new RuntimeException("unable to read from file:  " + imageFileName);
    }
  }
  
  public int getNumRows()
  {
    return cells.length;
  }
  
  public int getNumCols()
  {
    return cells[0].length;
  }
  
  private void init(int numRows, int numCols, Color bcolor, Color lcolor)
  {
    lastKeyPressed = -1;
    lastLocationClicked = null;
    
    //set the cell color to the provided bcolor or to the default BLACK 
    cellColor = (bcolor != null)? bcolor: Color.BLACK;
    
    lineColor = lcolor;
    
    cells = new Cell[numRows][numCols];
    for (int row = 0; row < numRows; row++)
    {
      for (int col = 0; col < numCols; col++)
        cells[row][col] = new Cell(cellColor);
    }
    
    frame = new JFrame("Grid");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addKeyListener(this);
    
    int cellSize = Math.max(Math.min(500 / getNumRows(), 500 / getNumCols()), 1);    
    setPreferredSize(new Dimension(cellSize * numCols, cellSize * numRows));
    addMouseListener(this);
    frame.getContentPane().add(this);
    
    frame.pack();
    frame.setVisible(true);
  }
  
  private void showImage(BufferedImage image)
  {
    for (int row = 0; row < getNumRows(); row++)
    {
      for (int col = 0; col < getNumCols(); col++)
      {
        int x = col * image.getWidth() / getNumCols();
        int y = row * image.getHeight() / getNumRows();
        int c = image.getRGB(x, y);
        int red = (c & 0x00ff0000) >> 16;
        int green = (c & 0x0000ff00) >> 8;
        int blue = c & 0x000000ff;
        cells[row][col].setColor(new Color(red, green, blue));
      }
    }
    repaint();
  }
  
  private int getCellSize()
  {
    int cellWidth = getWidth() / getNumCols();
    int cellHeight = getHeight() / getNumRows();
    return Math.min(cellWidth, cellHeight);
  }
  
  public void keyPressed(KeyEvent e)
  {
    lastKeyPressed = e.getKeyCode();
  }
  
  public void keyReleased(KeyEvent e)
  {
    //ignored
  }
  
  public void keyTyped(KeyEvent e)
  {
    //ignored
  }
  
  public void mousePressed(MouseEvent e)
  {
    int cellSize = getCellSize();
    int row = e.getY() / cellSize;
    if (row < 0 || row >= getNumRows())
      return;
    int col = e.getX() / cellSize;
    if (col < 0 || col >= getNumCols())
      return;
    lastLocationClicked = new Location(row, col);
  }
  
  public void mouseReleased(MouseEvent e)
  {
    //ignore
  }
  
  public void mouseClicked(MouseEvent e)
  {
    //ignore
  }
  
  public void mouseEntered(MouseEvent e)
  {
    //ignore
  }
  
  public void mouseExited(MouseEvent e)
  {
    //ignore
  }
  
  private static java.awt.Color toJavaColor(Color color)
  {
    return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
  }
  
  public void paintComponent(Graphics g)
  {
    if (background!=null)
      g.drawImage(background, 0, 0,frame.getWidth(), frame.getHeight(), null);
    
    for (int row = 0; row < getNumRows(); row++)
    {
      for (int col = 0; col < getNumCols(); col++)
      {
        Location loc = new Location(row, col);
        Cell cell = cells[loc.getRow()][loc.getCol()];
        
        int cellSize = getCellSize();
        
        int x = col * cellSize;
        int y = row * cellSize; 
        if (background==null) {
          Color color = cell.getColor();
          g.setColor(toJavaColor(color));
          
          g.fillRect(x, y, cellSize, cellSize);
        }
        
        String imageFileName = cell.getImageFileName();
        if (imageFileName != null)
        {
          URL url = getClass().getResource(imageFileName);
          if (url == null)
            System.out.println("File not found:  " + imageFileName);
          else
          {
            
            Image image = new ImageIcon(url).getImage();
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            int max;
            if (width > height)
            {
              int drawHeight = cellSize * height / width;
              g.drawImage(image, x, y + (cellSize - drawHeight) / 2, cellSize, drawHeight, null);
            }
            else
            {
              int drawWidth = cellSize * width / height;
              g.drawImage(image, x + (cellSize - drawWidth) / 2, y, drawWidth, cellSize, null);
            }
          }
        }
        
        if (lineColor != null)
        {
          g.setColor(toJavaColor(lineColor));
          g.drawRect(x, y, cellSize, cellSize);
        }
      }    
    }
  }
  
  public void setTitle(String title)
  {
    frame.setTitle(title);
  }
  
  public boolean isValid(Location loc)
  {
    int row = loc.getRow();
    int col = loc.getCol();
    return 0 <= row && row < getNumRows() && 0 <= col && col < getNumCols();
  }
  
  public void setColor(Location loc, Color color)
  {
    if (!isValid(loc))
      throw new RuntimeException("cannot set color of invalid location " + loc + " to color " + color);
    cells[loc.getRow()][loc.getCol()].setColor(color);
    repaint();
  }
  
  public Color getColor(Location loc)
  {
    if (!isValid(loc))
      throw new RuntimeException("cannot get color from invalid location " + loc);
    return cells[loc.getRow()][loc.getCol()].getColor();
  }
  
  public void setImage(Location loc, String imageFileName)
  {
    if (!isValid(loc))
      throw new RuntimeException("cannot set image for invalid location " + loc + " to \"" + imageFileName + "\"");
    cells[loc.getRow()][loc.getCol()].setImageFileName(imageFileName);
    repaint();
  }
  
  public String getImage(Location loc)
  {
    if (!isValid(loc))
      throw new RuntimeException("cannot get image for invalid location " + loc);
    return cells[loc.getRow()][loc.getCol()].getImageFileName();
  }
  
  public static void pause(int milliseconds)
  {
    try
    {
      Thread.sleep(milliseconds);
    }
    catch(Exception e)
    {
      //ignore
    }
  }
  
  //returns -1 if no key pressed since last call.
  //otherwise returns the code for the last key pressed.
  public int checkLastKeyPressed()
  {
    int key = lastKeyPressed;
    lastKeyPressed = -1;
    return key;
  }
  
  //returns null if no location clicked since last call.
  public Location checkLastLocationClicked()
  {
    Location loc = lastLocationClicked;
    lastLocationClicked = null;
    return loc;
  }
  
  public void load(String imageFileName)
  {
    showImage(loadImage(imageFileName));
    setTitle(imageFileName);
  }
  
  public void save(String imageFileName)
  {
    try
    {
      BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
      paintComponent(bi.getGraphics());
      int index = imageFileName.lastIndexOf('.');
      if (index == -1)
        throw new RuntimeException("invalid image file name:  " + imageFileName);
      ImageIO.write(bi, imageFileName.substring(index + 1), new File(imageFileName));
    }
    catch(IOException e)
    {
      throw new RuntimeException("unable to save image to file:  " + imageFileName);
    }
  }
  
  public void setLineColor(Color color)
  {
    lineColor = color;
    repaint();
  } 
  
  public void showMessageDialog(String message)
  {
    JOptionPane.showMessageDialog(this, message);
  }
  
  public String showInputDialog(String message)
  {
    return JOptionPane.showInputDialog(this, message);
  }
}