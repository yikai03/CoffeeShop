import java.lang.reflect.Array;
import java.util.ArrayList;

public class Test extends Thread{
    CoffeeShop shop;
    Barista b1;
    Barista b2;
    Barista b3;
    ArrayList<Customer> customers;

    public Test(CoffeeShop shop, Barista b1, Barista b2, Barista b3){
        this.shop = shop;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        customers = new ArrayList<Customer>(); 
    }

    public void run(){        
        while(shop.LeftCustomer < 20){
            try{
                System.out.println("test still in while loop, customer in shop" + shop.LeftCustomer);
                Thread.sleep(1000);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("===================================================================================================================");
        System.out.println(b1.getBaristaId() + " status: "+b1.getState());
        System.out.println(b2.getBaristaId() + " status: "+b2.getState());
        System.out.println(b3.getBaristaId() + " status: "+b3.getState());

        // // cg
        // System.out.println("Customer Generator: "+cg.getState());

        for(int i = 0; i < customers.size(); i++){
            System.out.println(customers.get(i).GetCustomerId() + " status: "+customers.get(i).getState());
        }
    }
}
