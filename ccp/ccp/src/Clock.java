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
        while(!barista1.isClosingTime()){
            try {
                TimeUnit.SECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
                System.out.println( "Find: "+shop.QueueOfFindingSeat.size());
                System.out.println("Order: " + shop.QueueOfOrder.size());
                System.out.println("Take: " + shop.QueueOfTakingDrink.size());
                break;
            }            
        }
        }
    }
}
