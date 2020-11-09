package completableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

public class PriceFinder {
    private final List<Shop> shops = Arrays.asList(
            new Shop("EBay1"),
            new Shop("EBay2"),
            new Shop("EBay3"),
            new Shop("EBay4"),
            new Shop("EBay5"),
            new Shop("EBay6"),
            new Shop("EBay7"),
            new Shop("EBay8"),
            new Shop("EBay9"),
            new Shop("EBay10"),
            new Shop("EBay11"),
            new Shop("EBay12"),
            new Shop("EBay13"),
            new Shop("EBay14"),
            new Shop("EBay15"),
            new Shop("EBay16"),
            new Shop("EBay17"));

    private final Executor executor = Executors.newFixedThreadPool(18, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true); // 자바 프로그램이 종료되면 스레드 종료
            return thread;
        }
    });


    /*
     * 메서드의 반환 형식은 List<String>이기 때문에 join을 호출하여 모든 동작이 끝나기를 기다린다.
     * 스트림 연산은 게으른 특성이 있으므로 만약 아래를 하나의 스트림을 사용했다면  순차적으로 처리 됬을 것이다.
     * */
    public List<String> findPricesWithFuture(String product) {
        List<CompletableFuture<String>> priceInFutures =
                shops.stream()
                        .map(shop -> CompletableFuture
                                .supplyAsync(
                                        () -> {
                                            return String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(product));
                                        }
                                ))
                        .collect(Collectors.toList());
        return priceInFutures.stream()
                .map((future) -> {
                    String result = future.join();
                    return result;
                })
                .collect(Collectors.toList());
    }

    public List<String> findPricesWithFutureAndExecutor(String product) {

        List<CompletableFuture<String>> priceInFutures =
                shops.stream()
                        .map(shop -> CompletableFuture
                                .supplyAsync(
                                        () -> {
                                            return String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(product));
                                        }
                                        , executor))
                        .collect(Collectors.toList());
        return priceInFutures.stream()
                .map((future) -> {
                    String result = future.join();
                    return result;
                })
                .collect(Collectors.toList());
    }

    //순차 계산을 병렬로 처리하여 성능을 계선한다.
    public List<String> findPricesWithParallelStream(String product) {
        return shops.parallelStream()
                .map(shop -> String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(product)))
                .collect(Collectors.toList());
    }

    //각 1초의 대기시간이 존재하는 작업이 순차적으로 계산되므로 4개의 작업에 대해 4009ms가 소요된다.
    public List<String> findPricesWithStream(String product) {
        return shops.stream()
                .map(shop -> String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(product)))
                .collect(Collectors.toList());
    }

    public List<String> findPricesWithPipline(String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture
                        .supplyAsync(() -> {
                            System.out.println("get price : " + shop.getName());
                            return shop.getPriceSync(product);
                        },executor)) //CompletableFuture<String>
                .map(future -> future.thenApply(price -> { //위의 CompletableFuture가 모두 완료 되어야 맵핑 됨(동기화)
                    System.out.println(price);
                    return Quote.parse(price);
                } ))
                .map(future -> future.thenCompose(quote -> CompletableFuture
                        .supplyAsync(() -> {
                            Discount discount = new Discount();
                            System.out.println(quote.getShopName());
                            return discount.applyDiscount(quote);
                        },executor)))
                .collect(Collectors.toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public void findPricesWithPipline2(String product) {
        CompletableFuture[] futures = shops.stream()
                .map(shop -> CompletableFuture
                        .supplyAsync(() -> {
                            System.out.println("get price : " + shop.getName());
                            return shop.getPriceSync(product);
                        },executor)
                        .handle((s,t) -> s!=null? s : t.getMessage())) //CompletableFuture<String>
                .map(future -> future.thenApply(price -> { //위의 CompletableFuture가 모두 완료 되어야 맵핑 됨(동기화)
                    System.out.println(price);
                    return Quote.parse(price);
                } ))
                .map(future -> future.thenCompose(quote -> CompletableFuture
                        .supplyAsync(() -> {
                            Discount discount = new Discount();
                            System.out.println(quote.getShopName());
                            if (quote.getShopName() != null) {
                                throw new RuntimeException("Computation error!");
                            }
                            return discount.applyDiscount(quote);
                        },executor)
                        .handle((s,t) -> s!=null? s : t.getMessage())))
                .map(future -> future.thenAccept(result-> System.out.println("result : " + result)))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
        //동시에 여러 Future를 병려로 실행 가능하다. 하지만 Void 타입의 Futre를 반환하므로 이어서 추가연산이 불가능 하다.
        //(각각 future마다 get메소드를 통해 결과를 받아온다.)

    }
}
