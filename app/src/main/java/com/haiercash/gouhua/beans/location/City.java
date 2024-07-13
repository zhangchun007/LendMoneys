package com.haiercash.gouhua.beans.location;

import java.io.Serializable;
import java.util.List;

/**
 * city
 */
public class City implements Serializable {

    private String code;
    private String version;
    private List<District> districts;
    private String name;
    private String type;
    private String parent;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getParent() {
        return parent;
    }

}
