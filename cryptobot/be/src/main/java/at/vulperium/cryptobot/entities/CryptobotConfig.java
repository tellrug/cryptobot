package at.vulperium.cryptobot.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Ace on 26.12.2017.
 */
@Entity
@Table(name = "CRYPTOBOT_CONFIG")
public class CryptobotConfig extends BaseEntity<String> {

    @Id
    @Column(name = "KEY", nullable = false, updatable = false)
    private String key;

    @Column(name = "VALUE", nullable = false, updatable = false)
    private String value;

    @Override
    public String getId() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
