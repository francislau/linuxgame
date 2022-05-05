package com.francin.linux.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rank {

    private int id;

    private String realname;

    private double score;

    private int finish;
}
