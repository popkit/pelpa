package org.popkit.leap.elpa.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.popkit.core.logger.LeapLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:14:27
 */
public class ArchiveVo {

    private List<Integer> ver;
    private String desc;
    private String type;   // "single"
    private PropsItem props;
    private JSONObject deps;

    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }

    public static ArchiveVo fromJSONString(String json) {
        return JSONObject.parseObject(json, ArchiveVo.class);
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    public void setDeps(List<DepsItem> depsItemList) {
        if (this.deps == null) {
            this.deps = new JSONObject();
        }
        if (CollectionUtils.isNotEmpty(depsItemList)) {
            for (DepsItem item : depsItemList) {
                this.deps.put(item.getName(), item.getVersions());
            }
        }
    }

    public List<DepsItem> getDepsList() {
        if (deps == null) {
            return new ArrayList<DepsItem>();
        }
        List<DepsItem> result = new ArrayList<DepsItem>();
        for (String key : deps.keySet()) {
            DepsItem depsItem = new DepsItem();
            depsItem.setName(key);
            JSONArray jsonArray = deps.getJSONArray(key);
            List<Integer> version = convert(jsonArray);
            if (version != null) {
                depsItem.setVersions(version);
                result.add(depsItem);
            }
        }
        return result;
    }

    private List<Integer> convert(JSONArray jsonArray) {
        try {
            List<Integer> result = new ArrayList<Integer>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String version = jsonArray.getString(i);
                result.add(Integer.parseInt(version));
            }
            return result;
        } catch (Exception e) {
            LeapLogger.warn("error convert jsonArray to List<Integer>", e);
            return null;
        }
    }

    public void setKeywords(List<String> keywords) {
        if (props == null) {
            this.props = new PropsItem();
        }

        this.props.setKeywords(keywords);
    }

    public void setPropsUrl(String url) {
        if (props == null) {
            this.props = new PropsItem();
        }

        this.props.setUrl(url);
    }


    public List<Integer> getVer() {
        return ver;
    }

    public void setVer(List<Integer> ver) {
        this.ver = ver;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PropsItem getProps() {
        return props;
    }

    public void setProps(PropsItem props) {
        this.props = props;
    }

    public JSONObject getDeps() {
        return deps;
    }

    public void setDeps(JSONObject deps) {
        this.deps = deps;
    }
}
