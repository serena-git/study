package completableFuture;

import utils.Timers;

public class Discount {
    public enum Code{
        NONE(0),SILVER(5),GOLD(10),PLATINUM(15), DIAMOND(20);
        private final int percentage;

        Code(int percentage){
            this.percentage = percentage;
        }
    }
    public String applyDiscount(Quote quote){
        return quote.getShopName() + "price is" + apply(quote.getPrice(), quote.getDiscountCode());
    }
    private double apply(double price, Code code){
        Timers.delay(); // 할인 가격 계산 서버도 1초의 딜레이가 존재
        return price * (100 - code.percentage) / 100;
    }
}
