package org.platform.lxc.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import org.platform.lxc.model.JobStatus;

public class JobDto {

  @ApiModelProperty
  private String key;

  @ApiModelProperty
  private String description;

  @ApiModelProperty
  private JobStatus jobStatus;

  @ApiModelProperty
  private Date startDate;

  @ApiModelProperty
  private Date endDate;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public JobStatus getJobStatus() {
    return jobStatus;
  }

  public void setJobStatus(JobStatus jobStatus) {
    this.jobStatus = jobStatus;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
}
