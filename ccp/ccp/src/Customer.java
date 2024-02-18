import java.util.logging.Level;
import java.util.logging.Logger;

public class Customer extends Thread{
    private String Id;
    private boolean ShareSeat;
    private String Order;
    private CoffeeShop shop;
    private boolean isInSeat;
    private boolean TakenDrink;
    Color color = new Color();
    private String MyColor;
    private String MyColorReset;
    private static int CurCappu =0, CurEx =0, CurJuice=0;
    private LeaveTimer LeaveTimer;
    private boolean LeaveDueTimer;
    
    public Customer(String id, CoffeeShop shop){
        this.Id = id;
        this.ShareSeat = GenerateShareSeat();
        this.isInSeat = false;
        this.Order = GenerateOrder();
        this.shop = shop;      
        this.MyColor = color.GenerateColor();
        this.MyColorReset = color.getRESET();
        this.LeaveTimer = new LeaveTimer(shop, this);
        this.LeaveDueTimer = false;
    }
    
    public String GetCustomerId(){
        return this.Id;
    }
    
    public String GetMyColor(){
        return this.MyColor;
    }

    public String GetMyColorReset(){
        return this.MyColorReset;
    }

    public boolean isShareSeat() {
        return this.ShareSeat;
    }

    public boolean isInSeat() {
        return isInSeat;
    }

    public void setInSeat(boolean isInSeat) {
        this.isInSeat = isInSeat;
    }

    public boolean isTakenDrink() {
        return TakenDrink;
    }

    public void setTakenDrink(boolean TakenDrink) {
        this.TakenDrink = TakenDrink;
    }

    public void setShareSeat(boolean ShareSeat) {
        this.ShareSeat = ShareSeat;
    }

    public String getOrder() {
        return this.Order;
    }
    

    public void startTimer(){
        LeaveTimer.start();
    }

    public void setLeaveDueTimer(boolean LeaveDueTimer) {
        this.LeaveDueTimer = LeaveDueTimer;
    }

    public boolean getLeaveDueTimer() {
        return LeaveDueTimer;
    }
    
    public static String GenerateOrder(){
        double randomValue = Math.random();

        int MaxCappu = 14, MaxEx = 4, MaxJuice = 2;
        if ((randomValue < 0.7 && CurCappu<MaxCappu) || (CurEx==MaxEx && CurJuice == MaxJuice)) {
            CurCappu++;
            return "Cappuccino";
        } else if ((randomValue < 0.9 && CurEx < MaxEx) || (CurCappu == MaxCappu && CurJuice == MaxJuice) ) {
            CurEx++;
            return "Expresso";
        } else if(CurJuice < MaxJuice) {
            CurJuice++;
            return "Juice";
        }
        return "Test";
    }
    
    public static boolean GenerateShareSeat(){
        double randomValue = Math.random();
        return randomValue<0.8;
    }
    
    
    public void run(){        
        try {            
            shop.AddCustomerToOrderQueue(this);
        } catch (InterruptedException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }    
        
//        shop.Order();
    }
    
    
}

