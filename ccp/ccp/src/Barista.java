public class Barista extends Thread {
    private String Id;//Barista ID
    private CoffeeShop shop;//The shop the barista is in
    private String CurrentWorkingDrink;//The drink the barista is currently making
    private Customer CurrentWorkingCustomer;//The customer the barista is currently serving
    Color color = new Color();//Color class for color output
    private String MyColor;//Color for output
    private String MyColorReset;//Reset color
    private boolean ClosingTime = false;//Check if the shop is closing
    public Barista (String id, CoffeeShop shop){//Constructor
        this.Id = id;
        this.shop = shop;
        this.CurrentWorkingDrink = null;
        this.CurrentWorkingCustomer = null;
        this.MyColor = color.GenerateColor2();
        this.MyColorReset = color.getRESET();
    }

    public void setCurrentWorkingDrink(String CurrentWorkingDrink) {//Set the drink the barista is currently making
        this.CurrentWorkingDrink = CurrentWorkingDrink;
    }

    public void setCurrentWorkingCustomer(Customer customer) {//Set the customer the barista is currently serving
        this.CurrentWorkingCustomer = customer;
    }

    public String getMyColor() {//Get the color for output
        return MyColor;
    }

    public String getMyColorReset() {//Get the reset color
        return MyColorReset;
    }

    public Customer getCurrentWorkingCustomer() {//Get the customer the barista is currently serving
        return CurrentWorkingCustomer;
    }

    public String getCurrentWorkingDrink() {//Get the drink the barista is currently making
        return CurrentWorkingDrink;
    }

    public void setCustomerTakenDrink(boolean TakenDrink) {//Set the customer has taken the drink
        this.CurrentWorkingCustomer.setTakenDrink(TakenDrink);
    }

    public String getCurrentWorkingCustomerID() {//Get the customer ID the barista is currently serving
        return CurrentWorkingCustomer.GetCustomerId();
    }
        
    public String getBaristaId() {//Get the barista ID
        return Id;
    }
    
    public void setClosingTime(boolean ClosingTime) {//Set the shop is closing
        this.ClosingTime = ClosingTime;
    }

    public boolean isClosingTime() {//Check if the shop is closing
        return ClosingTime;
    }

    public void run(){//Run the barista
        while(!ClosingTime){//While the shop is not closing
            shop.Work(this);//The barista works
        }
    }
}
