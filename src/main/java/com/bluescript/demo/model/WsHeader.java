package com.bluescript.demo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import java.util.*;

import org.springframework.stereotype.Component;
import com.bluescript.demo.model.EmVariable;

@Data
@Component

public class WsHeader {
    private String wsTransid;
    private String wsTermid;
    private int wsTasknum;
    private double wsAddrDfhcommarea;
    private int wsCalen;
    private EmVariable emVariable;

    public String toFixedWidthString() {
        return wsTransid + wsTermid + wsTasknum + wsAddrDfhcommarea + wsCalen + emVariable.toFixedWidthString();
    }

}