package ZooStock.zoostock_backend.price;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class PriceScheduler {

    private final PriceHistoryService priceHistoryService;

    public PriceScheduler(
            PriceHistoryService priceHistoryService
    ) {
        this.priceHistoryService = priceHistoryService;
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void schedule() {
        priceHistoryService.updatePrice();
    }
}