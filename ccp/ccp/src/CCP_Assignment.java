public class CCP_Assignment {

    public static void main(String[] args) {
        CoffeeShop shop = new CoffeeShop();
        CustomerGenerator cg = new CustomerGenerator(shop);
        Barista barista1 = new Barista("1",  shop);
        Barista barista2 = new Barista("2", shop);
        Barista barista3 = new Barista("3", shop);
        Clock clock = new Clock(barista1, shop, barista2, barista3);
        clock.start();
        barista1.start();
        barista2.start();
        barista3.start();
        cg.start();

        // try {
        //     Thread.sleep(60000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // System.out.println("cg thread status: " + cg.getState());
        // System.out.println("barista1 thread status: " + barista1.getState());
        // System.out.println("barista2 thread status: " + barista2.getState());
        // System.out.println("barista3 thread status: " + barista3.getState());
    }
    
}

