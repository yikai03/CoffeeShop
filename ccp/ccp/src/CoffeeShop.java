import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoffeeShop {
    BlockingQueue<Customer> QueueOfOrder = new LinkedBlockingQueue<>(5);
    private BlockingQueue<Barista> QueueOfBaristaTakingOrder = new LinkedBlockingQueue<>();
    BlockingQueue<Customer> QueueOfTakingDrink = new LinkedBlockingQueue<>();
    BlockingQueue<Customer> QueueOfFindingSeat = new LinkedBlockingQueue<>();
    private Table[] tables;
    private BlockingQueue<Barista> QueueOfBaristaMakingDrink = new LinkedBlockingQueue<>();
    private Lock ExpressoMachine = new ReentrantLock();
    private Lock MilkMachine = new ReentrantLock();
    private Lock JuiceTap = new ReentrantLock();
    private int TotalCappu = 0, TotalEx = 0, TotalJuice = 0;

    public CoffeeShop() {
        tables = new Table[5];
        for(int i = 0; i < 5; i++) {
            tables[i] = new Table(i+1);
        }
    }
    
    public void AddCustomerToOrderQueue(Customer customer) throws InterruptedException{//Make it synchronized to prevent race condition, where customer come after goes the first of seat finding queue
        boolean success = false;
        synchronized(QueueOfOrder){
            System.out.println(customer.GetMyColor()+ "Customer " + customer.GetCustomerId() + " Arrived in Coffee Shop." + "Size of queue now : " + QueueOfOrder.size() + " Remaining Capacity: " + QueueOfOrder.remainingCapacity() + " ShareSeat: " + customer.isShareSeat() + customer.GetMyColorReset());
            success = QueueOfOrder.offer(customer, 0, TimeUnit.MILLISECONDS);
        }
            if (success) {
                NotifyBaristaToWork();
            } else {
                System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": Order Queue is too long, I am leaving. Bye" + customer.GetMyColorReset());
            }

            while(!customer.isTakenDrink()){
                synchronized(QueueOfTakingDrink){
                    QueueOfTakingDrink.wait();
                }
            }

            if(customer.isTakenDrink()){
                Customer cus = QueueOfTakingDrink.poll();
                AddCustomerToFindingSeatQueue(cus);}
                        
    }
    
    public void AddCustomerToTakingDrinkQueue(Customer customer){//For them to take their drink
        try {
            QueueOfTakingDrink.put(customer);
        } catch (InterruptedException ex) {
            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void AddCustomerToFindingSeatQueue(Customer customer){
        try {
            QueueOfFindingSeat.put(customer);  
        } catch (InterruptedException ex) {
            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
        }
        AddCustomerToSeat();        
    }       

    public void AddCustomerToSeat(){ 
        Customer customer = QueueOfFindingSeat.poll();    
        if(customer!=null){
            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId()+ ": I have taken my " + customer.getOrder() + customer.GetMyColorReset());
            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId()+ ": I am now trying to find a seat" + customer.GetMyColorReset());
            while(customer.isInSeat() == false){
                for(int i = 0; i < 5; i++){
                    try {
                            tables[i].FindSeat(customer);                                                                            
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if(customer.isInSeat() == true){
                        break;
                    }
                }
            }
        }
    }

    // public void TakeDrink() {
    //     synchronized(QueueOfTakingDrink) {
    //         while (true) {
    //             Customer customer = QueueOfTakingDrink.peek();
    //             if (customer != null && customer.isTakenDrink()) {
    //                 System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": I am now taking my " + customer.getOrder() + customer.GetMyColorReset());                    
    //                 AddCustomerToFindingSeatQueue(QueueOfTakingDrink.poll());
    //             } else {
    //                 try {
    //                     QueueOfTakingDrink.wait();
    //                 } catch (InterruptedException e) {
    //                     e.printStackTrace();
    //                 }
    //             }
    //         }
    //     }
    // }

    public boolean IsOrderQueueFull(){
        return QueueOfOrder.remainingCapacity()==0;
    }
    
//     public void Order(){
//        synchronized (QueueOfFindingSeat){
//        try {            
//            Thread.sleep(1000); //Stimulate the ordering process
//        } catch (InterruptedException ex) {
//            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
//        }
//            Customer customer = QueueOfOrder.poll();
//            if(customer != null){
//            System.out.println("Customer " + customer.GetCustomerId()+ ": I want to order " + customer.getOrder());           
//            AddCustomerToFindingSeatQueue(customer);
//            }
//        }
//    }     
     public void Work(Barista barista){        
        while(QueueOfOrder.remainingCapacity() == 5 && !barista.isClosingTime()){
            System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am sleeping" + barista.getMyColorReset());            
            WaitCustomerToCome();}
        
        if(barista.isClosingTime() & QueueOfOrder.isEmpty()){
            System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am going home" + barista.getMyColorReset());
            return;
        }
        // System.out.println("Barista " + barista.getBaristaId() + " trying to put itself in the synchronized queue of barista taking order");
         synchronized(QueueOfBaristaTakingOrder){
             try {
                //  System.out.println("Try putting barista " + barista.getBaristaId() + " in the queue of barista taking order");
                 QueueOfBaristaTakingOrder.put(barista);
                //  System.out.println("Barista " + barista.getBaristaId() + " in the queue of barista taking order");
                 Customer customer = QueueOfOrder.poll();
                //  System.out.println("Customer polled from order queue");
                 if(customer!= null){
                 System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": Hi, May I take your order? (To Customer " + customer.GetCustomerId() + ")" + barista.getMyColorReset());
                 Thread.sleep(500);//Stimulate order time                 
                 System.out.println(customer.GetMyColor()+ "Customer " + customer.GetCustomerId()+ ": Hi, I want to order " + customer.getOrder() + customer.GetMyColorReset());
                 barista.setCurrentWorkingDrink(customer.getOrder());         
                 barista.setCurrentWorkingCustomer(customer);        
                AddCustomerToTakingDrinkQueue(customer);
                //  System.out.println("Customer added to the taking drink queue");                                                                  
                 QueueOfBaristaMakingDrink.put(barista);
                    // System.out.println("Barista " + barista.getBaristaId() + " in the queue of barista making drink");
                 QueueOfBaristaTakingOrder.take();
                    // System.out.println("Barista " + barista.getBaristaId() + " removed from the queue of barista taking order");
                 }
             } catch (InterruptedException ex) {
                 Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
             }
         }
        try {
            MakeDrink();
            System.out.println("Barista " + barista.getBaristaId() + ": I am done with the order");
        } catch (InterruptedException ex) {
            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
        }                        
     }  
        
     
     public void MakeDrinkProcess(Barista barista, String machine){
        int processTime = 0;
        while(processTime < 100){
            processTime+= 25;
            System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using " + machine + " for making " + barista.getCurrentWorkingDrink() + " for Customer " + barista.getCurrentWorkingCustomerID() + " (" + processTime + "%)" + barista.getMyColorReset());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
     }

     public void MakeDrink() throws InterruptedException{
        Barista barista = null;         
        try {
            barista = QueueOfBaristaMakingDrink.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
        }
         if(barista!=null){
             if("Expresso".equals(barista.getCurrentWorkingDrink())){
                System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " trying to use expresso machine (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Expresso)" + barista.getMyColorReset());
                
                    ExpressoMachine.lock();
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using expresso machine (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Expresso)" + barista.getMyColorReset());
                    MakeDrinkProcess(barista, "Expresso Machine");
                    // synchronized(QueueOfTakingDrink){   
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": Expresso for Customer " + barista.getCurrentWorkingCustomerID() + " is ready" + barista.getMyColorReset());
                    barista.setCustomerTakenDrink(true);
                    // AddCustomerToFindingSeatQueue(barista.getCurrentWorkingCustomer());
                    TotalEx++;
                    ExpressoMachine.unlock();
                    synchronized(QueueOfTakingDrink){
                        QueueOfTakingDrink.notify();
                    }
             }
             else if("Cappuccino".equals(barista.getCurrentWorkingDrink())){
                System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " trying to use expresso machine (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Cappuccino)" + barista.getMyColorReset());
                ExpressoMachine.lock();

                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using expresso machine (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Cappuccino)" + barista.getMyColorReset());
                    MakeDrinkProcess(barista, "Expresso Machine");
                    ExpressoMachine.unlock();                

                System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " trying to use milk frother (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Cappuccino)" + barista.getMyColorReset());
                MilkMachine.lock();

                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using milk frother (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Cappuccino)" + barista.getMyColorReset());
                    MakeDrinkProcess(barista, "Milk Frother");
                    // synchronized(QueueOfTakingDrink){   
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": Cappuccino for Customer " + barista.getCurrentWorkingCustomerID() + " is ready" + barista.getMyColorReset());
                    barista.setCustomerTakenDrink(true);
                    // AddCustomerToFindingSeatQueue(barista.getCurrentWorkingCustomer());
                    TotalCappu++;
                    MilkMachine.unlock();
                    synchronized(QueueOfTakingDrink){
                        QueueOfTakingDrink.notify();
                    }
                
             }
             else if("Juice".equals(barista.getCurrentWorkingDrink())){                
                System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " trying to use juice tap (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Juice)" + barista.getMyColorReset());
                JuiceTap.lock();

                        System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using juice tap (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Juice)" + barista.getMyColorReset());
                        MakeDrinkProcess(barista, "Juice Tap");   
                        // synchronized(QueueOfTakingDrink){                                             
                        System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": Juice for Customer " + barista.getCurrentWorkingCustomerID() + " is ready" + barista.getMyColorReset());
                        barista.setCustomerTakenDrink(true);
                        // AddCustomerToFindingSeatQueue(barista.getCurrentWorkingCustomer());
                        TotalJuice++;
                        JuiceTap.unlock();
                        synchronized(QueueOfTakingDrink){
                            QueueOfTakingDrink.notify();
                        }
                    
             }else{System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " is confused" + barista.getMyColorReset());}
         }
        }
    
    synchronized void NotifyBaristaToWork(){
        notify();
    }
    
    synchronized public void WaitCustomerToCome(){
        try {
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showTotalDrink(){
        System.out.println("===========================================");
        System.out.println("Total Expresso: " + TotalEx);
        System.out.println("Total Cappuccino: " + TotalCappu);
        System.out.println("Total Juice: " + TotalJuice);
        System.out.println("===========================================");
    }

}
