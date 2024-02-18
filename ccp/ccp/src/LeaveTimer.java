public class LeaveTimer extends Thread{
    CoffeeShop shop;//The shop the customer is in
    Customer customer;//The customer

    public LeaveTimer(CoffeeShop shop, Customer customer){//Constructor
        this.shop = shop;
        this.customer = customer;
    }

    public void CountLeaveTime(){
        try {
            Thread.sleep(6000);//Customer will leave after 6 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized(shop.QueueOfOrder){
            if(shop.QueueOfOrder.contains(customer)){//If the customer is still in the queue
                shop.QueueOfOrder.remove(customer);//Remove the customer from the queue
                customer.setLeaveDueTimer(true);//Set the customer leave due to timer
                shop.CustomerInShop--;//Decrement the customer in shop
                shop.LeftCustomer++;//Increment the left customer
                System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + " has left the shop" + customer.GetMyColorReset());//Print out that the customer has left the shop
            }
        }        
        return;
    }

    public void run(){
        CountLeaveTime();//Start the leave timer
    }
}
