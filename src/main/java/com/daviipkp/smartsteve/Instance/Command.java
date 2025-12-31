package com.daviipkp.smartsteve.Instance;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Getter
@Setter
public class Command {


    private String CMD_ID;
    private Supplier<Boolean> executable;

    public Command(Supplier<Boolean> execute, String ID) {
        this.executable = execute;
        this.CMD_ID = ID;
    }
    public boolean execute() {
        return executable.get();
    }
}