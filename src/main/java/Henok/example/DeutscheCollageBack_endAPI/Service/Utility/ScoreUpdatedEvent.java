package Henok.example.DeutscheCollageBack_endAPI.Service.Utility;

import org.springframework.context.ApplicationEvent;

// ScoreUpdatedEvent.java
public class ScoreUpdatedEvent extends ApplicationEvent {

    private final Long studentUserId;

    public ScoreUpdatedEvent(Object source, Long studentUserId) {
        super(source);
        this.studentUserId = studentUserId;
    }

    public Long getStudentUserId() {
        return studentUserId;
    }
}