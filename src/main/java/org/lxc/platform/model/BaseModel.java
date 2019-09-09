package org.lxc.platform.model;

import com.google.common.base.Objects;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class BaseModel implements Serializable {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BaseModel baseModel = (BaseModel) o;
        return Objects.equal(id, baseModel.id) &&
                Objects.equal(version, baseModel.version);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, version);
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }
}
