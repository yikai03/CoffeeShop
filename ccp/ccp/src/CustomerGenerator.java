import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerGenerator extends Thread{
    CoffeeShop shop;
    boolean Closing;
    int CustomerCount = 0, MaxCustomer = 20;
    
    //Function that randomize customer order and seat sharing
    
    public CustomerGenerator(CoffeeShop shop){
        this.shop = shop;
    }
    
    public void run(){    
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(CustomerGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(!Closing && CustomerCount<MaxCustomer){
        Customer customer = new Customer(Integer.toString(CustomerCount+1), shop);//Change the argument to random
        customer.start();
        
        synchronized (this){
            CustomerCount++;
        }
        
            try {
                TimeUnit.SECONDS.sleep((long)(Math.random()*2));
            } catch (InterruptedException ex) {
                Logger.getLogger(CustomerGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

