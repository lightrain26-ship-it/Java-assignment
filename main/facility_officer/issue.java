package facility_officer;

import java.time.LocalDate;

public class issue {
    private final String issueId;
    private final String roomId;
    private final String equipmentId;
    private int affected;
    private final String reportedBy;
    private final LocalDate dateReported;
    private String status;
    private final String briefDescription;

    // Constructor
    public issue(String issueId, String roomId, String equipmentId, int affected, String reportedBy, LocalDate dateReported, String status, String briefDescription) {
        if (affected < 0) {
            throw new IllegalArgumentException("Affected quantity cannot be negative");
        }

        if (dateReported == null){
            throw new IllegalArgumentException("Date cannot be empty");
        }

        if (briefDescription == null || briefDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Brief description cannot be empty");
        }

        if (briefDescription.contains("|")) {
            throw new IllegalArgumentException("Brief description cannot contain the character \"|\"");
        }

        this.issueId = issueId;
        this.roomId = roomId;
        this.equipmentId = equipmentId;
        this.affected = affected;
        this.reportedBy = reportedBy;
        this.dateReported = dateReported;
        setStatus(status);  // Validate and set status
        this.briefDescription = briefDescription;
    }

    // Getters
    public String getIssueId() {
        return issueId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }   

    public int getAffected() {
        return affected;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public LocalDate getDateReported() {
        return dateReported;
    }

    public String getStatus() {
        return status;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setStatus(String status) {
        if (status.equals("In Progress") || status.equals("Resolved")) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Status must be either 'In Progress' or 'Resolved'");
        }
    }

    public String[] issueRows() {
        return new String[]{
            issueId, 
            roomId, 
            equipmentId, 
            String.valueOf(affected), // Convert int to String
            reportedBy, 
            dateReported.toString(), // saved as yyyy-mm-dd
            status, 
            briefDescription};

    }
}