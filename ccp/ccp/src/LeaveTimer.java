public class LeaveTimer extends Thread{
    CoffeeShop shop;
    Customer customer;

    public LeaveTimer(CoffeeShop shop, Customer customer){
        this.shop = shop;
        this.customer = customer;
    }

    public void CountLeaveTime(){
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized(shop.QueueOfOrder){
            if(shop.QueueOfOrder.contains(customer)){
                shop.QueueOfOrder.remove(customer);
                customer.setLeaveDueTimer(true);
                shop.CustomerInShop--;
                shop.LeftCustomer++;
                System.out.println(customer.GetMyColor() + "Customer " + customer.GetCustomerId() + " has left the shop" + customer.GetMyColorReset());
            }
        }
        //System.out.println("Customer " + customer.GetCustomerId() + " has left the shop");
        return;
    }

    public void run(){
        CountLeaveTime();
    }
}
