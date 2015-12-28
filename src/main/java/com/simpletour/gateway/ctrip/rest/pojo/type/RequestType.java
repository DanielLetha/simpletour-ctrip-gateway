package com.simpletour.gateway.ctrip.rest.pojo.type;

/**
 * Created by Jeff.Song on 2015/12/28.
 */

public class RequestType {
    private HeaderType header;

    private BodyType body;

    public HeaderType getHeader() {
        return header;
    }

    public void setHeader(HeaderType header) {
        this.header = header;
    }

    public BodyType getBody() {
        return body;
    }

    public void setBody(BodyType body) {
        this.body = body;
    }
}
