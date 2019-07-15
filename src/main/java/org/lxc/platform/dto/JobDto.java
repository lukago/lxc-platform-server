package org.lxc.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import org.lxc.platform.model.JobCode;
import org.lxc.platform.model.JobStatus;

public class JobDto {

  @ApiModelProperty
  private String key;

  @ApiModelProperty
  private String description;

  @ApiModelProperty
  private JobStatus jobStatus;

  @ApiModelProperty
  private JobCode jobCode;

  @ApiModelProperty
  private Date startDate;

  @ApiModelProperty
  private Date endDate;

  @ApiModelProperty
  private LxcStatusDto result;

  @ApiModelProperty
  private UserSafeDto createdBy;

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

  public UserSafeDto getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(UserSafeDto createdBy) {
    this.createdBy = createdBy;
  }

  public LxcStatusDto getResult() {
    return result;
  }

  public void setResult(LxcStatusDto lxcStatusDto) {
    this.result = lxcStatusDto;
  }

  public JobCode getJobCode() {
    return jobCode;
  }

  public void setJobCode(JobCode jobCode) {
    this.jobCode = jobCode;
  }
}
