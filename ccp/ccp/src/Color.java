public class Color {
    private String RESET = "\u001B[0m";
    private String RED = "\u001B[38;5;202m";
    private String GREEN = "\u001B[38;5;213m";
    private String YELLOW = "\u001B[38;5;186m";
    private String MAGENTA = "\u001B[38;5;97m";
    private String Color1 = "\u001B[38;5;219m";
    private String Color2 = "\u001B[38;5;81m";
    private String Color3 = "\u001B[38;5;230m";

    private static int count=0;
    private static int count2=0;

    private String[] colors = {RED, GREEN, YELLOW, MAGENTA};
    private String[] colors2 = {Color1, Color2, Color3};
    public String getRESET() {
        return RESET;
    }

    public String GenerateColor(){    
        return colors[count++%4];
    }

    public String GenerateColor2(){    
        return colors2[count2++%3];
    }


}
