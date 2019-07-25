package com.nowcoder.test;


import org.junit.Test;

import java.util.Date;

public class dateTest {

    public Date q1;
    public Date q2;

    public dateTest() {
        this.q1 = new Date();
        this.q2 = new Date();
    }

    @Test
    public void date3(){
        System.out.println(q1.getTime() - q2.getTime());
    }
}
