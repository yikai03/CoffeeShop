// Purpose: This class is used to generate colors for the output of the program.
public class Color {
    private String RESET = "\u001B[0m";//Reset color
    private String RED = "\u001B[38;5;202m";//Red color
    private String GREEN = "\u001B[38;5;213m";//Green color
    private String YELLOW = "\u001B[38;5;186m";//Yellow color
    private String MAGENTA = "\u001B[38;5;97m";//Magenta color
    private String Color1 = "\u001B[38;5;219m";//Color 1
    private String Color2 = "\u001B[38;5;81m";//Color 2    
    private String Color3 = "\u001B[38;5;230m";//Color 3

    private static int count=0;//Count for color
    private static int count2=0;//Count for color 2

    private String[] colors = {RED, GREEN, YELLOW, MAGENTA};//Color array
    private String[] colors2 = {Color1, Color2, Color3};//Color 2 array
    public String getRESET() {//Get reset color
        return RESET;
    }

    public String GenerateColor(){//Generate color
        return colors[count++%4];//Return color
    }

    public String GenerateColor2(){    
        return colors2[count2++%3];//Return color 2
    }


}
