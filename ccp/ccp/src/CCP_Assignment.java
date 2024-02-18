public class CCP_Assignment {

    public static void main(String[] args) {
        CoffeeShop shop = new CoffeeShop();
        Barista barista1 = new Barista("1",  shop);
        Barista barista2 = new Barista("2", shop);
        Barista barista3 = new Barista("3", shop);
        Test test = new Test(shop, barista1, barista2, barista3);
        Clock clock = new Clock(barista1, shop, barista2, barista3);
        CustomerGenerator cg = new CustomerGenerator(shop, test);
        clock.start();
        barista1.start();
        barista2.start();
        barista3.start();
        cg.start();
        test.start();
    }
    
}

