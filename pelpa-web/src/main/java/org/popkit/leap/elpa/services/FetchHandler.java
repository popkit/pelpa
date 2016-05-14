package org.popkit.leap.elpa.services;

import org.popkit.leap.elpa.entity.RecipeDo;

import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:10:07
 */
public interface FetchHandler {

    boolean validate(RecipeDo recipeDo, Map<String, Object> extra);

    void execute(RecipeDo recipeDo, Map<String, Object> extra);
}
