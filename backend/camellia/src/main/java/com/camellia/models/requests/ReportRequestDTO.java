package com.camellia.models.requests;

import java.time.LocalDateTime;

public class ReportRequestDTO {
    private LocalDateTime submissionDate;

    

    //private long regId;

    private long cultivarId;

    private String reportText;

    public ReportRequestDTO(){}

    public ReportRequestDTO reportRequestToDTO(ReportRequest cr){
        ReportRequestDTO rrdto = new ReportRequestDTO();
        rrdto.setSubmissionDate(cr.getSubmissionDate());
        rrdto.setReportText(cr.getReportText());
        // this.regId = cr.getRegId();
        // this.specimenId = cr.getTo_identify_specimen().getSpecimenId();
        return rrdto;
    }



    public LocalDateTime getSubmissionDate() {
        return this.submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public long getCultivarId() {
        return this.cultivarId;
    }

    public void setCultivarId(long cultivarId) {
        this.cultivarId = cultivarId;
    }
    

    // public long getRegId() {
    //     return this.regId;
    // }

    // public void setRegId(long regId) {
    //     this.regId = regId;
    // }

    public String getReportText(){
        return this.reportText;
    }

    public void setReportText(String reportText){
        this.reportText = reportText;
    }

    public String toString(){
        return "Reported DTO cultivar ID: " + this.cultivarId 
        + ", with message: " + this.reportText;
    }
}
