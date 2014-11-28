package com.oraclecorp.cxshop.facebook.mobile;


import java.net.URL;

import oracle.adfmf.dc.ws.rest.RestServiceAdapter;
import oracle.adfmf.framework.api.AdfmfJavaUtilities;
import oracle.adfmf.framework.api.JSONBeanSerializationHelper;
import oracle.adfmf.framework.api.Model;
import oracle.adfmf.java.beans.PropertyChangeListener;
import oracle.adfmf.java.beans.PropertyChangeSupport;

public class RESTJSONBean {

    private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public RESTJSONBean() {
    }


    private String accessToken;
    private String jsonResponse = "";
    private RESTJSONResponse response = null;



    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }


    public void setAccessToken(String accessToken) {
        String oldAccessToken = this.accessToken;
        this.accessToken = accessToken;
        propertyChangeSupport.firePropertyChange("accessToken", oldAccessToken, accessToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setJsonResponse(String response) {
        String oldResponse = this.jsonResponse;
        this.jsonResponse = response;
        propertyChangeSupport.firePropertyChange("jsonResponse", oldResponse, response);
    }

    public String getJsonResponse() {
        return jsonResponse;
    }

    public void setResponse(RESTJSONResponse response) {
        RESTJSONResponse oldResponse = this.response;
        this.response = response;
        propertyChangeSupport.firePropertyChange("response", oldResponse, response);
    }

    public RESTJSONResponse getResponse() {
        return response;
    }

    public void loadData() {
        
        RestServiceAdapter restServiceAdapter = Model.createRestServiceAdapter();

        // Clear any previously set request properties, if any
        restServiceAdapter.clearRequestProperties();

        // Set the connection name
        restServiceAdapter.setConnectionName("FB_GRAPH");

        // Specify the type of request
        restServiceAdapter.setRequestType(RestServiceAdapter.REQUEST_TYPE_GET);

        // Specify the number of retries
        restServiceAdapter.setRetryLimit(0);

        String accessToken = (String)AdfmfJavaUtilities.getELValue("#{applicationScope.access_token}");
        setAccessToken(accessToken);
        setJsonResponse("/me?access_token=" + accessToken);

        // Set the URI which is defined after the endpoint in the connections.xml.
        // The request is the endpoint + the URI being set
        restServiceAdapter.setRequestURI("/me?access_token=" + accessToken);
        
        AdfmfJavaUtilities.setELValue("#{applicationScope.requestURI}", restServiceAdapter.getRequestURI());
        //setJsonResponse("/me?access_token=" + getAccessToken());

        // Execute SEND and RECEIVE operation
        try {
            // For GET request, there is no payload
            setJsonResponse(restServiceAdapter.send(""));
            
            // Now create a new RESTJSONResponse object and parse the JSON string returned into this class
            RESTJSONResponse res = new RESTJSONResponse();
            res = (RESTJSONResponse)JSONBeanSerializationHelper.fromJSON(RESTJSONResponse.class, getJsonResponse());
            setResponse(res);
        } catch (Exception e) {
            e.printStackTrace();
            setJsonResponse(e.getMessage());
        }
    }

}
