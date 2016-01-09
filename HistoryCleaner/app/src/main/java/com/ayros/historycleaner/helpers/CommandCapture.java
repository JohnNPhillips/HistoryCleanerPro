package com.ayros.historycleaner.helpers;

import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

public class CommandCapture extends Command
{
    private StringBuilder sb = new StringBuilder();

    public CommandCapture(int id, String... command)
    {
        super(id, false, command);
    }

    @Override
    public void commandOutput(int id, String line) {
        sb.append(line).append('\n');

        super.commandOutput(id, line);
    }

    @Override
    public void commandTerminated(int id, String reason)
    {
        super.commandTerminated(id, reason);
    }

    @Override
    public void commandCompleted(int id, int exitcode)
    {
        super.commandCompleted(id, exitcode);
    }

    @Override
    public String toString()
    {
        return sb.toString();
    }
}
