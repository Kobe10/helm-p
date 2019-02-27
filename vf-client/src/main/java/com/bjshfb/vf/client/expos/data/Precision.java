package com.bjshfb.vf.client.expos.data;

import java.io.Serializable;

/**
 * @program: helm
 * @description: Ipc操作精度
 * @author: fuzq
 * @create: 2018-08-29 19:24
 **/
public class Precision implements Serializable {
    private float xPre;//横向精度

    private float yPre;//纵向精度

    private float zoomPre;//缩放精度

    public Precision(float xPre, float yPre, float zoomPre) {
        this.xPre = xPre;
        this.yPre = yPre;
        this.zoomPre = zoomPre;
    }

    public float getxPre() {
        return xPre;
    }

    public void setxPre(float xPre) {
        this.xPre = xPre;
    }

    public float getyPre() {
        return yPre;
    }

    public void setyPre(float yPre) {
        this.yPre = yPre;
    }

    public float getZoomPre() {
        return zoomPre;
    }

    public void setZoomPre(float zoomPre) {
        this.zoomPre = zoomPre;
    }
}
