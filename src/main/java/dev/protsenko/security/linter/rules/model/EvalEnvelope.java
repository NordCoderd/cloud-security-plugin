package dev.protsenko.security.linter.rules.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EvalEnvelope {
    @JsonProperty("result")
    private List<AlertResult> result;

    public EvalEnvelope() {}

    public EvalEnvelope(List<AlertResult> result) {
        this.result = result;
    }

    public List<AlertResult> getResult() {
        return result;
    }

    public void setResult(List<AlertResult> result) {
        this.result = result;
    }
}

