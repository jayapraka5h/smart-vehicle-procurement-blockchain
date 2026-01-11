package com.smartvehicle.model;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vehicleNumber;
    
    @Lob
    @Column(columnDefinition="BINARY VARYING") // Or just @Lob usually works with Postgres + Spring Boot
    private byte[] image;

    private String imageContentType;

    // private String imagePath; // We are removing file-based storage path
    
    @Column(length = 2000)
    private String accidentsHistory;

    private String documentPath;

    @Column(nullable = false)
    private BigDecimal price;

    private String blockHash; // To simulate blockchain record

    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        AVAILABLE, PENDING, SOLD
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public String getImageContentType() { return imageContentType; }
    public void setImageContentType(String imageContentType) { this.imageContentType = imageContentType; }
    
    // public String getImagePath() { return imagePath; }
    // public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getAccidentsHistory() { return accidentsHistory; }
    public void setAccidentsHistory(String accidentsHistory) { this.accidentsHistory = accidentsHistory; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getBlockHash() { return blockHash; }
    public void setBlockHash(String blockHash) { this.blockHash = blockHash; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
