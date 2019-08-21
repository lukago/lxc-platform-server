package org.lxc.platform.model;

import com.google.common.base.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="table_lxc_statuses")
public class LxcStatus  extends BaseModel {

  @Column(length = 2048)
  private String statusResult;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private User owner;

  @Column
  private int port;

  @Column
  private String name;

  public String getStatusResult() {
    return statusResult;
  }

  public void setStatusResult(String statusResult) {
    this.statusResult = statusResult;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LxcStatus lxcStatus = (LxcStatus) o;
    return Objects.equal(id, lxcStatus.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "LxcStatus{" +
        "id=" + id +
        ", statusResult='" + statusResult + '\'' +
        '}';
  }
}
