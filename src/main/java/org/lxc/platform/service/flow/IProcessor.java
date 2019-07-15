package org.lxc.platform.service.flow;

import java.util.function.Consumer;
import reactor.core.Disposable;

public interface IProcessor<T> {

  Disposable subscribe(Consumer<? super T> consumer);

  void onNext(T t);
}
