package completableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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


    /*
    * 메서드의 반환 형식은 List<String>이기 때문에
    *
    * */
    public List<String> findPricesWithFuture(String product){
        List<CompletableFuture<String>> priceInFutures =
                shops.stream()
                .map(shop -> CompletableFuture
                        .supplyAsync(()->String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(product))))
                .collect(Collectors.toList());
        return priceInFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    //순차 계산을 병렬로 처리하여 성능을 계선한다.
    public List<String> findPricesWithParallelStream(String product){
        return shops.parallelStream()
                .map(shop -> String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(product)))
                .collect(Collectors.toList());
    }

    //각 1초의 대기시간이 존재하는 작업이 순차적으로 계산되므로 4개의 작업에 대해 4009ms가 소요된다.
    public List<String> findPricesWithStream(String product){
        return  shops.stream()
                .map(shop -> String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(product)))
                .collect(Collectors.toList());
    }
}
