package com.example.diploma.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Logistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logistics_id;

    @OneToOne
    private User user;
}
