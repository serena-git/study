package completableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PriceFinder {
    private static final String PRODUCT_NAME = "my product";
    private final List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"),
            new Shop("EBay"),
            new Shop("BestPrice1"),
            new Shop("LetsSaveBig1"),
            new Shop("MyFavoriteShop1"),
            new Shop("BuyItAll1"),
            new Shop("EBay1"),
            new Shop("BestPrice2"),
            new Shop("LetsSaveBig2"),
            new Shop("MyFavoriteShop2"),
            new Shop("BuyItAll2"),
            new Shop("EBay2"),
            new Shop("EBay16"),
            new Shop("EBay17"));


    public List<String> findPrices(String product){
        List<CompletableFuture<String>> priceInFutures =
                shops.stream()
                .map(shop -> CompletableFuture
                        .supplyAsync(()->String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(PRODUCT_NAME))))
                .collect(Collectors.toList());
        return priceInFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    //순차 계산을 병렬로 처리하여 성능을 계선한다.
    public List<String> findPricesWithParallelStream(String product){
        return shops.parallelStream()
                .map(shop -> String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(PRODUCT_NAME)))
                .collect(Collectors.toList());
    }

    //각 1초의 대기시간이 존재하는 작업이 순차적으로 계산되므로 4개의 작업에 대해 4009ms가 소요된다.
    public List<String> findPricesWithStream(String product){
        return  shops.stream()
                .map(shop -> String.format("%s 가격은 %.2f", shop.getName(), shop.getPriceSync(PRODUCT_NAME)))
                .collect(Collectors.toList());
    }
}
