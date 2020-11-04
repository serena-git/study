package completableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Shop {
    private String shopName;
    private Random random;

    public Shop(String shopName) {
        this.shopName = shopName;
        this.random = new Random();
    }

    /*
     *  1초동안 블록되는 동기 메소드
     * */
    public double getPrice(String product) {
        return someLongComputation(product);
    }

    /*
     *  오래 걸리는 작업을 별도의 스레드로 실행하는 비동기 메소드
     * */
    public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> priceInFuture = new CompletableFuture<>(); // 계산 결과를 포함시킬 Future
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    double price = someLongComputation(product);
                    priceInFuture.complete(price); // 시간이 오래 걸리는 계산 완료후 Future에 값 셋팅
                } catch (Exception exeption) {
                    priceInFuture.completeExceptionally(exeption); //문제 발생시 발생한 에러 포함시키고 종료
                }
            }
        }).start();//다른 스레드에서 비동기적으로 수행
        return priceInFuture; // 계산 결과와 상관없이 바로 리턴됨
    }

    /*
     *  팩토리 메서드를 이용하여 CompletableFuture생성 동작과 예외처리방법은 getPriceAsync()와 동일
     * */
    public Future<Double> getPriceAsyncWithSupplyAsync(String product) {
        return CompletableFuture
                .supplyAsync(() -> someLongComputation(product)); //supplier를 전달받는 팩토리 메서드
    }

    private double someLongComputation(String product) {
        delay();
        return random.nextDouble() + product.charAt(0) + product.charAt(1);
    }

    private void delay() {
        try {
            Thread.sleep(1000L); // block for 1sec.
//            throw new IllegalArgumentException("not avaliable");
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void doSomethingElse() {
        System.out.println("Do something else ..");
    }

    public static void main(String[] args) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"));
        Shop shop = new Shop();
        Future<Double> priceInFuture = shop.getPriceAsync("my favorite product"); //상점에 제품가격 요청
        doSomethingElse(); //제품가격 계산하는 동안 다른 작업 수행
        try {
            double price = priceInFuture.get(); //가격정보를 가져온다. 처리되지 않으면 블럭된다.
            System.out.println(price);
        } catch (InterruptedException e) {
            System.out.println("Interrupt occurred during execution.");
        } catch (ExecutionException e) {
            System.out.println("exception occurred during operation.");
        } catch (Exception e) {
            System.out.println("exception occurred.");
        }
    }
}
