package org.lxc.platform.service.flow;

import java.util.function.Consumer;
import org.lxc.platform.dto.JobDto;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;

@Component
public class JobMsgService implements IProcessor<JobDto> {

  private final EmitterProcessor<JobDto> processor = EmitterProcessor.create();

  @Override
  public Disposable subscribe(Consumer<? super JobDto> consumer) {
    return processor.subscribe(consumer);
  }

  @Override
  public void onNext(JobDto job) {
    processor.onNext(job);
  }
}
