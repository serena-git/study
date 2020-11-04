멀티코어 활용하려면 하나의 큰 task를 실행하는 것 보다 subtask로 나누어서 병렬로 실행하는 것이 좋다. 이것은 Java 7에서 지원하는 ForkJoin 프레임워크나 Java8에서 지원하는 병렬 스트림으로 간단하고 효과적으로 구현할 수 있다.

### Future

자바 5부터 Future인터페이스를 제공하고 있다. 비동기 연산을 모델링 하는데 이용되며 연산이 끝났을 때 결과에 접근할 수 있는 레퍼런스를 제공한다. 시간이 많이 소요 되는 작업을 Future내부에 설정하면 호출자 스레드가 결과를 기다리는 동안 다른 작업을 수행 할 수 있다. 처리된 작업은 get메서드로 결과를 가져올 수 있는데 호출했을 때 결과가 준비되지 않았다면 완료될 때 까지 스레드가 블럭된다. Future는 결과값의 핸들일 뿐이며 계산이 완료되면 get()을 사용하여 결과를 얻을 수 있다.

##### completeExceptionally()

연산을 수행하는 동안 Error가 발생하면 연산 중인 스레드에만 영향을 받는다. 호출자 스레드는 계속 진행 가능 하므로 작업의 순서가 꼬이거나 get()의 결과가 반환될 때까지 영원히 기다려야 할 수도 있으므로 get()에 타임아웃을 설정하는 것이 좋다. 

영원히 기다리는 문제는 해결될 수 있어도 Error의 원인을 알 수 없으므로 completeExceptionally()를 이용하여 CompletableFuture내부에서 발생한 예외 상황을 클라이언트로 전달하여 Error발생 원인을 알 수 있도록 한다.

```java
public Future<Double> getPriceAsync(String product){
        CompletableFuture<Double> priceInFuture = new CompletableFuture<>();
        new Thread(() -> {
                try{
                    double price = calculatePrice(product);
                    priceInFuture.complete(price);
                }catch(Exception exeption){
                    priceInFuture.completeExceptionally(exeption); 
                }
            }
        }).start();
        return priceInFuture;
    }
```



##### supplyAsync()

supplyAsync()는 Supplier를 인수로 받아 내부적으로 ForkJoinPool의 Executor중 하나가 Supplier를 실행하여 비동기적으로 결과를 생성하는 팩토리 메서드 이다. 인수로 Executor를 전달하는 것도 가능하다.

> * ForkJoinPool ?