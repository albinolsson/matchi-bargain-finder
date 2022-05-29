package com.matchi.bargain.finder.handler;

import com.matchi.bargain.finder.model.Court;
import com.matchi.bargain.finder.model.RequestParameters;

import java.util.List;

public interface SearchRequestHandlerHelper {
    List<Court> getCourts(RequestParameters requestParameters);
}
