import java.util.logging.Level;
import java.util.logging.Logger;

public class Customer extends Thread{
    private String Id;//Customer ID
    private boolean ShareSeat;//Check if the customer is willing to share seat
    private String Order;//Customer order
    private CoffeeShop shop;//The shop the customer is in
    private boolean isInSeat;//Check if the customer is in seat
    private boolean TakenDrink;//Check if the customer has taken the drink
    Color color = new Color();//Color class for color output
    private String MyColor;//Color for output
    private String MyColorReset;//Reset color
    private static int CurCappu =0, CurEx =0, CurJuice=0;//Current number of Cappuccino, Expresso, and Juice
    private LeaveTimer LeaveTimer;//Leave timer
    private boolean LeaveDueTimer;//Check if the customer is due to leave
    
    public Customer(String id, CoffeeShop shop){//Constructor
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
    
    public String GetCustomerId(){//Get customer ID
        return this.Id;
    }
    
    public String GetMyColor(){//Get customer color
        return this.MyColor;
    }

    public String GetMyColorReset(){//Get reset color
        return this.MyColorReset;
    }

    public boolean isShareSeat() {//Check if the customer is willing to share seat
        return this.ShareSeat;
    }

    public boolean isInSeat() {//Check if the customer is in seat
        return isInSeat;    
    }

    public void setInSeat(boolean isInSeat) {//Set the customer in seat
        this.isInSeat = isInSeat;
    }

    public boolean isTakenDrink() {//Check if the customer has taken the drink
        return TakenDrink;
    }

    public void setTakenDrink(boolean TakenDrink) {//Set the customer has taken the drink
        this.TakenDrink = TakenDrink;
    }

    public void setShareSeat(boolean ShareSeat) {//Set the customer is willing to share seat
        this.ShareSeat = ShareSeat;
    }

    public String getOrder() {//Get the customer order
        return this.Order;
    }
    
    public void startTimer(){//Start the leave timer
        LeaveTimer.start();
    }

    public void setLeaveDueTimer(boolean LeaveDueTimer) {//Set the customer is due to leave
        this.LeaveDueTimer = LeaveDueTimer;
    }

    public boolean getLeaveDueTimer() {//Get if the customer is due to leave
        return LeaveDueTimer;
    }
    
    public static String GenerateOrder(){//Randomly generate the order
        double randomValue = Math.random();//Random value
        int MaxCappu = 14, MaxEx = 4, MaxJuice = 2;//Maximum number of Cappuccino, Expresso, and Juice
        //If the random value is less than 0.7 and the current number of Cappuccino is less than the maximum number of Cappuccino
        if ((randomValue < 0.7 && CurCappu<MaxCappu) || 
        (randomValue >= 0.7 && randomValue < 0.9 && CurEx==MaxEx && CurCappu < MaxCappu) || 
        (randomValue >= 0.9 && CurJuice == MaxJuice && CurCappu < MaxCappu)) {            
            CurCappu++;//Increment the current number of Cappuccino            
            return "Cappuccino";//Return Cappuccino as Customer order
        }
        //If the random value is less than 0.9 and the current number of Expresso is less than the maximum number of Expresso 
        else if ((randomValue < 0.9 && CurEx < MaxEx) || (CurCappu == MaxCappu && CurJuice == MaxJuice)) {
            CurEx++;//Increment the current number of Expresso
            return "Expresso";//Return Expresso as Customer order
        } else if(CurJuice < MaxJuice) {//If the current number of Juice is less than the maximum number of Juice
            CurJuice++;//Increment the current number of Juice
            return "Juice";//Return Juice as Customer order
        }
        return "I am full";//Return I am full if the customer is full
    }
    
    public static boolean GenerateShareSeat(){//Randomly generate if the customer is willing to share seat
        double randomValue = Math.random();//Random value
        return randomValue<0.8;//Return true if the random value is less than 0.8
    }
    
    
    public void run(){        
        try {            
            shop.AddCustomerToOrderQueue(this);//Add customer to the order queue
        } catch (InterruptedException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
}