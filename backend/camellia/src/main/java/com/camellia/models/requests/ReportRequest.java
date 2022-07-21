package com.camellia.models.requests;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Column;

import com.camellia.models.cultivars.Cultivar;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



// Reports from users due to non camellia related posts


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "report_request")
public class ReportRequest extends Request{
    

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( referencedColumnName = "cultivar_id", name="cultivar_id", nullable=false)
    @JsonIncludeProperties("cultivar_id")
    private Cultivar cultivar;

    @Column(name = "report_text")
    private String reportText;


    public Cultivar getCultivar() {
        return this.cultivar;
    }

    public void setCultivar(Cultivar cultivar) {
        this.cultivar = cultivar;
    }

    public String getReportText(){
        return this.reportText;
    }

    public void setReportText(String reportText){
        this.reportText = reportText;
    }

    public long getCultivarId(){
        return this.cultivar.getId();
    }

    public String getCultivarN(){
        return this.cultivar.toString();
    }


    public String toString(){
        return "Reported cultivar ID: " + this.cultivar.getId() + "->" + this.cultivar
        + ", with message: " + this.reportText
        + ", from user: " + this.getRegUser().getUserId() + ", at " + this.getSubmissionDate();
    }




}
