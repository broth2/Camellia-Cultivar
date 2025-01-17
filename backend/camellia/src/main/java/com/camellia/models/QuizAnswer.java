package com.camellia.models;

import javax.persistence.*;

import com.camellia.models.cultivars.Cultivar;
import com.camellia.models.specimens.Specimen;
import com.camellia.models.specimens.SpecimenType;
import com.camellia.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "quiz_answer", uniqueConstraints={
    @UniqueConstraint(columnNames = {"userId", "specimen_id"})
})
public class QuizAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "correct")
    private boolean correct;

    @Column(name = "specimen_type")
    private SpecimenType specimenType;

    @ManyToOne
    @JoinColumn( name="userId", nullable=false)
    @JsonIgnoreProperties("user")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn( referencedColumnName = "specimen_id", name="specimen_id", nullable=false)
    @JsonIgnoreProperties("specimen_id")
    private Specimen specimen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( referencedColumnName = "cultivar_id", name="cultivar_id", nullable=false)
    @JsonIgnoreProperties("cultivar_id")
    private Cultivar cultivar;
    

    public Long getId() {
        return this.id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCorrect(boolean isCorrect){
        this.correct = isCorrect;
    }

    public boolean getCorrect(){
        return this.correct;
    }

    public void setSpecimenType(SpecimenType specimenType){
        this.specimenType = specimenType;
    }

    public SpecimenType getSpecimenType(){
        return this.specimenType;
    }

    public boolean isReference() {
        return specimenType.equals(SpecimenType.REFERENCE);
    }

    public boolean isToIdentify() {
        return specimenType.equals(SpecimenType.TO_IDENTIFY);
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }
    
    public Specimen getSpecimen(){
        return this.specimen;
    }

    public void setCultivar(Cultivar cultivar) {
        this.cultivar = cultivar;
    }

    public Cultivar getCultivar() {
        return cultivar;
    }

    public String toString(){
        return "[id: " + this.id + ",correct:" + this.correct + ",specimentype" + this.specimenType.getCode() + 
        ",user:"+ this.user.getEmail() + ",specimen(expected): " + this.specimen.getSpecimenId() + ",cultivar(answered): "+ this.cultivar.getId() + "]";
    }
}
