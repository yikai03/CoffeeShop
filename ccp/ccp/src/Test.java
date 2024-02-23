import java.util.ArrayList;
//A test case class to test the status of the threads, not really meant for testing the functionality of the program
public class Test extends Thread{
    CoffeeShop shop;
    Barista b1;
    Barista b2;
    Barista b3;
    ArrayList<Customer> customers;//List of customers

    public Test(CoffeeShop shop, Barista b1, Barista b2, Barista b3){//Constructor
        this.shop = shop;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        customers = new ArrayList<Customer>(); //Create a list of customers
    }

    public void run(){        
        while(shop.LeftCustomer < 20){//While all the customer left the shop
            try{
                Thread.sleep(1000);//Wait for 1 second
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

        System.out.println("===================================================================================================================");//Print out the status of the threads
        System.out.println("Barista " + b1.getBaristaId() + " status: "+b1.getState());
        System.out.println("Barista " + b2.getBaristaId() + " status: "+b2.getState());
        System.out.println("Barista " + b3.getBaristaId() + " status: "+b3.getState());

        for(int i = 0; i < customers.size(); i++){
            System.out.println("Customer " + customers.get(i).GetCustomerId() + " status: "+customers.get(i).getState());
        }
    }
}
