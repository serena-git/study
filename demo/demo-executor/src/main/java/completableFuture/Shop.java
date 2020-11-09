package completableFuture;

import utils.Timers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Shop {
    private String name;
    private Random random;

    public Shop(String name) {
        this.name = name;
        this.random = new Random();
    }

    public String getName() {
        return name;
    }

    /*
     *  1초동안 블록되는 동기 메소드
     * */
    public String getPriceSync(String product) {
        Random random = new Random();
        double price = someLongComputation(product);
        Discount.Code code = Discount.Code.values()[random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s",name, price, code);
    }

    /*
     *  오래 걸리는 작업을 별도의 스레드로 실행하는 비동기 메소드
     * */
    public CompletableFuture<Double> getPriceAsync(String product) {
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
    public CompletableFuture<Double> getPriceAsyncWithSupplyAsync(String product) {
        return CompletableFuture
                .supplyAsync(() -> someLongComputation(product)); //supplier를 전달받는 팩토리 메서드
    }

    private double someLongComputation(String product) {
        Timers.delay();
        return random.nextDouble() + product.charAt(0) + product.charAt(1);
    }

    private static void doSomethingElse() {
        System.out.println("Do something else ..");
    }
}
