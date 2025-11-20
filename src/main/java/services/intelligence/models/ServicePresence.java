package services.intelligence.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service presence information across platforms
 */
public class ServicePresence {
    private Map<String, Boolean> services;
    private Integer totalServicesFound;
    private List<String> categories;

    public ServicePresence() {
        this.services = new HashMap<>();
        this.categories = new ArrayList<>();
        this.totalServicesFound = 0;
    }

    public static ServicePresence empty() {
        return new ServicePresence();
    }

    public boolean has(String serviceName) {
        return services.getOrDefault(serviceName.toLowerCase(), false);
    }

    public int count() {
        return totalServicesFound;
    }

    // Getters and Setters
    public Map<String, Boolean> getServices() {
        return services;
    }

    public void setServices(Map<String, Boolean> services) {
        this.services = services;
        this.totalServicesFound = (int) services.values().stream().filter(Boolean::booleanValue).count();
    }

    public Integer getTotalServicesFound() {
        return totalServicesFound;
    }

    public void setTotalServicesFound(Integer totalServicesFound) {
        this.totalServicesFound = totalServicesFound;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void addService(String serviceName, boolean present) {
        this.services.put(serviceName.toLowerCase(), present);
        if (present) {
            this.totalServicesFound++;
        }
    }
}
