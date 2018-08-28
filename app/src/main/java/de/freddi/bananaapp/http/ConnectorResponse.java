package de.freddi.bananaapp.http;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

class ConnectorResponse {
    private String m_strResponsePayload = null;
    private Exception m_exception = null;

    public String getResponsePayload() {
        return m_strResponsePayload;
    }

    public void setResponsePayload(final String strResponsePayload) {
        this.m_strResponsePayload = strResponsePayload;
    }

    public void setException(final Exception e) {
        this.m_exception = e;
    }

    public Exception getException() {
        return this.m_exception;
    }

    public boolean hasValidPayload() {
        return StringUtils.isNotBlank(m_strResponsePayload);
    }

    public boolean hasStatusOk() {
        if (!hasValidPayload()) {
            return false;
        }
        try {
            if (new JSONObject(m_strResponsePayload).getString("status").endsWith("ok")) {
                return true;
            }
        } catch (final Exception e) {
            //e.printStackTrace();
        }
        return false;
    }
}
