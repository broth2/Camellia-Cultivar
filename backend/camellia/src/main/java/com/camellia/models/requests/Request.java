package com.camellia.models.requests;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;


import com.camellia.models.users.User;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class Request {
    
    @Id
    @GeneratedValue(generator = "request-sequence-generator")
    @GenericGenerator(
        name = "request-sequence-generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
                    @Parameter(name = "sequence_name", value = "request_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
        }
    )
    private long request_id;

    @Column(name = "submission_date", nullable = true)
    private LocalDateTime submissionDate;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( referencedColumnName = "userId", name="reg_user_id", nullable=false)
    @JsonIncludeProperties("reg_user_id")
    private User regUser;



    public long getRequest_id() {
        return this.request_id;
    }

    public void setRequest_id(long request_id) {
        this.request_id = request_id;
    }

    public LocalDateTime getSubmissionDate() {
        return this.submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public User getRegUser() {
        return this.regUser;
    }

    public void setRegUser(User reg_user) {
        this.regUser = reg_user;
    }

    public long getRegId(){
        return regUser.getUserId();
    }

    public String getFname(){
        return this.regUser.getFirstName();
    }

    public String getLname(){
        return this.regUser.getLastName();
    }

}
