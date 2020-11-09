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



##### thenApply()

##### thenCompose()

두 비동기 연산을 파이프라인으로 만들수 있는 메서드로 첫 번째 연산의 결과를 두번째 연산으로 전달한다. Async 접미사가 붙은 메소드가 존재하는데 이것은 이전 작업과 다른 스레드에서 실행되도록 스레드 풀로 작업을 제출한다. 만약 두 개의 작업이 의존적이라면 스레드 전환 오버헤드가 적게 발생하면서 효율성이 좀 더 좋은 thenCompose를 사용하도록 한다.



##### 컬렉션 계산의 병렬화

* 스트림 병렬화

  병렬 스트림으로 변환해서 컬렉션을 처리하는 방법으로 I/O가 포함되지 않은 계산 중심의 동작을 실행할 때는 스트림 인터페이스가 가장 구현하기 간단하며 효율적일 수 있다.

* CompletableFuture 병렬화

  컬렉션을 반복하면서 CompletableFuture 내부의 연산으로 반드는 것이다. 이를 이용하면 전체적인 계산이 블록되지 않도록 스레드 풀 크기를 조절 할 수 있다. I/O를 기다리는 작업을 병렬로 실행 할 때는 더 많은 유연성을 제공하며 대기/계산의 비율에 적합한 스레드 수를 설정할 수 있다. 특히 스트림의 게으른 특성 때문에 스트림에서 I/O를 실제로 언제 처리할지 예측하기 어려운 문제도 있다.