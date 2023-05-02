package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//数据浏览查询
public class DataOverViewQueryDTO implements Serializable {

    private LocalDateTime begin;

    private LocalDateTime end;

}
