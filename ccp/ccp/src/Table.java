import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Table {
    CoffeeShop shop;
    private int TableNumber, Chair1, Chair2;
    private ReentrantLock chair1Lock;
    private ReentrantLock chair2Lock;
    private ReentrantLock tableLock;
    private boolean chair1IsOccupied, chair2IsOccupied;
    
    public Table (int tablenumber, CoffeeShop shop){
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
        int processTime = 0;
        while(processTime < 100){
            processTime+= 25;
            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": I am drinking my " + customer.getOrder() + " (" + processTime + "%)" + customer.GetMyColorReset());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println(customer.GetMyColor()+ "Customer " + customer.GetCustomerId() + ": I am done drinking my " + customer.getOrder() + customer.GetMyColorReset());
     }
    
    public void FindSeat(Customer customer) throws InterruptedException{
                if(customer!=null && customer.isShareSeat()){
                    if(chair1Lock.tryLock(1000, TimeUnit.MILLISECONDS) && chair1IsOccupied == false){
                        customer.setInSeat(true);
                        shop.QueueOfAccordingSeat.poll();
                        chair1IsOccupied = true;
                        try{
                            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": I found a seat in Table " + TableNumber + " in Chair " + Chair1 + customer.GetMyColorReset());
                            DrinkDrinkProcess(customer);
                            //Drinking process

                            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": I am leaving Table " + TableNumber + " in Chair " + Chair1 + customer.GetMyColorReset());
                            // shop.IncrementLock.lock();
                            synchronized(shop.QueueOfOrder){
                            shop.CustomerInShop--;
                            shop.LeftCustomer++;}
                            // shop.IncrementLock.unlock();
                        }                
                        finally{
                            chair1IsOccupied = false;
                            chair1Lock.unlock();
                        }
                    }
                    else if(chair2Lock.tryLock(1000, TimeUnit.MILLISECONDS) && chair2IsOccupied == false){
                        customer.setInSeat(true);
                        shop.QueueOfAccordingSeat.poll();
                        chair2IsOccupied = true;
                        try{
                            System.out.println(customer.GetMyColor()+ "Customer " + customer.GetCustomerId() + ": I found a seat in Table " + TableNumber + " in Chair " + Chair2 + customer.GetMyColorReset());
                            DrinkDrinkProcess(customer);
                            //Drinking process
                            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": I am leaving Table " + TableNumber + " in Chair " + Chair2 + customer.GetMyColorReset());
                            // shop.IncrementLock.lock();
                            synchronized(shop.QueueOfOrder){
                            shop.CustomerInShop--;
                            shop.LeftCustomer++;}
                            // shop.IncrementLock.unlock();
                        }          
                        finally{
                            chair2IsOccupied = false;
                            chair2Lock.unlock();
                        }
                    }
                }
                else if(customer!=null && !customer.isShareSeat() && !chair1IsOccupied && !chair2IsOccupied){
                    if(tableLock.tryLock(1000, TimeUnit.MILLISECONDS)){
                        customer.setInSeat(true);   
                        shop.QueueOfAccordingSeat.poll();                     
                        chair1IsOccupied = true;
                        chair2IsOccupied = true;
                        System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": I found a seat in Table " + TableNumber + " and I do not want to share table" + customer.GetMyColorReset());
                        try {
                            DrinkDrinkProcess(customer);
                            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": I am leaving Table " + TableNumber + customer.GetMyColorReset());
                            // shop.IncrementLock.lock();
                            synchronized(shop.QueueOfOrder){
                            shop.CustomerInShop--;
                            shop.LeftCustomer++;}
                            // shop.IncrementLock.unlock();
                        }
                        finally{
                            chair1IsOccupied = false;
                            chair2IsOccupied = false;
                            tableLock.unlock();
                        }
                    }
                }            
        }
    
}

