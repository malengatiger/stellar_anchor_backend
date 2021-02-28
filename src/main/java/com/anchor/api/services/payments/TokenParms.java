package com.anchor.api.services.payments;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement()
public class TokenParms {
    private ParamEncryption paramEncryption;

    public TokenParms(ParamEncryption paramEncryption) {
        this.paramEncryption = paramEncryption;
    }

    public ParamEncryption getParamEncryption() { return paramEncryption; }
    public void setParamEncryption(ParamEncryption value) { this.paramEncryption = value; }
}

// ParamEncryption.java

 class ParamEncryption {
    private Parameters parameters;
    private String xmlns;

     public ParamEncryption(Parameters parameters) {
         this.parameters = parameters;
     }

     public Parameters getParameters() { return parameters; }
    public void setParameters(Parameters value) { this.parameters = value; }

    public String getXmlns() { return xmlns; }
    public void setXmlns(String value) { this.xmlns = value; }
}

// Parameters.java


 class Parameters {
    private Parameter parameter;

     public Parameters(Parameter parameter) {
         this.parameter = parameter;
     }

     public Parameter getParameter() { return parameter; }
    public void setParameter(Parameter value) { this.parameter = value; }
}

// Parameter.java

 class Parameter {
    private String paramKey;
    private String paramValue;

     public Parameter(String paramKey, String paramValue) {
         this.paramKey = paramKey;
         this.paramValue = paramValue;
     }

     public String getParamKey() { return paramKey; }
    public void setParamKey(String value) { this.paramKey = value; }

    public String getParamValue() { return paramValue; }
    public void setParamValue(String value) { this.paramValue = value; }
}
