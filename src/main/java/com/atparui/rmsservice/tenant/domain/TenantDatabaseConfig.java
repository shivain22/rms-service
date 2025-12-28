package com.atparui.rmsservice.tenant.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Tenant database configuration retrieved from Gateway.
 */
public class TenantDatabaseConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("databaseUrl")
    private String databaseUrl;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("maxPoolSize")
    private Integer maxPoolSize = 20;

    @JsonProperty("connectionTimeout")
    private Integer connectionTimeout = 30000;

    @JsonProperty("validationQuery")
    private String validationQuery = "SELECT 1";

    @JsonProperty("rmsServiceClientId")
    private String rmsServiceClientId;

    @JsonProperty("rmsServiceClientSecret")
    private String rmsServiceClientSecret;

    public TenantDatabaseConfig() {}

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public String getRmsServiceClientId() {
        return rmsServiceClientId;
    }

    public void setRmsServiceClientId(String rmsServiceClientId) {
        this.rmsServiceClientId = rmsServiceClientId;
    }

    public String getRmsServiceClientSecret() {
        return rmsServiceClientSecret;
    }

    public void setRmsServiceClientSecret(String rmsServiceClientSecret) {
        this.rmsServiceClientSecret = rmsServiceClientSecret;
    }

    @Override
    public String toString() {
        return (
            "TenantDatabaseConfig{" +
            "tenantId='" +
            tenantId +
            '\'' +
            ", databaseUrl='" +
            databaseUrl +
            '\'' +
            ", username='" +
            username +
            '\'' +
            ", maxPoolSize=" +
            maxPoolSize +
            ", connectionTimeout=" +
            connectionTimeout +
            '}'
        );
    }
}
