package com.daviipkp.smartsteve.implementations.triggers;

import com.daviipkp.smartsteve.Instance.Trigger;
import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.VoiceService;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Entity
public class TimeTrigger extends Trigger {

    private LocalDateTime targetTime;

    public TimeTrigger(String context, LocalDateTime time) {
        super(context);
        this.targetTime=time;
    }

    public TimeTrigger() {

    }

    @Override
    public boolean shouldFire() {
        return LocalDateTime.now().isAfter(targetTime);
    }

    @Override
    public void execute(LLMService llmS) {
        VoiceService.speak(llmS.callDefInstructedModel("", "A TRIGGERED WAS CALLED. Please warn the user. Context: " + getContext(), false).getSteveResponse(), () -> {});
    }
}
