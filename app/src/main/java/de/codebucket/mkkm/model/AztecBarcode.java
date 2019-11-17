package de.codebucket.mkkm.model;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contracts")
public class AztecBarcode {

    @PrimaryKey
    private String barcodeId;

    private String ticketId;

    private String passengerId;

    private byte[] data;

    private Date createdAt;

    public AztecBarcode(String barcodeId, String ticketId, String passengerId, byte[] data, Date createdAt) {
        this.barcodeId = barcodeId;
        this.ticketId = ticketId;
        this.passengerId = passengerId;
        this.data = data;
        this.createdAt = createdAt;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public byte[] getData() {
        return data;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
