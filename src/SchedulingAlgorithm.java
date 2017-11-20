// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Vector;
import java.io.*;

class SchedulingAlgorithm {

    static Results run(int quantum, int runtime, Vector<Process> processVec, Results result, int numberOfTickets, String resultsFile) {

        result.schedulingType = "Interactive (Preemptive)";
        result.schedulingName = "Lottery";

        Vector<Process> processVector = new Vector<>();
        processVector = processVec; // local list of processes, to prevent changes in global parameter

        int comptime = 0;
        int currentProcess;
        int previousProcess;
        int size = processVector.size();
        int completed = 0; // number of processes that finished with their work

        try {

            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

            // run first lottery on start
            Lottery lottery = new Lottery(numberOfTickets);
            lottery.run(processVector);
            currentProcess = lottery.getWinner();

            Process process = processVector.elementAt(currentProcess);
            process.arrivaltime = comptime;
            out.println(comptime + ":     process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");

            while (comptime < runtime) {

                // if process has completed
                if (process.cpudone == process.cputime) {

                    completed++;
                    out.println(comptime + ":     process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");

                    // remove process and update number of tickets
                    processVector.remove(process);
                    lottery.updateNumber(lottery.getNumberOfTickets() - process.numTickets);

                    // if all processes are done
                    if (completed == size) {
                        result.compuTime = comptime;
                        out.close();
                        return result;
                    }

                    //choose next process in lottery
                    int i;
                    while (true) {
                        lottery.run(processVector); // run lottery to find out which process should run next
                        i = lottery.getWinner();
                        process = processVector.elementAt(i);
                        if (process.cpudone < process.cputime) {
                            currentProcess = i;
                            break;
                        }
                    }

                    process = processVector.elementAt(currentProcess);
                    if (process.cpudone == 0) process.arrivaltime = comptime;
                    out.println(comptime + ":     process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");
                }

                if (process.ioblocking == process.ionext) {

                    out.println(comptime + ":     process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");

                    process.numblocked++;
                    process.ionext = 0;
                    process.lotnext = 0;
                    previousProcess = currentProcess;

                    // if process is not the only left, choose next in lottery
                    if (processVector.size() > 1) {

                        int i;
                        while (true) {
                            lottery.run(processVector);
                            i = lottery.getWinner();
                            process = processVector.elementAt(i);
                            if (process.cpudone < process.cputime && previousProcess != i) {
                                currentProcess = i;
                                break;
                            }
                        }

                        process = processVector.elementAt(currentProcess);
                        if (process.cpudone == 0) process.arrivaltime = comptime;
                        out.println(comptime + ":     process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");
                    }
                }

                if (process.ioblocking > 0) {
                    process.ionext++;
                }

                // periodic lottery run
                if (process.lotnext == quantum){
                    process.lotnext = 0;

                    if (processVector.size() == 1) currentProcess = 0; // if only one process left, start it

                    else {
                        int i;
                        while (true) {
                            lottery.run(processVector);
                            i = lottery.getWinner();
                            process = processVector.elementAt(i);
                            if (process.cpudone < process.cputime) {
                                currentProcess = i;
                                break;
                            }
                        }
                    }

                    process = processVector.elementAt(currentProcess);
                    if (process.cpudone == 0) process.arrivaltime = comptime;
                    out.println(comptime + ":     process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");
                }
                process.cpudone++;
                process.lotnext++;
                comptime++;
            }

           out.close();
            System.out.println(processVector.size());

        } catch (IOException e) { /* Handle exceptions */ }
        result.compuTime = comptime;
        return result;
    }

}
