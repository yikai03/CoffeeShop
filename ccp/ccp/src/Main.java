public class Main {
    public static void main(String[] args) {
        CoffeeShop shop = new CoffeeShop();//Create a coffee shop
        Barista barista1 = new Barista("1",  shop);//Create 3 barista
        Barista barista2 = new Barista("2", shop);
        Barista barista3 = new Barista("3", shop);
        Test test = new Test(shop, barista1, barista2, barista3);//Create a test case
        Clock clock = new Clock(barista1, shop, barista2, barista3);//Create a clock
        CustomerGenerator cg = new CustomerGenerator(shop, test);//Create a customer generator
        clock.start();
        barista1.start();
        barista2.start();
        barista3.start();
        cg.start();
        test.start();
    }
}
