
public class Barista extends Thread {
    private String Id;
    private CoffeeShop shop;
    private String CurrentWorkingDrink;
    private Customer CurrentWorkingCustomer;
    Color color = new Color();
    private String MyColor;
    private String MyColorReset;
    private boolean ClosingTime = false;
    public Barista (String id, CoffeeShop shop){
        this.Id = id;
        this.shop = shop;
        this.CurrentWorkingDrink = null;
        this.CurrentWorkingCustomer = null;
        this.MyColor = color.GenerateColor2();
        this.MyColorReset = color.getRESET();
    }

    public void setCurrentWorkingDrink(String CurrentWorkingDrink) {
        this.CurrentWorkingDrink = CurrentWorkingDrink;
    }

    public void setCurrentWorkingCustomer(Customer customer) {
        this.CurrentWorkingCustomer = customer;
    }

    public String getMyColor() {
        return MyColor;
    }

    public String getMyColorReset() {
        return MyColorReset;
    }

    public Customer getCurrentWorkingCustomer() {
        return CurrentWorkingCustomer;
    }

    public String getCurrentWorkingDrink() {
        return CurrentWorkingDrink;
    }

    public void setCustomerTakenDrink(boolean TakenDrink) {
        this.CurrentWorkingCustomer.setTakenDrink(TakenDrink);
    }

    public String getCurrentWorkingCustomerID() {
        return CurrentWorkingCustomer.GetCustomerId();
    }
        
    public String getBaristaId() {
        return Id;
    }
    
    public void setClosingTime(boolean ClosingTime) {
        this.ClosingTime = ClosingTime;
    }

    public boolean isClosingTime() {
        return ClosingTime;
    }

    public void run(){
        while(!ClosingTime){
            shop.Work(this);
        }
    }
}
