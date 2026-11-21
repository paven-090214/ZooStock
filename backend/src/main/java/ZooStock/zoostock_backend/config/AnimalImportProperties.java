package ZooStock.zoostock_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "animal.import")
public record AnimalImportProperties(
        boolean enabled,
        List<Long> taxonIds
) {}