package de.codebucket.mkkm.api.model;

import com.google.gson.annotations.SerializedName;

public class AssignRequest {

    @SerializedName("ticket_id")
    private String ticketId;

    public AssignRequest(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketId() {
        return ticketId;
    }
}
