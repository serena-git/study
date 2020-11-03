package completableFuture;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Shop {
    private Random random;

    public Shop() {
        this.random = new Random();
    }

    /*
    *  1초동안 블록되는 동기 메소드
    * */
    public double getPrice(String product){
        return calculatePrice(product);
    }

    public Future<Double> getPriceAsync(String product){
        CompletableFuture<Double> priceInFuture = new CompletableFuture<>(); // 계산 결과를 포함시킬 Future
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    double price = calculatePrice(product);
                    priceInFuture.complete(price); // 시간이 오래 걸리는 계산 완료후 Future에 값 셋팅
                }catch(Exception exeption){
                    priceInFuture.completeExceptionally(exeption); //문제 발생시 발생한 에러 포함시키고 종료
                }
            }
        }).start();//다른 스레드에서 비동기적으로 수행
        return priceInFuture; // 계산 결과와 상관없이 바로 리턴됨
    }
    private double calculatePrice(String product){
        delay();
        return random.nextDouble() + product.charAt(0) +product.charAt(1);
    }

    private void delay() throws IllegalArgumentException{
        try{
            Thread.sleep(1000L); // 1sec.
            throw new IllegalArgumentException("not avaliable");
        }catch (InterruptedException exception){
            throw new RuntimeException(exception);
        }
    }

    public static void main(String[] args) {
        Shop shop = new Shop();
        Future<Double> priceInFuture = shop.getPriceAsync("my favorite product"); //상점에 제품가격 요청
//        doSomethingElse();//제품가격 계산하는 동안 다른 작업 수행
        try {
            double price = priceInFuture.get(); //가격정보를 가져온다. 처리되지 않으면 블럭된다.
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (Exception e){
            System.out.println("예외");
        }
        System.out.println(shop.getPrice("my favorite product"));
    }
}
