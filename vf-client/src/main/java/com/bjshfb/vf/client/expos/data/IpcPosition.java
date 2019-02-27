package com.bjshfb.vf.client.expos.data;

import java.io.Serializable;

/**
 * @program: helm
 * @description: Ipc位置信息
 * @author: fuzq
 * @create: 2018-08-30 09:35
 **/
public class IpcPosition implements Serializable {
    private float x;
    private float y;
    private float zoom;

    private float xMax;
    private float xMin;
    private float yMax;
    private float yMin;
    private float zoomMax;
    private float zoomMin;

    public float getZoomMax() {
        return zoomMax;
    }

    public void setZoomMax(float zoomMax) {
        this.zoomMax = zoomMax;
    }

    public float getZoomMin() {
        return zoomMin;
    }

    public void setZoomMin(float zoomMin) {
        this.zoomMin = zoomMin;
    }

    public float getxMax() {
        return xMax;
    }

    public void setxMax(float xMax) {
        this.xMax = xMax;
    }

    public float getxMin() {
        return xMin;
    }

    public void setxMin(float xMin) {
        this.xMin = xMin;
    }

    public float getyMax() {
        return yMax;
    }

    public void setyMax(float yMax) {
        this.yMax = yMax;
    }

    public float getyMin() {
        return yMin;
    }

    public void setyMin(float yMin) {
        this.yMin = yMin;
    }

    private String xType;

    public String getxType() {
        return xType;
    }

    public void setxType(String xType) {
        this.xType = xType;
    }

    public IpcPosition(float x, float y, float zoom) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
}
