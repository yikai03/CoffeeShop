import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoffeeShop {
    BlockingQueue<Customer> QueueOfOrder = new LinkedBlockingQueue<>(5);//Queue of order
    BlockingQueue<Barista> QueueOfBaristaTakingOrder = new LinkedBlockingQueue<>();//Queue of barista taking order
    BlockingQueue<Customer> QueueOfFindingSeat = new LinkedBlockingQueue<>();//Queue of finding seat
    BlockingQueue<Customer> QueueOfAccordingSeat = new LinkedBlockingQueue<>(1);//Queue of according seat
    private Table[] tables;//Tables
    BlockingQueue<Barista> QueueOfBaristaMakingDrink = new LinkedBlockingQueue<>();//Queue of barista making drink
    private Lock ExpressoMachine = new ReentrantLock();//Lock for expresso machine
    private Lock MilkMachine = new ReentrantLock();//Lock for milk frother
    private Lock JuiceTap = new ReentrantLock();//Lock for juice tap
    private int TotalCappu = 0, TotalEx = 0, TotalJuice = 0;//Total number of Cappuccino, Expresso, and Juice
    private int CappuPrice = 9, ExPrice = 6, JuicePrice = 7;//Price of Cappuccino, Expresso, and Juice
    int CustomerInShop = 0;//Number of customer in shop
    int LeftCustomer = 0;//Number of customer left
    Lock IncrementLock = new ReentrantLock();//Lock for incrementing customer in shop and left customer

    public CoffeeShop() {//Constructor
        tables = new Table[5];//Create 5 tables
        for(int i = 0; i < 5; i++) {//For each table
            tables[i] = new Table(i+1, this);//Create a table with table number
        }
    }
    
    public void AddCustomerToOrderQueue(Customer customer) throws InterruptedException{
        boolean success = false;//Check if the customer is successfully added to the queue
        synchronized(QueueOfOrder){//Synchronize the queue of order
            //Print out that the customer arrived in the coffee shop
            System.out.println(customer.GetMyColor()+ "Customer " + customer.GetCustomerId() + " Arrived in Coffee Shop." + "Size of queue now : " + QueueOfOrder.size() + " Remaining Capacity: " + QueueOfOrder.remainingCapacity() + " ShareSeat: " + customer.isShareSeat() + customer.GetMyColorReset());
            success = QueueOfOrder.offer(customer, 0, TimeUnit.MILLISECONDS);//Try to add the customer to the queue            
            CustomerInShop++;//Increment the customer in shop            
        }
        if (success) {//If the customer is successfully added to the queue
            NotifyBaristaToWork();//Notify the barista to work
            customer.startTimer();//Start the leave timer
        } else {//If the customer is not successfully added to the queue
            //Print out that the customer is leaving the coffee shop
            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": Order Queue is too long, I am leaving. Bye" + customer.GetMyColorReset());            
            synchronized(QueueOfOrder){//Synchronized the queue of order
                CustomerInShop--;//Decrement the customer in shop
                LeftCustomer++;//Increment the left customer
            }
            return;
        }

        while(true){//While true
            while(!customer.isTakenDrink() && !customer.getLeaveDueTimer()){//While the customer has not taken the drink and not due to leave
                synchronized(QueueOfFindingSeat){//Synchronized the queue of finding seat
                    QueueOfFindingSeat.wait();//Wait for the customer to take the drink
                }
            }

            if(customer.getLeaveDueTimer()){//If the customer is due to leave
                break;//Break the loop
            }

            if(customer.isTakenDrink()){//If the customer has taken the drink
                AddCustomerToFindingSeatQueue(customer);//Add the customer to the queue of finding seat
                break;//Break the loop
            }
        }                        
    }

    public void AddCustomerToFindingSeatQueue(Customer customer){
        try {
            QueueOfFindingSeat.put(customer);//Add the customer to the queue of finding seat
        } catch (InterruptedException ex) {
            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
        }
        AddCustomerToSeat();//Add the customer to the seat
    }       

    public void AddCustomerToSeat(){ 
        synchronized(QueueOfAccordingSeat){//Synchronize the queue of according seat
            Customer cus = QueueOfFindingSeat.poll();//Poll the customer from the queue of finding seat
            try {
                QueueOfAccordingSeat.put(cus);//Put the customer to the queue of according seat
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Customer customer = QueueOfAccordingSeat.peek();//Peek the customer from the queue of according seat
        if(customer!=null){//If the customer is not null
            //Print out that the customer is trying to find a seat
            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId()+ ": I have taken my " + customer.getOrder() + customer.GetMyColorReset());
            System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId()+ ": I am now trying to find a seat" + customer.GetMyColorReset());
            while(customer.isInSeat() == false){//While the customer is not in seat
                for(int i = 0; i < 5; i++){//For each table
                    try {
                            tables[i].FindSeat(customer);//Find a seat                                        
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(customer.isInSeat() == true){//If the customer is in seat                     
                        break;//Break the loop
                    }
                }

                //If customer who did not want to share table, cannnot find a seat after search all the table. He will be willing to share the table
                if(customer.isInSeat() == false && customer.isShareSeat() == false){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    customer.setShareSeat(true);//Set the customer is willing to share seat
                    //Print out that the customer is willing to share the table
                    System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + ": I am now willing to share the table" + customer.GetMyColorReset());
                }
                //Since if customer thread will try lock a table for 1 second, after 5 table, it will be 5 second, add on the 1 second sleep, 6 will be total. 
                //Hence, meeting the requirement: After certain amount of time. 
            }
        }
    }

    public boolean IsOrderQueueFull(){
        return QueueOfOrder.remainingCapacity()==0;
    }
    
    public void Work(Barista barista){        
        while(QueueOfOrder.remainingCapacity() == 5 && !barista.isClosingTime()){//While the queue of order is full and not closing time
            System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am sleeping" + barista.getMyColorReset());//Print out that the barista is sleeping
            WaitCustomerToCome();//Wait for the customer to come
        }
        
        if(barista.isClosingTime() /*& QueueOfOrder.isEmpty()*/){//If it is closing time
            System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am going home" + barista.getMyColorReset());//Print out that the barista is going home
            return;
        }else if(LeftCustomer == 20){//If all the customer left
            System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am going home" + barista.getMyColorReset());//Print out that the barista is going home
            return;
        }

        Customer customer = null;//The customer
        synchronized(QueueOfBaristaTakingOrder){
            try {
                QueueOfBaristaTakingOrder.put(barista);//Put the barista to the queue of barista taking order
                customer = QueueOfOrder.poll();//Poll the customer from the queue of order
                if(customer!= null){//If the customer is not null
                    //Print out that the barista is taking the order
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": Hi, May I take your order? (To Customer " + customer.GetCustomerId() + ")" + barista.getMyColorReset());
                    Thread.sleep(500);//Stimulate order time 
                    //Print out that the barista is taking the order                
                    System.out.println(customer.GetMyColor()+ "Customer " + customer.GetCustomerId()+ ": Hi, I want to order " + customer.getOrder() + customer.GetMyColorReset());
                    barista.setCurrentWorkingDrink(customer.getOrder());//Set the current working drink
                    barista.setCurrentWorkingCustomer(customer);//Set the current working customer
                    QueueOfBaristaMakingDrink.put(barista);//Put the barista to the queue of barista making drink
                    QueueOfBaristaTakingOrder.take();//Take the barista from the queue of barista taking order
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            if(customer!=null){
                MakeDrink(customer);//Make the drink
            }
            //Print out that the barista is done with the order
            System.out.println("Barista " + barista.getBaristaId() + ": I am done with the order");
        } catch (InterruptedException ex) {
            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
        }                        
    }  
        
     
    public void MakeDrinkProcess(Barista barista, String machine){
        int processTime = 0;//Reset process time
        while(processTime < 100){//While process time is less than 100
            processTime+= 25;//Increment process time
            //Print out the making drink process
            System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using " + machine + " for making " + barista.getCurrentWorkingDrink() + " for Customer " + barista.getCurrentWorkingCustomerID() + " (" + processTime + "%)" + barista.getMyColorReset());
            try {
                Thread.sleep(500);//Stimulate making drink time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void MakeDrink(Customer customer) throws InterruptedException{
        Barista barista = null;//The barista
        try {
            barista = QueueOfBaristaMakingDrink.poll(1000, TimeUnit.MILLISECONDS);//Poll the barista from the queue of barista making drink
        } catch (InterruptedException ex) {
            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(barista!=null){//If the barista is not null
            if("Expresso".equals(barista.getCurrentWorkingDrink())){//If the current working drink is expresso
                //Print out that the barista is trying to use expresso machine
                System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " trying to use expresso machine (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Expresso)" + barista.getMyColorReset());
                //Lock the expresso machine
                ExpressoMachine.lock();
                    //Print out that the barista is using expresso machine
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using expresso machine (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Expresso)" + barista.getMyColorReset());
                    //Make the drink process
                    MakeDrinkProcess(barista, "Expresso Machine");
                    //Print out that the expresso is ready  
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": Expresso for Customer " + barista.getCurrentWorkingCustomerID() + " is ready" + barista.getMyColorReset());
                    barista.setCustomerTakenDrink(true);//Set the customer has taken the drink
                    synchronized(QueueOfFindingSeat){//Synchronize the queue of finding seat
                        QueueOfFindingSeat.notifyAll();//Notify all the customer to find seat
                    }
                    TotalEx++;//Increment the total expresso
                ExpressoMachine.unlock();

            }
            else if("Cappuccino".equals(barista.getCurrentWorkingDrink())){//If the current working drink is cappuccino
                //Print out that the barista is trying to use expresso machine
                System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " trying to use expresso machine (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Cappuccino)" + barista.getMyColorReset());
                ExpressoMachine.lock();
                    //Print out that the barista is using expresso machine
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using expresso machine (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Cappuccino)" + barista.getMyColorReset());
                    MakeDrinkProcess(barista, "Expresso Machine");//Make the drink process
                ExpressoMachine.unlock();                
                //Print out that the barista is trying to use milk frother
                System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " trying to use milk frother (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Cappuccino)" + barista.getMyColorReset());
                MilkMachine.lock();
                    //Print out that the barista is using milk frother
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using milk frother (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Cappuccino)" + barista.getMyColorReset());
                    MakeDrinkProcess(barista, "Milk Frother");//Make the drink process
                    //Print out that the cappuccino is ready
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": Cappuccino for Customer " + barista.getCurrentWorkingCustomerID() + " is ready" + barista.getMyColorReset());
                    barista.setCustomerTakenDrink(true);//Set the customer has taken the drink
                    synchronized(QueueOfFindingSeat){//Synchronize the queue of finding seat
                        QueueOfFindingSeat.notifyAll();//Notify all the customer to find seat
                    }
                    TotalCappu++;//Increment the total cappuccino
                MilkMachine.unlock();
            }
            else if("Juice".equals(barista.getCurrentWorkingDrink())){//If the current working drink is juice            
                System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " trying to use juice tap (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Juice)" + barista.getMyColorReset());
                JuiceTap.lock();
                    //Print out that the barista is using juice tap
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": I am using juice tap (For Customer " + barista.getCurrentWorkingCustomerID() + "'s Juice)" + barista.getMyColorReset());
                    MakeDrinkProcess(barista, "Juice Tap");//Make the drink process   
                    //Print out that the juice is ready                                      
                    System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + ": Juice for Customer " + barista.getCurrentWorkingCustomerID() + " is ready" + barista.getMyColorReset());
                    barista.setCustomerTakenDrink(true);//Set the customer has taken the drink
                    synchronized(QueueOfFindingSeat){//Synchronize the queue of finding seat
                        QueueOfFindingSeat.notifyAll();//Notify all the customer to find seat
                    }
                    TotalJuice++;//Increment the total juice
                JuiceTap.unlock();
            }else{System.out.println(barista.getMyColor() + "\t\t\t\t\tBarista " + barista.getBaristaId() + " is confused" + barista.getMyColorReset());}//else, the barista is confused
        }
    }
    
    synchronized void NotifyBaristaToWork(){//Notify the barista to work
        notify();
    }
    
    synchronized public void WaitCustomerToCome(){//Wait for the customer to come
        try {
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(CoffeeShop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showTotalDrink(){//Show the total drink
        System.out.println("===========================================");
        System.out.println("Total Expresso: " + TotalEx);
        System.out.println("Total Cappuccino: " + TotalCappu);
        System.out.println("Total Juice: " + TotalJuice);
        System.out.println("Sales of Expresso: " + TotalEx*ExPrice);
        System.out.println("Sales of Cappuccino: " + TotalCappu*CappuPrice);
        System.out.println("Sales of Juice: " + TotalJuice*JuicePrice);
        System.out.println("Total Sales: " + (TotalEx*ExPrice + TotalCappu*CappuPrice + TotalJuice*JuicePrice));
        System.out.println("===========================================");
    }

}
