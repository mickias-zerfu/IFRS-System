/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 20 - 12 - 2020
 */

package com.eyob.IFRSSystem.domain;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "purchases")
public class purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long purchase_id;

    @Column(name = "vendor_id")
    private Long vendor_id;

    @Column(name = "freight")
    private Double freight;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "purchase_date")
    private Date purchase_date;




}
