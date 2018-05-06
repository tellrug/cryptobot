package at.vulperium.cryptobot.dtos;

import at.vulperium.cryptobot.enums.BenachrichtigungTyp;

import java.io.Serializable;

/**
 * Created by 02ub0400 on 22.02.2018.
 */
public class BenachrichtigungDTO implements Serializable {

    private String betreff;
    private String text;
    private BenachrichtigungTyp benachrichtigungTyp;

    public BenachrichtigungTyp getBenachrichtigungTyp() {
        return benachrichtigungTyp;
    }

    public void setBenachrichtigungTyp(BenachrichtigungTyp benachrichtigungTyp) {
        this.benachrichtigungTyp = benachrichtigungTyp;
    }

    public String getBetreff() {
        return betreff;
    }

    public void setBetreff(String betreff) {
        this.betreff = betreff;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
