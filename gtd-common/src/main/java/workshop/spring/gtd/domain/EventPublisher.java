package workshop.spring.gtd.domain;

public interface EventPublisher<T> {

    void log(T event);
}
