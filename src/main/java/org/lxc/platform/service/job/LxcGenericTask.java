package org.lxc.platform.service.job;

import java.util.Date;
import java.util.UUID;
import java.util.function.Supplier;
import org.lxc.platform.dto.JobDto;
import org.lxc.platform.model.Job;
import org.lxc.platform.model.JobCode;
import org.lxc.platform.model.JobStatus;
import org.lxc.platform.model.LxcStatus;
import org.lxc.platform.model.User;
import org.lxc.platform.respository.JobRepository;
import org.lxc.platform.respository.LxcStatusRepository;
import org.lxc.platform.service.flow.IProcessor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LxcGenericTask<T extends LxcStatus> implements JobTask {

  private final static Logger LOG = LoggerFactory.getLogger(LxcCreateTask.class);

  private final IProcessor<JobDto> processor;
  private final JobRepository jobRepository;
  private final LxcStatusRepository lxcStatusRepository;
  private final String description;
  private final Supplier<T> supplier;
  private final JobCode jobCode;
  private final User createdBy;
  private final String jobKey;
  private final ModelMapper modelMapper;

  public LxcGenericTask(
      IProcessor<JobDto> processor,
      JobRepository jobRepository,
      LxcStatusRepository lxcStatusRepository,
      ModelMapper modelMapper,
      String description,
      JobCode jobCode,
      User createdBy,
      Supplier<T> supplier) {
    this.processor = processor;
    this.jobRepository = jobRepository;
    this.lxcStatusRepository = lxcStatusRepository;
    this.description = description;
    this.createdBy = createdBy;
    this.supplier = supplier;
    this.jobKey = UUID.randomUUID().toString();
    this.jobCode = jobCode;
    this.modelMapper = modelMapper;
  }

  @Override
  public JobDto getJob() {
    return modelMapper.map(jobRepository.findByKey(jobKey).orElse(new Job()), JobDto.class);
  }

  @Override
  public T run() {
    Job job = new Job();
    job.setStartDate(new Date());
    job.setKey(jobKey);
    job.setJobCode(jobCode);
    job.setJobStatus(JobStatus.STARTED);
    job.setDescription(description);
    job.setCreatedBy(createdBy);
    Job startedJob = jobRepository.save(job);
    processor.onNext(modelMapper.map(startedJob, JobDto.class));

    try {
      T result = supplier.get();
      LxcStatus savedLxcStatus = lxcStatusRepository.save(result);
      startedJob.setJobStatus(JobStatus.DONE);
      startedJob.setEndDate(new Date());
      startedJob.setResult(savedLxcStatus);
      Job doneJob = jobRepository.save(startedJob);
      processor.onNext(modelMapper.map(doneJob, JobDto.class));
      return result;
    } catch (Exception e) {
      LOG.info("Generic job exception in {}: {}", jobCode, e.getMessage());
      LxcStatus lxcStatus = new LxcStatus();
      lxcStatus.setStatusResult("failed");
      LxcStatus savedLxcStatus = lxcStatusRepository.save(lxcStatus);
      startedJob.setJobStatus(JobStatus.FAILED);
      startedJob.setEndDate(new Date());
      startedJob.setResult(savedLxcStatus);
      Job failedJob = jobRepository.save(startedJob);
      processor.onNext(modelMapper.map(failedJob, JobDto.class));
      throw new IllegalThreadStateException(jobCode + " exception: " + e.getMessage());
    }
  }
}
