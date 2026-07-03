package dev.jsinco.brewery.api.integration;

public interface IntegrationManager {
    <T extends Integration> void register(IntegrationType<? extends T> type, T integration);
}