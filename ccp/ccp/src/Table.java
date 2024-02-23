import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Table {
    CoffeeShop shop;
    private int TableNumber, Chair1, Chair2;//Table number, chair number
    private ReentrantLock chair1Lock;//Lock for chair 1
    private ReentrantLock chair2Lock;//Lock for chair 2
    private ReentrantLock tableLock;//Lock for the table
    private boolean chair1IsOccupied, chair2IsOccupied;//Check if the chair is occupied
    
    public Table (int tablenumber, CoffeeShop shop){//Constructor
        this.TableNumber = tablenumber;
        this.Chair1 = 1;
        this.Chair2 = 2;
        this.chair1IsOccupied = false;
        this.chair2IsOccupied = false;        
        this.chair1Lock = new ReentrantLock();
        this.chair2Lock = new ReentrantLock();                
        this.tableLock = new ReentrantLock();
        this.shop = shop;
    }

    public int getTableNumber() {
        return TableNumber;
    }

    public int getChair1() {
        return Chair1;
    }

    public int getChair2() {
        return Chair2;
    }
    public void DrinkDrinkProcess(Customer customer){
        int processTime = 25;//Reset Process time for drinking
        System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + 
        ": I am starting to drink my " + customer.getOrder() + customer.GetMyColorReset());
        while(processTime <= 100){
            //Print out the drinking process
            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + 
            ": I am drinking my " + customer.getOrder() + " (" + processTime + "%)" + customer.GetMyColorReset());
            processTime+= 25;//Increment process time
            try {
                Thread.sleep((long) (350 + Math.random() * 350));//Randomly generate the drinking time from 3 second to 6 second                
            } catch (InterruptedException e) {                
                e.printStackTrace();
            }
        }
        //Print out that the customer is done drinking
        System.out.println(customer.GetMyColor()+ "Customer " + customer.GetCustomerId() + 
        ": I am done drinking my " + customer.getOrder() + customer.GetMyColorReset());
     }
    
    public void FindSeat(Customer customer) throws InterruptedException{
        if(customer!=null && customer.isShareSeat()){//If the customer want to share seat
            if(chair1Lock.tryLock(1000, TimeUnit.MILLISECONDS) && chair1IsOccupied == false){//If chair 1 is not occupied
                customer.setInSeat(true);//Set the customer in seat
                shop.QueueOfAccordingSeat.poll();//Remove the customer from the queue
                chair1IsOccupied = true;//Set the chair 1 is occupied
                try{
                    //Print out that the customer found a seat
                    System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + 
                    ": I found a seat in Table " + TableNumber + " in Chair " + Chair1 + customer.GetMyColorReset());
                    DrinkDrinkProcess(customer);//Drinking process
                    //Print out that the customer is leaving the seat
                    System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + 
                    ": I am leaving Table " + TableNumber + " in Chair " + Chair1 + customer.GetMyColorReset());                        
                    synchronized(shop.QueueOfOrder){
                        shop.CustomerInShop--;//Decrement the customer in shop
                        shop.LeftCustomer++;//Increment the left customer
                    }
                }                
                finally{
                    chair1IsOccupied = false;//Set the chair 1 is not occupied
                    chair1Lock.unlock();//Unlock the chair 1
                }
            }
            else if(chair2Lock.tryLock(1000, TimeUnit.MILLISECONDS) && chair2IsOccupied == false){//If chair 2 is not occupied
                customer.setInSeat(true);//Set the customer in seat
                shop.QueueOfAccordingSeat.poll();//Remove the customer from the queue
                chair2IsOccupied = true;//Set the chair 2 is occupied
                try{
                    System.out.println(customer.GetMyColor()+ "Customer " + customer.GetCustomerId() + 
                    ": I found a seat in Table " + TableNumber + " in Chair " + Chair2 + customer.GetMyColorReset());
                    DrinkDrinkProcess(customer);//Drinking process
                    //Print out that the customer is leaving the seat                            
                    System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + 
                    ": I am leaving Table " + TableNumber + " in Chair " + Chair2 + customer.GetMyColorReset());                            
                    synchronized(shop.QueueOfOrder){
                        shop.CustomerInShop--;//Decrement the customer in shop
                        shop.LeftCustomer++;//Increment the left customer
                    }                            
                }          
                finally{
                    chair2IsOccupied = false;//Set the chair 2 is not occupied
                    chair2Lock.unlock();//Unlock the chair 2
                }
            }
        }
        else if(customer!=null && !customer.isShareSeat() && !chair1IsOccupied && !chair2IsOccupied){//If the customer do not want to share seat
            if(tableLock.tryLock(1000, TimeUnit.MILLISECONDS)){//If the table is not occupied
                customer.setInSeat(true);   //Set the customer in seat
                shop.QueueOfAccordingSeat.poll();                     //Remove the customer from the queue
                chair1IsOccupied = true;//Set the chair 1 is occupied
                chair2IsOccupied = true;//Set the chair 2 is occupied
                //Print out that the customer found a seat
                System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + 
                ": I found a seat in Table " + TableNumber + " and I do not want to share table" + customer.GetMyColorReset());
                try {
                    DrinkDrinkProcess(customer);//Drinking process
                    //Print out that the customer is leaving the seat
                    System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + 
                    ": I am leaving Table " + TableNumber + customer.GetMyColorReset());                            
                    synchronized(shop.QueueOfOrder){//Synchronize the queue of order
                        shop.CustomerInShop--;//Decrement the customer in shop
                        shop.LeftCustomer++;//Increment the left customer
                    }                            
                }
                finally{
                    chair1IsOccupied = false;//Set the chair 1 is not occupied
                    chair2IsOccupied = false;//Set the chair 2 is not occupied
                    tableLock.unlock();//Unlock the table
                }
            }
        }            
    }    
}

