package com.atparui.rmsservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A BranchTable.
 */
@Table("branch_table")
@JsonIgnoreProperties(value = { "new" })
@org.springframework.data.elasticsearch.annotations.Document(indexName = "branchtable")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BranchTable implements Serializable, Persistable<UUID> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private UUID id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    @Column("table_number")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String tableNumber;

    @Size(max = 255)
    @Column("table_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String tableName;

    @NotNull(message = "must not be null")
    @Column("capacity")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer capacity;

    @Size(max = 50)
    @Column("floor")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String floor;

    @Size(max = 100)
    @Column("section")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String section;

    @Size(max = 50)
    @Column("status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String status;

    @Size(max = 500)
    @Column("qr_code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String qrCode;

    @Size(max = 500)
    @Column("qr_code_url")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String qrCodeUrl;

    @Column("is_active")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @org.springframework.data.annotation.Transient
    private boolean isPersisted;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "restaurant" }, allowSetters = true)
    private Branch branch;

    @Column("branch_id")
    private UUID branchId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public BranchTable id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTableNumber() {
        return this.tableNumber;
    }

    public BranchTable tableNumber(String tableNumber) {
        this.setTableNumber(tableNumber);
        return this;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableName() {
        return this.tableName;
    }

    public BranchTable tableName(String tableName) {
        this.setTableName(tableName);
        return this;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public BranchTable capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getFloor() {
        return this.floor;
    }

    public BranchTable floor(String floor) {
        this.setFloor(floor);
        return this;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getSection() {
        return this.section;
    }

    public BranchTable section(String section) {
        this.setSection(section);
        return this;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStatus() {
        return this.status;
    }

    public BranchTable status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public BranchTable qrCode(String qrCode) {
        this.setQrCode(qrCode);
        return this;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCodeUrl() {
        return this.qrCodeUrl;
    }

    public BranchTable qrCodeUrl(String qrCodeUrl) {
        this.setQrCodeUrl(qrCodeUrl);
        return this;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public BranchTable isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @org.springframework.data.annotation.Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public BranchTable setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Branch getBranch() {
        return this.branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
        this.branchId = branch != null ? branch.getId() : null;
    }

    public BranchTable branch(Branch branch) {
        this.setBranch(branch);
        return this;
    }

    public UUID getBranchId() {
        return this.branchId;
    }

    public void setBranchId(UUID branch) {
        this.branchId = branch;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BranchTable)) {
            return false;
        }
        return getId() != null && getId().equals(((BranchTable) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BranchTable{" +
            "id=" + getId() +
            ", tableNumber='" + getTableNumber() + "'" +
            ", tableName='" + getTableName() + "'" +
            ", capacity=" + getCapacity() +
            ", floor='" + getFloor() + "'" +
            ", section='" + getSection() + "'" +
            ", status='" + getStatus() + "'" +
            ", qrCode='" + getQrCode() + "'" +
            ", qrCodeUrl='" + getQrCodeUrl() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
