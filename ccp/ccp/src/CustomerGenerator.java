import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerGenerator extends Thread{
    CoffeeShop shop;
    boolean Closing;//Check if the shop is closing
    int CustomerCount = 0, MaxCustomer = 20;//20 customer will come to the shop
    Test test;
        
    public CustomerGenerator(CoffeeShop shop, Test test){
        this.shop = shop;
        this.test = test;
    }
    
    public int getMaxCustomer(){
        return MaxCustomer;
    }

    public void run(){    
        try {
            Thread.sleep(500);//Wait for the shop to open
        } catch (InterruptedException ex) {
            Logger.getLogger(CustomerGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(!Closing && CustomerCount<MaxCustomer){
            Customer customer = new Customer(Integer.toString(CustomerCount+1), shop);//Create customer with different ID
            test.customers.add(customer);//Add customer to the list for testing purpose
            customer.start();//Start the customer thread
            
            synchronized (this){//Synchronize the customer generator
                CustomerCount++;//Increment the customer count
            }
            
            try {
                //Randomly generate the time for the next customer to come
                TimeUnit.SECONDS.sleep((long)(Math.random()*2));
            } catch (InterruptedException ex) {
                Logger.getLogger(CustomerGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

