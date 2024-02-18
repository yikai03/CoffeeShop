import java.util.concurrent.TimeUnit;

public class Clock extends Thread{
    Barista barista1;
    Barista barista2;
    Barista barista3;
    CoffeeShop shop;

    public Clock(Barista barista1, CoffeeShop shop, Barista barista2, Barista barista3){
        this.barista1 = barista1;
        this.barista2 = barista2;
        this.barista3 = barista3;
        this.shop = shop;
    }

    public void run(){
        //Closing Assumption 1: after 60 seconds, the shop will close
        //Closing Assumption 2: If all the customer left and all barista is sleeping, the shop will close
        while(!barista1.isClosingTime()){
            for(int i = 0; i < 60; i++){
                try {                
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //If after 60 loop, stil did not met below condition, Closing Assumption 1 will reach
                if(shop.LeftCustomer == 20 && barista1.getState() == Thread.State.WAITING && barista2.getState() == Thread.State.WAITING && barista3.getState() == Thread.State.WAITING){
                    System.out.println("\u001b[37;47m" + "Shop: Seems like there is no more customer, the shop will close" + "\u001b[0m");
                    barista1.setClosingTime(true);
                    barista2.setClosingTime(true);
                    barista3.setClosingTime(true);
                    for(int j = 0; j < 3; j++){
                        synchronized(shop){
                            shop.NotifyBaristaToWork();
                        }
                    }
                    while(true){
                        if(shop.LeftCustomer == 20 && (barista1.getState() == Thread.State.TERMINATED) && (barista2.getState() == Thread.State.TERMINATED) && (barista3.getState() == Thread.State.TERMINATED)){
                            shop.showTotalDrink();
                            break;
                        }
                    }
                    break;
                }
            }

            if(shop.LeftCustomer == 20){
                break;
            }

            while(shop.CustomerInShop!=0){
                try {
                    System.out.println("\u001b[37;47m" + "Clock: Seems like there is still " + shop.CustomerInShop + " customer in shop! Wait for another 3 second..." + "\u001b[0m");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            System.out.println("\u001b[37;47m" + "Clock: It's closing time!" + "\u001b[0m");
            barista1.setClosingTime(true);
            barista2.setClosingTime(true);
            barista3.setClosingTime(true);
            for(int i = 0; i < 3; i++){
                synchronized(shop){
                    shop.NotifyBaristaToWork();
                }
            }
        
            while(true){
            if((barista1.getState() == Thread.State.TERMINATED) && (barista2.getState() == Thread.State.TERMINATED) && (barista3.getState() == Thread.State.TERMINATED)){
                shop.showTotalDrink();
                // System.out.println( "Find: "+shop.QueueOfFindingSeat.size());
                // System.out.println("Order: " + shop.QueueOfOrder.size());
                // // System.out.println("Take: " + shop.QueueOfTakingDrink.size());
                // System.out.println("B Making: " + shop.QueueOfBaristaMakingDrink.size());
                // System.out.println("B Taking: " + shop.QueueOfBaristaTakingOrder.size());

                break;
            }            
        }
        }
    }
}
